package stub_RMI.client_appserver;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LoginStub extends Remote {

    boolean ping() throws RemoteException;

    String getLoginToken(String username, String password) throws RemoteException;

    boolean loginWithToken(String token) throws RemoteException;
}
