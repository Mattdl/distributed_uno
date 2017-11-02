package client.service.game;

import client.Main;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.Game;
import stub_RMI.client_appserver.GameStub;
import stub_RMI.client_appserver.LobbyStub;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CheckPlayersService extends Service<Void> {

    private static final Logger LOGGER = Logger.getLogger(CheckPlayersService.class.getName());


    private Game game;

    public CheckPlayersService(Game game) {
        this.game = game;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                LOGGER.log(Level.INFO, "Starting Task: CheckPlayersService");

                Registry myRegistry = LocateRegistry.getRegistry(Main.appServer.getIp(), Main.appServer.getPort());
                GameStub gameService = (GameStub) myRegistry.lookup("GameService");

                boolean successful = gameService.hasEverybodyJoined(game.getGameName());

                LOGGER.log(Level.INFO, "CheckPlayersService result = {0}", successful);

                return null;
            }
        };
    }
}
