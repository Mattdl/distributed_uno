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

    private AppServer appServer;

    public RegisterService(AppServer appServer) throws RemoteException {
        this.appServer = appServer;
    }

    /**
     * This service is used to register new clients. A new salt will be generated for each new user. The hashed password and the salt will be persisted to the database.
     * @param username
     * @param password
     * @return
     * @throws RemoteException
     */
    public boolean register(String username, String password) throws RemoteException {
        LOGGER.info("Registering for username = {}, password = {}", username, password);

        byte[] salt = Passwords.getNextSalt();

        //Hash password
        byte[] hash = Passwords.hash(password.toCharArray(), salt);

        boolean successful = false;

        String hashRet = Base64.getEncoder().encodeToString(hash);
        String saltRet = Base64.getEncoder().encodeToString(salt);

        User user = new User(username, hashRet, saltRet);

        boolean persistedToDb = false;

        while (!persistedToDb) {
            try {

                successful = appServer.getUserDbService().persistUser(user, true);
                persistedToDb = true;
                LOGGER.info("APPSERVER SUCCESSFUL PERSIST");

            } catch (RemoteException e) {
                LOGGER.error("APPSERVER COULD NOT CONNECT TO DATABASE FOR ACTION, Database={}", appServer.getDatabaseToString());
                LOGGER.error("APPSERVER CONNECTION ERROR, msg = {}", e.getMessage());
                //e.printStackTrace();

                AppServer.retrieveNewDatabaseInfo(appServer);
                AppServer.registerAsClientWithDatabase(appServer);
                LOGGER.info("APPSERVER RETRYING PERSIST");
            }
        }

        LOGGER.error("SUCCESSFUL CREATION OF USER!!!");


        return successful;
    }

}
