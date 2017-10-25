package dispatcher;



import dispatcher.model.Server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class Dispatcher {

    private final int DISPATCHER_PORT = 1099;

    private List<Server> onlineServers;

    private void startServer() {

        //SERVER-SIDE RMI
        //Init all RMI service bindings
        try {
            // create on dispatcher port
            Registry registry = LocateRegistry.createRegistry(DISPATCHER_PORT);

            // create a new service for the Clients
            registry.rebind("DispatcherService", new DispatcherService());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //CLIENT-SIDE RMI to application servers
        new DispatcherThread().start();

        System.out.println("system is ready");
    }

    public static void main(String[] args) {
        new Dispatcher().startServer();
    }
}
