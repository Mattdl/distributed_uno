package db_server;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import model.Game;
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
    private Dao<Player, String> playerDao;

    public GameDbService() throws RemoteException {

    }

    public GameDbService(ConnectionSource conn) throws RemoteException, SQLException {
        this.conn = conn;
        this.playerDao = DaoManager.createDao(conn, Player.class);
        //this. gameDao = DaoManager.createDao(conn, Game.class);
    }

}
