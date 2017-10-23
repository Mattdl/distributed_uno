package client;

import client.service.FetchService;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {

    private static final Logger LOGGER = Logger.getLogger( Main.class.getName() );


    public static SceneFactory sceneFactory;

    @Override
    public void start(Stage primaryStage) throws Exception{

        LOGGER.log(Level.INFO,"Launching client application");

        //Multiple threads should run here (Service!)
        FetchService fetchService = new FetchService();
        fetchService.start();

        final int HEIGHT = 600;
        final int WIDTH = 900;
        sceneFactory = new SceneFactory(WIDTH,HEIGHT);
        primaryStage.setScene(sceneFactory.getLoginScene());


        primaryStage.setTitle("UNO");
        primaryStage.setAlwaysOnTop( false );
        primaryStage.setResizable( true );
        primaryStage.show();

        LOGGER.log(Level.INFO,"Client application LAUNCHED");
    }


    public static void main(String[] args) {
        launch(args);
    }
}
