package app_server.service;

import model.Game;
import model.Move;
import stub_RMI.client_appserver.GameStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class GameService extends UnicastRemoteObject implements GameStub {

    private Game game;

    public GameService() throws RemoteException {
    }

    @Override
    public Game getInitGameInfo() throws RemoteException {
        return null;
    }

    @Override
    public synchronized Move getPlayedMove() throws RemoteException {

        return null;
    }
}
