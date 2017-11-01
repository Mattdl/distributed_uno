package stub_RMI.client_appserver;

import model.Game;
import model.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface LobbyStub extends Remote {

    List<Game> getJoinableGames(List<Game> clientGames) throws RemoteException;

    void createNewGame(Player initPlayer, String gameName, int gameSize) throws RemoteException;


}
