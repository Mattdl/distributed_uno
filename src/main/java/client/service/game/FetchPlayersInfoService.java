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

public class FetchPlayersInfoService extends Service<Boolean> {
    private Game game;
    private boolean isInit;
    private boolean isPlaying;
    private Boolean isGameFinished;

    public FetchPlayersInfoService(Game game, boolean isInit, Boolean isGameFinished) {
        this.game = game;
        this.isInit = isInit;
        this.isGameFinished = isGameFinished;
        this.isPlaying = true;
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {

                Registry myRegistry = LocateRegistry.getRegistry(Main.appServer.getIp(), Main.appServer.getPort());
                GameStub gameService = (GameStub) myRegistry.lookup("GameService");

                boolean isSuccessful;

                //must only be performed once for the initialization
                do {
                    //RMI call
                    List<Player> ret = gameService.getPlayerUpdates(game.getGameName(), Main.currentPlayer, isInit);

                    isSuccessful = ret != null;

                    if (isSuccessful && isInit) {
                        game.setPlayerList(ret);
                    }

                    for (Player player : game.getPlayerList()) {
                        if (player.getHandSize() == 0) {
                            isGameFinished = true;
                        }
                    }

                } while (!isInit && isPlaying);

                return isSuccessful;
            }
        };
    }
}
