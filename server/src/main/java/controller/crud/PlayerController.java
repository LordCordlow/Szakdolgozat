package controller.crud;

import model.bean.Player;

import java.util.List;

public interface PlayerController {
    List<Player> getAllPlayer();
    boolean addPlayer(Player player);
    Player getPlayerById(int id);
    Player getPlayerByUsername(String username);
    Player checkCredentials(Player player);
}
