package app_server.service;

import app_server.AppServer;
import model.Game;
import model.Lobby;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stub_RMI.appserver_dbserver.GameDbStub;
import stub_RMI.client_appserver.GameLobbyStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Service to check if everybody joined the game from the GameLobby
 */
public class GameLobbyService extends UnicastRemoteObject implements GameLobbyStub {

    final Logger LOGGER = LoggerFactory.getLogger(GameLobbyService.class);

    private Lobby lobby;
    private GameDbStub gameDbService;

    private AppServer appServer;


    public GameLobbyService(Lobby lobby, GameDbStub gameDbService, AppServer appServer) throws RemoteException {
        this.lobby = lobby;
        this.gameDbService = gameDbService;
        this.appServer = appServer;
    }

    /**
     * RMI call that is called on client side after that a player has initialized his game.
     * It only returns true when all players in the game have joined (and initialized) the game.
     *
     * @param gameName
     * @return
     * @throws RemoteException
     */
    @Override
    public synchronized boolean hasEverybodyJoined(String gameName) throws RemoteException {
        LOGGER.info("Entering hasEverybodyJoined");

        try {
            Game game = lobby.findGame(gameName);
            game.addJoinedPlayer();
            if (game.getJoinedPlayers() < game.getPlayerList().size()) {
                LOGGER.info("Waiting on other players");
                wait();
            } else {

                if (!game.isInitialyPersisted()) {
                    LOGGER.info("Persisting Game Object to Database!");

                    boolean persistedToDb = false;

                    while (!persistedToDb) {
                        try {
                            gameDbService.persistGame(game, true);
                            persistedToDb = true;
                        } catch (Exception e) {
                            LOGGER.error("APPSERVER COULD NOT CONNECT TO DATABASE FOR ACTION");
                            AppServer.retrieveNewDatabaseInfo(appServer);
                            AppServer.registerAsClientWithDatabase(appServer);
                        }
                    }
                    game.setInitialyPersisted(true);

                    //TODO delete this, just for testing
                    //Game gameRet = gameDbService.fetchGame(game.getGameId());
                    //LOGGER.info("FETCHED GAME FROM DATABASE = {}", gameRet);

                    //LOGGER.info("CONTENT OF PLAYERLIST = {}", gameRet.getPlayerList());


                    LOGGER.info("Game Object PERSISTED to Database!");
                }

                notifyAll();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
