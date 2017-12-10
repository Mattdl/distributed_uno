package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Used for account purposes.
 */
@DatabaseTable(tableName = "User")
public class User {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String hash;

    @DatabaseField(canBeNull = false)
    private String salt;

    @DatabaseField(foreign = true)
    private Player player;

    @DatabaseField
    private String token;

    @DatabaseField
    private Date tokenExpTime;

    public User() {
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
}
