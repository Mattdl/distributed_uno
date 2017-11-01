package app_server.service;

import db_server.GameDbService;
import javafx.util.Pair;
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
    private int lobbyVersion;

    public LobbyService(Lobby lobby) throws RemoteException {
        this.lobby = lobby;
        this.lobbyVersion = 0;
    }

    /**
     * First method called to init joinable games
     *
     * @return
     * @throws RemoteException
     */
    public synchronized Pair<List<Game>, Integer> getJoinableGames(int version) throws RemoteException {

        try {
            //If it has already received joinable games one time, let wait until notify
            if (version >= this.lobbyVersion) {
                wait();
            }

            List<Game> games = new LinkedList<>();
            for (Game game : lobby.getGameList()) {
                if (game.isJoinable()) {
                    games.add(game);
                }
            }

            return new Pair<>(games, version);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public synchronized boolean createNewGame(Player initPlayer, String gameName, int gameSize, String password)
            throws RemoteException {
        //TODO extend with password (if time)

        lobby.addGame(new Game(gameName, gameSize, initPlayer));
        lobbyUpdated();

        return true;
    }

    private void lobbyUpdated() {
        lobbyVersion++;
        notifyAll();
    }
}
