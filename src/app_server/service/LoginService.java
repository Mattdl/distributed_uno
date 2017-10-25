package app_server.service;

import stub_RMI.client_appserver.LoginStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class LoginService extends UnicastRemoteObject implements LoginStub {

    public LoginService() throws RemoteException {
    }

    @Override
    public boolean ping() throws RemoteException {
        return true;
    }
}
