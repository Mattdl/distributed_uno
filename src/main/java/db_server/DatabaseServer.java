package db_server;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import com.j256.ormlite.table.TableUtils;
import model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseServer {

    final Logger LOGGER = LoggerFactory.getLogger(DatabaseServer.class);

    private ConnectionSource conn;

    private static String dbIp = "localhost";
    private static int dbPort = 7000;

    private static List<Server> otherDatabases = new ArrayList<>();

    private String databaseUrl;

    private Dao<Game, String> gameDao;
    private Dao<Move, String> moveDao;
    private Dao<Player, String> playerDao;
    private Dao<Card, String> cardDao;

    private void startServer() {

        initDb("uno_port" + dbPort + ".db");

        if (conn != null) {
            //Init RMI services

            try {
                Registry registry = LocateRegistry.createRegistry(dbPort);

                //Bind RMI implementations to service names
                registry.rebind("UserDbService", new UserDbService());
                registry.rebind("GameDbService", new GameDbService(gameDao, moveDao, playerDao, cardDao));

            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("system is ready");


        }
    }

    private void initDb(String fileName) {

        databaseUrl = "jdbc:sqlite:" + fileName;

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


    public static void main(String[] args) {

        dbIp = args[0];
        dbPort = Integer.valueOf(args[1]);

        int argCount = 2;

        while (argCount < args.length) {

            otherDatabases.add(
                    new Server(
                            args[argCount],
                            Integer.valueOf(args[argCount + 1])
                    )
            );

            argCount += 2;
        }


        new DatabaseServer().startServer();
    }
}
