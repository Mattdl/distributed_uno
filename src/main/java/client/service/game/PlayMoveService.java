package client.service.game;

import client.Main;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.Card;
import model.Game;
import model.Move;
import stub_RMI.client_appserver.GameStub;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayMoveService extends Service<Boolean> {

    private static final Logger LOGGER = Logger.getLogger(PlayMoveService.class.getName());

    private Game game;
    private Move playedMove;

    public PlayMoveService(Game game, Move playedMove) {
        this.game = game;
        this.playedMove = playedMove;

        if (playedMove.getCard() == null) {
            playedMove.setMoveType(Move.MoveType.DRAW_CARD);
        }
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                LOGGER.info("Entering PlayMoveService");
                Registry myRegistry = LocateRegistry.getRegistry(Main.appServer.getIp(), Main.appServer.getPort());
                GameStub gameService = (GameStub) myRegistry.lookup("GameService");

                Card drawnCard = gameService.playMove(game.getGameId(), playedMove, Main.token);

                LOGGER.log(Level.INFO, "Drawn card from PlayMoveService = {0}", drawnCard);

                if (drawnCard == null) {
                    LOGGER.info("Returned drawn Card is null");
                } else {
                    LOGGER.log(Level.INFO, "Returned drawn Card = {0}", drawnCard);

                    game.setDrawnCardForPlayer(drawnCard, Main.currentPlayer);
                }

                return true;
            }
        };
    }
}
