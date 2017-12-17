package app_server.service;

import db_server.GameDbService;
import game_logic.GameLogic;
import model.Card;
import model.Game;
import model.Lobby;
import model.Move;
import model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stub_RMI.appserver_dbserver.GameDbStub;
import stub_RMI.client_appserver.GameStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameService extends UnicastRemoteObject implements GameStub {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameService.class.getName());

    private final GameLogic gameLogic;

    private Lobby lobby;
    //private GameDbService gameDbService;

    //RMI
    private GameDbStub gameDbService;


    public GameService(Lobby lobby, GameDbStub gameDbService) throws RemoteException {
        this.lobby = lobby;
        this.gameLogic = new GameLogic();
        this.gameDbService = gameDbService;
    }

    /**
     * RMI service to return the initial 7 cards of a player. If one of the players does not have a hand yet,
     * hands are distributed to all players that do not have a hand yet.
     *
     * @param gameName
     * @param player   The player to request the hand of
     * @return The hand of the player
     * @throws RemoteException
     */
    @Override
    public synchronized List<Card> initCards(String gameName, Player player) throws RemoteException {
        LOGGER.info("In InitCard method");

        Game game = lobby.findGame(gameName);

        if (game != null) {
            LOGGER.info("Found game for initCards");
            Player retPlayer = game.findPlayer(player);
            if (retPlayer != null) {
                LOGGER.info("Player not null for initCards: {0}", retPlayer);

                if (retPlayer.getHand().isEmpty()) {
                    distributeHandsToPlayers(game);
                }

                return retPlayer.getHand();
            }
        }
        LOGGER.info("Returning null for hand");

        return null;
    }

    /**
     * Method that distributes a set of cards (a hand) to all players that don't have a hand.
     *
     * @param game
     */
    private synchronized void distributeHandsToPlayers(Game game) {
        LOGGER.info("distributeHandsToPlayers");

        for (Player player : game.getPlayerList()) {
            if (player.getHand().isEmpty()) {
                LOGGER.info("Distributing cards for player = {0}", player);
                game.givePlayerInitHand(7, player);
                LOGGER.info("Player hand distributed = {0}", player.getHand());
            }
            LOGGER.info("Player {0} already had a hand...", player);
        }
    }

    /**
     * RMI call to get the current player of the game and the last played card. If no initial or starting player is assigned,
     * assign a random initial player to the game. The method also provides the first card for a new game.
     *
     * @param gameName
     * @return
     */
    @Override
    public synchronized Move getCurrentPlayerAndLastCard(String gameName, boolean init) throws RemoteException {
        LOGGER.info("In getCurrentPlayerAndLastCard method");

        try {
            Game game = lobby.findGame(gameName);

            if (!init) {
                LOGGER.info("WAITING in getCurrentPlayerAndLastCard until move is played.");
                wait();
                LOGGER.info("CONTINUE in getCurrentPlayerAndLastCard, move is played.");
            }

            if (game != null) {

                //Set random starting player
                if (game.getCurrentPlayer() == null) {
                    game.setCurrentPlayer(game.getPlayerList().get(new Random().nextInt(game.getPlayerList().size())));
                }

                //Set first card
                if (!game.hasPlayedCards()) {
                    game.drawFirstCard();
                }

                return new Move(game.getCurrentPlayer(), game.getLastPlayedCard());
            }

            LOGGER.info("game not found in getCurrentPlayerAndLastCard");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * RMI call to give back the list of players and their updated values.
     *
     * @param gameName
     * @param client
     * @return
     * @throws RemoteException
     */
    @Override
    public synchronized List<Player> getPlayerUpdates(String gameName, Player client, boolean init) throws RemoteException {

        LOGGER.info("Entering getPlayerUpdates");

        try {
            Game game = lobby.findGame(gameName);

            if (!init) {
                wait();
            }

            if (game != null) {
                LOGGER.info("Found game in getPlayerUpdates");

                return game.getLightPlayerList();
            }

            LOGGER.error("Could not find game '{0}'in getPlayerUpdates", gameName);

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error in getPlayerUpdates");
        }
        return null;
    }

    /**
     * Updates the game on the application server and notifies all client RMI calls to fetch info for the game.
     * When the card parameter is null, this means that a card is drawn. In this case, a Card object will be returned
     * to the player.
     *
     * @param gameName
     * @param move     the played card and the player that played it, card is null when a card is drawn
     * @return A drawn card, when the passed card parameter is null
     * @throws RemoteException
     */
    @Override
    public synchronized Card playMove(String gameName, Move move) throws RemoteException {
        Game game = lobby.findGame(gameName);

        if (game != null) {
            LOGGER.info("game found in playMove");

            //If the move is a played Card
            if (move.getCard() != null) {
                LOGGER.info("Card is not null in playMove");

                if (gameLogic.isValidMove(move.getCard(), game.getLastPlayedCard())) {
                    LOGGER.info("Card is not null in playMove");
                    return updateGame(game, move);
                }

                LOGGER.error("In playMove: NO VALID CARD PLAYED, topcard = {0}, played card = {1}",
                        new Object[]{game.getLastPlayedCard(), move.getCard()});
            } else {
                //If the move is a drawn card
                LOGGER.info("Card is null in playMove");

                return updateGame(game, move);
            }
        }

        LOGGER.info("game not found in playMove");

        return null;
    }

    /**
     * Return plus cards if the player has received plus-cards.
     *
     * @param gameName
     * @param player
     * @return
     * @throws RemoteException
     */
    @Override
    public synchronized List<Card> getPlusCards(String gameName, Player player) throws RemoteException {
        Game game = lobby.findGame(gameName);
        Player serverSidePlayer = game.findPlayer(player);
        List<Card> ret = new ArrayList<>();

        try {

            while (!((game.getLastPlayedCard().getCardType() == Card.CardType.PLUS2
                    || game.getLastPlayedCard().getCardType() == Card.CardType.PLUS4)
                    && game.isPlayerAfterLastPlayer(serverSidePlayer))
                    || game.getLastPlayedCard().isHasFetchedCards()) {
                LOGGER.info("Nah the notify did not have plus cards for me, just waitin...");
                wait();
            }

            LOGGER.info("LEAVING WHILE, last card = {0}", game.getLastPlayedCard());

            game.getLastPlayedCard().setHasFetchedCards(true);

            if (game.getLastPlayedCard().getCardType() == Card.CardType.PLUS2) {
                LOGGER.info("Fetching PLUS2 playercards.");
                List<Card> hand = serverSidePlayer.getHand();

                LOGGER.info("Fetching PLUS2 playercards. Hand = {0}, HandSize = {1}", new Object[]{hand, hand.size()});

                for (int i = hand.size() - 2; i < hand.size(); i++)
                    ret.add(hand.get(i));

                LOGGER.info("Fetching PLUS2 playercards. ret = {0}, retSize = {1}", new Object[]{ret, ret.size()});

                return ret;
            }

            if (game.getLastPlayedCard().getCardType() == Card.CardType.PLUS4) {
                LOGGER.info("Fetching PLUS4 playercards.");
                List<Card> hand = serverSidePlayer.getHand();

                LOGGER.info("Fetching PLUS4 playercards. Hand = {0}, HandSize = {1}", new Object[]{hand, hand.size()});

                for (int i = hand.size() - 4; i < hand.size(); i++)
                    ret.add(hand.get(i));

                LOGGER.info("Fetching PLUS4 playercards. ret = {0}, retSize = {1}", new Object[]{ret, ret.size()});

                return ret;
            }

            LOGGER.error("NO PLUS CARDS RETURNED! For player = {0}", serverSidePlayer);

        } catch (Exception e) {
            e.printStackTrace();
        }

        LOGGER.error("Returning null for pluscards to player ={0}", serverSidePlayer);

        return null;
    }

    private synchronized Card updateGame(Game game, Move move) {
        LOGGER.info("Entering updateGame");

        try {
            //gameDbService.fetchGame(game.getGameId());
            //LOGGER.info("FETCH GAME BEFORE MOVE, game = {}", game);

            LOGGER.info("APPSERVER trying to persist MOVE for gameId = {},move={}", game.getGameId(), move);


            gameDbService.persistMove(game.getGameId(), move, true);
            LOGGER.info("MOVE persisted to database for game = {}", game);

            //gameDbService.fetchGame(game.getGameId());
            //LOGGER.info("FETCH GAME AFTER MOVE, game = {}", game);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Player serverPlayer = game.findPlayer(move.getPlayer());

        if (serverPlayer != null) {
            move.setPlayer(serverPlayer);

            //Update game
            Card ret = gameLogic.gameUpdate(game, move);
            LOGGER.info("game updated");

            //Notify everybody that game has updated
            notifyAll();
            LOGGER.info("updateGame: notified everybody!");

            return ret;
        }
        LOGGER.info("Player not found!");

        return null;
    }
}
