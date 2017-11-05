package client.controller;


import client.service.game.CheckPlayersService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Card;
import model.Game;

import java.util.logging.Level;
import java.util.logging.Logger;


public class GameController {

    private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());

    private Game game;

    @FXML
    private ListView<Card> handListView;

    @FXML
    private ImageView lastCardPlayed;

    @FXML
    private Text serverInfoText;

    public GameController(Game game) {
        this.game = game;
    }

    @FXML
    public void initialize() {

        displayServerInfo("Waiting for all players to join...");

        LOGGER.log(Level.INFO, "Everybody's waiting to start");


        CheckPlayersService checkPlayersService = new CheckPlayersService(game);
        checkPlayersService.setOnSucceeded(event -> {
            boolean successful = (boolean) event.getSource().getValue();

            if (successful) {
                displayServerInfo("Everybody is ready to play!");
            } else {
                displayServerInfo("The game could not start, we lost someone...");
            }
        });
        checkPlayersService.start();
        LOGGER.log(Level.INFO, "CheckPlayerService started");


        //Used to create ListView with images of cards in hand (UNTESTED)

        //Platform.runLater(() -> {
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
        //});

        LOGGER.log(Level.INFO, "Initalize method ended.");

    }

    private void displayServerInfo(String msg) {
        LOGGER.log(Level.INFO, "Starting setText thread");
        Platform.runLater(() -> {
            serverInfoText.setText(msg);
            LOGGER.log(Level.INFO, "Server info text is placed!");
        });
    }

    /*
    private void displayInitGameFailDialog() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                serverInfoText.setText("Waiting for all players to join...");
                alert.close();
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setX(getStageCenterX());
                alert.setY(getStageCenterY());
                alert.setTitle("Say goodbye to UNO");
                alert.setHeaderText("There seems to be a problem");
                alert.setContentText("Not all players could join the game");
                alert.showAndWait();

            }
        });
    }

    private void displayInitGameConfirmDialog() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                alert.close();
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setX(getStageCenterX());
                alert.setY(getStageCenterY());
                alert.setTitle("Welcome to UNO");
                alert.setHeaderText("Let's get started!");
                alert.setContentText("All players are in, can you beat them?");
                alert.showAndWait();
            }
        });
    }

    private int getStageCenterX() {
        Stage stage = (Stage) lastCardPlayed.getScene().getWindow();
        return (int) (stage.getX() + stage.getWidth() / 2);
    }

    private int getStageCenterY() {
        Stage stage = (Stage) lastCardPlayed.getScene().getWindow();
        return (int) (stage.getY() + stage.getHeight() / 2);
    }*/


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

}
