package dispatcher;

import model.ApplicationServer;
import model.DbServer;
import model.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stub_RMI.client_dispatcher.DispatcherStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import static dispatcher.Dispatcher.*;

//RMI handles multi-threading itself: may be or may not be in multiple threads
public class DispatcherService extends UnicastRemoteObject implements DispatcherStub {

    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherService.class.getName());

    public DispatcherService() throws RemoteException {
    }

    /**
     * Method called by every new client to get assigned to an AppServer.
     *
     * @return
     */
    @Override
    public synchronized Server retrieveServerInfo() throws RemoteException {
        LOGGER.info("DISPATCHER RETRIEVING NEW APPSERVER");
        LOGGER.info("DISPATCHER STATUS: dbServers = {}, appServers = {}", dbServers, appServers);


        for (ApplicationServer applicationServer : Dispatcher.appServers) {

            if (applicationServer.getAssignedClientsCount() < Dispatcher.DEFAULT_MAX_PLAYER_LOAD_APPSERVER) {

                applicationServer.incrementClientCount();
                LOGGER.info("UPDATED DISPATCHER STATUS: dbServers = {}, appServers = {}", dbServers, appServers);

                return applicationServer;
            }
        }

        // If no more space is available, create and start new appserver
        ApplicationServer ret = Dispatcher.startNewAppServer();

        LOGGER.info("DISPATCHER RETRIEVED NEW APP SERVER");
        LOGGER.info("DISPATCHER STATUS: dbServers = {}, appServers = {}", dbServers, appServers);

        return ret;
    }

    /**
     * Method called by appServer when he failed to connect with the initial db info that we gave him on startup
     *
     * @return
     * @throws RemoteException
     */
    public synchronized Server retrieveActiveDatabaseInfo(Server currentAppServer) throws RemoteException {
        LOGGER.info("retrieveActiveDatabaseInfo");
        LOGGER.info("DISPATCHER STATUS: dbServers = {}, appServers = {}", dbServers, appServers);

        ApplicationServer appServer = Dispatcher.findAppServer(currentAppServer);

        if (appServer != null) {
            int iterations = 0;
            DbServer assignedDbServer = findDbServer(appServer.getAssignedDbServer());
            DbServer dbServer = null;

            //LOGGER.debug("retrieveActiveDatabaseInfo, assignedDbServer = {}", assignedDbServer);

            while (iterations < Dispatcher.dbServers.size() - 1 && dbServer == null) {

                DbServer possibleDbServer = Dispatcher.dbServers.get(appServer.getNewDatabaseIndex());

                /*LOGGER.debug("retrieveActiveDatabaseInfo, iteration = {}, tmp db server = {}, assigned db server = {}",
                        assignedDbServer,tmpServer,assignedDbServer);*/

                if (!possibleDbServer.equals(assignedDbServer)) {

                    // Update all assignments and counts
                    DbServer dbServerInList = findDbServer(possibleDbServer);

                    if (dbServerInList != null) {
                        dbServerInList.decrementAssignedAppServerCount();
                    } else {
                        LOGGER.error("DATABASE WAS NOT FOUND!");
                    }

                    appServer.setAssignedDbServer(possibleDbServer);
                    possibleDbServer.incrementAssignedAppServerCount();

                    // The return value
                    dbServer = possibleDbServer;
                }

                int newIndex = appServer.getNewDatabaseIndex() + 1 % Dispatcher.dbServers.size();
                appServer.setNewDatabaseIndex(newIndex);
                iterations++;
            }

            if (iterations == Dispatcher.dbServers.size()) {
                LOGGER.error("NO OTHER DATABASE FOUND FOR THE APPSERVER!");
            }

            LOGGER.info("DISPATCHER STATUS: dbServers = {}, appServers = {}", dbServers, appServers);

            return dbServer;

        } else {
            LOGGER.error("APPSERVER WAS NOT FOUND IN DISPATCHER!");
            LOGGER.info("DISPATCHER STATUS: dbServers = {}, appServers = {}", dbServers, appServers);
            return null;
        }
    }

    /**
     * Decrementing client count
     *
     * @param appServer
     * @throws RemoteException
     */
    @Override
    public void clientQuitingSession(Server appServer) throws RemoteException {
        LOGGER.info("DISPATCHER: Quiting call from client to APPSERVER = {}", appServer);
        ApplicationServer localAppServer = findAppServer(appServer);

        if (localAppServer != null) {
            localAppServer.decrementClientCount();
        }

        LOGGER.info("DISPATCHER STATUS: dbServers = {}, appServers = {}", dbServers, appServers);
    }
}
