package stub_RMI.client_appserver;

import javafx.util.Pair;
import model.Game;
import model.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface LobbyStub extends Remote {

    Pair<List<Game>,Integer> getJoinableGames(int version) throws RemoteException;

    void createNewGame(Player initPlayer, String gameName, int gameSize) throws RemoteException;


}
