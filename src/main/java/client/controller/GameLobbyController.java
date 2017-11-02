package client.controller;

import client.Main;
import client.service.game_lobby.GameLobbyService;
import client.service.game_lobby.LeaveGameService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import model.Game;
import model.Player;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameLobbyController {

    private static final Logger LOGGER = Logger.getLogger(LobbyController.class.getName());

    private Game currentGame;

    private GameLobbyService gameLobbyService;

    @FXML
    public void initialize() {
        gameLobbyService = new GameLobbyService(currentGame);
        gameLobbyService.start();
    }

    /**
     * Called by button in GameLobby view //TODO
     */
    @FXML
    public void leaveGame() {
        LeaveGameService leaveGameService = new LeaveGameService(currentGame.getGameName());
        leaveGameService.setOnSucceeded(event -> {
            String failMsg = (String) event.getSource().getValue();

            if (failMsg == null) {
                gameLobbyService.setInGameLobby(false);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                switchToLobbyScene(stage, null);
            } else {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("UNO");
                        alert.setHeaderText("Failed to leave the game");
                        alert.setContentText(failMsg);
                        alert.showAndWait();
                    }
                });
            }
        });
        leaveGameService.start();
    }

/*    private void switchToGameScene(Stage stage, Object o) {
        LOGGER.log(Level.INFO, "switching To GameScene");

        stage.setScene(Main.sceneFactory.getGameScene(o.toString()));

        LOGGER.log(Level.INFO, "switched To GameScene");
    }*/

    private void switchToLobbyScene(Stage stage, String msg) {
        LOGGER.log(Level.INFO, "switching To LobbyScene");

        stage.setScene(Main.sceneFactory.getLobbyScene(msg));

        LOGGER.log(Level.INFO, "switched To LobbyScene");
    }
}
