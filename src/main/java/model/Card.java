package model;


import javafx.scene.image.Image;

import java.io.Serializable;

public class Card implements Serializable {

    private transient Image image;
    private CardType cardType;
    private CardColor color;
    private Integer value;
    private boolean hasFetchedCards;

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

    public void setColor(CardColor color) {
        this.color = color;
    }

    public Integer getValue() {
        return value;
    }

    public boolean isHasFetchedCards() {
        return hasFetchedCards;
    }

    public void setHasFetchedCards(boolean hasFetchedCards) {
        this.hasFetchedCards = hasFetchedCards;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Card card = (Card) o;

        if (cardType != card.cardType) return false;
        if (color != card.color) return false;
        return value != null ? value.equals(card.value) : card.value == null;
    }

    @Override
    public int hashCode() {
        int result = cardType != null ? cardType.hashCode() : 0;
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return cardType + "_" + color + ": " + value;
    }
}
