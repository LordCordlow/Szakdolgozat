package jchess.model;

public class Lobby {
    protected String id;
    protected Player host;
    protected String hostSocketId;
    private Player guest;
    private String guestSocketId;

    public Lobby(String id, Player host, String hostSocketId, Player guest, String guestSocketId) {
        this.id = id;
        this.host = host;
        this.hostSocketId = hostSocketId;
        this.guest = guest;
        this.guestSocketId = guestSocketId;
    }

    public Lobby(String id, Player host, String hostSocketId) {
        this.id = id;
        this.host = host;
        this.hostSocketId = hostSocketId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Player getHost() {
        return host;
    }

    public void setHost(Player host) {
        this.host = host;
    }

    public String getHostSocketId() {
        return hostSocketId;
    }

    public void setHostSocketId(String hostSocketId) {
        this.hostSocketId = hostSocketId;
    }

    public Player getGuest() {
        return guest;
    }

    public void setGuest(Player guest) {
        this.guest = guest;
    }

    public String getGuestSocketId() {
        return guestSocketId;
    }

    public void setGuestSocketId(String guestSocketId) {
        this.guestSocketId = guestSocketId;
    }

    @Override
    public String toString() {
        return "Lobby{" +
                "id='" + id + '\'' +
                ", host=" + host +
                ", hostSocketId='" + hostSocketId + '\'' +
                ", guest=" + guest +
                ", guestSocketId='" + guestSocketId + '\'' +
                '}';
    }
}
