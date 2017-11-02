package client.service.lobby;

import client.Main;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.Player;
import stub_RMI.client_appserver.LobbyStub;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CreateGameService extends Service<Boolean> {

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

                Registry myRegistry = LocateRegistry.getRegistry(Main.appServer.getIp(), Main.appServer.getPort());
                LobbyStub lobbyService = (LobbyStub) myRegistry.lookup("LobbyService");

                boolean successful = lobbyService.createNewGame(initPlayer, name, size, password);

                return successful;
            }
        };
    }
}
