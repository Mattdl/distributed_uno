package client.service.login;

import client.Main;
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
     *
     * @return
     */
    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {

                try {
                    Registry myRegistry = LocateRegistry.getRegistry(Main.appServer.getIp(), Main.appServer.getPort());
                    //LOGGER.log(Level.INFO, "Registry retrieved: {0}", myRegistry);

                    LoginStub loginService = (LoginStub) myRegistry.lookup("LoginService");
                    //LOGGER.log(Level.INFO, "loginService retrieved: {0}", loginService);

                    boolean succesfulLogin = false;

                    if (Main.token != null) {
                        succesfulLogin = loginService.loginWithToken(Main.token);
                    }

                    if(!succesfulLogin) {

                        String token = loginService.getLoginToken(username, password);

                        succesfulLogin = token != null;

                        if (succesfulLogin) {
                            Main.token = token;
                        }
                    }

                    LOGGER.log(Level.INFO, "Loginserver retrieve token succesful=", succesfulLogin);

                    return succesfulLogin;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        };
    }
}
