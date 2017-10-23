package client;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static SceneFactory sceneFactory;

    @Override
    public void start(Stage primaryStage) throws Exception{

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
    }


    public static void main(String[] args) {
        launch(args);
    }
}
