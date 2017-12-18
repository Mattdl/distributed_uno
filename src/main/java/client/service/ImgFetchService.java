package client.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ImgFetchService extends Service<Void> {

    private static final Logger LOGGER = Logger.getLogger(ImgFetchService.class.getName());

    private boolean hasFetchedAllCards;

    public ImgFetchService() {
        this.hasFetchedAllCards = false;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                LOGGER.log(Level.INFO, "Calling background Task");

                //The fetching loop
                while (hasFetchedAllCards) {

                    //TODO RMI call to fetch game-data

                    LOGGER.log(Level.INFO, "GameData fetch received: {0}");

                    //TODO update game-model

                    //tmp
                    hasFetchedAllCards = false;
                }

                return null;
            }
        };
    }
}
