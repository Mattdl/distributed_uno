package client.service.lobby;

import app_server.service.LobbyService;
import client.Main;
import client.controller.LobbyController;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.Player;
import stub_RMI.client_appserver.LobbyStub;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateGameService extends Service<String> {

    private static final Logger LOGGER = Logger.getLogger(CreateGameService.class.getName());


    private String name;
    private int size;
    private Player initPlayer;
    private String password;

    public CreateGameService(String name, int size, Player initPlayer, String password) {
        this.name = name;
        this.size = size;
        this.initPlayer = initPlayer;
        this.password = password;
    }

    /**
     * Tries to create a new game through RMI on server
     * @return
     */
    protected Task<String> createTask() {
        return new Task<String>() {
            @Override
            protected String call() throws Exception {

                String msg = Main.appServer.getIp() + ": " + Main.appServer.getPort();

                Registry myRegistry = LocateRegistry.getRegistry(Main.appServer.getIp(), Main.appServer.getPort());
                LOGGER.log(Level.INFO, msg);
                LobbyStub lobbyService = (LobbyStub) myRegistry.lookup("LobbyService");

                LOGGER.log(Level.INFO, "Lookup successful");


                String gameId = lobbyService.createNewGame(initPlayer, name, size, password);

                LOGGER.log(Level.INFO, "Return value, created game with Id = {0}",gameId);

                return gameId;
            }
        };
    }
}
