package stub_RMI.client_appserver;

import model.Card;
import model.Move;
import model.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GameStub extends Remote {

    List<Card> initCards(String gameName, Player player) throws RemoteException;

    List<Player> getPlayerUpdates(String gameName, Player client, boolean init) throws RemoteException;

    Card playMove(String gameName, Move move) throws RemoteException;

    Move getCurrentPlayerAndLastCard(String gameName, boolean init) throws RemoteException;

    List<Card> getPlusCards(String gameName, Player player) throws RemoteException;
}
