package stub_RMI.appserver_dbserver;

import model.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface UserDbStub extends Remote {

    boolean persistUser(User user, boolean propagate) throws RemoteException;

    User fetchUser(String username) throws RemoteException;

    List<User> copyDatabase() throws RemoteException;

}
