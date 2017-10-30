package db_server;

import model.Game;
import stub_RMI.appserver_dbserver.GameDbStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;

public class GameDbService extends UnicastRemoteObject implements GameDbStub {

    public GameDbService() throws RemoteException {
    }

    @Override
    public List<Game> getJoinableGames() throws RemoteException {

        //TODO query instead
        List<Game> games = new LinkedList<>();

        return games;
    }
}
