package db_server;

import stub_RMI.Stub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class UserDbService extends UnicastRemoteObject implements Stub {

    public UserDbService() throws RemoteException {
    }
}
