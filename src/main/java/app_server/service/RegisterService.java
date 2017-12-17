package app_server.service;

import db_server.UserDbService;
import model.Passwords;
import model.User;
import stub_RMI.appserver_dbserver.UserDbStub;
import stub_RMI.client_appserver.RegisterStub;
import sun.security.util.Password;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RegisterService extends UnicastRemoteObject implements RegisterStub {
    private  UserDbStub userDbService;
    private byte[] salt;

    public RegisterService(UserDbStub userDbService) throws RemoteException {
        this.userDbService = userDbService;
        //Create new salt for current user
        salt = Passwords.getNextSalt();
    }

    public boolean register(String username, String password){
        //Hash password
        byte[] hash = Passwords.hash(password.toCharArray(), salt);

        boolean successful = false;

        User user = new User(username, new String(hash), salt.toString());

        try {
            successful = userDbService.persistUser(user, true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return successful;
    }

}
