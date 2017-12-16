package db_server;

import model.DbServer;
import stub_RMI.appserver_dbserver.UserDbStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserDbService extends UnicastRemoteObject implements UserDbStub {

    private List<DbServer> otherDatabases;
    private ReadWriteLock otherDatabasesLock = new ReentrantReadWriteLock();

    public UserDbService() throws RemoteException {
    }

    public UserDbService(List<DbServer> otherDatabases) throws RemoteException {
        this.otherDatabases = otherDatabases;
    }

    @Override
    public boolean saveUserinfo() throws RemoteException {
        return false;
    }

    public synchronized void updateOtherDatabases(List<DbServer> otherDatabases) {
        //TODO
    }
}
