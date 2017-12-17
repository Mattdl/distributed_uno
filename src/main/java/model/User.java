package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

/**
 * Used for account purposes.
 */
@DatabaseTable(tableName = "User")
public class User implements Serializable{
    public static final String USERNAME_FIELD_NAME = "username";


    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, columnName = USERNAME_FIELD_NAME)
    private String username;

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

    public User(String username, String hash, String salt) {
        this.username = username;
        this.hash = hash;
        this.salt = salt;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
