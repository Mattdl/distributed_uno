package db_server;

import com.j256.ormlite.dao.CloseableIterator;
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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class GameDbService extends UnicastRemoteObject implements GameDbStub {

    final Logger LOGGER = LoggerFactory.getLogger(GameDbService.class);

    private Dao<Game, String> gameDao;
    private Dao<Move, String> moveDao;
    private Dao<Player, String> playerDao;
    private Dao<Card, String> cardDao;

    public GameDbService() throws RemoteException {

    }

    public GameDbService(Dao<Game, String> gameDao, Dao<Move, String> moveDao, Dao<Player, String> playerDao, Dao<Card, String> cardDao) throws RemoteException {
        this.gameDao = gameDao;
        this.moveDao = moveDao;
        this.playerDao = playerDao;
        this.cardDao = cardDao;
    }

    /**
     * Called once at creation of the Game on the AppServer.
     * Be careful when you call this multiple times! The previous Game will be updated and Players will keep the cards
     * of the previous init call!
     *
     * @param gameToPersist
     * @return
     * @throws RemoteException
     */
    @Override
    public synchronized boolean persistGame(Game gameToPersist) throws RemoteException {

        LOGGER.info("Persisting Game = {}", gameToPersist);

        //If you want to update a whole game object, best is to erase everything and do new inserts.

        try {
            //Game gameInDb = gameDao.queryForId(gameToPersist.getGameId());

            createOrUpdateGame(gameToPersist);

            LOGGER.info("Game was not in database. New entry inserted, Game = {}", gameToPersist);


        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        LOGGER.info("Game persisted, Game = {}", gameToPersist);

        persistGameToOtherDatabases(gameToPersist);

        return true;
    }

    /**
     * Propagate the persisting to all other databases
     *
     * @param gameToPersist
     */
    private void persistGameToOtherDatabases(Game gameToPersist) {

        for (DbServer otherDbServer : DatabaseServer.otherDatabases) {

            if (otherDbServer.isConnected()) {

                GameDbStub gameDbStub = otherDbServer.getGameDbStubs();

                try {

                    gameDbStub.persistGame(gameToPersist);

                    LOGGER.info("GAME '{}' was persisted to other database = {}", gameToPersist.getGameId(), otherDbServer);

                } catch (Exception e) {
                    LOGGER.error("COULD NOT PERSIST TO OTHER DATABASE : {}");
                    e.printStackTrace();
                }
            }
        }
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
    public synchronized boolean persistMove(String gameName, Move move) throws RemoteException {
        LOGGER.info("Persisting 1 move = {}, for game = {}", move, gameName);

        try {
            Game game = gameDao.queryForId(gameName);

            ForeignCollection<Move> movesForeign = (ForeignCollection<Move>) game.getMovesCollection();

            // Should add the move
            movesForeign.add(move);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        persistMoveToOtherDatabases(gameName, move);

        return true;
    }

    private void persistMoveToOtherDatabases(String gameName, Move move) {
        for (DbServer otherDbServer : DatabaseServer.otherDatabases) {

            if (otherDbServer.isConnected()) {

                GameDbStub gameDbStub = otherDbServer.getGameDbStubs();

                try {

                    gameDbStub.persistMove(gameName, move);

                    LOGGER.info("MOVE for GAME '{}' was persisted to other database = {}", gameName, otherDbServer);

                } catch (Exception e) {
                    LOGGER.error("COULD NOT PERSIST TO OTHER DATABASE : {}");
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public synchronized boolean persistPlayer(String gameName, Player player) throws RemoteException {
        return false;
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
}
