package client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class SceneFactory {

    private final int WIDTH;
    private final int HEIGHT;

    public SceneFactory(int width, int height) {
        this.WIDTH = width;
        this.HEIGHT = height;
    }

    public Scene getLoginScene() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        return new Scene(root, WIDTH, HEIGHT);
    }
}
