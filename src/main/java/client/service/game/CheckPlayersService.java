package client.service.game;

import client.Main;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.Game;
import stub_RMI.client_appserver.GameLobbyStub;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service used by the client at an initial game. It only returns true if all clients have successfully setup the game.
 * The game is setup when all init cards, the middle card and current player are fetched,
 * and when a player has it's own hand.
 */
public class CheckPlayersService extends Service<Boolean> {

    private static final Logger LOGGER = Logger.getLogger(CheckPlayersService.class.getName());

    private Game game;

    public CheckPlayersService(Game game) {
        this.game = game;
    }

    /**
     * Service that requests all the init RMI calls and waits until all players in the game have initialized.
     *
     * @return
     */
    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                LOGGER.log(Level.INFO, "Starting Task: CheckPlayersService");

                Registry myRegistry = LocateRegistry.getRegistry(Main.appServer.getIp(), Main.appServer.getPort());
                GameLobbyStub gameService = (GameLobbyStub) myRegistry.lookup("GameLobbyService");

                boolean successful = gameService.hasEverybodyJoined(game.getGameId());

                LOGGER.log(Level.INFO, "CheckPlayersService result = {0}", successful);

                return successful;
            }
        };
    }
}
