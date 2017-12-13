package dispatcher;


import app_server.AppServer;
import db_server.DatabaseServer;
import model.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class Dispatcher {

    final Logger LOGGER = LoggerFactory.getLogger(Dispatcher.class);

    // DISPATCHER
    private final int DISPATCHER_PORT = 1099;

    // APP SERVERS
    public static final int STARTING_APPSERVER_PORT = 1100;
    public static final String STARTING_APPSERVER_IP = "localhost";

    // DB SERVERS
    private final int DB_SERVER_COUNT = 1;
    public static final int STARTING_DBSERVER_PORT = 7000;
    public static final String STARTING_DBSERVER_IP = "localhost";
    public static final int DEFAULT_MAX_GAME_LOAD_APPSERVER = 2;


    private List<Server> appServers;
    private List<Server> dbServers;

    private void init() {
        LOGGER.info("DISPATCHER STARTING setup");

        // Init servers
        initDbServers(DB_SERVER_COUNT);
        initInitialAppServer();

        LOGGER.info("DISPATCHER FINISHED init");

        // Init RMI
        initRMI();

        LOGGER.info("DISPATCHER FINISHED init");

        // Start servers
        startDbServers();
        startAppServers();

        LOGGER.info("DISPATCHER FINISHED startups");


    }

    /**
     * Setup RMI connections.
     * This Dispatcher only has a server-side RMI interface.
     * This is used by the AppServers and Clients.
     */
    private void initRMI() {

        //SERVER-SIDE RMI, used by AppServers and Clients
        try {
            //Init all RMI service bindings
            Registry registry = LocateRegistry.createRegistry(DISPATCHER_PORT);

            // create a new service for the Clients
            registry.rebind("DispatcherService", new DispatcherService(appServers, dbServers));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Init the first AppServer, if server gets overloaded, it requests the Dispatcher for new AppServers
     */
    private void initInitialAppServer() {
        appServers = new ArrayList<>();
        appServers.add(new Server(STARTING_APPSERVER_IP, STARTING_APPSERVER_PORT));
    }

    /**
     * Init all db servers
     *
     * @param dbServerCount
     */
    private void initDbServers(int dbServerCount) {
        dbServers = new ArrayList<>();

        for (int i = 0; i < dbServerCount; i++) {
            dbServers.add(new Server(STARTING_DBSERVER_IP, STARTING_DBSERVER_PORT + i));
        }
    }

    private void startDbServers() {

        for (Server dbServer : dbServers) {
            LOGGER.info("Starting database {}", dbServer);

            //Setup db
            String[] stringArgs = getDbServerArgsWithout(dbServer);

            DatabaseServer.main(stringArgs);
        }
    }

    private String[] getDbServerArgsWithout(Server dbServer) {
        String[] ret = new String[(dbServers.size()) * 2];

        ret[0] = dbServer.getIp();
        ret[1] = String.valueOf(dbServer.getPort());

        int argCount = 2;
        for (int i = 0; i < dbServers.size(); i++) {

            Server tmp = dbServers.get(i);

            if (tmp != dbServer) {

                ret[argCount] = tmp.getIp();
                ret[argCount + 1] = String.valueOf(tmp.getPort());

                argCount += 2;
            }
        }

        return ret;
    }

    private void startAppServers() {

        for (Server appServer : appServers) {
            LOGGER.info("Starting AppServer from dispatch, AppServer = {}", appServer);

            String[] serverArgs = new String[5];
            serverArgs[0] = STARTING_APPSERVER_IP;
            serverArgs[1] = String.valueOf(STARTING_APPSERVER_PORT);
            serverArgs[2] = STARTING_DBSERVER_IP;
            serverArgs[3] = String.valueOf(STARTING_DBSERVER_PORT);
            serverArgs[4] = String.valueOf(DEFAULT_MAX_GAME_LOAD_APPSERVER);

            AppServer.main(serverArgs);
        }
    }

    public static void main(String[] args) {
        new Dispatcher().init();
    }
}
