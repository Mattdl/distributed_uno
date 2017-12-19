package app_server;

import app_server.service.*;
import db_server.DatabaseServer;
import db_server.GameDbService;
import db_server.UserDbService;
import dispatcher.DispatcherService;
import model.Lobby;
import model.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stub_RMI.appserver_dbserver.GameDbStub;
import stub_RMI.appserver_dbserver.UserDbStub;
import stub_RMI.client_dispatcher.DispatcherStub;

import java.rmi.ConnectException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Base64;
import java.util.Date;

import static security.JWTUtils.generateApiSecret;


public class AppServer {

    static final Logger LOGGER = LoggerFactory.getLogger(AppServer.class);

    private String dbIp;
    private int dbPort;

    private Server currentServer;
    private String ip;
    private int port;

    private final String DISPATCHER_IP = "localhost";
    private final int DISPATCHER_PORT = 1099;

    private GameDbStub gameDbService;
    private UserDbStub userDbService;
    private static DispatcherStub dispatcherService;

    public static String apiSecret;

    private Lobby lobby;

    public AppServer(String ip, int port, String dbIp, int dbPort) {
        this.currentServer = new Server(ip, port);
        this.ip = ip;
        this.port = port;
        this.dbIp = dbIp;
        this.dbPort = dbPort;

        if (apiSecret == null) {
            apiSecret = generateApiSecret(50);
        }
    }

    private void startServer() {

        registerAsClientWithDispatcher(DISPATCHER_IP, DISPATCHER_PORT);
        registerAsClientWithDatabase(this);

        initData();

        try {
            Registry registry = LocateRegistry.createRegistry(port);

            //Bind RMI implementations to service names
            registry.rebind("LoginService", new LoginService(userDbService, this));

            registry.rebind("GameService", new GameService(lobby, gameDbService, this));

            registry.rebind("GameLobbyService", new GameLobbyService(lobby, gameDbService, this));

            registry.rebind("LobbyService", new LobbyService(lobby, gameDbService, this));

            registry.rebind("RegisterService", new RegisterService(userDbService, this));

        } catch (Exception e) {
            e.printStackTrace();
        }


        LOGGER.info("system is ready");
    }

    private void registerAsClientWithDispatcher(String dispatcherIp, int dispatcherPort) {
        Registry myRegistry = null;

        try {
            myRegistry = LocateRegistry.getRegistry(dispatcherIp, dispatcherPort);

            dispatcherService = (DispatcherStub) myRegistry.lookup("DispatcherService");

        } catch (ConnectException ce) {
            LOGGER.error("APPSERVER FAILED CONNECTING TO DISPATCHER, RMI ConnectException");
            //TODO if no connetion, try again after some time
        } catch (Exception e) {
            e.printStackTrace();
        }

        LOGGER.info("Leaving registerAsClientWithDispatcher");
    }

    /**
     * RMI call to Dispatcher to retrieve new database info
     */
    public static void retrieveNewDatabaseInfo(AppServer appServer) {
        LOGGER.info("APPSERVER ASKING NEW DATABASE INFO, current database server = {}:{}", appServer.dbIp, appServer.dbPort);

        try {
            LOGGER.info("Entering retrieveNewDatabaseInfo on APPSERVER, current dbServer = {}:{}", appServer.dbIp, appServer.dbPort);
            Server server = dispatcherService.retrieveActiveDatabaseInfo(appServer.currentServer);

            LOGGER.info("NEW DATABASE INFO RETRIEVED on APPSERVER, dbserver = {}", server);

            appServer.dbPort = server.getPort();
            appServer.dbIp = server.getIp();

        } catch (Exception e) {
            e.printStackTrace();
        }

        LOGGER.info("APPSERVER RETRIEVED NEW DATABASE INFO= {}:{}", appServer.dbIp, appServer.dbPort);
    }

    /**
     * Method to initialise a new lobby. Init objects in this
     */
    private void initData() {
        lobby = new Lobby(0);
    }


    /**
     * This app server is an RMI client of the database, to persist/retrieve games.
     *
     * @return
     */
    public static boolean registerAsClientWithDatabase(AppServer appServer) {

        LOGGER.info("Entering registerAsClientWithDatabase, dbIp={}, dbPort={}", appServer.dbIp, appServer.dbPort);

        Registry myRegistry = null;

        while (myRegistry == null || appServer.gameDbService == null || appServer.userDbService == null) {
            try {
                myRegistry = LocateRegistry.getRegistry(appServer.dbIp, appServer.dbPort);

                if (myRegistry != null) {
                    appServer.gameDbService = (GameDbStub) myRegistry.lookup("GameDbService");
                    appServer.userDbService = (UserDbStub) myRegistry.lookup("UserDbService");
                }

                LOGGER.info("APPSERVER CONNECTED TO DATABASE, database server = {}:{}", appServer.dbIp, appServer.dbPort);


            } catch (Exception e) {
                LOGGER.info("APPSERVER FAILED CONNECTING TO DATABASE, database server = {}:{}", appServer.dbIp, appServer.dbPort);

                //e.printStackTrace();

                //if no connetion, ask dispatcher for new dbIP+port
                retrieveNewDatabaseInfo(appServer);
            }
        }

        LOGGER.info("Leaving registerAsClientWithDatabase");

        return true;
    }

    public static void main(String[] args) {

        /* Parse this:
            serverArgs[0] = STARTING_APPSERVER_IP;
            serverArgs[1] = String.valueOf(STARTING_APPSERVER_PORT);
            serverArgs[2] = STARTING_DBSERVER_IP;
            serverArgs[3] = String.valueOf(STARTING_DBSERVER_PORT);
         */

        String ip = args[0];
        int port = Integer.parseInt(args[1]);

        String dbIp = args[2];
        int dbPort = Integer.parseInt(args[3]);


        new AppServer(ip, port, dbIp, dbPort).startServer();
    }
}
