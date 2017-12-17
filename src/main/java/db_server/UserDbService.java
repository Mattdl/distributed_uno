package db_server;

import com.j256.ormlite.dao.Dao;
import model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stub_RMI.appserver_dbserver.UserDbStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserDbService extends UnicastRemoteObject implements UserDbStub {

    final Logger LOGGER = LoggerFactory.getLogger(UserDbService.class);


    private List<DbServer> otherDatabases;
    private ReadWriteLock otherDatabasesLock = new ReentrantReadWriteLock();
    private Dao<User, String > userDao;

    public UserDbService() throws RemoteException {
    }

    public UserDbService(List<DbServer> otherDatabases, Dao<User, String> userDao) throws RemoteException {
        this.otherDatabases = otherDatabases;
        this.userDao = userDao;
    }

    @Override
    public boolean saveUserinfo() throws RemoteException {
        return false;
    }

    public synchronized boolean persistUser(User userToPersist, boolean propagate) throws RemoteException {

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

    private boolean createUniqueUser(User userToPersist) throws SQLException{
        //Query to ensure that username is unique
        List<User> userList =
                userDao.query(
                        userDao.queryBuilder().where()
                                .eq(User.USERNAME_FIELD_NAME, userToPersist.getUsername())
                                .prepare());

        if(userList.isEmpty()){
            userDao.createIfNotExists(userToPersist);
            return true;
        }

        return false;
    }

    /**
     * Propagate the persisting to all other databases
     *
     * @param userToPersist
     */
    private void persistUserToOtherDatabases(User userToPersist) {

        otherDatabasesLock.readLock().lock();

        for (DbServer otherDbServer : otherDatabases) {

            if (otherDbServer.isConnected()) {

                UserDbStub userDbStub = otherDbServer.getUserDbStub();

                try {

                    userDbStub.persistUser(userToPersist, false);

                    LOGGER.info("USER '{}' was persisted to other database = {}", userToPersist.getUsername(), otherDbServer);

                } catch (Exception e) {
                    LOGGER.error("COULD NOT PERSIST TO OTHER DATABASE : {}");
                    e.printStackTrace();
                }
            }
        }

        otherDatabasesLock.readLock().unlock();

    }

    /**
     * Searches winning user in database and adds his score.
     * @param player
     * @param score
     * @return
     * @throws RemoteException
     */
    @Override
    public synchronized void updateWinner(Player player, int score) throws RemoteException {

        LOGGER.info("FetchingUser in database");

        try {
            //Find corresponding user in database
            List<User> user =
                    userDao.query(
                            userDao.queryBuilder().where()
                                    .eq(User.PLAYER_FIELD_NAME, player)
                                    .prepare());

            if (user.size() == 1) {
                User winner = user.get(0);
                winner.addScore(score);
                userDao.update(winner);
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    public synchronized void updateOtherDatabases(List<DbServer> otherDatabases) {
        //TODO
    }
}
