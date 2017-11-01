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


    @Override
    public String getLoginToken(String username, String password) throws RemoteException {
        //TODO decrypt username & password

        //TODO check login with database
        //return null if failed login

        //TODO generate token
        return "DMLKJMLSDF123";
    }
}
