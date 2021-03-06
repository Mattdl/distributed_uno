package db_server;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import com.j256.ormlite.table.TableUtils;
import model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stub_RMI.appserver_dbserver.GameDbStub;
import stub_RMI.appserver_dbserver.UserDbStub;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DatabaseServer {

    public static HashMap<DatabaseServer, Registry> databaseInstances = new HashMap<>();

    static final Logger LOGGER = LoggerFactory.getLogger(DatabaseServer.class);
    private boolean instanceRunning;

    private Server currentServer;
    private String dbIp;
    private int dbPort;

    private List<DbServer> otherDatabases;
    private ReadWriteLock otherDatabasesLock = new ReentrantReadWriteLock();


    private String databaseUrl;

    private GameDbService gameDbService;
    private UserDbService userDbService;

    private Dao<Game, String> gameDao;
    private Dao<Move, String> moveDao;
    private Dao<Player, String> playerDao;
    private Dao<Card, String> cardDao;
    private Dao<User, String> userDao;

    private int connRefreshTime = 60 * 1000;
    private TimerTask timerTask;

    public DatabaseServer(String dbIp, int dbPort, List<DbServer> otherDatabases) {
        this.currentServer = new Server(dbIp, dbPort);
        this.dbIp = dbIp;
        this.dbPort = dbPort;
        this.otherDatabases = otherDatabases;
        this.instanceRunning = true;

        LOGGER.info("DATABASE '{}:{}' OTHER DATABASES INITIALIZED:{}'", dbIp, dbPort, otherDatabases);
    }

    private void startServer() {
        // Init database
        initDb("uno_port" + dbPort + ".db");

        //Init RMI services
        try {

            Registry registry = databaseInstances.get(this);

            boolean instanceAlreadyExists = registry != null;

            if (!instanceAlreadyExists) {
                registry = LocateRegistry.createRegistry(dbPort);
                databaseInstances.put(this, registry);
            } else {
                databaseInstances.remove(this);
                databaseInstances.put(this, registry);
            }

            userDbService = new UserDbService(userDao, playerDao, this);
            gameDbService = new GameDbService(gameDao, moveDao, playerDao, cardDao, this);

            //Bind RMI implementations to service names
            registry.rebind("UserDbService", userDbService);
            registry.rebind("GameDbService", gameDbService);

        } catch (Exception e) {
            e.printStackTrace();
        }


        // Connect with other databases
        //registerAsClientWithOtherDatabases();
        fetchUpdatesFromOtherDatabases();

        LOGGER.info("DATABASE '{}:{}' is READY", dbIp, dbPort);
    }

    /**
     * Iterates over all other databases and fetches all pending updates.
     */
    private void fetchUpdatesFromOtherDatabases() {

        LOGGER.info("CHECKING PEER DATABASE CONNECTIONS OF '{}:{}'", dbIp, dbPort);

        for (DbServer otherDbServer : otherDatabases) {

            tryConnectionWithDatabase(otherDbServer);

        }

        LOGGER.info("CHECKED PEER DATABASE CONNECTIONS OF '{}:{}'", dbIp, dbPort);
    }

    public void tryConnectionWithDatabase(DbServer otherDbServer) {
        Registry myRegistry;
        GameDbStub gameDbStub = null;
        UserDbStub userDbStub = null;

        try {
            myRegistry = LocateRegistry.getRegistry(otherDbServer.getIp(), otherDbServer.getPort());

            if (myRegistry != null) {
                gameDbStub = (GameDbStub) myRegistry.lookup("GameDbService");
                userDbStub = (UserDbStub) myRegistry.lookup("UserDbService");
            }

            // If new connection
            otherDbServer.setGameDbStub(gameDbStub);
            otherDbServer.setUserDbStub(userDbStub);

            // Fetch updates
            userDbService.fetchUpdatesFromOtherDatabase(otherDbServer, currentServer);
            gameDbService.fetchUpdatesFromOtherDatabase(otherDbServer, currentServer);

            LOGGER.info("DATABASE '{}:{}' CONNECTED TO OTHER DATABASE, other database server = '{}:{}'", dbIp, dbPort, otherDbServer.getIp(), otherDbServer.getPort());


        } catch (Exception e) {
            LOGGER.info("DATABASE '{}:{}' FAILED CONNECTING TO OTHER DATABASE, other database server = '{}:{}'", dbIp, dbPort, otherDbServer.getIp(), otherDbServer.getPort());
            //e.printStackTrace();
        }
    }

    private void initDb(String fileName) {

        databaseUrl = "jdbc:sqlite:" + fileName;

        ConnectionSource conn = null;

        try {
            conn = new JdbcConnectionSource(databaseUrl);

            LOGGER.info("Connection established to {}", databaseUrl);

            //INIT DAO's
            moveDao = DaoManager.createDao(conn, Move.class);
            gameDao = DaoManager.createDao(conn, Game.class);
            cardDao = DaoManager.createDao(conn, Card.class);
            playerDao = DaoManager.createDao(conn, Player.class);
            userDao = DaoManager.createDao(conn, User.class);

            //CREATE TABLES
            TableUtils.createTableIfNotExists(conn, Game.class);
            TableUtils.createTableIfNotExists(conn, Card.class);
            TableUtils.createTableIfNotExists(conn, Move.class);
            TableUtils.createTableIfNotExists(conn, Player.class);
            TableUtils.createTableIfNotExists(conn, User.class);

        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            if (conn != null) {
                LOGGER.info("Database Created with all tables!");
                conn.closeQuietly();
            }
        }
    }

    /**
     * This DB server is an RMI client of the other databases, to persist.
     *
     * @return
     */
    private synchronized void registerAsClientWithOtherDatabases() {

        //TODO remove timer task

        Timer timer = new Timer();

        timerTask = new TimerTask() {

            @Override
            public void run() {
                // Your database code here

                LOGGER.info("CHECKING PEER DATABASE CONNECTIONS OF '{}:{}'", dbIp, dbPort);

                boolean updated = false;

                for (DbServer otherDbServer : otherDatabases) {

                    if (!otherDbServer.isConnected()) {

                        Registry myRegistry;
                        GameDbStub gameDbStub = null;
                        UserDbStub userDbStub = null;

                        try {
                            myRegistry = LocateRegistry.getRegistry(otherDbServer.getIp(), otherDbServer.getPort());

                            if (myRegistry != null) {
                                gameDbStub = (GameDbStub) myRegistry.lookup("GameDbService");
                                userDbStub = (UserDbStub) myRegistry.lookup("UserDbService");
                            }

                            // If new connection
                            otherDbServer.setGameDbStub(gameDbStub);
                            otherDbServer.setUserDbStub(userDbStub);

                            updated = true;

                            LOGGER.info("DATABASE '{}:{}' CONNECTED TO OTHER DATABASE, other database server = '{}:{}'", dbIp, dbPort, otherDbServer.getIp(), otherDbServer.getPort());


                        } catch (Exception e) {
                            LOGGER.info("DATABASE '{}:{}' FAILED CONNECTING TO OTHER DATABASE, other database server = '{}:{}'", dbIp, dbPort, otherDbServer.getIp(), otherDbServer.getPort());

                            //e.printStackTrace();
                        }
                    }
                }

                LOGGER.info("CHECKED PEER DATABASE CONNECTIONS OF '{}:{}', update = {}", dbIp, dbPort, updated);
            }
        };

        timer.scheduleAtFixedRate(timerTask, 2 * 1000, connRefreshTime);
    }

    public DbServer findDbServer(Server server) {

        for (DbServer dbServer : otherDatabases) {
            if (dbServer.getIp().equals(server.getIp()) && dbServer.getPort() == server.getPort()) {
                return dbServer;
            }
        }
        return null;
    }

    public static void restartDatabaseServer(DbServer targetDbServer) {

    }


    /**
     * Shuts down the target database. This is a simulation, as unexporting RemoteObjects in RMI removes them from the whole runtime.
     *
     * @param targetDbServer
     */
    public static void stopDatabaseServer(DbServer targetDbServer) {
        LOGGER.info("STOPPING DATABASE, targetDbServer = {} ", targetDbServer);

        Iterator<Map.Entry<DatabaseServer, Registry>> it = databaseInstances.entrySet().iterator();
        boolean found = false;

        Map.Entry<DatabaseServer, Registry> pair = null;

        LOGGER.info("STOPPING DATABASE: before while, entrysize = {}", databaseInstances.size());
        while (it.hasNext() && !found) {

            pair = it.next();
            DatabaseServer databaseServer = pair.getKey();

            //LOGGER.info("In the while, databaseServer = {} ", databaseServer);
            LOGGER.info("STOPPING PROCEDURE: Step0 = iterating: instance = {}", databaseServer);


            if (databaseServer.instanceRunning
                    && databaseServer.dbIp.equals(targetDbServer.getIp())
                    && databaseServer.dbPort == targetDbServer.getPort()) {
                found = true;

                LOGGER.info("STOPPING PROCEDURE: Step1 = found instance");

                Registry registry = pair.getValue();

                try {
                    //registry.unbind("GameDbService");
                    //registry.unbind("UserDbService");

                    LOGGER.info("STOPPING PROCEDURE: Step2 = Unbinding services");

                    //UnicastRemoteObject.unexportObject(databaseServer.gameDbService, false);
                    //UnicastRemoteObject.unexportObject(databaseServer.userDbService, false);
                    //databaseServer.timerTask.cancel();

                    LOGGER.info("STOPPING PROCEDURE: Step3 = Unexporting objects from registry");

                } catch (Exception e) {
                    e.printStackTrace();
                }
                LOGGER.info("STOPPING PROCEDURE: FINISHED");


                databaseServer.instanceRunning = false;

                LOGGER.info("STOPPED DATABASE  '{}:{}'", databaseServer.dbIp, databaseServer.dbPort);
            }
        }
    }

    public boolean isInstanceRunning() {
        return instanceRunning;
    }

    public String getDbIp() {
        return dbIp;
    }

    public int getDbPort() {
        return dbPort;
    }

    public List<DbServer> getOtherDatabases() {
        return otherDatabases;
    }

    public ReadWriteLock getOtherDatabasesLock() {
        return otherDatabasesLock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatabaseServer that = (DatabaseServer) o;
        return dbPort == that.dbPort &&
                Objects.equals(dbIp, that.dbIp);
    }

    @Override
    public int hashCode() {

        return Objects.hash(dbIp, dbPort);
    }

    @Override
    public String toString() {
        return "DatabaseServer{" +
                "instanceRunning=" + instanceRunning +
                ", dbIp='" + dbIp + '\'' +
                ", dbPort=" + dbPort +
                '}';
    }

    public static void main(String[] args) {

        // Init current database
        String dbIp = args[0];
        int dbPort = Integer.valueOf(args[1]);

        List<DbServer> otherDatabases = new LinkedList<>();

        // Init other databases
        int argCount = 2;

        while (argCount < args.length) {

            otherDatabases.add(
                    new DbServer(
                            args[argCount],
                            Integer.valueOf(args[argCount + 1])
                    )
            );

            argCount += 2;
        }

        DatabaseServer databaseServer = new DatabaseServer(dbIp, dbPort, otherDatabases);
        databaseServer.startServer();
    }
}
