package controller.crud;

import model.bean.Player;
import model.dao.PlayerDAO;
import model.dao.PlayerDAOImpl;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class PlayerControllerImpl implements PlayerController {
    private PlayerDAO dao = new PlayerDAOImpl();
    private static PlayerController single_instance = null;

    private PlayerControllerImpl() { }

    public static PlayerController getInstance() {
        if (single_instance == null) {
            single_instance = new PlayerControllerImpl();
        }
        return single_instance;
    }

    @Override
    public List<Player> getAllPlayer() {
        return dao.getAllPlayer();
    }

    @Override
    public boolean addPlayer(Player player) {
        return dao.addPlayer(player);
    }

    @Override
    public Player getPlayerById(int id) {
        return dao.getPlayerById(id);
    }

    @Override
    public Player getPlayerByUsername(String username) {
        return dao.getPlayerByUsername(username);
    }

    @Override
    public Player checkCredentials(Player player) {
        Player playerFromDB = dao.getPlayerByUsername(player.getUsername());
        if (playerFromDB != null) {
            if (BCrypt.checkpw(player.getPassword(), playerFromDB.getPassword())) {
                return playerFromDB;
            }
        }
        return null;
    }
}
