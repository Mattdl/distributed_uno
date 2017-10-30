package client.service;

import client.Main;
import dispatcher.DispatcherService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import stub_RMI.client_appserver.LoginStub;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginService extends Service<Boolean> {

    private static final Logger LOGGER = Logger.getLogger(LoginService.class.getName());

    private String username;
    private String password;

    public LoginService(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Background Task to obtain a login token
     * @return
     */
    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {

                boolean isSuccessful;

                try {
                    Registry myRegistry = LocateRegistry.getRegistry(Main.appServer.getIp(), Main.appServer.getPort());
                    //LOGGER.log(Level.INFO, "Registry retrieved: {0}", myRegistry);

                    LoginStub loginService = (LoginStub) myRegistry.lookup("LoginService");
                    //LOGGER.log(Level.INFO, "loginService retrieved: {0}", loginService);

                    String token = loginService.getLoginToken(username,password);
                    isSuccessful = token != null;

                    if (isSuccessful) {
                        Main.token = token;
                    }

                    LOGGER.log(Level.INFO, "Loginserver retrieve token succesful=", isSuccessful);

                    return isSuccessful;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        };
    }
}
