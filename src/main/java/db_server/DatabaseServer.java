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
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DatabaseServer {

    final Logger LOGGER = LoggerFactory.getLogger(DatabaseServer.class);

    private String dbIp;
    private int dbPort;

    private List<DbServer> otherDatabases;

    private String databaseUrl;

    private GameDbService gameDbService;
    private UserDbService userDbService;

    private Dao<Game, String> gameDao;
    private Dao<Move, String> moveDao;
    private Dao<Player, String> playerDao;
    private Dao<Card, String> cardDao;

    public DatabaseServer(String dbIp, int dbPort, List<DbServer> otherDatabases) {
        this.dbIp = dbIp;
        this.dbPort = dbPort;
        this.otherDatabases = otherDatabases;

        LOGGER.info("DATABASE '{}:{}' OTHER DATABASES INITIALIZED:{}'", dbIp, dbPort, otherDatabases);
    }

    private void startServer() {
        // Init database
        initDb("uno_port" + dbPort + ".db");

        //Init RMI services
        try {
            Registry registry = LocateRegistry.createRegistry(dbPort);

            gameDbService = new GameDbService(otherDatabases, gameDao, moveDao, playerDao, cardDao);
            userDbService = new UserDbService(otherDatabases);

            //Bind RMI implementations to service names
            registry.rebind("UserDbService", gameDbService);
            registry.rebind("GameDbService", userDbService);

        } catch (Exception e) {
            e.printStackTrace();
        }


        // Connect with other databases
        registerAsClientWithOtherDatabases();

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

            //CREATE TABLES
            TableUtils.createTableIfNotExists(conn, Game.class);
            TableUtils.createTableIfNotExists(conn, Card.class);
            TableUtils.createTableIfNotExists(conn, Move.class);
            TableUtils.createTableIfNotExists(conn, Player.class);

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

        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                // Your database code here

                LOGGER.info("Entering registerAsClientWithOtherDatabases");

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

                            e.printStackTrace();
                        }
                    }
                    LOGGER.info("Leaving registerAsClientWithOtherDatabases");
                }

                if(updated) {
                    gameDbService.updateOtherDatabases(otherDatabases);
                }
            }
        }, 0, 2 * 60 * 1000);
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

        new DatabaseServer(dbIp, dbPort, otherDatabases).startServer();
    }
}
