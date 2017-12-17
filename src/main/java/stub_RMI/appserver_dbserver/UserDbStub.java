package stub_RMI.appserver_dbserver;

import model.Player;
import model.User;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UserDbStub extends Remote {

    boolean persistUser(User user, boolean propagate) throws RemoteException;

    User fetchUser(String username) throws RemoteException;

}
