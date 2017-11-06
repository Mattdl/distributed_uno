package client.controller;


import client.Main;
import client.service.game.CheckPlayersService;
import client.service.game.FetchCurrentPlayerAndCardService;
import client.service.game.FetchInitCardsService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Card;
import model.Game;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GameController implements Observer{

    private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());

    private int succeededInitCalls = 0;

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
        game.addObserver(this);
    }

    @FXML
    public void initialize() {

        alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Welcome to UNO");
        alert.setHeaderText("yoU kNOw, it's UNO");
        alert.setContentText("Waiting for all players to join...");
        LOGGER.log(Level.INFO, "Everybody's waiting to start");

        initServices();

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

    /**
     * Calls init services, when everything is returned, the CheckPlayersService is called to wait on other players to start
     * the game.
     */
    private void initServices() {
        Platform.runLater(new Runnable() {
            final int initCallCount = 3;

            @Override
            public void run() {

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

                //First RMI init call
                FetchCurrentPlayerAndCardService currentPlayerCall = new FetchCurrentPlayerAndCardService(game,true);
                currentPlayerCall.setOnSucceeded(event -> {
                    succeededInitCalls++;
                    if (succeededInitCalls >= initCallCount) {
                        checkPlayersService.start();
                    }
                });
                currentPlayerCall.start();

                //Second RMI init call
                FetchInitCardsService cardsCall = new FetchInitCardsService(game);
                cardsCall.setOnSucceeded(event -> {
                    succeededInitCalls++;
                    if (succeededInitCalls >= initCallCount) {
                        checkPlayersService.start();
                    }
                });
                cardsCall.start();

                //Third RMI call


            }
        });
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    private void displayFailureDialog() {
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Say goodbye to UNO");
        alert.setHeaderText("There seems to be a problem");
        alert.setContentText("Not all players could join the game");
        alert.showAndWait();
    }

    private void displayConfirmationDialog() {
        alert = new Alert(Alert.AlertType.CONFIRMATION);
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
    public void eindeSpel() {
        //TODO: implementeren zodat automatisch gebeurt wanneer spel gedaan is
        Stage stage = (Stage) endGameButton.getScene().getWindow();
        switchToWinnerScene(stage, null);


    }

    private void switchToWinnerScene(Stage stage, String msg) {
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
