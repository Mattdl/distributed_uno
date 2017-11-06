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
     * RMI call to get the starting player of the Game.
     * @param gameName
     * @return
     * @throws RemoteException
     */
    @Override
    public synchronized Player getCurrentPlayer(String gameName) throws RemoteException {
        Game game = lobby.findGame(gameName);

        return game.getCurrentPlayer();
    }

    /**
     *
     * @param gameName
     * @return
     * @throws RemoteException
     */
    @Override
    public synchronized Card getLastPlayedCard(String gameName) throws RemoteException {
        return null;
    }

    @Override
    public synchronized List<Player> getUpdatedPlayers(String gameName, Player client) throws RemoteException {
        return null;
    }

    @Override
    public synchronized Card playMove(String gameName, Card card) throws RemoteException {
        Game game = lobby.findGame(gameName);
        return null;
    }
}
