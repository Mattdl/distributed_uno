package app_server;

import stub_RMI.Stub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class StubImpl extends UnicastRemoteObject implements Stub {

    public StubImpl() throws RemoteException {
    }
}
