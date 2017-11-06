package stub_RMI.client_appserver;

import model.Card;
import model.Game;
import model.Move;
import model.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GameStub extends Remote {

    List<Card> initCards(String gameName, Player player) throws RemoteException;

    Player getStartingPlayer(String gameName) throws RemoteException;

    Move getLastPlayedMove(String gameName) throws RemoteException;

    Card playMove(String gameName, Card card) throws RemoteException;
}
