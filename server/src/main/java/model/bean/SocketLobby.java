package model.bean;

import controller.system.SystemController;
import model.chess.ColoredPieceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SocketLobby {
    private String lobbyId;
    private List<Integer> playerIds;
    private Map<Integer, Boolean> readies;
    private int matchDuration = 5;
    private List<ColoredPieceType> hostTeamBank;
    private List<ColoredPieceType> oppTeamBank;

    public SocketLobby(String lobbyId, int hostId) {
        this.lobbyId = lobbyId;
        this.playerIds = new ArrayList<>();
        this.readies = new HashMap<>();
        this.hostTeamBank = new ArrayList<>();
        this.oppTeamBank = new ArrayList<>();
        this.addPlayer(hostId);
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public boolean isPlayerInLobby(int playerId) {
        for (Integer id : playerIds) {
            if (id == playerId) {
                return true;
            }
        }
        return false;
    }

    public void addPlayer(int id) {
        this.playerIds.add(id);
        this.readies.put(id, false);
    }

    public void removePlayer(int id) {
        int index = 0;
        for (Integer i : playerIds) {
            if (i == id) {
                index = playerIds.indexOf(i);
            }
        }
        playerIds.remove(index);
        this.readies.remove(id);
    }

    public void assignReady(int id) {
        for (Map.Entry item : readies.entrySet()) {
            if ((Integer) item.getKey() == id) {
                item.setValue(true);
            }
        }
    }

    public boolean isAllReady() {
        for (Map.Entry item : readies.entrySet()) {
            if (!(Boolean) item.getValue()) {
                return false;
            }
        }
        return true;
    }

    public int getMatchDuration() {
        return matchDuration;
    }

    public void setMatchDuration(int matchDuration) {
        this.matchDuration = matchDuration;
    }

    public void addPieceToBank(String pieceStr, int playerId, String teamStr) {
        ColoredPieceType pieceToBank = ColoredPieceType.valueOf(pieceStr);
        System.out.println("piece to bank: " + pieceToBank);
        if (teamStr.equals("host")) {
            hostTeamBank.add(pieceToBank);
        } else {
            oppTeamBank.add(pieceToBank);
        }
    }

    public void removePieceFromBank(ColoredPieceType piece, boolean isHostTeam) {
        if (isHostTeam) {
            hostTeamBank.removeIf(t -> t == piece);
        } else {
            oppTeamBank.removeIf(t -> t == piece);
        }
    }

    @Override
    public String toString() {
        return "SocketLobby{" +
                "lobbyId='" + lobbyId + '\'' +
                ", playerIds=" + playerIds +
                ", readies=" + readies +
                ", matchDuration=" + matchDuration +
                '}';
    }
}
