package stub_RMI.appserver_dbserver;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameDbStub extends Remote {

    boolean updatePlayerCards(String username) throws RemoteException;
}
