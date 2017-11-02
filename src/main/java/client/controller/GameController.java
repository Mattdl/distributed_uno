package client.controller;


import client.service.game.CheckPlayersService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import model.Card;
import model.Game;


public class GameController {

    private Game game;

    private Alert alert;

    @FXML
    private ListView<Card> handListView;

    @FXML
    private ImageView lastCardPlayed;


    public GameController(Game game) {
        this.game = game;
    }

    @FXML
    public void initialize() {

        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Welcome to UNO");
        alert.setHeaderText("yoU kNOw, it's UNO");
        alert.setContentText("Waiting for all players to join...");
        alert.showAndWait();

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

}
