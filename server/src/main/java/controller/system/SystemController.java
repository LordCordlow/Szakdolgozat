package controller.system;

import model.bean.Lobby;
import model.bean.Player;
import model.bean.TLobby;

import java.util.*;
import java.util.logging.Logger;

public class SystemController {
    private HashMap<String, Player> onlinePlayers;
    private List<Lobby> lobbies;
    private List<TLobby> tandemLobbies;
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static SystemController single_instance;

    private SystemController() {
        onlinePlayers = new HashMap<>();
        lobbies = new ArrayList<>();
        tandemLobbies = new ArrayList<>();
    }

    public static SystemController getInstance() {
        if (single_instance == null) {
            single_instance = new SystemController();
        }
        return single_instance;
    }

    public void addOnlinePlayer(String socketId, Player player) {
        onlinePlayers.put(socketId, player);
    }

    public boolean logoutPlayer(int playerId) {
        String socketId = null;
        for (Map.Entry item : onlinePlayers.entrySet()) {
            Player current = (Player) item.getValue();
            if (current.getId() == playerId) {
                logger.info(current.getUsername() + " disconnected from server");
                socketId = (String) item.getKey();
            }
        }
        if (socketId != null) {
            onlinePlayers.remove(socketId);
            return true;
        }
        return false;
    }

    public List<Player> getOnlinePlayers() {
        List<Player> players = new ArrayList<>();
        for (Map.Entry item : onlinePlayers.entrySet()) {
            players.add((Player) item.getValue());
        }
        return players;
    }

    public boolean isOnline(int playerId) {
        for (Player p : getOnlinePlayers()) {
            if (p.getId() == playerId) {
                return true;
            }
        }
        return false;
    }

    public List<Lobby> getLobbies() {
        return lobbies;
    }

    public List<TLobby> getTandems() {
        return tandemLobbies;
    }

    public List<Lobby> getOpenLobbies() {
        List<Lobby> result = new ArrayList<>();
        for (Lobby l : lobbies) {
            if (l.getGuest() == null) {
                result.add(l);
            }
        }
        return result;
    }

    public List<TLobby> getOpenTandems() {
        List<TLobby> result = new ArrayList<>();
        for (TLobby l : tandemLobbies) {
            if (l.getHostTeam().size() < 2 || l.getOppTeam().size() < 2) {
                result.add(l);
            }
        }
        return result;
    }

    public String createLobby(Player host) {
        String socketId = null;
        for (Map.Entry item : onlinePlayers.entrySet()) {
            Player p = (Player) item.getValue();
            if (p.getId() == host.getId()) {
                socketId = (String) item.getKey();
            }
        }

        Lobby lobby = null;
        if (socketId != null) {
            lobby = new Lobby(host, socketId);
        }

        if (lobby != null) {
            logger.info("lobby created by: " + lobby.getHost().getUsername());
            lobbies.add(lobby);
            return lobby.getId();
        }
        return null;
    }

    public String createTandem(Player host) {
        String socketId = null;
        for (Map.Entry item : onlinePlayers.entrySet()) {
            Player p = (Player) item.getValue();
            if (p.getId() == host.getId()) {
                socketId = (String) item.getKey();
            }
        }
        TLobby tandem = null;
        if (socketId != null) {
            tandem = new TLobby(host, socketId);
        }

        if (tandem != null) {
            logger.info("tandem lobby created by: " + tandem.getHost().getUsername());
            tandemLobbies.add(tandem);
            return tandem.getId();
        }
        return null;
    }

    public Lobby joinToLobby(Player player, String lobbyId) {
        Player playerToJoin = null;
        for (Player p : getOnlinePlayers()) {
            if (p.getId() == player.getId()) {
                playerToJoin = p;
            }
        }
        if (playerToJoin != null) {
            for (Lobby l : lobbies) {
                if (l.getId().equals(lobbyId)) {
                    l.setGuest(playerToJoin);
                    for (Map.Entry item : onlinePlayers.entrySet()) {
                        Player p = (Player) item.getValue();
                        if (playerToJoin.getId() == p.getId()) {
                            l.setGuestSocketId((String) item.getKey());
                            logger.info(playerToJoin.getUsername()
                            + " joined to lobby hosted by: " + l.getHost().getUsername());
                            return l;
                        }
                    }
                }
            }
        }
        return null;
    }

    public TLobby joinToTandem(Player player, String lobbyId) {
        Player playerToJoin = null;
        for (Player p : getOnlinePlayers()) {
            if (p.getId() == player.getId()) {
                playerToJoin = p;
            }
        }
        if (playerToJoin != null) {
            for (TLobby l : tandemLobbies) {
                if (l.getId().equals(lobbyId)) {
                    boolean isAdded = false;
                    if (l.getHostTeam().size() < 2) {
                        l.addToTeam(playerToJoin, true);
                        isAdded = true;
                    } else if (l.getOppTeam().size() < 2) {
                        l.addToTeam(playerToJoin, false);
                        isAdded = true;
                    }

                    if (isAdded) {
                        for (Map.Entry item : onlinePlayers.entrySet()) {
                            Player p = (Player) item.getValue();
                            if (playerToJoin.getId() == p.getId()) {
                                l.setGuestSocketId((String) item.getKey());
                                logger.info(playerToJoin.getUsername()
                                        + " joined to tandem lobby hosted by: " + l.getHost().getUsername());
                                return l;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public Lobby getLobbyById(String lobbyId) {
        for (Lobby l : lobbies) {
            if (l.getId().equals(lobbyId)) {
                return l;
            }
        }
        return null;
    }

    public TLobby getTandemById(String lobbyId) {
        for (TLobby l : tandemLobbies) {
            if (l.getId().equals(lobbyId)) {
                return l;
            }
        }
        return null;
    }

    public boolean deleteLobby(Lobby lobby) {
        boolean remove = lobbies.remove(lobby);
        if (remove) {
            logger.info("lobby hosted by "
            + lobby.getHost().getUsername() + " deleted");
        }
        return remove;
    }

    public boolean deleteTandem(TLobby tandem) {
        boolean remove = tandemLobbies.remove(tandem);
        if (remove) {
            logger.info("tandem lobby hosted by "
                    + tandem.getHost().getUsername() + " deleted");
        }
        return remove;
    }

    public boolean isInLobby(int playerId) {
        for (Lobby l : lobbies) {
            Player host = l.getHost();
            Player guest = l.getGuest();
            if (l.getHost().getId() == playerId) {
                return true;
            }
            if (guest != null && guest.getId() == playerId) {
                return true;
            }
        }
        return false;
    }

    public boolean isInTandem(int playerId) {
        for (TLobby l : tandemLobbies) {
            if (l.isInLobby(playerId)) {
                return true;
            }
        }
        return false;
    }

    public boolean isGuestEmpty(String lobbyId) {
        for (Lobby l : lobbies) {
            if (l.getId().equals(lobbyId) && l.getGuest() == null) {
                return true;
            }
        }
        return false;
    }

    public boolean isTandemOpen(String id) {
        for (TLobby l : tandemLobbies) {
            if (l.getId().equals(id)) {
                if (l.getHostTeam().size() < 2 || l.getOppTeam().size() < 2) {
                    return true;
                }
            }
        }
        return false;
    }
}
