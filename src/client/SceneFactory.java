package client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SceneFactory {

    private static final Logger LOGGER = Logger.getLogger( SceneFactory.class.getName() );

    private final int WIDTH;
    private final int HEIGHT;

    public SceneFactory(int width, int height) {
        this.WIDTH = width;
        this.HEIGHT = height;
    }

    public Scene getLoginScene() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("layout/login.fxml"));
            return new Scene(root, WIDTH, HEIGHT);
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.log(Level.SEVERE,"Could not load login.fxml");

        }
        return null;
    }

    public Scene getLobbyScene(String msg) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("layout/lobby.fxml"));

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Welcome to UNO");
            alert.setHeaderText("Join the lobby!");
            alert.setContentText(msg);
            alert.showAndWait();

            return new Scene(root, WIDTH, HEIGHT);
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.log(Level.SEVERE,"Could not load lobby.fxml");
        }
        return null;
    }
}
