package model.dao;

import model.bean.Player;

import java.util.List;

public interface PlayerDAO {
    boolean addPlayer(Player player);
    List<Player> getAllPlayer();
    Player getPlayerById(int id);
    Player getPlayerByUsername(String username);
    Player getPlayerByCredentials(String username, String password);
}
