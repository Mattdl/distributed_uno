package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

/**
 * Used for account purposes.
 */
@DatabaseTable(tableName = "User")
public class User implements Serializable {
    public static final String PLAYERNAME_FIELD_NAME = "username";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String hash;

    @DatabaseField(canBeNull = false)
    private String salt;

    @DatabaseField(foreign = true, columnName = PLAYERNAME_FIELD_NAME)
    private Player player;

    @DatabaseField
    private String token;

    @DatabaseField
    private Date tokenExpTime;

    @DatabaseField
    private int highscore;

    public User() {
    }

    public User(String username, String hash, String salt) {
        this.hash = hash;
        this.salt = salt;
        this.player = new Player(username);
        highscore = 0;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", hash='" + hash + '\'' +
                ", salt='" + salt + '\'' +
                ", player=" + player +
                ", token='" + token + '\'' +
                ", tokenExpTime=" + tokenExpTime +
                '}';
    }

    public int getHighscore() {
        return highscore;
    }

    public void setHighscore(int highscore) {
        this.highscore = highscore;
    }

    public void addScore(int amount){
        this.highscore += amount;
    }
}
