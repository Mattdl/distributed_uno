package app_server;

import app_server.service.GameLobbyService;
import app_server.service.GameService;
import app_server.service.LobbyService;
import app_server.service.LoginService;
import db_server.GameDbService;
import model.Lobby;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Logger;

public class AppServer {

    private static final Logger LOGGER = Logger.getLogger(AppServer.class.getName());

    final String DB_IP = "localhost";
    final int DB_PORT = 1200;

    private Lobby lobby;

    private void startServer(String ip, int port) {

        GameDbService gameDbService = registerAsClientWithDatabase(DB_IP, DB_PORT);

        initData();

        try {
            Registry registry = LocateRegistry.createRegistry(port);

            //Bind RMI implementations to service names
            registry.rebind("LoginService", new LoginService());

            registry.rebind("GameService", new GameService(lobby));

            registry.rebind("GameLobbyService", new GameLobbyService(lobby));

            registry.rebind("LobbyService", new LobbyService(lobby));

        } catch (Exception e) {
            e.printStackTrace();
        }


        LOGGER.info("system is ready");
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
    private GameDbService registerAsClientWithDatabase(String dbIP, int dbPort) {

        try {
            Registry myRegistry = LocateRegistry.getRegistry(dbIP, dbPort);

            GameDbService gameDbService = (GameDbService) myRegistry.lookup("GameDbService");

            return gameDbService;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        String ip = args[0];
        int port = Integer.parseInt(args[1]);

        new AppServer().startServer(ip, port);
    }
}
