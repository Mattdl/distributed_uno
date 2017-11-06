package client.service.game;

import client.Main;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.Game;
import model.Player;
import stub_RMI.client_appserver.GameLobbyStub;
import stub_RMI.client_appserver.GameStub;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class FetchCurrentPlayerService extends Service<Boolean> {
    private Game game;

    public FetchCurrentPlayerService(Game game) {
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
                Player currentPlayer = gameService.getCurrentPlayer(game.getGameName());

                game.setCurrentPlayer(currentPlayer);

                boolean isSuccessful = currentPlayer != null;

                return isSuccessful;
            }
        };
    }
}
