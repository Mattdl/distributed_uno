package app_server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {
    private void startServer() {
        try {
            // create on port 1099
            Registry registry = LocateRegistry.createRegistry(1099);

            // create a new service named CounterService
            //TODO name
            registry.rebind("NAMEService", new StubImpl());

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("system is ready");
    }

    public static void main(String[] args) {
        //TODO multithreading
        new ServerMain().startServer();
    }
}
