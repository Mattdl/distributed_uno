package client.service.game;

import client.Main;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.Game;
import model.Move;
import stub_RMI.client_appserver.GameStub;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Service for the client to fetch the current player (the player who's turn it is) and the last played card from the
 * server.
 */
public class FetchCurrentPlayerAndCardService extends Service<Boolean> {
    private Game game;

    public FetchCurrentPlayerAndCardService(Game game) {
        this.game = game;
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {

                Registry myRegistry = LocateRegistry.getRegistry(Main.appServer.getIp(), Main.appServer.getPort());
                GameStub gameService = (GameStub) myRegistry.lookup("GameService");

                //RMI call
                Move ret = gameService.getCurrentPlayerAndLastCard(game.getGameName());

                boolean isSuccessful = ret != null;

                if (isSuccessful) {
                    game.setCurrentPlayer(ret.getPlayer());
                    game.setLastPlayedCard(ret.getCard());
                }

                return isSuccessful;
            }
        };
    }
}
