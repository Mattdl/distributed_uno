package app_server;

import app_server.service.GameService;
import app_server.service.LoginService;
import db_server.GameDbService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class AppServer {
    final String DB_IP = "localhost";
    final int DB_PORT = 1200;

    private void startServer(String ip, int port) {
        GameDbService gameDbService = registerClientRMI();

        try {
            Registry registry = LocateRegistry.createRegistry(port);

            //Bind RMI implementations to service names
            registry.rebind("LoginService", new LoginService());

            registry.rebind("GameService", new GameService(gameDbService));

        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println("system is ready");
    }

    private GameDbService registerClientRMI(){

        try {
            Registry myRegistry = LocateRegistry.getRegistry(DB_IP, DB_PORT);

            GameDbService gameDbService = (GameDbService) myRegistry.lookup("GameDbService");

            return gameDbService;

        }catch(Exception e){
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
