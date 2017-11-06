package client.service.game;

import client.Main;
import client.controller.GameController;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.Card;
import model.Game;
import model.Move;
import model.Player;
import stub_RMI.client_appserver.GameStub;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InitService extends Service<Boolean> {

    private static final Logger LOGGER = Logger.getLogger(InitService.class.getName());

    private Game game;

    public InitService(Game game) {
        this.game = game;
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {

                Registry myRegistry = LocateRegistry.getRegistry(Main.appServer.getIp(), Main.appServer.getPort());
                GameStub gameService = (GameStub) myRegistry.lookup("GameService");

                //Fetch init cards
                List<Card> cards = gameService.initCards(game.getGameName(), Main.currentPlayer);

                boolean isSuccessful = cards != null;

                if (isSuccessful) {
                    LOGGER.log(Level.INFO,"Retrieved init cards = {0}",cards);
                    game.setCurrentPlayerHand(cards, Main.currentPlayer);
                }else{
                    LOGGER.log(Level.INFO,"Retrieved null for cards");
                }

                //Get current player and top card
                Move ret = gameService.getCurrentPlayerAndLastCard(game.getGameName(), true);

                isSuccessful = ret != null;

                if (isSuccessful) {
                    LOGGER.log(Level.INFO,"Retrieved init player = {0}",ret.getPlayer());
                    LOGGER.log(Level.INFO,"Retrieved init card = {0}",ret.getCard());

                    game.setCurrentPlayer(ret.getPlayer());
                    game.setLastPlayedCard(ret.getCard());
                }

                //Fetch player info
                List<Player> retList = gameService.getPlayerUpdates(game.getGameName(), Main.currentPlayer, true);

                isSuccessful = retList != null;

                if (isSuccessful) {
                    LOGGER.log(Level.INFO,"Retrieved game Player list = {0}",retList);

                    game.setPlayerList(retList);
                }

                LOGGER.log(Level.INFO,"Finished the InitService");

                return true;
            }
        };
    }
}
