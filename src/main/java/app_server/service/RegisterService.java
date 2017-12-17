package app_server.service;

import model.User;
import security.Passwords;
import stub_RMI.appserver_dbserver.UserDbStub;
import stub_RMI.client_appserver.RegisterStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RegisterService extends UnicastRemoteObject implements RegisterStub {
    private UserDbStub userDbService;


    public RegisterService(UserDbStub userDbService) throws RemoteException {
        this.userDbService = userDbService;
    }

    public boolean register(String username, String password) {

        byte[] salt = Passwords.getNextSalt();

        //Hash password
        byte[] hash = Passwords.hash(password.toCharArray(), salt);

        boolean successful = false;

        User user = new User(username, new String(hash), new String(salt));

        try {

            successful = userDbService.persistUser(user, true);

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return successful;
    }

}
