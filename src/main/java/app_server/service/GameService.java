package app_server.service;

import model.Card;
import model.Game;
import model.Lobby;
import model.Move;
import model.Player;
import stub_RMI.client_appserver.GameStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class GameService extends UnicastRemoteObject implements GameStub {

    private Lobby lobby;
    //private GameDbService gameDbService;

    public GameService(Lobby lobby) throws RemoteException {
        this.lobby = lobby;
    }

    /**
     * RMI service to return the initial 7 cards of a player. Is read only and needs not to be synchronized, as a the
     * Game object will only be adapted after initialization.
     *
     * @param gameName
     * @param player
     * @return
     * @throws RemoteException
     */
    @Override
    public List<Card> initCards(String gameName, Player player) throws RemoteException {
        Game game = lobby.findGame(gameName);

        return game.findPlayer(player).getHand();
    }

    /**
     * -    * RMI call to get the current player of the Game and the last played card
     *
     * @param gameName
     * @return
     */
    @Override
    public synchronized Move getCurrentPlayerAndLastCard(String gameName, boolean init) {
        try {
            Game game = lobby.findGame(gameName);

            if (!init) {
                wait();
            }

            return new Move(game.getCurrentPlayer(), game.getLastPlayedCard());
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
    public synchronized List<Player> getPlayerUpdates(String gameName, Player client) throws RemoteException {
        try {
            Game game = lobby.findGame(gameName);

            wait();

            return game.getLightPlayerList();
        }catch (Exception e){
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
