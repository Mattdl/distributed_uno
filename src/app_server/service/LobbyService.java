package app_server.service;

import db_server.GameDbService;
import model.Game;
import stub_RMI.client_appserver.GameStub;
import stub_RMI.client_appserver.LobbyStub;
import stub_RMI.client_appserver.LoginStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class LobbyService extends UnicastRemoteObject implements LobbyStub {

    private GameDbService gameDbService;

    public LobbyService(GameDbService gameDbService) throws RemoteException {
        this.gameDbService = gameDbService;
    }

    @Override
    public List<Game> getJoinableGames() throws RemoteException {

        //Get games from database that are not full
        return gameDbService.getJoinableGames();
    }
}
