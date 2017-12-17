package stub_RMI.appserver_dbserver;

import model.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

public interface UserDbStub extends Remote {

    boolean saveUserinfo() throws RemoteException;

    boolean persistUser(User user, boolean propagate) throws RemoteException;

}
