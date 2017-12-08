package db_server;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseServer {

    private ConnectionSource conn;
    private final int PORT = 7000;

    private void startServer() {

        initDb("uno.db");

        if(conn!=null){
            //Init RMI services

            try {
                Registry registry = LocateRegistry.createRegistry(PORT);

                //Bind RMI implementations to service names
                registry.rebind("UserDbService", new UserDbService());
                registry.rebind("GameDbService", new GameDbService(conn));

            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("system is ready");


        }
    }

    private void initDb(String fileName) {

        String url = "jdbc:sqlite:" + fileName;

        try {
            conn = new JdbcConnectionSource(url);

            if (conn != null) {
                System.out.println("The database type is " + conn.getDatabaseType().getDatabaseName());
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public static void main(String[] args) {
        new DatabaseServer().startServer();
    }
}
