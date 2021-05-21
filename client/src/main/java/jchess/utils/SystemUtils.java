package jchess.utils;

import jchess.controller.connection.RESTLobbyController;
import jchess.controller.connection.RESTTandemController;
import jchess.model.*;

import javax.websocket.Session;
import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SystemUtils {
    private Player user;
    private Player opponent;
    private String currentLobby;
    private List<Lobby> lobbies;
    private List<TLobby> tandems;
    private List<Player> onlineUsers;
    private List<VMatch> vMatches;
    private List<VTMatch> vTandems;
    private String socketId;
    private boolean isWhite = true;
    private int matchDuration = 0;
    private static SystemUtils single_instance = null;
    public Properties appProps;
    public Session session;
    private boolean isPlayerReady = false;
    private boolean isOpponentReady = false;
    private TLobby currentTandem;
    private Timestamp matchStartTime;
    private Timestamp matchEndTime;

    private SystemUtils() {
        onlineUsers = new ArrayList<>();
        lobbies = new ArrayList<>();
        tandems = new ArrayList<>();
        vMatches = new ArrayList<>();

        try {
            InputStream path = getClass().getClassLoader().getResourceAsStream("jchess/properties/application.properties");
            appProps = new Properties();
            appProps.load(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SystemUtils getInstance() {
        if (single_instance == null) {
            single_instance = new SystemUtils();
        }
        return single_instance;
    }

    public void setSocketId(String socketId) {
        this.socketId = socketId;
    }

    public Player getUser() {
        return user;
    }

    public void setUser(Player user) {
        this.user = user;
    }

    public Player getOpponent() {
        return opponent;
    }

    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    public List<Player> getOnlineUsers() {
        return onlineUsers;
    }

    public void setOnlineUsers(List<Player> players) {
        onlineUsers = players;
    }

    public String getCurrentLobby() {
        return currentLobby;
    }

    public void setCurrentLobby(String currentLobby) {
        this.currentLobby = currentLobby;
    }

    public List<Lobby> getLobbies() {
        return lobbies;
    }

    public void setLobbies(List<Lobby> lobbies) {
        this.lobbies = lobbies;
    }

    public void clearLobbyData() {
        if (currentTandem == null) {
            if (isWhite) {
                try {
                    RESTLobbyController.leaveLobby(currentLobby);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (currentTandem.getHost().getId() == user.getId()) {
                try {
                    RESTTandemController.leaveTandem(currentTandem.getId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        currentLobby = null;
        currentTandem = null;
        isOpponentReady = false;
        isPlayerReady = false;
        matchStartTime = null;
        matchEndTime = null;
        matchDuration = 0;
        opponent = null;
    }

    public void setMatches(List<VMatch> VMatches) {
        this.vMatches = VMatches;
    }

    public List<VMatch> getMatches() {
        return vMatches;
    }

    public String getSocketId() {
        return socketId;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public void setWhite(boolean white) {
        isWhite = white;
    }

    public boolean isPlayerReady() {
        return isPlayerReady;
    }

    public void setPlayerReady(boolean playerReady) {
        isPlayerReady = playerReady;
    }

    public boolean isOpponentReady() {
        return isOpponentReady;
    }

    public void setOpponentReady(boolean opponentReady) {
        isOpponentReady = opponentReady;
    }

    public int getMatchDuration() {
        return matchDuration;
    }

    public void setMatchDuration(int matchDuration) {
        this.matchDuration = matchDuration;
    }

    public Timestamp getMatchStartTime() {
        return matchStartTime;
    }

    public void setMatchStartTime(Timestamp matchStartTime) {
        this.matchStartTime = matchStartTime;
    }

    public Timestamp getMatchEndTime() {
        return matchEndTime;
    }

    public void setMatchEndTime(Timestamp matchEndTime) {
        this.matchEndTime = matchEndTime;
    }

    public TLobby getCurrentTandem() {
        return currentTandem;
    }

    public void setCurrentTandem(TLobby currentTandem) {
        this.currentTandem = currentTandem;
    }

    public List<TLobby> getTandems() {
        return tandems;
    }

    public void setTandems(List<TLobby> tandems) {
        this.tandems = tandems;
    }

    public List<VTMatch> getVTandems() {
        return vTandems;
    }

    public void setVTandems(List<VTMatch> vTandems) {
        this.vTandems = vTandems;
    }
}
