package app_server.service;

import app_server.AppServer;
import dispatcher.Dispatcher;
import game_logic.GameLogic;
import model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import security.JWTUtils;
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

    private AppServer appServer;


    public GameService(Lobby lobby, AppServer appServer) throws RemoteException {
        this.lobby = lobby;
        this.gameLogic = new GameLogic();
        this.appServer = appServer;
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
    public synchronized List<Card> initCards(String gameName, Player player, String token) throws RemoteException {
        if(!JWTUtils.validateJWT(token, AppServer.apiSecret))
            return null;

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
    public synchronized Move getCurrentPlayerAndLastCard(String gameName, boolean init, String token) throws RemoteException {
        if(!JWTUtils.validateJWT(token, AppServer.apiSecret))
            return null;
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
    public synchronized List<Player> getPlayerUpdates(String gameName, Player client, boolean init, String token) throws RemoteException {
        if(!JWTUtils.validateJWT(token, AppServer.apiSecret))
            return null;

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
    public synchronized Card playMove(String gameName, Move move, String token) throws RemoteException {
        if(!JWTUtils.validateJWT(token, AppServer.apiSecret))
            return null;

        Game game = lobby.findGame(gameName);

        if (game != null) {
            LOGGER.info("game found in playMove");

            //If the move is a played Card
            if (move.getCard() != null) {
                LOGGER.info("Card is not null in playMove");

                if (gameLogic.isValidMove(move.getCard(), game.getLastPlayedCard())) {
                    LOGGER.info("Valid move in playMove");
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
    public synchronized List<Card> getPlusCards(String gameName, Player player, String token) throws RemoteException {
        if(!JWTUtils.validateJWT(token, AppServer.apiSecret))
            return null;

        Game game = lobby.findGame(gameName);
        Player serverSidePlayer = game.findPlayer(player);
        List<Card> ret = new ArrayList<>();

        try {

            while (!mayFetchPlusCards(game, serverSidePlayer)) {
                LOGGER.info("Nah the notify did not have plus cards for me, just waitin...");
                wait();
            }

            Move lastMove = game.getLastMove();

            lastMove.setHasFetchedCards(true);

            LOGGER.info("LEAVING WHILE, last move = {}", lastMove);

            if (lastMove.getCard().getCardType() == Card.CardType.PLUS2) {
                LOGGER.info("Fetching PLUS2 playercards.");
                List<Card> hand = serverSidePlayer.getHand();

                LOGGER.info("Fetching PLUS2 playercards. Hand = {0}, HandSize = {1}", new Object[]{hand, hand.size()});

                for (int i = hand.size() - 2; i < hand.size(); i++)
                    ret.add(hand.get(i));

                LOGGER.info("Fetching PLUS2 playercards. ret = {0}, retSize = {1}", new Object[]{ret, ret.size()});

                return ret;
            }

            if (lastMove.getCard().getCardType() == Card.CardType.PLUS4) {
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

    private boolean mayFetchPlusCards(Game game, Player serverSidePlayer) {

        Move lastMove = game.getLastMove();

        boolean isLastCardPlusCard = lastMove.getMoveType() == Move.MoveType.PLUS_CARD;
        boolean isForCurrentPlayer = serverSidePlayer.equals(lastMove.getPlayer());
        boolean hasAlreadyFetchedCards = lastMove.hasFetchedCards();

        LOGGER.debug("PLUS CARDS FOR PLAYER '{}'? isLastCardPlusCard = {}, isForCurrentPlayer = {}, hasAlreadyFetchedCards = {}",
                serverSidePlayer, isLastCardPlusCard, isForCurrentPlayer, hasAlreadyFetchedCards);

        return isLastCardPlusCard && isForCurrentPlayer && !hasAlreadyFetchedCards;
    }

    /**
     * Method used to update the game and to persist the played Move. Notifies everybody to tell that game has changed.
     * @param game
     * @param move
     * @return
     * @throws RemoteException
     */
    private synchronized Card updateGame(Game game, Move move) throws RemoteException {
        LOGGER.info("Entering updateGame");

        try {
            //gameDbService.fetchGame(game.getGameId());
            //LOGGER.info("FETCH GAME BEFORE MOVE, game = {}", game);

            LOGGER.info("APPSERVER trying to persist MOVE for gameId = {},move={}", game.getGameId(), move);

            boolean persistedToDb = false;

            while (!persistedToDb) {
                try {
                    appServer.getGameDbService().persistMove(game.getGameId(), move, true);
                    LOGGER.info("MOVE persisted to database for game = {}", game);
                    persistedToDb = true;
                    LOGGER.info("APPSERVER SUCCESSFUL PERSIST");

                } catch (Exception e) {
                    LOGGER.error("APPSERVER COULD NOT CONNECT TO DATABASE FOR ACTION");
                    AppServer.retrieveNewDatabaseInfo(appServer);
                    AppServer.registerAsClientWithDatabase(appServer);
                    LOGGER.info("APPSERVER RETRYING PERSIST");
                }
            }

            //gameDbService.fetchGame(game.getGameId());
            //LOGGER.info("FETCH GAME AFTER MOVE, game = {}", game);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Player serverPlayer = game.findPlayer(move.getPlayer());

        if (serverPlayer != null)

        {
            move.setPlayer(serverPlayer);

            //Update game
            Card ret = gameLogic.gameUpdate(game, move);
            LOGGER.info("game updated");

            //Check if game is finished
            if (serverPlayer.getHand().isEmpty()) {
                finishGame(game, serverPlayer);
            }

            //Notify everybody that game has updated
            notifyAll();
            LOGGER.info("updateGame: notified everybody!");

            return ret;
        }
        LOGGER.info("Player not found!");

        return null;
    }

    /**
     * Adds score to database.
     *
     * @param game
     * @return
     * @throws RemoteException
     */
    protected synchronized void finishGame(Game game, Player winner) throws RemoteException {
        LOGGER.warn("Game FINISHED");

        int score = gameLogic.calculateScore(game);

        winner.addScore(score);

        boolean persistedToDb = false;

        //Update userscore in database
        while (!persistedToDb) {
            try {
                appServer.getGameDbService().updateWinner(winner);
                persistedToDb = true;
                LOGGER.info("APPSERVER SUCCESSFUL PERSIST");

            } catch (Exception e) {
                LOGGER.error("APPSERVER COULD NOT CONNECT TO DATABASE FOR ACTION");
                AppServer.retrieveNewDatabaseInfo(appServer);
                AppServer.registerAsClientWithDatabase(appServer);
                LOGGER.info("APPSERVER RETRYING PERSIST");
            }
        }
    }

    /**
     * Returns winnerName + score in list.
     *
     * @param gameName
     * @return
     * @throws RemoteException
     */
    public List<String> getGameResults(String gameName, String token) throws RemoteException {
        if(!JWTUtils.validateJWT(token, AppServer.apiSecret))
            return null;

        Game game = lobby.findGame(gameName);

        int score = gameLogic.calculateScore(game);

        //Winner is player who played last move
        Player winner = game.getLastPlayedMoveNotDrawnCard().getPlayer();

        List<String> results = new ArrayList<>();
        results.add(winner.getName());
        results.add(Integer.toString(score));

        return results;
    }


    @Override
    public List<Card> fetchCardImageMappings(String token) throws RemoteException {
        if(!JWTUtils.validateJWT(token, AppServer.apiSecret))
            return null;

        LOGGER.info("APPSERVER REQUESTING IMAGES");
        List<Card> ret = null;
        boolean persistedToDb = false;

        while (!persistedToDb) {

            try {
                ret = appServer.getGameDbService().fetchCardImageMappings(Dispatcher.isHolliday);
                persistedToDb = true;
                LOGGER.info("APPSERVER SUCCESSFUL PERSIST");

            } catch (Exception e) {
                LOGGER.error("APPSERVER COULD NOT CONNECT TO DATABASE FOR ACTION");
                AppServer.retrieveNewDatabaseInfo(appServer);
                AppServer.registerAsClientWithDatabase(appServer);
                LOGGER.info("APPSERVER RETRYING PERSIST");
            }
        }

        LOGGER.info("APPSERVER RETURNING IMAGES = {}", ret);
        return ret;
    }
}
