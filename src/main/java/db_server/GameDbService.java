package db_server;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import model.Game;
import model.Move;
import model.Player;
import stub_RMI.appserver_dbserver.GameDbStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class GameDbService extends UnicastRemoteObject implements GameDbStub {

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

        try {
            Game gameInDb = gameDao.queryForId(gameToPersist.getGameName());

            if(gameInDb == null){
                gameDao.create(gameToPersist);
            }
            else{
                gameDao.update(gameToPersist);
            }

        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }

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
        try{
            Game game = gameDao.queryForId(gameName);

            return game;
        }catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }
}
