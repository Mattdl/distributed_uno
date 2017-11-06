package app_server;

import javafx.embed.swing.SwingFXUtils;
import model.Card;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeckBuilder {

    private static final Logger LOGGER = Logger.getLogger(DeckBuilder.class.getName() );

    /**
     * Generates a shuffled deck containing each colored card twice and each special uncolored card 4 times
     * @return Deck
     */
    public LinkedList<Card> makeDeck(){
        LinkedList<Card> deck = new LinkedList<>();

        //Generate each colored card twice
        for(int i = 0; i < 2; i++) {
            for (Card.CardColor color : Card.CardColor.values()) {
                //Generate all numbered cards
                for (Number number : Number.values()) {
                   /* BufferedImage img = null;
                    try {
                        img = ImageIO.read(new File(getClass().getResource("/textures/" + number + "_" + color + ".png").toURI()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOGGER.info("Error loading card image: "+"/textures/" + number + "_" + color + ".png");
                    }
                    LOGGER.log(Level.INFO,"Creating normal card");
                    Card card = new Card(SwingFXUtils.toFXImage(img, null), Card.CardType.NORMAL, color, number.getValue());*/
                    Card card = new Card(null, Card.CardType.NORMAL, color, number.getValue());
                    LOGGER.log(Level.INFO,"Created normal card");
                    deck.add(card);


                }
                //Generate each special colored card
                for (Card.CardType cardType : Card.CardType.values()) {
                    //All normal cards already generated above
                    if (cardType == Card.CardType.NORMAL)
                        continue;

                    BufferedImage img = null;

                    if(i==0 && (cardType == Card.CardType.PLUS4 || cardType == Card.CardType.PICK_COLOR)){
                        //Make each special uncolored card four times, once for each color only in first iteration
                       /* try {
                            img = ImageIO.read(new File(getClass().getResource("/textures/" + cardType + ".png").toURI()));
                        } catch (Exception e) {
                            LOGGER.info("Error loading card image :" + "/textures/" + cardType + ".png");
                        }*/
                        LOGGER.log(Level.INFO,"Creating PLUS4/COLOR card");
                        //deck.add(new Card(SwingFXUtils.toFXImage(img, null), cardType, null, null));
                        deck.add(new Card(null, cardType, null, null));

                    }

                    /*try {
                        img = ImageIO.read(new File(getClass().getResource("/textures/" + cardType + "_" + color + ".png").toURI()));
                    } catch (Exception e) {
                        LOGGER.info("Error loading card image : "+"/textures/" + cardType + "_" + color + ".png");
                    }*/
                    LOGGER.log(Level.INFO,"Creating special card: "+cardType.toString()+", "+color.toString());
                    //deck.add(new Card(SwingFXUtils.toFXImage(img, null), cardType, color, null));
                    Card card = new Card(null, cardType, color, null);
                    LOGGER.log(Level.INFO,"Created special card");
                    deck.add(card);

                }
            }
        }

        LOGGER.log(Level.INFO, "Deck created");
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
