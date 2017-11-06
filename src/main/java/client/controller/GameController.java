package client.controller;


import client.Main;
import client.service.game.CheckPlayersService;
import client.service.game_lobby.LeaveGameService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Card;
import model.Game;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GameController {

    private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());


    private Game game;

    private Alert alert;

    @FXML
    private ListView<Card> handListView;

    @FXML
    private ImageView lastCardPlayed;

    @FXML
    private Button endGameButton;


    public GameController(Game game) {
        this.game = game;
    }

    @FXML
    public void initialize() {

        alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Welcome to UNO");
        alert.setHeaderText("yoU kNOw, it's UNO");
        alert.setContentText("Waiting for all players to join...");
        LOGGER.log(Level.INFO, "Everybody's waiting to start");


        CheckPlayersService checkPlayersService = new CheckPlayersService(game);
        checkPlayersService.setOnSucceeded(event -> {
            boolean successful = (boolean) event.getSource().getValue();

            if (successful) {
                alert.close();
                displayConfirmationDialog();
            } else {
                alert.close();
                displayFailureDialog();
            }
        });
        checkPlayersService.start();

        //Used to create ListView with images of cards in hand (UNTESTED)

        ObservableList<Card> observableList = FXCollections.observableList(Main.currentPlayer.getHand());
        handListView = new ListView<>(observableList);
        handListView.setCellFactory(listView -> new ListCell<Card>() {
            private ImageView imageView = new ImageView();

            @Override
            public void updateItem(Card card, boolean empty) {
                super.updateItem(card, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    imageView.setImage(card.getImage());
                    setGraphic(imageView);
                }
            }
        });


    }

    private void displayFailureDialog() {
        alert= new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Say goodbye to UNO");
        alert.setHeaderText("There seems to be a problem");
        alert.setContentText("Not all players could join the game");
        alert.showAndWait();
    }

    private void displayConfirmationDialog() {
        alert= new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Welcome to UNO");
        alert.setHeaderText("yoU kNOw, it's UNO");
        alert.setContentText("Waiting for all players to join...");
        alert.showAndWait();
    }


    //Solution found on the net to update cardview, need to implement and extend listener

   /* @Override
    public void cardDrawn(final Card card)
    {
        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                handListView.getItems().add(card);
            }
        });
    }

    @Override
    public void cardPlayed(final Card card)
    {
        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                handListView.getItems().remove(card);
            }
        });
    }*/

   @FXML
   public void eindeSpel(){
       //TODO: implementeren zodat automatisch gebeurt wanneer spel gedaan is
       Stage stage = (Stage) endGameButton.getScene().getWindow();
       switchToWinnerScene(stage, null);


   }

    private void switchToWinnerScene(Stage stage, String msg){
        LOGGER.log(Level.INFO, "switching To WinnerScene");

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setResizable(false);
        popup.initStyle(StageStyle.TRANSPARENT);
        popup.setTitle("Winner Screen");


        popup.setScene(Main.sceneFactory.getWinnerScene(stage, game));
        popup.show();

        //stage.setScene(Main.sceneFactory.getWinnerScene(game));



        LOGGER.log(Level.INFO, "switched To WinnerScene");
    }

}
