package model;

public class Player {
    private String name;
    private String password;
    //private String token;


    public Player(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
