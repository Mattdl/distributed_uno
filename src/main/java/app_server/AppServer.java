package app_server;

import app_server.service.GameLobbyService;
import app_server.service.GameService;
import app_server.service.LobbyService;
import app_server.service.LoginService;
import db_server.GameDbService;
import db_server.UserDbService;
import dispatcher.DispatcherService;
import model.Lobby;

import java.rmi.ConnectException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Logger;

public class AppServer {

    private static final Logger LOGGER = Logger.getLogger(AppServer.class.getName());

    private static String dbIp;
    private static int dbPort;

    private static int maxGameLoad;

    private final String DISPATCHER_IP = "localhost";
    private final int DISPATCHER_PORT = 1099;

    private GameDbService gameDbService;
    private UserDbService userDbService;
    private DispatcherService dispatcherService;


    private Lobby lobby;

    private void startServer(String ip, int port) {

        registerAsClientWithDatabase(dbIp, dbPort);
        registerAsClientWithDispatcher(DISPATCHER_IP, DISPATCHER_PORT);

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

            dispatcherService = (DispatcherService) myRegistry.lookup("DispatcherService");

        }catch(ConnectException ce){
            LOGGER.warning("APPSERVER FAILED CONNECTING TO DISPATCHER, RMI ConnectException");
            //TODO if no connetion, try again after some time

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if(myRegistry == null || gameDbService == null || userDbService == null){
            //TODO if no connetion, ask dispatcher for new dbIP+por
            LOGGER.warning("APPSERVER COULD NOT CONNECT TO DATABASE");
        }
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
        Registry myRegistry = null;

        try {
            myRegistry = LocateRegistry.getRegistry(dbIP, dbPort);

            gameDbService = (GameDbService) myRegistry.lookup("GameDbService");
            userDbService = (UserDbService) myRegistry.lookup("UserDbService");

        } catch (Exception e) {
            e.printStackTrace();
            //TODO if no connetion, ask dispatcher for new dbIP+port
        }

        if(myRegistry == null || gameDbService == null || userDbService == null){
            //TODO if no connetion, ask dispatcher for new dbIP+por
            LOGGER.warning("APPSERVER COULD NOT CONNECT TO DATABASE");
        }
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
