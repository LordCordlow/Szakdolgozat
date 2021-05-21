package jchess.model;

public class SocketMessage {
    private String socketId;
    private Player player;
    private String messageType;
    private ChessMove move;

    public SocketMessage() { }

    public SocketMessage(String socketId, Player player, String messageType, ChessMove move) {
        this.socketId = socketId;
        this.player = player;
        this.messageType = messageType;
        this.move = move;
    }

    public String getSocketId() {
        return socketId;
    }

    public void setSocketId(String socketId) {
        this.socketId = socketId;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public ChessMove getMove() {
        return move;
    }

    public void setMove(ChessMove move) {
        this.move = move;
    }

    @Override
    public String toString() {
        return "SocketMessage{" +
                "socketId='" + socketId + '\'' +
                ", player=" + player +
                ", messageType='" + messageType + '\'' +
                ", move=" + move +
                '}';
    }
}
