package client.service.login;

import client.Main;
import client.controller.LoginController;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import stub_RMI.client_appserver.LoginStub;
import stub_RMI.client_dispatcher.DispatcherStub;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PingService extends Service<Boolean> {

    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {

                boolean isSuccessful = false;

                if (Main.appServer != null) {
                    //RMI init
                    Registry myRegistry = LocateRegistry.getRegistry(Main.appServer.getIp(), Main.appServer.getPort());
                    //LOGGER.log(Level.INFO, "Registry retrieved: {0}", myRegistry);

                    LoginStub loginService = (LoginStub) myRegistry.lookup("LoginService");
                    //LOGGER.log(Level.INFO, "loginService retrieved: {0}", loginService);

                    isSuccessful = loginService.ping();
                }

                return isSuccessful; //Calls succeeded()
            }
        };
    }
}