package app_server;

import app_server.service.GameService;
import app_server.service.LoginService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class AppServer {
    private void startServer(String ip, int port) {
        try {
            Registry registry = LocateRegistry.createRegistry(port);

            //Bind RMI implementations to service names
            registry.rebind("LoginService", new LoginService());
            registry.rebind("GameService", new GameService());


        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("system is ready");
    }

    public static void main(String[] args) {
        String ip = args[0];
        int port = Integer.parseInt(args[1]);

        new AppServer().startServer(ip,port);
    }
}
