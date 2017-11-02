package client.service.lobby;

import client.controller.LobbyController;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.Player;
import stub_RMI.client_appserver.LobbyStub;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateGameService extends Service<Boolean> {

    private static final Logger LOGGER = Logger.getLogger(LobbyController.class.getName());


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
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {

                LOGGER.log(Level.INFO, "Trying to create game in CreateGameService");


                Registry myRegistry = LocateRegistry.getRegistry("localhost", 1200);

                LOGGER.log(Level.INFO, "Stuck in between");


                LobbyStub lobbyService = (LobbyStub) myRegistry.lookup("LobbyService");

                LOGGER.log(Level.INFO, "Succesfully created game in CreateGameService");


                boolean successful = lobbyService.createNewGame(initPlayer, name, size, password);

                return successful;
            }
        };
    }
}
