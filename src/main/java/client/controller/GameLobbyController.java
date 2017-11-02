package client.controller;

import client.service.game_lobby.LeaveGameService;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import model.Game;

public class GameLobbyController {

    private Game currentGame;

    @FXML
    public void initialize() {
        
    }

    /**
     * Called by button in GameLobby view //TODO
     */
    @FXML
    public void leaveGame(){
        LeaveGameService leaveGameService = new LeaveGameService(currentGame.getGameName());
        leaveGameService.setOnSucceeded(event -> {
            String failMsg = (String) event.getSource().getValue();

            if(failMsg == null) {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                switchToGameScene(stage, null);
            }
            else{
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("UNO");
                alert.setHeaderText("Failed to leave the game");
                alert.setContentText(failMsg);
                alert.showAndWait();
            }
        });
        leaveGameService.start();
    }

    private void switchToGameScene(Stage stage, Object o) {
        //TODO
    }
}
