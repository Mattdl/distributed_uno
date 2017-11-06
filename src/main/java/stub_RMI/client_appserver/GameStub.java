package stub_RMI.client_appserver;

import model.Card;
import model.Game;
import model.Move;
import model.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GameStub extends Remote {

    List<Card> initCards(Player player) throws RemoteException;

    Player getStartingPlayer() throws RemoteException;

    Move getLastPlayedMove() throws RemoteException;

    Card playMove(Card card) throws RemoteException;
}
