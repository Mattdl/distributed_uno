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
import java.util.Date;


public class AppServer {

    final Logger LOGGER = LoggerFactory.getLogger(AppServer.class);

    private String dbIp;
    private int dbPort;

    private Server currentServer;
    private String ip;
    private int port;

    private final String DISPATCHER_IP = "localhost";
    private final int DISPATCHER_PORT = 1099;

    private GameDbStub gameDbService;
    private UserDbStub userDbService;
    private DispatcherStub dispatcherService;

    private Lobby lobby;

    public AppServer(String ip, int port, String dbIp, int dbPort) {
        this.currentServer = new Server(ip, port);
        this.ip = ip;
        this.port = port;
        this.dbIp = dbIp;
        this.dbPort = dbPort;
    }

    private void startServer() {

        registerAsClientWithDispatcher(DISPATCHER_IP, DISPATCHER_PORT);
        registerAsClientWithDatabase();

        initData();

        try {
            Registry registry = LocateRegistry.createRegistry(port);

            //Bind RMI implementations to service names
            registry.rebind("LoginService", new LoginService());

            registry.rebind("GameService", new GameService(lobby, gameDbService));

            registry.rebind("GameLobbyService", new GameLobbyService(lobby, gameDbService));

            registry.rebind("LobbyService", new LobbyService(lobby));

            registry.rebind("RegisterService", new RegisterService(userDbService));

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
    private void retrieveNewDatabaseInfo() {
        LOGGER.info("APPSERVER ASKING NEW DATABASE INFO, current database server = {}:{}", dbIp, dbPort);

        try {
            LOGGER.info("Entering retrieveNewDatabaseInfo on APPSERVER, current dbServer = {}:{}", dbIp, dbPort);
            Server server = dispatcherService.retrieveActiveDatabaseInfo(currentServer);

            LOGGER.info("NEW DATABASE INFO RETRIEVED on APPSERVER, dbserver = {}", server);

            dbPort = server.getPort();
            dbIp = server.getIp();

        } catch (Exception e) {
            e.printStackTrace();
        }

        LOGGER.info("APPSERVER RETRIEVED NEW DATABASE INFO= {}:{}", dbIp, dbPort);
    }

    /**
     * Method to initialise a new lobby. Init objects in this
     */
    private void initData() {
        lobby = new Lobby(0);
    }


    /**
     * This app server is an RMI client of the databse, to persist/retrieve games.
     *
     * @return
     */
    private void registerAsClientWithDatabase() {

        LOGGER.info("Entering registerAsClientWithDatabase, dbIp={}, dbPort={}", dbIp, dbPort);

        Registry myRegistry = null;

        while (myRegistry == null || gameDbService == null || userDbService == null) {
            try {
                myRegistry = LocateRegistry.getRegistry(dbIp, dbPort);

                if (myRegistry != null) {
                    gameDbService = (GameDbStub) myRegistry.lookup("GameDbService");
                    userDbService = (UserDbStub) myRegistry.lookup("UserDbService");
                }

                LOGGER.info("APPSERVER CONNECTED TO DATABASE, database server = {}:{}", dbIp, dbPort);


            } catch (Exception e) {
                LOGGER.info("APPSERVER FAILED CONNECTING TO DATABASE, database server = {}:{}", dbIp, dbPort);

                //e.printStackTrace();

                //if no connetion, ask dispatcher for new dbIP+port
                retrieveNewDatabaseInfo();
            }
        }

        LOGGER.info("Leaving registerAsClientWithDatabase");
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
