package client.service.game_lobby;

import client.Main;
import client.controller.LobbyController;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.Game;
import stub_RMI.client_appserver.LobbyStub;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service that obtains the information for an in-game Lobby
 */
public class GameLobbyService extends Service<Void> {

    private static final Logger LOGGER = Logger.getLogger(GameLobbyService.class.getName());

    private Game clientGame;
    private boolean inGameLobby;

    public GameLobbyService(Game game) {
        this.clientGame = game;
        clientGame.setVersion(-1);
        inGameLobby = true;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                LOGGER.info("Starting GameLobbyService Task");
                Registry myRegistry = LocateRegistry.getRegistry(Main.appServer.getIp(), Main.appServer.getPort());
                LobbyStub lobbyService = (LobbyStub) myRegistry.lookup("LobbyService");

                LOGGER.info("Connected to RMI registry");


                while (inGameLobby) {

                    LOGGER.info("Requesting GameLobby info");
                    Game serverSideGame = lobbyService.getGameLobbyInfo(clientGame.getVersion(), clientGame.getGameName());

                    //We have to make copy in order to notify the observers of the game
                    clientGame.makeCopy(serverSideGame);

                    LOGGER.log(Level.INFO, "Received from server GameLobby info, game = {0}", serverSideGame);
                    }
                return null;
            }
        };
    }

    public Game getGame() {
        return clientGame;
    }

    public void setGame(Game game) {
        this.clientGame = game;
    }

    public boolean isInGameLobby() {
        return inGameLobby;
    }

    public void setInGameLobby(boolean inGameLobby) {
        this.inGameLobby = inGameLobby;
    }
}
