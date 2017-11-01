package client.service.game_lobby;

import client.Main;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import stub_RMI.client_appserver.LobbyStub;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LeaveGameService extends Service<String> {

    private String gameName;

    public LeaveGameService(String gameName) {
        this.gameName = gameName;
    }

    @Override
    protected Task<String> createTask() {
        return new Task<String>() {
            @Override
            protected String call() throws Exception {
                Registry myRegistry = LocateRegistry.getRegistry("localhost", 1200);

                LobbyStub lobbyService = (LobbyStub) myRegistry.lookup("LobbyService");

                String ret = lobbyService.leaveGame(Main.currentPlayer, gameName);

                //TODO LOGGER

                return ret;
            }
        };
    }
}
