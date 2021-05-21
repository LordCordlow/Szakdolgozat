package model.bean;

import java.util.ArrayList;
import java.util.List;

public class TLobby extends Lobby {
    private List<Player> hostTeam;
    private List<Player> oppTeam;

    public TLobby(Player host, String hostSocketId) {
        super(host, hostSocketId);
        this.hostTeam = new ArrayList<>();
        this.oppTeam = new ArrayList<>();
        this.hostTeam.add(this.host);
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

    public List<Player> getHostTeam() {
        return hostTeam;
    }

    public List<Player> getOppTeam() {
        return oppTeam;
    }

    @Override
    public String toString() {
        return "TLobby{" +
                "host=" + host +
                ", hostSocketId='" + hostSocketId + '\'' +
                ", hostTeam=" + hostTeam +
                ", oppTeam=" + oppTeam +
                '}';
    }
}
