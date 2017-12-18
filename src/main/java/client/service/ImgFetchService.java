package client.service;

import app_server.DeckBuilder;
import client.Main;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.WritableImage;
import model.Card;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stub_RMI.client_appserver.GameStub;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImgFetchService extends Service<Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImgFetchService.class.getName());

    public static Map<Card, WritableImage> imageMap;

    public static boolean hasFetchedAllCards = false;

    public ImgFetchService() {

    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                LOGGER.info("CLIENT STARTING: FETCH IMAGES");

                Registry myRegistry = LocateRegistry.getRegistry(Main.appServer.getIp(), Main.appServer.getPort());
                GameStub gameService = (GameStub) myRegistry.lookup("GameService");

                LOGGER.info( "CLIENT FETCHING IMAGES");

                List<Card> ret = gameService.fetchCardImageMappings();

                LOGGER.info("CLIENT FETCHED IMAGES, ret = {}", ret);

                if (ret != null) {
                    initHashMap(ret);
                    hasFetchedAllCards = true;
                }

                LOGGER.info("CLIENT IMAGES MAP SIZE = {}", imageMap.size());

                return null;
            }
        };
    }

    private void initHashMap(List<Card> ret) {
        imageMap = new HashMap<>();

        for (Card card : ret) {

            //Convert to JAVAFX image
            WritableImage img = DeckBuilder.byteArrayToJavaFXImage(card.getSerializableImage());

            // See card object, only type + value + color are used
            imageMap.put(card, img);
        }
    }
}
