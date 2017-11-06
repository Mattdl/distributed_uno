package client.service.game;

import client.Main;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.Card;
import model.Game;
import model.Player;
import stub_RMI.client_appserver.GameStub;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class FetchInitCardsService extends Service<Boolean> {
    private Game game;

    public FetchInitCardsService(Game game) {
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
                List<Card> cards = gameService.initCards(game.getGameName(), Main.currentPlayer);
                Main.currentPlayer.setHand(cards);

                boolean isSuccessful = cards != null;

                return isSuccessful;
            }
        };
    }
}
