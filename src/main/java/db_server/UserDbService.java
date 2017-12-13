package db_server;

import stub_RMI.appserver_dbserver.UserDbStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class UserDbService extends UnicastRemoteObject implements UserDbStub {

    public UserDbService() throws RemoteException {
    }

    @Override
    public boolean saveUserinfo() throws RemoteException {
        return false;
    }
}
