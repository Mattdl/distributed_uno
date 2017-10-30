package db_server;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import model.Game;
import stub_RMI.appserver_dbserver.GameDbStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

public class GameDbService extends UnicastRemoteObject implements GameDbStub {

    private Connection conn;
    private Dao<Game, String> gameDao;

    public GameDbService() throws RemoteException {

    }

    public GameDbService(Connection conn) throws RemoteException {
        this.conn = conn;
        //this. gameDao = DaoManager.createDao(conn, Game.class);
    }

    @Override
    public List<Game> getJoinableGames() throws RemoteException {

        //TODO query instead
        //List<Game> games = gameDao.queryForAll();

        return null;
    }
}
