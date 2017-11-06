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

    public FetchPlayersInfoService(Game game, boolean isInit) {
        this.game = game;
        this.isInit = isInit;
        isPlaying = true;
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
                    List<Player> ret = gameService.getPlayerUpdates(game.getGameName(), Main.currentPlayer, true);

                    isSuccessful = ret != null;

                    if (isSuccessful && isInit) {
                        game.setPlayerList(ret);
                    }

                } while (!isInit && isPlaying);

                return isSuccessful;
            }
        };
    }
}
