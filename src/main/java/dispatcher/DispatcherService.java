package dispatcher;

import model.ApplicationServer;
import model.DbServer;
import model.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stub_RMI.client_dispatcher.DispatcherStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

//RMI handles multi-threading itsel: may be or may not be in multiple threads
public class DispatcherService extends UnicastRemoteObject implements DispatcherStub {

    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherService.class.getName());

    public DispatcherService() throws RemoteException {
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
    public synchronized Server retrieveActiveDatabaseInfo(Server currentAppServer) throws RemoteException {
        LOGGER.info("retrieveActiveDatabaseInfo");

        ApplicationServer appServer = Dispatcher.findAppServer(currentAppServer);

        if (appServer != null) {
            int iterations = 0;
            Server assignedDbServer = appServer.getAssignedDbServer();
            DbServer dbServer = null;

            LOGGER.debug("retrieveActiveDatabaseInfo, assignedDbServer = {}", assignedDbServer);

            while (iterations < Dispatcher.dbServers.size() && dbServer == null) {

                DbServer tmpServer = Dispatcher.dbServers.get(appServer.getNewDatabaseIndex());

                LOGGER.debug("retrieveActiveDatabaseInfo, iteration = {}, tmp db server = {}, assigned db server = {}",
                        assignedDbServer,tmpServer,assignedDbServer);

                if (!assignedDbServer.equals(tmpServer)) {
                    dbServer = tmpServer;
                }

                int newIndex = appServer.getNewDatabaseIndex() + 1 % Dispatcher.dbServers.size();
                appServer.setNewDatabaseIndex(newIndex);
                iterations++;
            }

            if (iterations == Dispatcher.dbServers.size()) {
                LOGGER.error("NO OTHER DATABASE FOUND FOR THE APPSERVER!");
            }

            return dbServer;

        } else {
            LOGGER.error("APPSERVER WAS NOT FOUND IN DISPATCHER!");

            return null;
        }

        /*
        Server server = new Server();
        server.setIp(Dispatcher.STARTING_DBSERVER_IP);
        server.setPort(Dispatcher.STARTING_DBSERVER_PORT);
        */

    }

    @Override
    public void shutAppServerDown(Server server) throws RemoteException {

    }

    @Override
    public void startNewAppServer() throws RemoteException {

    }
}
