package app_server.service;

import app_server.AppServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import security.JWTUtils;
import stub_RMI.client_appserver.LoginStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class LoginService extends UnicastRemoteObject implements LoginStub {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameService.class.getName());

    private final long TTL = 24 * 60 * 60 * 1000; //24h Time to live

    public LoginService() throws RemoteException {
    }

    @Override
    public boolean ping() throws RemoteException {
        return true;
    }


    @Override
    public String getLoginToken(String username, String password) throws RemoteException {

        //TODO check login with database


        //Get password in plain text from user, hash with salt from database, check with hash from database
        //return null if failed login

        //TODO generate token
        String token = JWTUtils.createJWT(username, null, username, TTL, AppServer.apiSecret);

        return token;
    }

    @Override
    public boolean loginWithToken(String token) throws RemoteException {
        return JWTUtils.validateJWT(token, AppServer.apiSecret);
    }
}
