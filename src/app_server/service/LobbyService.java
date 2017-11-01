package app_server.service;

import db_server.GameDbService;
import model.Game;
import model.Lobby;
import model.Player;
import stub_RMI.client_appserver.LobbyStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;

public class LobbyService extends UnicastRemoteObject implements LobbyStub {

    private Lobby lobby;
    private GameDbService gameDbService;

    public LobbyService(Lobby lobby) throws RemoteException {
        this.lobby = lobby;
    }

    /**
     * First method called to init joinable games
     *
     * @return
     * @throws RemoteException
     */
    @Override
    public synchronized List<Game> getJoinableGames(List<Game> games) throws RemoteException {

        try {
            //If it has already received joinable games one time, let wait until notify
            if (!games.isEmpty()) {
                wait();
            }

            games = new LinkedList<>();
            for (Game game : lobby.getGameList()) {
                if (game.isJoinable()) {
                    games.add(game);
                }
            }

            return games;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public synchronized void createNewGame(Player initPlayer, String gameName, int gameSize) throws RemoteException {
        lobby.addGame(new Game(gameName, gameSize, initPlayer));
        notifyAll();
    }
}
