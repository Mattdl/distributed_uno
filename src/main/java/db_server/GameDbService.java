package db_server;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import model.Card;
import model.Game;
import model.Move;
import model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stub_RMI.appserver_dbserver.GameDbStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

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

    @Override
    public synchronized boolean persistGame(Game gameToPersist) throws RemoteException {

        LOGGER.info("Persisting Game = {}", gameToPersist);

        try {
            //Game gameInDb = gameDao.queryForId(gameToPersist.getGameId());

            createOrUpdateGame(gameToPersist);

            LOGGER.info("Game was not in database. New entry inserted, Game = {}", gameToPersist);


        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        LOGGER.info("Game persisted, Game = {}", gameToPersist);

        return true;
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
        LOGGER.info("Persisting player = {}",player);

        //First object itself
        playerDao.createOrUpdate(player);

        // Then persist Foreign Key List objects
        for (Card card : player.getHand()) {
            card.setPlayer(player);
            cardDao.createOrUpdate(card);
        }
    }

    private void createMove(Move move) throws SQLException {
        LOGGER.info("Persisting move = {}",move);
        //First persist the foreign key objects
        cardDao.createOrUpdate(move.getCard());

        if(move.getPlayer() != null) {
            createPlayer(move.getPlayer());
        }


        //The object itself
        moveDao.createOrUpdate(move);

    }

    @Override
    public synchronized boolean persistMove(String gameName, Move move) throws RemoteException {

        try {
            Game game = gameDao.queryForId(gameName);

            Collection<Move> moves = game.getMovesCollection();
            //moves.

            //ForeignCollection<Move> movesForeign = (ForeignCollection<Move>) moves;

            //movesForeign.add(move);

            //game.addMove(move);

            //gameDao.update(game);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
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

                for(Player player:game.getPlayerList()){
                    player.setHand(new ArrayList<>(player.getHandCollection()));
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
