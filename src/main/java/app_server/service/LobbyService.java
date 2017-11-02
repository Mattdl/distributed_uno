package app_server.service;

import client.controller.LobbyController;
import javafx.util.Pair;
import model.Game;
import model.Lobby;
import model.Player;
import stub_RMI.client_appserver.LobbyStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LobbyService extends UnicastRemoteObject implements LobbyStub {

    private static final Logger LOGGER = Logger.getLogger(LobbyService.class.getName());


    private Lobby lobby;
    //private GameDbService gameDbService;
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

        LOGGER.info("Getting joinable games.");

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

            LOGGER.info("Found joinable games.");

            return new Pair<>(games, version);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * Method to create a new game
     *
     * @param initPlayer
     * @param gameName
     * @param gameSize
     * @param password
     * @return Returns a message if failed, null if successful
     * @throws RemoteException
     */
    public synchronized boolean createNewGame(Player initPlayer, String gameName, int gameSize, String password)
            throws RemoteException {
        //TODO extend with password (if time)

        LOGGER.log(Level.INFO, "createNewGame @Server");


        //TODO CHECK IF NAME IS UNIQUE!
        lobby.addGame(new Game(gameName, gameSize, initPlayer));
        LOGGER.log(Level.INFO, "New game added to list");

        lobbyUpdated();

        return true;
    }

    /**
     * Method to join a game in the lobby
     *
     * @param player
     * @param gameName
     * @return
     * @throws RemoteException
     */
    @Override
    public String joinGame(Player player, String gameName) throws RemoteException {
        Game gameInLobby = lobby.findGame(gameName);

        if (gameInLobby != null) {
            if (gameInLobby.isJoinable()) {
                gameInLobby.addPlayer(player);
                lobbyUpdated();
                return null;
            } else {
                return "Could not join the game...";
            }
        } else {
            return "Game could not be found in the lobby...";
        }
    }

    /**
     * Method that makes a client leave a game. If it was the last client, the game is removed.
     * @param player
     * @param gameName
     * @return
     * @throws RemoteException
     */
    @Override
    public String leaveGame(Player player, String gameName) throws RemoteException {
        Game gameInLobby = lobby.findGame(gameName);

        if (gameInLobby != null) {
            if (gameInLobby.getPlayerList().size() <= 1) {
                lobby.getGameList().remove(gameInLobby);
                lobbyUpdated();
                return null;
            } else {
                if (gameInLobby.removePlayer(player)) {
                    lobbyUpdated();
                    return null;
                } else {
                    return "Player could not be found in Game playerlist...";
                }
            }
        } else {
            return "Game could not be found in the lobby...";
        }
    }

    private void lobbyUpdated() {
        lobbyVersion++;
        LOGGER.log(Level.INFO, "lobbyUpdated method");

        notifyAll();

        LOGGER.log(Level.INFO, "Notified everybody");

    }
}
