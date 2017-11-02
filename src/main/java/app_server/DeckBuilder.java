package app_server;

import dispatcher.Dispatcher;
import model.Card;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class DeckBuilder {

    private static final Logger LOGGER = Logger.getLogger(DeckBuilder.class.getName() );

    /**
     * Generates a shuffled deck containing each colored card twice and each special uncolored card 4 times
     * @return Deck
     */
    public static List<Card> makeDeck(){
        List<Card> deck = new ArrayList<>();

        //Generate each colored card twice
        for(int i = 0; i < 2; i++) {
            for (Card.CardColor color : Card.CardColor.values()) {
                //Generate all numbered cards
                for (Number number : Number.values()) {
                    BufferedImage img = null;
                    try {
                        img = ImageIO.read(new File("textures/" + number + "_" + color + ".png"));
                    } catch (IOException e) {
                        LOGGER.info("Error loading card image");
                    }
                    //deck.add(new Card(img, Card.CardType.NORMAL, color, number.getValue()));
                }
                //Generate each special colored card
                for (Card.CardType cardType : Card.CardType.values()) {
                    //All normal cards already generated above
                    if (cardType == Card.CardType.NORMAL)
                        continue;

                    BufferedImage img = null;

                    if(i==0 && (cardType == Card.CardType.PLUS4 || cardType == Card.CardType.PICK_COLOR)){
                        //Make each special uncolored card four times, once for each color only in first iteration
                        try {
                            img = ImageIO.read(new File("textures/" + cardType + ".png"));
                        } catch (IOException e) {
                            LOGGER.info("Error loading card image");
                        }

                        //deck.add(new Card(img, cardType, null, null));
                    }

                    try {
                        img = ImageIO.read(new File("textures/" + cardType + "_" + color + ".png"));
                    } catch (IOException e) {
                        LOGGER.info("Error loading card image");
                    }
                    //deck.add(new Card(img, cardType, color, null));
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
