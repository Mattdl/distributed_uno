package client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

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
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("layout/login.fxml"));
            return new Scene(root, WIDTH, HEIGHT);
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.log(Level.SEVERE,"Could not load login.fxml");
        }
        return null;
    }

    public Scene getLobbyScene(String msg) {
        try {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("layout/lobby.fxml"));
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

    public Scene getCreateGameScene(String msg) {
        try {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("layout/createGame.fxml"));
            //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("layout/lobby.fxml"));
            //Parent root = fxmlLoader.load();

            //TODO msg to LobbyController
            //LobbyController lobbyController = (LobbyController) fxmlLoader.getController();
            //lobbyController.setLoginMsg(msg);

            return new Scene(root, WIDTH, HEIGHT);
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.log(Level.SEVERE,"Could not load createGame.fxml");
        }
        return null;
    }

    public Scene getGameScene(String msg) {
        try {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("layout/game.fxml"));
            //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("layout/lobby.fxml"));
            //Parent root = fxmlLoader.load();

            //TODO msg to LobbyController
            //LobbyController lobbyController = (LobbyController) fxmlLoader.getController();
            //lobbyController.setLoginMsg(msg);

            return new Scene(root, WIDTH, HEIGHT);
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.log(Level.SEVERE,"Could not load game.fxml");
        }
        return null;
    }
}
