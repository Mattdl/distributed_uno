package db_server;

import com.j256.ormlite.dao.Dao;
import model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stub_RMI.appserver_dbserver.UserDbStub;

import javax.xml.crypto.Data;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserDbService extends UnicastRemoteObject implements UserDbStub {

    final Logger LOGGER = LoggerFactory.getLogger(UserDbService.class);

    private Dao<User, String> userDao;
    private Dao<Player, String> playerDao;
    private Lock lock = new ReentrantLock();

    private DatabaseServer databaseServer;

    public UserDbService() throws RemoteException {
    }

    public UserDbService(Dao<User, String> userDao, Dao<Player, String> playerDao, DatabaseServer databaseServer) throws RemoteException {
        this.userDao = userDao;
        this.playerDao = playerDao;
        this.databaseServer = databaseServer;
    }

    /**
     * Persist user to database. Checks if username is unique with the createUniqueUser method.
     * @param userToPersist
     * @param propagate
     * @return
     * @throws RemoteException
     */
    public synchronized boolean persistUser(User userToPersist, boolean propagate) throws RemoteException {

        throwIfNotRunning();


        LOGGER.info("Persisting User = {}", userToPersist);

        //If you want to update a whole game object, best is to erase everything and do new inserts.

        try {
            boolean successful = createUniqueUser(userToPersist);

            LOGGER.info("User was not in database. New entry inserted, User = {}", userToPersist);

            return successful;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;

        } finally {
            LOGGER.info("User persisted, User = {}", userToPersist);

            if (propagate) {
                persistUserToOtherDatabases(userToPersist);
            }
        }

    }

    /**
     * Method used to determine if username is unique
     * @param userToPersist
     * @return boolean
     * @throws SQLException
     */
    private boolean createUniqueUser(User userToPersist) throws SQLException {
        //Query to ensure that username is unique
        List<User> userList =
                userDao.query(
                        userDao.queryBuilder().where()
                                .eq(User.PLAYERNAME_FIELD_NAME, userToPersist.getPlayer().getName())
                                .prepare());

        if (userList.isEmpty()) {

            createPlayer(userToPersist.getPlayer());

            userDao.createIfNotExists(userToPersist);
            return true;
        }

        return false;
    }

    private void createPlayer(Player player) throws SQLException {
        LOGGER.info("Creating player = {}", player);

        //First object itself
        playerDao.create(player);
    }

    /**
     * Propagate the persisting to all other databases.
     * If no connection, retry to connect!
     *
     * @param userToPersist
     */
    private void persistUserToOtherDatabases(User userToPersist) {

        databaseServer.getOtherDatabasesLock().readLock().lock();

        for (DbServer otherDbServer :  databaseServer.getOtherDatabases()) {

            UserDbStub userDbStub = otherDbServer.getUserDbStub();

            if (userDbStub == null) {
                databaseServer.tryConnectionWithDatabase(otherDbServer);
                userDbStub = otherDbServer.getUserDbStub();
            }

            try {

                userDbStub.persistUser(userToPersist, false);

                LOGGER.info("USER '{}' was persisted to other database = {}", userToPersist.getPlayer().getName(), otherDbServer);

            } catch (NullPointerException e) {
                LOGGER.error("DATABASE '{}'COULD NOT CONNECT TO OTHER DATABASE : {}", this.databaseServer, otherDbServer);

            } catch (Exception e) {
                LOGGER.error("DATABASE '{}'COULD NOT PERSIST TO OTHER DATABASE : {}", this.databaseServer, otherDbServer);
                e.printStackTrace();

                otherDbServer.addUserToQueue(userToPersist);
                LOGGER.error("DATABASE '{}' ADDED USER TO UPDATE QUEUE of {}", this.databaseServer, otherDbServer);
            }

        }

        databaseServer.getOtherDatabasesLock().readLock().unlock();

    }

    /**
     * Fetch user in the database.
     *
     * @param username
     * @return null, if user not present
     * @throws RemoteException
     */
    @Override
    public User fetchUser(String username) throws RemoteException {

        throwIfNotRunning();

        List<User> userList = new LinkedList<>();

        try {
            userList = userDao.query(
                    userDao.queryBuilder().where()
                            .eq(User.PLAYERNAME_FIELD_NAME, username)
                            .prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (userList.size() == 1) {
            return userList.get(0);
        }

        if (userList.size() > 1) {
            LOGGER.error("USER TABLE HAS MULTIPLE USERS WITH SAME NAME");
        }

        return null;
    }


    /**
     * The server-side method of the database to return all pending updates for the requesting db server.
     *
     * @param requestingDbServer
     * @return
     * @throws RemoteException
     */
    @Override
    public List<User> fetchQueueingUserUpdates(Server requestingDbServer) throws RemoteException {

        throwIfNotRunning();

        LOGGER.info("DATABASE '{}' IS REQUESTING UPDATES", requestingDbServer);

        DbServer dbServer = databaseServer.findDbServer(requestingDbServer);

        LOGGER.info("DATABASE '{}' IS REQUESTING UPDATES: found dbServer = {}", requestingDbServer, dbServer);

        if (dbServer != null) {

            List<User> ret = new ArrayList<>(dbServer.getUserUpdateQueue());

            // Clear the updates
            dbServer.getUserUpdateQueue().clear();

            LOGGER.info("DATABASE '{}' IS REQUESTING UPDATES: cleared db update list size = {}", requestingDbServer, dbServer.getUserUpdateQueue().size());
            LOGGER.info("DATABASE '{}' IS REQUESTING UPDATES: returning = {}", requestingDbServer, ret);

            return ret;
        }

        LOGGER.info("DATABASE '{}' IS REQUESTING UPDATES: Database not found!", requestingDbServer);

        return null;
    }

    /**
     * Throws RemoteException if the instance is not running
     *
     * @throws RemoteException
     */
    private void throwIfNotRunning() throws RemoteException {
        if (!databaseServer.isInstanceRunning()) {
            LOGGER.error("DATABASE '{}:{}' NOT RUNNING", this.databaseServer.getDbIp(), this.databaseServer.getDbPort());
            throw new RemoteException("INSTANCE IS NOT RUNNING : '" + databaseServer.getDbIp() + ":" + databaseServer.getDbPort() + "'");
        }
    }

    @Override
    public String toString() {
        return "UserDbService{" +
                ", userDao=" + userDao +
                ", playerDao=" + playerDao +
                ", databaseServer=" + databaseServer +
                '}';
    }

    /**
     * This is the client side method of the database to fetch updates from other databases.
     *
     * @param otherDbServer
     * @param currentServer
     * @throws RemoteException
     */
    public void fetchUpdatesFromOtherDatabase(DbServer otherDbServer, Server currentServer) throws RemoteException {

        try {
            LOGGER.info("FETCHING USER UPDATES FROM dbServer = {}", otherDbServer);

            UserDbStub userDbStub = otherDbServer.getUserDbStub();

            if (userDbStub == null) {
                databaseServer.tryConnectionWithDatabase(otherDbServer);
                userDbStub = otherDbServer.getUserDbStub();
            }

            List<User> users = userDbStub.fetchQueueingUserUpdates(currentServer);

            LOGGER.info("FETCHED USER UPDATES FROM dbServer = {}, RETURNED USER UPDATES = {}", otherDbServer, users);

            if (users != null) {
                for (User user : users) {
                    persistUser(user, false);
                }
            }
        } catch (NullPointerException e) {
            LOGGER.error("DATABASE '{}'COULD NOT CONNECT TO OTHER DATABASE : {}", this.databaseServer, otherDbServer);

        } catch (Exception e) {
            LOGGER.error("ERROR FETCHING USER UPDATES FROM dbServer = {}", otherDbServer);
            e.printStackTrace();
        }
    }
}
