package app_server;

import dispatcher.Dispatcher;
import javafx.scene.image.Image;
import model.Card;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class DeckBuilder {

    private static final Logger LOGGER = Logger.getLogger(DeckBuilder.class.getName() );

    /**
     * Generates a shuffled deck containing each colored card twice and each special uncolored card 4 times
     * @return Deck
     */
    public static LinkedList<Card> makeDeck(){
        LinkedList<Card> deck = new LinkedList<>();

        //Generate each colored card twice
        for(int i = 0; i < 2; i++) {
            for (Card.CardColor color : Card.CardColor.values()) {
                //Generate all numbered cards
                for (Number number : Number.values()) {
                    Image img = new Image("file:textures/" + number + "_" + color + ".png");
                    /*try {
                        img = new Image("/textures/" + number + "_" + color + ".png");
                    } catch (IOException e) {
                        LOGGER.info("Error loading card image");
                    }*/
                    deck.add(new Card(img, Card.CardType.NORMAL, color, number.getValue()));
                }
                //Generate each special colored card
                for (Card.CardType cardType : Card.CardType.values()) {
                    //All normal cards already generated above
                    if (cardType == Card.CardType.NORMAL)
                        continue;

                    if(i==0 && (cardType == Card.CardType.PLUS4 || cardType == Card.CardType.PICK_COLOR)){
                        //Make each special uncolored card four times, once for each color only in first iteration
                        Image img = new Image("file:textures/" + cardType + ".png");
                        deck.add(new Card(img, cardType, null, null));
                    }

                        Image img = new Image("file:textures/" + cardType + "_" + color + ".png");
                        deck.add(new Card(img, cardType, color, null));
                }
            }
        }


        Collections.shuffle(deck);
        return deck;
    }


    public enum Number{
        ZERO(0), ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9);

        private final int value;

        Number(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
