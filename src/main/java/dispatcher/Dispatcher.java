package dispatcher;


import app_server.AppServer;
import db_server.DatabaseServer;
import model.Server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Dispatcher {

    private static final Logger LOGGER = Logger.getLogger(Dispatcher.class.getName());

    // DISPATCHER
    private final int DISPATCHER_PORT = 1099;

    // APP SERVERS
    public static final int STARTING_APPSERVER_PORT = 1100;
    public static final String STARTING_APPSERVER_IP = "localhost";

    // DB SERVERS
    private final int DB_SERVER_COUNT = 1;
    public static final int STARTING_DBSERVER_PORT = 7000;
    public static final String STARTING_DBSERVER_IP = "localhost";

    private List<Server> appServers;
    private List<Server> dbServers;

    private void init() {
        initDbServers(DB_SERVER_COUNT);

    }

    private void initDbServers(int dbServerCount) {
        dbServers = new ArrayList<>();

        for (int i = 0; i < dbServerCount; i++) {
            dbServers.add(new Server(STARTING_DBSERVER_IP,STARTING_DBSERVER_PORT + i));
        }
    }

    private void startAppServer() {

        LOGGER.info("Starting database");
        //Setup db
        DatabaseServer.main(new String[0]);

        LOGGER.info("Starting server from dispatch");

        //Startup one server
        String[] serverArgs = new String[2];
        serverArgs[0] = STARTING_APPSERVER_IP;
        serverArgs[1] = String.valueOf(STARTING_APPSERVER_PORT);
        AppServer.main(serverArgs);

        //SERVER-SIDE RMI
        try {
            //Init all RMI service bindings
            Registry registry = LocateRegistry.createRegistry(DISPATCHER_PORT);

            // create a new service for the Clients
            registry.rebind("DispatcherService", new DispatcherService());

        } catch (Exception e) {
            e.printStackTrace();
        }

        //CLIENT-SIDE RMI to application servers
        //new DispatcherThread().start();

        LOGGER.info("DISPATCHER is ready");
    }

    public static void main(String[] args) {
        new Dispatcher().init();
    }
}
