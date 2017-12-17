package client.service.login;

import client.Main;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import stub_RMI.client_appserver.LoginStub;
import stub_RMI.client_appserver.RegisterStub;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterService extends Service<Boolean> {
    private static final Logger LOGGER = Logger.getLogger(LoginService.class.getName());

    private String username;
    private String password;

    public RegisterService(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Background Task to obtain a register client
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
                    RegisterStub registerService = (RegisterStub) myRegistry.lookup("RegisterService");

                    boolean successfulRegister = false;



                    LOGGER.log(Level.INFO, "Register successful: ", successfulRegister);

                    return successfulRegister;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        };
    }
}
