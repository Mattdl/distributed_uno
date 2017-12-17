package app_server.service;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import security.Passwords;
import stub_RMI.appserver_dbserver.UserDbStub;
import stub_RMI.client_appserver.RegisterStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Base64;

public class RegisterService extends UnicastRemoteObject implements RegisterStub {

    private final Logger LOGGER = LoggerFactory.getLogger(RegisterService.class.getName());

    private UserDbStub userDbService;


    public RegisterService(UserDbStub userDbService) throws RemoteException {
        this.userDbService = userDbService;
    }

    public boolean register(String username, String password) {
        LOGGER.info("Registering for username = {}, password = {}", username, password);

        byte[] salt = Passwords.getNextSalt();

        //Hash password
        byte[] hash = Passwords.hash(password.toCharArray(), salt);

        boolean successful = false;

        String hashRet = Base64.getEncoder().encodeToString(hash);
        String saltRet = Base64.getEncoder().encodeToString(salt);

        User user = new User(username, hashRet, saltRet);

        try {

            successful = userDbService.persistUser(user, true);

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return successful;
    }

}
