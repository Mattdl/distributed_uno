package client.service.game;

import client.Main;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.Game;
import model.Move;
import model.Player;
import stub_RMI.client_appserver.GameStub;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FetchPlayersInfoService extends Service<Boolean> {

    private static final Logger LOGGER = Logger.getLogger(FetchPlayersInfoService.class.getName());

    private Game game;
    private boolean isInit;
    private Boolean isGameFinished;

    public FetchPlayersInfoService(Game game, boolean isInit, Boolean isGameFinished) {
        this.game = game;
        this.isInit = isInit;
        this.isGameFinished = isGameFinished;
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {

                LOGGER.info("Entering FetchPlayersInfoService");

                Registry myRegistry = LocateRegistry.getRegistry(Main.appServer.getIp(), Main.appServer.getPort());
                GameStub gameService = (GameStub) myRegistry.lookup("GameService");

                LOGGER.info("RMI registered in FetchPlayersInfoService");

                boolean isSuccessful;

                //must only be performed once for the initialization
                do {
                    //RMI call
                    List<Player> ret = gameService.getPlayerUpdates(game.getGameName(), Main.currentPlayer, isInit);
                    isSuccessful = ret != null;

                    if (isSuccessful) {
                        LOGGER.log(Level.INFO, "Successful received other player info: {0}", ret);
                        game.setPlayerList(ret);
                        LOGGER.log(Level.INFO, "Other player info is set!");
                    }

                    for (Player player : game.getPlayerList()) {
                        if (player.getHandSize() == 0) {
                            isGameFinished = true;
                            LOGGER.log(Level.INFO, "The game has FINISHED!");
                        }
                    }

                } while (!isInit && !isGameFinished);

                return isSuccessful;
            }
        };
    }

    public void setGameFinished(Boolean gameFinished) {
        isGameFinished = gameFinished;
    }
}
