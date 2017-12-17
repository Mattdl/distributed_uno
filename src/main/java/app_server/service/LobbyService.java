package app_server.service;

import model.Game;
import model.Lobby;
import model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stub_RMI.client_appserver.LobbyStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class LobbyService extends UnicastRemoteObject implements LobbyStub {

    private static final Logger LOGGER = LoggerFactory.getLogger(LobbyService.class.getName());


    private Lobby lobby;
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    //private GameDbService gameDbService;

    public LobbyService(Lobby lobby) throws RemoteException {
        this.lobby = lobby;
    }

    /**
     * First method called to init joinable games. We pass a Lobby object that is serializable, but only with the
     * joinable games. We don't want to pass ALL the games, as this is redundant information for the client.
     *
     * @return
     * @throws RemoteException
     */
    public synchronized Lobby getJoinableGames(int version) throws RemoteException {

        LOGGER.info("Getting joinable games.");

        try {
            //If it has already received joinable games one time, let wait until notify
            if (version >= this.lobby.getVersion()) {
                wait();
            }

            lock.readLock().lock();
            List<Game> joinableGames = new LinkedList<>();
            for (Game game : lobby.getGameList()) {
                if (game.isJoinable()) {
                    joinableGames.add(game);
                }
            }

            LOGGER.info("Found joinable games, lobby = {}", lobby);

            return new Lobby(joinableGames, lobby.getVersion());
        } catch (Exception e) {
            e.printStackTrace();
        }
        lock.readLock().unlock();

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
    public synchronized String createNewGame(Player initPlayer, String gameName, int gameSize, String password)
            throws RemoteException {
        //TODO extend with password (if time)

        try {
            LOGGER.info("createNewGame @Server, readlocks = {}, writelocks = {}",lock.);

            lock.writeLock().lock();

            Game game = new Game(gameName, gameSize, initPlayer);

            game.setDeck();
            lobby.addGame(game);

            LOGGER.info("New game added to list");

            lobbyUpdated();

            return game.getGameId();
        } finally {
            lock.writeLock().unlock();
        }
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
    public synchronized String joinGame(Player player, String gameName) throws RemoteException {

        LOGGER.info("Trying to join game!");

        lock.readLock().lock();

        Game gameInLobby = lobby.findGame(gameName);

        lock.readLock().unlock();


        try {
            if (gameInLobby != null) {
                if (gameInLobby.isJoinable()) {

                    lock.writeLock().lock();
                    gameInLobby.addPlayer(player);
                    gameUpdated(gameInLobby);
                    lock.writeLock().unlock();

                    return null;
                } else {
                    return "Could not join the game...";
                }
            } else {
                return "game could not be found in the lobby...";
            }
        } finally {
        }
    }

    /**
     * Method that makes a client leave a game. If it was the last client, the game is removed.
     *
     * @param player
     * @param gameName
     * @return
     * @throws RemoteException
     */
    @Override
    public synchronized String leaveGame(Player player, String gameName) throws RemoteException {
        LOGGER.info("Trying to remove PLAYER " + player.getName() + " from GAME " + gameName);

        lock.writeLock().lock();

        try {

            Game gameInLobby = lobby.findGame(gameName);

            if (gameInLobby != null) {
                LOGGER.info("gameInLobby is found, and != null");
                if (gameInLobby.getPlayerList().size() <= 1) {
                    LOGGER.info("Last player leaves, removing game");


                    lobby.getGameList().remove(gameInLobby);
                    gameUpdated(gameInLobby);

                    LOGGER.info("game removed");

                    return null;
                } else {
                    if (gameInLobby.removePlayer(player)) {
                        LOGGER.info("Player removed");

                        gameUpdated(gameInLobby);
                        return null;
                    } else {
                        LOGGER.info("Player could not be removed");
                        return "Player could not be found in game playerlist...";
                    }
                }
            } else {
                LOGGER.info("Could not find game, gameInLobby is null");
                return "game could not be found in the lobby...";
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public synchronized Game getGameLobbyInfo(int clientVersion, String gameName) throws RemoteException {
        LOGGER.info("Entering getGameLobbyInfo");

        lock.readLock().lock();
        Game game = lobby.findGame(gameName);
        lock.readLock().unlock();

        try {

            LOGGER.info("getGameLobbyInfo, clientversion={}, gameName = '{}', found game={}", clientVersion, gameName, game);
            //Use while here, because otherwise for every total Lobby update, it will continue...
            while (clientVersion >= game.getVersion()) {
                wait();
            }

            //TODO only return lightweight version of game
            return game;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Could not find the requested game for lobby info!");
        }

        return null;
    }

    private synchronized void gameUpdated(Game game) {
        game.updateVersion();
        lobbyUpdated();
    }

    private synchronized void lobbyUpdated() {
        lobby.updateVersion();
        LOGGER.info("LOBBY VERSION INCREMENTED");

        notifyAll();

        LOGGER.info("NOTIFIED OF LOBBY VERSION INCREMENTED");

    }
}
