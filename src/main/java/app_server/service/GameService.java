package app_server.service;

import app_server.AppServer;
import model.Card;
import model.Game;
import model.Lobby;
import model.Move;
import model.Player;
import stub_RMI.client_appserver.GameStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameService extends UnicastRemoteObject implements GameStub {

    private static final Logger LOGGER = Logger.getLogger(GameService.class.getName());


    private Lobby lobby;
    //private GameDbService gameDbService;

    public GameService(Lobby lobby) throws RemoteException {
        this.lobby = lobby;
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
                LOGGER.log(Level.INFO, "Player not null for initCards: {0}", retPlayer);

                if (retPlayer.getHand().isEmpty()) {
                    distributeHandsToPlayers(game);
                }

                return retPlayer.getHand();
            }
        }
        LOGGER.log(Level.INFO, "Returning null for hand");

        return null;
    }

    /**
     * Method that distributes a set of cards (a hand) to all players that don't have a hand.
     *
     * @param game
     */
    private synchronized void distributeHandsToPlayers(Game game) {
        LOGGER.log(Level.INFO, "distributeHandsToPlayers");

        for (Player player : game.getPlayerList()) {
            if (player.getHand().isEmpty()) {
                LOGGER.log(Level.INFO, "Distributing cards for player = {0}", player);
                game.givePlayerInitHand(7, player);
                LOGGER.log(Level.INFO, "Player hand distributed = {0}", player.getHand());
            }
            LOGGER.log(Level.INFO, "Player {0} already had a hand...", player);
        }
    }

    /**
     * RMI call to get the current player of the Game and the last played card. If no initial or starting player is assigned,
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

            LOGGER.info("Game not found in getCurrentPlayerAndLastCard");

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
        try {
            Game game = lobby.findGame(gameName);

            if (!init) {
                wait();
            }

            return game.getLightPlayerList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates the game on the application server and notifies all client RMI calls to fetch info for the game.
     *
     * @param gameName
     * @param card
     * @return
     * @throws RemoteException
     */
    @Override
    public synchronized Card playMove(String gameName, Card card) throws RemoteException {
        Game game = lobby.findGame(gameName);

        //TODO update game

        //TODO notify all

        return null;
    }
}
