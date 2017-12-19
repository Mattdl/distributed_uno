package db_server;

import app_server.DeckBuilder;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stub_RMI.appserver_dbserver.GameDbStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GameDbService extends UnicastRemoteObject implements GameDbStub {

    final Logger LOGGER = LoggerFactory.getLogger(GameDbService.class);

    private Dao<Game, String> gameDao;
    private Dao<Move, String> moveDao;
    private Dao<Player, String> playerDao;
    private Dao<Card, String> cardDao;

    private List<DbServer> otherDatabases;
    private ReadWriteLock otherDatabasesLock = new ReentrantReadWriteLock();

    private DatabaseServer databaseServer;


    public GameDbService() throws RemoteException {
    }

    public GameDbService(List<DbServer> otherDatabases, Dao<Game, String> gameDao, Dao<Move, String> moveDao, Dao<Player, String> playerDao, Dao<Card, String> cardDao, DatabaseServer databaseServer) throws RemoteException {
        this.otherDatabases = otherDatabases;
        this.gameDao = gameDao;
        this.moveDao = moveDao;
        this.playerDao = playerDao;
        this.cardDao = cardDao;
        this.databaseServer = databaseServer;
    }

    /**
     * Called once at creation of the Game on the ApplicationServer.
     * Be careful when you call this multiple times! The previous Game will be updated and Players will keep the cards
     * of the previous init call!
     *
     * @param gameToPersist
     * @return
     * @throws RemoteException
     */
    @Override
    public synchronized boolean persistGame(Game gameToPersist, boolean propagate) throws RemoteException {

        throwIfNotRunning();

        LOGGER.info("Persisting Game = {}", gameToPersist);

        //If you want to update a whole game object, best is to erase everything and do new inserts.

        try {
            //Game gameInDb = gameDao.queryForId(gameToPersist.getGameId());

            createOrUpdateGame(gameToPersist);

            LOGGER.info("Game was not in database. New entry inserted, Game = {}", gameToPersist);

            return true;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;

        } finally {
            new Thread() {
                @Override
                public void run() {
                    LOGGER.info("Game persisted, Game = {}", gameToPersist);

                    if (propagate) {
                        persistGameToOtherDatabases(gameToPersist);
                    }
                }
            }.start();
        }

    }

    /**
     * Propagate the persisting to all other databases
     *
     * @param gameToPersist
     */
    private void persistGameToOtherDatabases(Game gameToPersist) {

        otherDatabasesLock.readLock().lock();

        for (DbServer otherDbServer : otherDatabases) {

            if (otherDbServer.isConnected()) {

                GameDbStub gameDbStub = otherDbServer.getGameDbStub();

                try {

                    gameDbStub.persistGame(gameToPersist, false);

                    LOGGER.info("GAME '{}' was persisted to other database = {}", gameToPersist.getGameId(), otherDbServer);

                } catch (Exception e) {
                    LOGGER.error("DATABASE '{}'COULD NOT PERSIST TO OTHER DATABASE : {}", this.databaseServer, otherDbServer);
                    //e.printStackTrace();
                }
            }
        }

        otherDatabasesLock.readLock().unlock();

    }

    private void createOrUpdateGame(Game game) throws SQLException {

        //First object itself
        gameDao.createOrUpdate(game);

        //Then ForeignCollections
        for (Player player : game.getPlayerList()) {
            player.setGame(game);
            createPlayer(player);
        }

        for (Card card : game.getDeck()) {
            card.setGame(game);
            cardDao.createOrUpdate(card);
        }

        for (Move move : game.getMoves()) {
            move.setGame(game);
            createMove(move);
        }
    }

    private void createPlayer(Player player) throws SQLException {
        LOGGER.info("Creating player = {}", player);

        //First object itself
        playerDao.createOrUpdate(player);

        // Then persist Foreign Key List objects
        for (Card card : player.getHand()) {
            card.setPlayer(player);
            cardDao.createOrUpdate(card);
        }
    }

    private void createMove(Move move) throws SQLException {
        LOGGER.info("Creating move = {}", move);

        //First persist the foreign key objects
        cardDao.createOrUpdate(move.getCard());

        if (move.getPlayer() != null) {
            createPlayer(move.getPlayer());
        }


        //The object itself
        moveDao.createOrUpdate(move);

    }

    //TODO test if works
    @Override
    public synchronized boolean persistMove(String gameId, Move move, boolean propagate) throws RemoteException {

        throwIfNotRunning();

        LOGGER.info("Persisting 1 move = {}, for game = {}", move, gameId);

        try {
            Game game = gameDao.queryForId(gameId);

            ForeignCollection<Move> movesForeign = (ForeignCollection<Move>) game.getMovesCollection();

            // Should add the move
            movesForeign.add(move);

            LOGGER.info("DATABASE, Move was persisted = {}", move);

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {

            new Thread() {
                @Override
                public void run() {
                    LOGGER.info("PROPAGATE TO OTHER DATABASES = {}", propagate);

                    if (propagate) {
                        persistMoveToOtherDatabases(gameId, move);
                        LOGGER.info("PROPAGATED TO OTHER DATABASES ");
                    }

                    LOGGER.info("Ending persistMove");
                }
            }.start();
        }
    }

    private void persistMoveToOtherDatabases(String gameName, Move move) {

        otherDatabasesLock.readLock().lock();

        for (DbServer otherDbServer : otherDatabases) {

            if (otherDbServer.isConnected()) {

                GameDbStub gameDbStub = otherDbServer.getGameDbStub();

                try {

                    gameDbStub.persistMove(gameName, move, false);

                    LOGGER.info("MOVE for GAME '{}' was persisted to other database = {}", gameName, otherDbServer);

                } catch (Exception e) {
                    LOGGER.error("DATABASE '{}'COULD NOT PERSIST TO OTHER DATABASE : {}", this.databaseServer, otherDbServer);
                    //e.printStackTrace();
                }
            }
        }

        otherDatabasesLock.readLock().unlock();

    }

    /**
     * Method used to restore a Game, when the appServer has crashed.
     *
     * @param gameName
     * @return
     * @throws RemoteException
     */
    @Override
    public synchronized Game fetchGame(String gameName) throws RemoteException {

        throwIfNotRunning();

        LOGGER.info("Fetching Game with name = '{}'", gameName);

        try {
            Game game = gameDao.queryForId(gameName);

            LOGGER.info("GAME FOUND IN QUERY = {}", game);


            if (game != null) {

                // Converting ForeignCollections to Arraylists
                game.setMoves(new ArrayList<>(game.getMovesCollection()));
                game.setDeck(new ArrayList<>(game.getDeckCollection()));
                game.setPlayerList(new ArrayList<>(game.getPlayerListCollection()));

                LOGGER.info("FETCHED DECK = {}", game.getDeck());

                for (Player player : game.getPlayerList()) {
                    player.setHand(new ArrayList<>(player.getHandCollection()));

                    LOGGER.info("PLAYER HAND = {}", player.getHand());

                }
            }

            LOGGER.info("Game fetched with name = '{}', result = {}", gameName, game);

            return game;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Searches winning player in database and adds his score.
     *
     * @param player
     * @return
     * @throws RemoteException
     */
    @Override
    public synchronized void updateWinner(Player player) throws RemoteException {

        throwIfNotRunning();

        LOGGER.info("Fetching Player in database");

        try {
            //Find corresponding user in database
            LOGGER.info("Old highscore: " + playerDao.queryForId(player.getName()).getHighscore());
            playerDao.update(player);
            LOGGER.info("New highscore: " + playerDao.queryForId(player.getName()).getHighscore());
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    /**
     * Update the list of other databases
     *
     * @param otherDatabases
     */
    public void updateOtherDatabases(List<DbServer> otherDatabases) {
        LOGGER.info("DATABASE GAMESERVICE UPDATING CONNECTIONS");

        otherDatabasesLock.writeLock().lock();
        this.otherDatabases = otherDatabases;
        otherDatabasesLock.writeLock().unlock();
    }

    public List<Card> fetchCardImageMappings(boolean isSpecialEdition) throws RemoteException {

        throwIfNotRunning();

        LOGGER.info("DATABASE FETCHING CARDS FOR APPSERVER");
        return new DeckBuilder().getAllCardImageMappings(isSpecialEdition);
    }


    /**
     * Method used to fetch highscore from player
     *
     * @param playerName
     * @return
     * @throws RemoteException
     */
    @Override
    public synchronized int fetchPlayerScore(String playerName) throws RemoteException {

        throwIfNotRunning();

        try {

            Player player = playerDao.queryForId(playerName);
            int score = 0;

            if (player != null) {
                score = player.getHighscore();
            }

            return score;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Method used to recreate games from a database
     *
     * @return List of games
     * @throws RemoteException
     */
    @Override
    public synchronized List<Game> copyDatabase() throws RemoteException {

        throwIfNotRunning();

        List<Game> gameList = new ArrayList<>();
        try {
            List<Game> gameDbList = gameDao.queryForAll();
            for (Game game : gameDbList) {
                game = fetchGame(game.getGameId());
                gameList.add(game);
            }
            return gameList;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Throws RemoteException if the instance is not running
     *
     * @throws RemoteException
     */
    private void throwIfNotRunning() throws RemoteException {
        if (!databaseServer.isInstanceRunning()) {
            LOGGER.error("DATABASE '{}:{}' NOT RUNNING", this.databaseServer.getDbIp(), this.databaseServer.getDbPort());
            throw new RemoteException("INSTANCE IS NOT RUNNING : '" + databaseServer.getDbIp() + ":" + databaseServer.getDbPort() + "'");
        }
    }
}
