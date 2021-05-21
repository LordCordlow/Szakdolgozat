package model.bean;

import com.google.gson.annotations.JsonAdapter;

@JsonAdapter(PlayerAdapter.class)
public class Player {
    private int id;
    private String username;
    private String password;

    public Player() { }

    public Player(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public Player(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Player(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public Player(Player other) {
        this.username = other.username;
        this.password = other.password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
