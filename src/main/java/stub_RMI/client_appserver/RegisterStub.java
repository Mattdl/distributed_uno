package stub_RMI.client_appserver;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegisterStub extends Remote {

    boolean register(String username, String password) throws RemoteException;

}
