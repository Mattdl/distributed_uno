package client;

import client.controller.GameController;
import client.controller.GameLobbyController;
import client.controller.LobbyController;
import client.controller.WinnerController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Game;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SceneFactory {

    private static final Logger LOGGER = Logger.getLogger(SceneFactory.class.getName());

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
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Could not load login.fxml");
        }
        return null;
    }

    public Scene getLobbyScene(String msg) {
        try {
            //Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("layout/game.fxml"));

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("layout/lobby.fxml"));
            Parent root = fxmlLoader.load();

            //LobbyController lobbyController = fxmlLoader.getController();
            //lobbyController.setLoginMsg(msg);

            return new Scene(root, WIDTH, HEIGHT);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Could not load lobby.fxml");
        }
        return null;
    }

    public Scene getCreateGameScene(String msg) {
        try {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("layout/createGame.fxml"));

            return new Scene(root, WIDTH, HEIGHT);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Could not load createGame.fxml");
        }
        return null;
    }

    public Scene getGameScene(Game game) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("layout/game.fxml"));

            //To pass parameters between controllers
            fxmlLoader.setControllerFactory(new Callback<Class<?>, Object>() {
                @Override
                public Object call(Class<?> controllerClass) {
                    if (controllerClass == GameController.class) {
                        GameController gameController = new GameController(game);

                        LOGGER.log(Level.INFO, "SceneFactory GameController created with parameters");

                        return gameController;
                    } else {
                        try {
                            return controllerClass.newInstance();
                        } catch (Exception exc) {
                            throw new RuntimeException(exc);
                        }
                    }
                }
            });

            Parent root = fxmlLoader.load();

            return new Scene(root, WIDTH, HEIGHT);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Could not load game.fxml");
        }
        return null;
    }

    public Scene getGameLobbyScene(Game game) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("layout/gameLobby.fxml"));

            //To pass parameters between controllers
            fxmlLoader.setControllerFactory(new Callback<Class<?>, Object>() {
                @Override
                public Object call(Class<?> controllerClass) {
                    if (controllerClass == GameLobbyController.class) {
                        GameLobbyController gameLobbyController = new GameLobbyController(game);

                        LOGGER.log(Level.INFO, "SceneFactory GameLobbyController created with game = {0}", game);

                        return gameLobbyController;
                    } else {
                        try {
                            return controllerClass.newInstance();
                        } catch (Exception exc) {
                            throw new RuntimeException(exc);
                        }
                    }
                }
            });

            Parent root = fxmlLoader.load();

            return new Scene(root, WIDTH, HEIGHT);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Could not load gameLobby.fxml");
        }
        return null;
    }

    public Scene getWinnerScene(Stage stage, Game game) {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("layout/winner.fxml"));

            //To pass parameters between controllers
            fxmlLoader.setControllerFactory(new Callback<Class<?>, Object>() {
                @Override
                public Object call(Class<?> controllerClass) {
                    if (controllerClass == WinnerController.class) {
                        WinnerController winnerController = new WinnerController(stage, game);

                        LOGGER.log(Level.INFO, "SceneFactory winnerController created with parameters");

                        return winnerController;
                    } else {
                        try {
                            return controllerClass.newInstance();
                        } catch (Exception exc) {
                            throw new RuntimeException(exc);
                        }
                    }
                }
            });

            Parent root = fxmlLoader.load();

            return new Scene(root, WIDTH * 0.75, HEIGHT * 0.75);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Could not load winner.fxml");
        }
        return null;
    }
}
