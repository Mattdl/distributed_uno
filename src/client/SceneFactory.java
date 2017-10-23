package client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
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
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            return new Scene(root, WIDTH, HEIGHT);
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.log(Level.SEVERE,"Could not load login.fxml");

        }
        return null;
    }

    public Scene getLobbyScene() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("lobby.fxml"));
            return new Scene(root, WIDTH, HEIGHT);
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.log(Level.SEVERE,"Could not load lobby.fxml");
        }
        return null;
    }
}
