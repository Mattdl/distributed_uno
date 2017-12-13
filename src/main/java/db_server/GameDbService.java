package db_server;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import model.Game;
import model.Move;
import model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stub_RMI.appserver_dbserver.GameDbStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;

public class GameDbService extends UnicastRemoteObject implements GameDbStub {

    final Logger LOGGER = LoggerFactory.getLogger(GameDbService.class);


    private ConnectionSource conn;
    private Dao<Game, String> gameDao;
    private Dao<Move, String> moveDao;

    public GameDbService() throws RemoteException {

    }

    public GameDbService(Dao<Game, String> gameDao, Dao<Move, String> moveDao) throws RemoteException{
        this.moveDao = moveDao;
        this. gameDao = gameDao;
    }

    @Override
    public synchronized boolean persistGame(Game gameToPersist) throws RemoteException {

        LOGGER.info("Persisting Game = {}",gameToPersist);

        try {
            Game gameInDb = gameDao.queryForId(gameToPersist.getGameId());

            if(gameInDb == null){
                gameDao.create(gameToPersist);
                LOGGER.info("Game was not in database. New entry inserted, Game = {}",gameToPersist);
            }
            else{
                gameDao.update(gameToPersist);
                LOGGER.info("Game was in database. Entry updated, Game = {}",gameToPersist);
            }

        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }

        LOGGER.info("Game persisted, Game = {}",gameToPersist);

        return true;
    }

    @Override
    public synchronized boolean persistMove(String gameName, Move move) throws RemoteException {

        try {
            Game game = gameDao.queryForId(gameName);

            game.addMove(move);

            gameDao.update(game);

        }catch (SQLException e){
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

        LOGGER.info("Fetching Game with name = '{}'",gameName);

        try{
            Game game = gameDao.queryForId(gameName);

            LOGGER.info("Game fetched with name = '{}', result = {}",gameName,game);

            return game;
        }catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }
}
