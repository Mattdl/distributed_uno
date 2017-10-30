package stub_RMI.client_appserver;

import model.Game;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface LobbyStub extends Remote {

    List<Game> getJoinableGames() throws RemoteException;
}
