package model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import javafx.scene.image.Image;

import java.io.Serializable;
import java.util.Objects;

@DatabaseTable
public class Card implements Serializable {

    @DatabaseField(generatedId = true, canBeNull = false)
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

    private byte[] serializableImage;

    // ORMLITE: Returning fields for foreign keys
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Player player;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Game game;


    public Card() {
    }

    public Card(Image image, CardType cardType, CardColor color, int value) {
        this.image = image;
        this.cardType = cardType;
        this.color = color;
        this.value = value;
    }

    public Card(byte[] image, CardType cardType, CardColor color, int value) {
        this.serializableImage = image;
        this.cardType = cardType;
        this.color = color;
        this.value = value;
    }

    public Card(CardType cardType, Integer value) {
        this.cardType = cardType;
        this.value = value;
    }

    public Card(CardType cardType, CardColor color, int value) {
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

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public byte[] getSerializableImage() {
        return serializableImage;
    }

    public void setSerializableImage(byte[] serializableImage) {
        this.serializableImage = serializableImage;
    }

    /**
     * Enum used to specify the color of the card
     */
    public enum CardColor {
        BLUE,
        RED,
        YELLOW,
        GREEN
    }

    /**
     * Enum used to specify the functionality of the card
     */
    public enum CardType {
        NORMAL,
        PLUS2,
        PLUS4,
        SKIP,
        REVERSE,
        PICK_COLOR,
        BACK // For background img of the cards
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return value == card.value &&
                cardType == card.cardType &&
                color == card.color;
    }

    @Override
    public int hashCode() {

        return Objects.hash(cardType, color, value);
    }

    @Override
    public String toString() {
        return cardType + "_" + color + ": " + value;
    }
}
