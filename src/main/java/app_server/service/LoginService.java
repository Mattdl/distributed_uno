package app_server.service;

import app_server.AppServer;
import db_server.UserDbService;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import security.JWTUtils;
import security.Passwords;
import stub_RMI.appserver_dbserver.UserDbStub;
import stub_RMI.client_appserver.LoginStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Base64;

public class LoginService extends UnicastRemoteObject implements LoginStub {

    private final Logger LOGGER = LoggerFactory.getLogger(GameService.class.getName());

    private final long TTL = 24 * 60 * 60 * 1000; //24h Time to live

    private UserDbStub userDbService;

    public LoginService(UserDbStub userDbService) throws RemoteException {
        this.userDbService = userDbService;
    }

    @Override
    public boolean ping() throws RemoteException {
        return true;
    }


    @Override
    public String getLoginToken(String username, String inputPassword) throws RemoteException {
        LOGGER.info("GETTING LOGIN TOKEN FOR username = {}, password = {}", username, inputPassword);

        // fetch login with database
        User dbUser = userDbService.fetchUser(username);

        LOGGER.info("USER IN DATABASE FOUND user = {}", dbUser);

        if (dbUser == null) {
            return null;
        }

        // Valid login?
        if (!checkUserLogin(dbUser, username, inputPassword)) {
            LOGGER.info("INVALID LOGIN, dbUser = {}, username = {}, password = {}", dbUser, username, inputPassword);
            return null;
        }

        //Login valid!
        LOGGER.info("VALID LOGIN");

        // Generate JWT Token
        String token = JWTUtils.createJWT(username, null, username, TTL, AppServer.apiSecret);

        LOGGER.info("VALID LOGIN, RETURNING TOKEN = {}", token);

        return token;
    }

    private boolean checkUserLogin(User dbUser, String username, String inputPassword) {
        byte[] actualSalt = Base64.getDecoder().decode(dbUser.getSalt());
        byte[] actualHash = Base64.getDecoder().decode(dbUser.getHash());

        byte[] inputHash = Passwords.hash(inputPassword.toCharArray(), actualSalt);

        LOGGER.info("CHECKING USER LOGIN, inputHash = {}, actualHash = {}", inputHash, actualHash);


        return Arrays.equals(inputHash, actualHash);
    }

    @Override
    public boolean loginWithToken(String token) throws RemoteException {
        LOGGER.info("LOGING IN WITH TOKEN");

        return JWTUtils.validateJWT(token, AppServer.apiSecret);
    }
}
