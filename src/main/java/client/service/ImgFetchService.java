package client.service;

import app_server.DeckBuilder;
import client.Main;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.WritableImage;
import model.Card;
import stub_RMI.client_appserver.GameStub;

import java.awt.image.BufferedImage;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImgFetchService extends Service<Void> {

    private static final Logger LOGGER = Logger.getLogger(ImgFetchService.class.getName());

    public static boolean hasFetchedAllCards = false;

    public ImgFetchService() {

    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                LOGGER.log(Level.INFO, "Calling background Task");

                Registry myRegistry = LocateRegistry.getRegistry(Main.appServer.getIp(), Main.appServer.getPort());
                GameStub gameService = (GameStub) myRegistry.lookup("GameService");

                List<Card> ret = gameService.fetchCardImageMappings();

                if (ret != null) {
                    initHashMap(ret);
                    hasFetchedAllCards = true;
                }

                return null;
            }
        };
    }

    private void initHashMap(List<Card> ret) {
        Map<Card, WritableImage> map = new HashMap<>();

        for (Card card : ret) {

            //Convert to JAVAFX image
            WritableImage img = DeckBuilder.byteArrayToJavaFXImage(card.getSerializableImage());

            // See card object, only type + value + color are used
            map.put(card, img);
        }
    }
}
