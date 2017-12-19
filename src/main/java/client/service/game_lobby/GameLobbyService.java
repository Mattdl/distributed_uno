package client.service.game_lobby;

import client.Main;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stub_RMI.client_appserver.LobbyStub;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Service that obtains the information for an in-game Lobby
 */
public class GameLobbyService extends Service<Void> {

    final Logger LOGGER = LoggerFactory.getLogger(GameLobbyService.class);

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

                    LOGGER.info("Requesting GameLobby info, for game = {}",clientGame.getGameId());
                    Game serverSideGame = lobbyService.getGameLobbyInfo(clientGame.getVersion(), clientGame.getGameId(), Main.token);


                    //We have to make copy in order to notify the observers of the game
                    clientGame.makeCopy(serverSideGame);

                    LOGGER.info("Received from server GameLobby info, game = {0}", serverSideGame);
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
