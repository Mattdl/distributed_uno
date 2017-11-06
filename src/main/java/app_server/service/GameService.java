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
     * RMI service to return the initial 7 cards of a player.
     *
     * @param gameName
     * @param player
     * @return
     * @throws RemoteException
     */
    @Override
    public synchronized List<Card> initCards(String gameName, Player player) throws RemoteException {
        LOGGER.info("In InitCard method");

        Game game = lobby.findGame(gameName);

        if (game != null) {
            LOGGER.info("Found game");
            Player retPlayer = game.findPlayer(player);
            if (retPlayer != null) {
                LOGGER.log(Level.INFO, "Player not null for initCards: {0}", retPlayer);

                LinkedList<Card> ret = game.givePlayerInitHand(7);

                LOGGER.log(Level.INFO, "Setting list of cards to hand: ", ret);

                retPlayer.setHand(ret);

                LOGGER.log(Level.INFO, "Player received init hand, returning : ", retPlayer.getHand());

                return ret;
            }
        }
        LOGGER.log(Level.INFO, "Returning null for hand");

        return null;
    }

    /**
     * RMI call to get the current player of the Game and the last played card
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
                wait();
            }

            if (game != null) {
                LOGGER.info("Found game");

                //Set random starting player
                if(game.getCurrentPlayer() == null){
                    game.setCurrentPlayer(game.getPlayerList().get(new Random().nextInt(game.getPlayerList().size())));
                }

                //Set first card
                if (!game.hasPlayedCards()) {
                    game.drawFirstCard();
                }

                return new Move(game.getCurrentPlayer(), game.getLastPlayedCard());
            }
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
