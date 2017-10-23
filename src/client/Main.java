package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {



    @Override
    public void start(Stage primaryStage) throws Exception{

        //Multiple threads should run here (Service!)
        FetchService fetchService = new FetchService();
        fetchService.start();

        //Init scene
        Parent root = FXMLLoader.load(getClass().getResource("layout.fxml"));
        primaryStage.setTitle("UNO");
        primaryStage.setAlwaysOnTop( false );
        primaryStage.setResizable( true );
        primaryStage.setScene(new Scene(root)); //width and height changable
        primaryStage.show();


    }


    public static void main(String[] args) {
        launch(args);
    }
}
