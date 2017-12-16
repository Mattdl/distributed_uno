package dispatcher;


import app_server.AppServer;
import db_server.DatabaseServer;
import model.ApplicationServer;
import model.DbServer;
import model.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Dispatcher {

    static final Logger LOGGER = LoggerFactory.getLogger(Dispatcher.class);

    // DISPATCHER
    private final int DISPATCHER_PORT = 1099;

    // APP SERVERS
    public static final int STARTING_APPSERVER_PORT = 1100;
    public static final String STARTING_APPSERVER_IP = "localhost";

    // DB SERVERS
    private final int DB_SERVER_COUNT = 3;
    public static final int STARTING_DBSERVER_PORT = 7000;
    public static final String STARTING_DBSERVER_IP = "localhost";
    public static final int DEFAULT_MAX_PLAYER_LOAD_APPSERVER = 2;


    static List<ApplicationServer> appServers;
    static List<DbServer> dbServers;

    //TODO SET THESE PARAMS
    private boolean testFailureDatabase = false;

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
        startAppServer(appServers.get(0));

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
            registry.rebind("DispatcherService", new DispatcherService());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Init the first ApplicationServer, if server gets overloaded, it requests the Dispatcher for new AppServers
     */
    private void initInitialAppServer() {
        appServers = new ArrayList<>();
        appServers.add(new ApplicationServer(STARTING_APPSERVER_IP, STARTING_APPSERVER_PORT, dbServers.get(0)));
        dbServers.get(0).incrementAssignedAppServerCount();
    }

    /**
     * Init all db servers
     *
     * @param dbServerCount
     */
    private void initDbServers(int dbServerCount) {
        dbServers = new ArrayList<>();

        for (int i = 0; i < dbServerCount; i++) {
            dbServers.add(new DbServer(STARTING_DBSERVER_IP, STARTING_DBSERVER_PORT + i));
        }

        if (testFailureDatabase) {
            insertUnconnectableDatabase();
        }
    }

    /**
     * Method to insert unreachable database.
     * Used for testing reassignment.
     */
    private void insertUnconnectableDatabase() {
        dbServers.add(0, new DbServer(STARTING_DBSERVER_IP, STARTING_DBSERVER_PORT - 1));
    }

    private void startDbServers() {
        List<DbServer> databasesToStartup = new LinkedList<>();

        if (!testFailureDatabase) {
            databasesToStartup = dbServers;
        } else if (dbServers.size() > 1) {
            // First database is the failure database (not started)
            databasesToStartup = dbServers.subList(1, dbServers.size());
        }


        for (DbServer dbServer : databasesToStartup) {
            LOGGER.info("DISPATCHER STARTING DATABASE {}", dbServer);

            //Setup db
            String[] stringArgs = getDbServerArgsWithout(dbServer);

            DatabaseServer.main(stringArgs);

        }
    }

    /**
     * Get the params of the other databases in String array format.
     *
     * @param dbServer
     * @return
     */
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

    /**
     * Only startup one app-server! If load is exceeded, other appServers are started by request of the appServer.
     */
    private static void startAppServer(ApplicationServer appServer) {

        LOGGER.info("Starting ApplicationServer from dispatch, ApplicationServer = {}", appServer);

        String[] serverArgs = new String[5];
        serverArgs[0] = STARTING_APPSERVER_IP;
        serverArgs[1] = String.valueOf(STARTING_APPSERVER_PORT);
        serverArgs[2] = appServer.getAssignedDbServer().getIp();
        serverArgs[3] = String.valueOf(appServer.getAssignedDbServer().getPort());

        LOGGER.info("DISPATCHER Starting ApplicationServer with String args = {}", (Object[]) serverArgs);

        AppServer.main(serverArgs);

    }

    public static ApplicationServer findAppServer(Server server) {
        for (ApplicationServer appServer : appServers) {
            if (appServer.equals(server)) {
                return appServer;
            }
        }
        return null;
    }

    public static DbServer findDbServer(Server server) {
        for (DbServer dbServer : dbServers) {
            if (dbServer.equals(server)) {
                return dbServer;
            }
        }
        return null;
    }

    public static Server startNewAppServer() {
        int portOffset = appServers.size() - 1;

        //Init the new server
        ApplicationServer server = new ApplicationServer();
        server.setIp(Dispatcher.STARTING_APPSERVER_IP);
        server.setPort(Dispatcher.STARTING_APPSERVER_PORT + portOffset);

        // Get least loaded db-server
        server.setAssignedDbServer(getLeastOccupiedDbServer());

        //Add it to the list
        appServers.add(server);

        // Startup the server
        startAppServer(server);

        return server;
    }

    public static DbServer getLeastOccupiedDbServer() {

        DbServer minDbServer = null;
        int minCount = Integer.MAX_VALUE;

        for (DbServer dbServer : dbServers) {
            if (dbServer.getAssignedAppServerCount() < minCount) {
                minDbServer = dbServer;
                minCount = dbServer.getAssignedAppServerCount();
            }
        }

        return minDbServer;
    }

    public static void main(String[] args) {
        new Dispatcher().init();
    }
}
