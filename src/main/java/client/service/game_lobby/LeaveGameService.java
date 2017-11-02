package client.service.game_lobby;

import client.Main;
import client.controller.LoginController;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import stub_RMI.client_appserver.LobbyStub;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LeaveGameService extends Service<String> {

    private static final Logger LOGGER = Logger.getLogger(LeaveGameService.class.getName());


    private String gameName;

    public LeaveGameService(String gameName) {
        this.gameName = gameName;
    }

    @Override
    protected Task<String> createTask() {
        return new Task<String>() {
            @Override
            protected String call() throws Exception {
                Registry myRegistry = LocateRegistry.getRegistry(Main.appServer.getIp(), Main.appServer.getPort());

                LOGGER.log(Level.INFO, "Between myRegistry and lookup in LeaveGameService");

                LobbyStub lobbyService = (LobbyStub) myRegistry.lookup("LobbyService");

                LOGGER.log(Level.INFO, "Lookup successful");


                String ret = lobbyService.leaveGame(Main.currentPlayer, gameName);

                LOGGER.log(Level.INFO, "Successfully removed: " + ret);


                //TODO LOGGER

                return ret;
            }
        };
    }
}
