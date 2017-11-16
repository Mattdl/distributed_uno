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

public class FetchPlusCardsService extends Service<Void> {

    private boolean isGameFinished;
    private Game currentGame;

    public FetchPlusCardsService(Game currentGame, boolean isGameFinished) {
        this.isGameFinished = isGameFinished;
        this.currentGame = currentGame;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                Registry myRegistry = LocateRegistry.getRegistry(Main.appServer.getIp(), Main.appServer.getPort());
                GameStub gameService = (GameStub) myRegistry.lookup("GameService");

                boolean isSuccessful;

                while (!isGameFinished) {
                    List<Card> cards = gameService.getPlusCards(currentGame.getGameName(), Main.currentPlayer);

                    isSuccessful = cards != null;

                    if (isSuccessful) {
                        currentGame.addPlayerPlusCards(Main.currentPlayer, cards);
                    }
                }

                return null;
            }
        };
    }

    public void setGameFinished(boolean gameFinished) {
        isGameFinished = gameFinished;
    }
}
