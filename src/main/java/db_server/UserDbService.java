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

    private List<DbServer> otherDatabases;
    private ReadWriteLock otherDatabasesLock = new ReentrantReadWriteLock();
    private Dao<User, String> userDao;
    private Dao<Player, String> playerDao;
    private Lock lock = new ReentrantLock();

    private DatabaseServer databaseServer;

    public UserDbService() throws RemoteException {
    }

    public UserDbService(List<DbServer> otherDatabases, Dao<User, String> userDao, Dao<Player, String> playerDao, DatabaseServer databaseServer) throws RemoteException {
        this.otherDatabases = otherDatabases;
        this.userDao = userDao;
        this.playerDao = playerDao;
        this.databaseServer = databaseServer;
    }

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
     * Propagate the persisting to all other databases
     *
     * @param userToPersist
     */
    private void persistUserToOtherDatabases(User userToPersist) {

        otherDatabasesLock.readLock().lock();

        for (DbServer otherDbServer : otherDatabases) {

            UserDbStub userDbStub = otherDbServer.getUserDbStub();

            try {

                userDbStub.persistUser(userToPersist, false);

                LOGGER.info("USER '{}' was persisted to other database = {}", userToPersist.getPlayer().getName(), otherDbServer);

            } catch (Exception e) {
                LOGGER.error("DATABASE '{}'COULD NOT PERSIST TO OTHER DATABASE : {}", this.databaseServer, otherDbServer);
                //e.printStackTrace();

                otherDbServer.addUserToQueue(userToPersist);
                LOGGER.error("DATABASE '{}' ADDED USER TO UPDATE QUEUE of {}", this.databaseServer, otherDbServer);
            }

        }

        otherDatabasesLock.readLock().unlock();

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
     * Method used to recreate games from a database
     *
     * @return List of games
     * @throws RemoteException
     */
    /*
    @Override
    public synchronized List<User> copyDatabase() throws RemoteException {

        throwIfNotRunning();

        try {
            List<User> userList = userDao.queryForAll();

            return userList;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }*/
    @Override
    public List<User> fetchQueueingUserUpdates(Server requestingDbServer) throws RemoteException {
        LOGGER.info("DATABASE '{}' IS REQUESTING UPDATES", requestingDbServer);

        DbServer dbServer = databaseServer.findDbServer(requestingDbServer);

        LOGGER.info("DATABASE '{}' IS REQUESTING UPDATES: found dbServer = {}", requestingDbServer, dbServer);

        if (dbServer != null) {
            List<User> ret = new ArrayList<>(dbServer.getUserUpdateQueue());

            dbServer.getUserUpdateQueue().clear();

            LOGGER.info("DATABASE '{}' IS REQUESTING UPDATES: cleared db update list size = {}", requestingDbServer, dbServer.getUserUpdateQueue().size());

            LOGGER.info("DATABASE '{}' IS REQUESTING UPDATES: returning = {}", requestingDbServer, ret);

            return ret;
        }

        LOGGER.info("DATABASE '{}' IS REQUESTING UPDATES: Database not found!", requestingDbServer);

        return null;
    }

    /**
     * Update the list of other databases
     *
     * @param otherDatabases
     */
    public void updateOtherDatabases(List<DbServer> otherDatabases) {
        LOGGER.info("DATABASE GAMESERVICE UPDATING CONNECTIONS");

        otherDatabasesLock.writeLock().lock();
        this.otherDatabases = otherDatabases;
        otherDatabasesLock.writeLock().unlock();
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
                "otherDatabases=" + otherDatabases +
                ", userDao=" + userDao +
                ", playerDao=" + playerDao +
                ", databaseServer=" + databaseServer +
                '}';
    }
}
