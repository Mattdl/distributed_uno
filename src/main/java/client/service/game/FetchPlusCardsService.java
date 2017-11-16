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

public class FetchPlusCardsService extends Service<Void> {

    private static final Logger LOGGER = Logger.getLogger(FetchPlusCardsService.class.getName());

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

                LOGGER.log(Level.INFO,"Starting FetchPlusCardsService");

                Registry myRegistry = LocateRegistry.getRegistry(Main.appServer.getIp(), Main.appServer.getPort());
                GameStub gameService = (GameStub) myRegistry.lookup("GameService");

                boolean isSuccessful;

                while (!isGameFinished) {

                    LOGGER.log(Level.INFO,"Requesting plus cards");

                    List<Card> cards = gameService.getPlusCards(currentGame.getGameName(), Main.currentPlayer);

                    isSuccessful = cards != null;

                    if (isSuccessful) {
                        currentGame.addPlayerPlusCards(Main.currentPlayer, cards);
                        LOGGER.log(Level.INFO,"Plus cards received from server = {0}",cards);
                    }
                    else{
                        LOGGER.log(Level.SEVERE,"NULL RECEIVED from plus cards");
                    }
                }

                LOGGER.log(Level.INFO,"Ending FetchPlusCardsService");

                return null;
            }
        };
    }

    public void setGameFinished(boolean gameFinished) {
        isGameFinished = gameFinished;
    }
}
