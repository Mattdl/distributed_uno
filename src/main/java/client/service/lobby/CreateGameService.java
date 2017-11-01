package client.service.lobby;

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

    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {

                Registry myRegistry = LocateRegistry.getRegistry("localhost", 1200);
                LobbyStub lobbyService = (LobbyStub) myRegistry.lookup("LobbyService");

                boolean successful = lobbyService.createNewGame(initPlayer, name, size, password);

                return successful;
            }
        };
    }
}
