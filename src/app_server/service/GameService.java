package app_server.service;

import db_server.GameDbService;
import model.Game;
import model.Lobby;
import model.Move;
import stub_RMI.client_appserver.GameStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class GameService extends UnicastRemoteObject implements GameStub {

    private Lobby lobby;
    private GameDbService gameDbService;

    public GameService(Lobby lobby) throws RemoteException {
        this.lobby = lobby;
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
