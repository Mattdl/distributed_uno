package app_server.service;

import model.Passwords;
import stub_RMI.client_appserver.RegisterStub;
import sun.security.util.Password;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RegisterService extends UnicastRemoteObject implements RegisterStub {
    private byte[] salt;

    public RegisterService() throws RemoteException {
        //Create new salt for current user
        salt = Passwords.getNextSalt();
    }

    public boolean Register(String username, String password){
        //Hash password
        byte[] hash = Passwords.hash(password.toCharArray(), salt);




    }

}
