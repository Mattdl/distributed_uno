package model;


import javafx.scene.image.Image;

import java.io.Serializable;

public class Card implements Serializable {
    private Image image;
    private CardType cardType;
    private CardColor color;
    private Integer value;

    public Card(Image image, CardType cardType, CardColor color, Integer value) {
        this.image = image;
        this.cardType = cardType;
        this.color = color;
        this.value = value;
    }

    public Image getImage() {
        return image;
    }

    public CardType getCardType() {
        return cardType;
    }

    public CardColor getColor() {
        return color;
    }

    public int getValue() {
        return value;
    }

    /**
     * Enum used to specify the color of the card
     */
    public enum CardColor{
        BLUE,
        RED,
        YELLOW,
        GREEN
    }

    /**
     * Enum used to specify the functionality of the card
     */
    public enum CardType{
        NORMAL,
        PLUS2,
        PLUS4,
        SKIP,
        REVERSE,
        PICK_COLOR
    }
}
