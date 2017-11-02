package client.controller;

import client.Main;
import client.service.game_lobby.GameLobbyService;
import client.service.game_lobby.LeaveGameService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import model.Game;
import model.Player;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameLobbyController implements Observer {

    private static final Logger LOGGER = Logger.getLogger(LobbyController.class.getName());

    private Game currentGame;

    private GameLobbyService gameLobbyService;

    public GameLobbyController(Game game) {
        this.currentGame = game;
    }

    @FXML
    Button leaveGameButton;


    @FXML
    public void initialize() {
        currentGame.addObserver(this);
        gameLobbyService = new GameLobbyService(currentGame);
        gameLobbyService.start();
    }

    /**
     * Called by button in GameLobby view
     */
    @FXML
    public void leaveGame() {
        LOGGER.log(Level.INFO, "Called leaveGame method in GameLobbyController");

        LeaveGameService leaveGameService = new LeaveGameService(currentGame.getGameName());
        leaveGameService.setOnSucceeded(event -> {

            String failMsg = (String) event.getSource().getValue();
            LOGGER.log(Level.INFO, failMsg);

            if (failMsg == null) {

                gameLobbyService.setInGameLobby(false);
                Stage stage = (Stage) leaveGameButton.getScene().getWindow();
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

    @Override
    public void update(Observable o, Object arg) {
        //TODO update UI
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

            }
        });
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
