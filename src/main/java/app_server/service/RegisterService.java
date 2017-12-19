package app_server.service;

import app_server.AppServer;
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

    private AppServer appServer;

    public RegisterService(UserDbStub userDbService, AppServer appServer) throws RemoteException {
        this.userDbService = userDbService;
        this.appServer = appServer;
    }

    public boolean register(String username, String password) throws RemoteException {
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

        } catch (Exception e) {
            LOGGER.error("APPSERVER COULD NOT CONNECT TO DATABASE FOR ACTION");
            AppServer.retrieveNewDatabaseInfo(appServer);
            AppServer.registerAsClientWithDatabase(appServer);
        }

        return successful;
    }

}
