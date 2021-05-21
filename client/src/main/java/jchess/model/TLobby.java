package jchess.model;

import java.util.ArrayList;
import java.util.List;

public class TLobby extends Lobby {
    private List<Player> hostTeam;
    private List<Player> oppTeam;

    public TLobby(String id, Player host, String hostSocketId) {
        super(id, host, hostSocketId);
        this.hostTeam = new ArrayList<>();
        this.oppTeam = new ArrayList<>();
        this.hostTeam.add(host);
    }

    public void addToTeam(Player player, boolean isHostTeam) {
        if (isHostTeam) {
            this.hostTeam.add(player);
        } else {
            this.oppTeam.add(player);
        }
    }

    public boolean removeFromTeam(Player player) {
        Player playerToRemove = null;
        for (Player p : hostTeam) {
            if (p.getId() == player.getId()) {
                playerToRemove = p;
            }
        }
        if (playerToRemove != null) {
            return hostTeam.remove(playerToRemove);
        } else {
            for (Player p : oppTeam) {
                if (p.getId() == player.getId()) {
                    playerToRemove = p;
                }
            }

            if (playerToRemove != null) {
                return oppTeam.remove(playerToRemove);
            }
        }
        return false;
    }

    public boolean isInLobby(int playerId) {
        for (Player p : hostTeam) {
            if (p.getId() == playerId) {
                return true;
            }
        }
        for (Player p : oppTeam) {
            if (p.getId() == playerId) {
                return true;
            }
        }
        return false;
    }

    public boolean isOnFull() {
        return hostTeam.size() == 2 && oppTeam.size() == 2;
    }

    public boolean isHostTeamMember(int playerId) {
        return hostTeam.get(0).getId() == playerId || hostTeam.get(1).getId() == playerId;
    }

    public List<Player> getHostTeam() {
        return hostTeam;
    }

    public List<Player> getOppTeam() {
        return oppTeam;
    }

    @Override
    public String toString() {
        return "TLobby{" +
                "id='" + id + '\'' +
                ", host=" + host +
                ", hostSocketId='" + hostSocketId + '\'' +
                ", hostTeam=" + hostTeam +
                ", oppTeam=" + oppTeam +
                '}';
    }
}
