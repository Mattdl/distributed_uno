package client.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.Server;
import stub_RMI.client_dispatcher.DispatcherStub;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerInitiatorService extends Service<Server> {

    private static final Logger LOGGER = Logger.getLogger(ServerInitiatorService.class.getName());

    public static final String DISPATCHER_IP = "localhost";
    public static final int DISPATCHER_PORT = 1099;
    public static final String DISPATCHER_SERVICE = "DispatcherService";

    public ServerInitiatorService() {
    }

    @Override
    protected Task<Server> createTask() {
        return new Task<Server>() {
            @Override
            protected Server call() throws Exception {
                LOGGER.log(Level.INFO, "Calling ServerInitiatorService");

                try {
                    //RMI init
                    Registry myRegistry = LocateRegistry.getRegistry(DISPATCHER_IP, DISPATCHER_PORT);
                    //LOGGER.log(Level.INFO, "Registry retrieved: {0}", myRegistry);

                    DispatcherStub dispatcherService = (DispatcherStub) myRegistry.lookup(DISPATCHER_SERVICE);
                    //LOGGER.log(Level.INFO, "Dispatcher Service retrieved: {0}", dispatcherService);

                    boolean successfullConnection = false;
                    Server serverInfo = null;

                    while (!successfullConnection) {
                        //LOGGER.info("In connection cycle");
                        //RMI call
                        serverInfo = dispatcherService.retrieveServerInfo();

                        LOGGER.log(Level.INFO, "Received serverInfo: {0}", serverInfo);

                        successfullConnection = serverInfo != null;
                    }

                    return serverInfo;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }
}
