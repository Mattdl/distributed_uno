package app_server;

import app_server.service.GameLobbyService;
import app_server.service.GameService;
import app_server.service.LobbyService;
import app_server.service.LoginService;
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

    final Logger LOGGER = LoggerFactory.getLogger(DatabaseServer.class);

    private static String dbIp;
    private static int dbPort;

    private static int maxGameLoad;

    private final String DISPATCHER_IP = "localhost";
    private final int DISPATCHER_PORT = 1099;

    private GameDbStub gameDbService;
    private UserDbStub userDbService;
    private DispatcherStub dispatcherService;


    private Lobby lobby;

    private void startServer(String ip, int port) {

        registerAsClientWithDispatcher(DISPATCHER_IP, DISPATCHER_PORT);
        registerAsClientWithDatabase(dbIp, dbPort);

        initData();

        try {
            Registry registry = LocateRegistry.createRegistry(port);

            //Bind RMI implementations to service names
            registry.rebind("LoginService", new LoginService());

            registry.rebind("GameService", new GameService(lobby, gameDbService));

            registry.rebind("GameLobbyService", new GameLobbyService(lobby, gameDbService));

            registry.rebind("LobbyService", new LobbyService(lobby));

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

        }catch(ConnectException ce){
            LOGGER.error("APPSERVER FAILED CONNECTING TO DISPATCHER, RMI ConnectException");
            //TODO if no connetion, try again after some time
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        LOGGER.info("Leaving registerAsClientWithDispatcher");
    }

    /**
     * RMI call to Dispatcher to retrieve new database info
     */
    private void retrieveNewDatabaseInfo() {
        try {
            LOGGER.info("Entering retrieveNewDatabaseInfo on APPSERVER, current dbServer = {}:{}",dbIp,dbPort);
            Server server = dispatcherService.retrieveActiveDatabaseInfo();

            LOGGER.info("NEW DATABASE INFO RETRIEVED on APPSERVER, dbserver = {}",server);

            dbPort = server.getPort();
            dbIp = server.getIp();

        }catch(Exception e){
            e.printStackTrace();
        }

        LOGGER.info("Leaving retrieveNewDatabaseInfo on APPSERVER");
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
    private void registerAsClientWithDatabase(String dbIP, int dbPort) {

        LOGGER.info("Entering registerAsClientWithDatabase");

        Registry myRegistry = null;

        //long startTime = System.currentTimeMillis();
        //long elapsedTime = 0;
        //long waitingTimeForNewRequest = 5*1000;


        //elapsedTime < waitingTimeForNewRequest &&
        while( myRegistry == null || gameDbService == null || userDbService == null) {
            try {
                myRegistry = LocateRegistry.getRegistry(dbIP, dbPort);

                gameDbService = (GameDbStub) myRegistry.lookup("GameDbService");
                userDbService = (UserDbStub) myRegistry.lookup("UserDbService");

                LOGGER.info("APPSERVER CONNECTED TO DATABASE, database server = {}:{}",dbIP,dbPort);


            } catch (Exception e) {
                //elapsedTime = (new Date()).getTime() - startTime;

                e.printStackTrace();

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
            serverArgs[4] = String.valueOf(DEFAULT_MAX_GAME_LOAD_APPSERVER);
         */

        String ip = args[0];
        int port = Integer.parseInt(args[1]);

        dbIp = args[2];
        dbPort = Integer.parseInt(args[3]);

        maxGameLoad = Integer.parseInt(args[4]);

        new AppServer().startServer(ip, port);
    }
}
