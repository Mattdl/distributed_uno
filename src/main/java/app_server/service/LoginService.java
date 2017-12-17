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

public class LoginService extends UnicastRemoteObject implements LoginStub {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameService.class.getName());

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

        // fetch login with database
        User dbUser = userDbService.fetchUser(username);

        if (dbUser == null) {
            return null;
        }

        // Valid login?
        if (!checkUserLogin(dbUser, username, inputPassword)) {
            return null;
        }

        //Login valid!

        // Generate JWT Token
        String token = JWTUtils.createJWT(username, null, username, TTL, AppServer.apiSecret);

        return token;
    }

    private boolean checkUserLogin(User dbUser, String username, String inputPassword) {
        byte[] inputHash = Passwords.hash(inputPassword.toCharArray(), dbUser.getSalt().getBytes());
        byte[] actualHash = dbUser.getHash().getBytes();

        return Arrays.equals(inputHash, actualHash);
    }

    @Override
    public boolean loginWithToken(String token) throws RemoteException {
        return JWTUtils.validateJWT(token, AppServer.apiSecret);
    }
}
