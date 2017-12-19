package client.service.game;

import client.Main;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.Card;
import model.Game;
import stub_RMI.client_appserver.GameStub;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EndGameService extends Service<List<String>> {
    private static final Logger LOGGER = Logger.getLogger(CheckPlayersService.class.getName());

    private Game currentGame;

    public EndGameService(Game game) {
        this.currentGame = game;
    }

    @Override
    protected Task<List<String>> createTask() {
        return new Task<List<String>>() {
            @Override
            protected List<String> call() throws Exception {

                LOGGER.log(Level.INFO, "Starting Task: EndGameService");

                Registry myRegistry = LocateRegistry.getRegistry(Main.appServer.getIp(), Main.appServer.getPort());
                GameStub gameService = (GameStub) myRegistry.lookup("GameService");

                List<String> results = gameService.getGameResults(currentGame.getGameId(), Main.token);

                LOGGER.log(Level.INFO, "EndGameService result = {0}", results);

                return results;
            }
        };
    }
}
