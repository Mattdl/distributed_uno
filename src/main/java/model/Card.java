package model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import javafx.scene.image.Image;

import java.io.Serializable;
import java.util.Objects;

@DatabaseTable
public class Card implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;

    private transient Image image;

    @DatabaseField
    private CardType cardType;

    @DatabaseField
    private CardColor color;

    @DatabaseField
    private int value; //TODO, changed from Integer to int => Bugchecking

    @DatabaseField
    private boolean hasFetchedCards;

    public Card() {
    }

    public Card(Image image, CardType cardType, CardColor color, int value) {
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
        return id == card.id &&
                value == card.value &&
                hasFetchedCards == card.hasFetchedCards &&
                Objects.equals(image, card.image) &&
                cardType == card.cardType &&
                color == card.color;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, image, cardType, color, value, hasFetchedCards);
    }

    @Override
    public String toString() {
        return cardType + "_" + color + ": " + value;
    }
}
