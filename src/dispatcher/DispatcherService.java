package dispatcher;

import model.Server;
import stub_RMI.client_dispatcher.DispatcherStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

//RMI handles multi-threading itsel: may be or may not be in multiple threads
public class DispatcherService extends UnicastRemoteObject implements DispatcherStub {

    private static final Logger LOGGER = Logger.getLogger( DispatcherService.class.getName() );


    public DispatcherService() throws RemoteException {}


    /**
     * Method called by every new client
     * @return
     */
    @Override
    public synchronized Server retrieveServerInfo() throws RemoteException{
        LOGGER.info("retrieveServerInfo");

        Server server = new Server();
        server.setIp(Dispatcher.STARTING_SERVER_IP);
        server.setPort(Dispatcher.STARTING_SERVER_PORT);

        return server;
    }

}
