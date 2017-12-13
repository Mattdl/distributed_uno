package dispatcher;

import model.Server;
import stub_RMI.client_dispatcher.DispatcherStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.logging.Logger;

//RMI handles multi-threading itsel: may be or may not be in multiple threads
public class DispatcherService extends UnicastRemoteObject implements DispatcherStub {

    private static final Logger LOGGER = Logger.getLogger(DispatcherService.class.getName());

    private List<Server> appServers;
    private List<Server> dbServers;


    public DispatcherService(List<Server> appServers, List<Server> dbServers) throws RemoteException {
        this.appServers = appServers;
        this.dbServers = dbServers;
    }

    /**
     * Method called by every new client
     *
     * @return
     */
    @Override
    public synchronized Server retrieveServerInfo() throws RemoteException {
        LOGGER.info("retrieveServerInfo");

        Server server = new Server();
        server.setIp(Dispatcher.STARTING_APPSERVER_IP);
        server.setPort(Dispatcher.STARTING_APPSERVER_PORT);

        return server;
    }

    /**
     * Method called by appServer when he failed to connect with the initial db info that we gave him on startup
     *
     * @return
     * @throws RemoteException
     */
    //TODO logic
    public synchronized Server retrieveActiveDatabaseInfo() throws RemoteException {
        LOGGER.info("retrieveActiveDatabaseInfo");

        Server server = new Server();
        server.setIp(Dispatcher.STARTING_APPSERVER_IP);
        server.setPort(Dispatcher.STARTING_APPSERVER_PORT);

        return server;
    }

}
