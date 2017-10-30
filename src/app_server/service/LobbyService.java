package app_server.service;

import db_server.GameDbService;
import model.Game;
import model.Lobby;
import model.Player;
import stub_RMI.client_appserver.LobbyStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class LobbyService extends UnicastRemoteObject implements LobbyStub {

    private Lobby lobby;
    private GameDbService gameDbService;

    public LobbyService(Lobby lobby) throws RemoteException {
        this.lobby = lobby;
    }

    @Override
    public List<Game> getJoinableGames() throws RemoteException {

        //Get games from database that are not full
        return gameDbService.getJoinableGames();
    }

    @Override
    public void createNewGame(Player initPlayer, String gameName, int gameSize) throws RemoteException {
        lobby.addGame(new Game(gameName, gameSize, initPlayer));
    }
}
