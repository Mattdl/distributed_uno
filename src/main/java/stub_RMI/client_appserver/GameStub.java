package stub_RMI.client_appserver;

import com.sun.org.apache.regexp.internal.RE;
import model.Card;
import model.Game;
import model.Move;
import model.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GameStub extends Remote {

    List<Card> initCards(String gameName, Player player) throws RemoteException;

    Player getCurrentPlayer(String gameName) throws RemoteException;

    Card getLastPlayedCard(String gameName) throws RemoteException;

    List<Player> getUpdatedPlayers(String gameName, Player client) throws RemoteException;

    Card playMove(String gameName, Card card) throws RemoteException;
}
