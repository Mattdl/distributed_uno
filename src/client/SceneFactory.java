package client;

import client.controller.LobbyController;
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
            //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("layout/lobby.fxml"));
            //Parent root = fxmlLoader.load();

            //TODO msg to LobbyController
            //LobbyController lobbyController = (LobbyController) fxmlLoader.getController();
            //lobbyController.setLoginMsg(msg);

            return new Scene(root, WIDTH, HEIGHT);
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.log(Level.SEVERE,"Could not load lobby.fxml");
        }
        return null;
    }
}
