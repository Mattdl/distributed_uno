package stub_RMI.appserver_dbserver;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UserDbStub extends Remote {

    boolean saveUserinfo() throws RemoteException;

}
