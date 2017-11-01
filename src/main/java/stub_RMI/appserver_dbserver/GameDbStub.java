package stub_RMI.appserver_dbserver;

import model.Game;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GameDbStub extends Remote {

    List<Game> getJoinableGames() throws RemoteException;
}
