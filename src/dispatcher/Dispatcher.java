package dispatcher;



import app_server.AppServer;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Dispatcher {

    private final int DISPATCHER_PORT = 1099;

    public static final int STARTING_SERVER_PORT = 1100;
    public static final String STARTING_SERVER_IP = "localhost";


    //private List<Server> onlineServers;

    private void startServer() {

        //Startup one server
        String[] serverArgs = new String[2];
        serverArgs[0] = STARTING_SERVER_IP;
        serverArgs[1] = String.valueOf(STARTING_SERVER_PORT);
        AppServer.main(null);


        //SERVER-SIDE RMI
        try {
            //Init all RMI service bindings
            Registry registry = LocateRegistry.createRegistry(DISPATCHER_PORT);

            // create a new service for the Clients
            registry.rebind("DispatcherService", new DispatcherService());

        } catch (Exception e) {
            e.printStackTrace();
        }

        //CLIENT-SIDE RMI to application servers
        //new DispatcherThread().start();

        System.out.println("system is ready");
    }

    public static void main(String[] args) {
        new Dispatcher().startServer();
    }
}
