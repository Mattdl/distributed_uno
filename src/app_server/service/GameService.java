package app_server.service;

import db_server.GameDbService;
import model.Game;
import model.Move;
import stub_RMI.client_appserver.GameStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class GameService extends UnicastRemoteObject implements GameStub {

    private Game game;
    private GameDbService gameDbService;

    public GameService(GameDbService gameDbService) throws RemoteException {
        this.gameDbService = gameDbService;
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
