package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import model.Player;
import model.Server;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static SceneFactory sceneFactory;
    public static Server appServer;
    public static String token;
    public static Player currentPlayer;

    @Override
    public void start(Stage primaryStage) throws Exception {

        LOGGER.log(Level.INFO, "Launching client application");


        //Multiple threads should run here (Service!)
        //ImgFetchService fetchService = new ImgFetchService();
        //fetchService.start();

        final int HEIGHT = 600;
        final int WIDTH = 900;
        sceneFactory = new SceneFactory(WIDTH, HEIGHT);
        primaryStage.setScene(sceneFactory.getLoginScene());


        primaryStage.setTitle("UNO");
        primaryStage.setAlwaysOnTop(false);
        primaryStage.setResizable(false);
        primaryStage.show();

        LOGGER.log(Level.INFO, "Client application LAUNCHED");
    }


    public static void main(String[] args) {
        launch(args);
    }
}