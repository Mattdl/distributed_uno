package app_server;

import app_server.service.GameLobbyService;
import app_server.service.GameService;
import app_server.service.LobbyService;
import app_server.service.LoginService;
import db_server.GameDbService;
import db_server.UserDbService;
import model.Lobby;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Logger;

public class AppServer {

    private static final Logger LOGGER = Logger.getLogger(AppServer.class.getName());

    //TODO outcomment this, dbIP mus tbe provided by the dispatcher, if connection is lost to db, dispatcher must be contacted
    final String DB_IP = "localhost";
    final int DB_PORT = 1200;

    private final String DISPATCHER_IP = "localhost";
    private final int DISPATCHER_PORT = 1099;

    private GameDbService gameDbService;
    private UserDbService userDbService;


    private Lobby lobby;

    private void startServer(String ip, int port) {

        registerAsClientWithDatabase(DB_IP, DB_PORT);
        registerAsClientWithDispatcher(DISPATCHER_IP, DISPATCHER_PORT);

        initData();

        try {
            Registry registry = LocateRegistry.createRegistry(port);

            //Bind RMI implementations to service names
            registry.rebind("LoginService", new LoginService());

            registry.rebind("GameService", new GameService(lobby, gameDbService));

            registry.rebind("GameLobbyService", new GameLobbyService(lobby));

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

            gameDbService = (GameDbService) myRegistry.lookup("GameDbService");

        } catch (Exception e) {
            e.printStackTrace();
            //TODO if no connetion, ask dispatcher for new dbIP+port
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
        String ip = args[0];
        int port = Integer.parseInt(args[1]);

        new AppServer().startServer(ip, port);
    }
}
