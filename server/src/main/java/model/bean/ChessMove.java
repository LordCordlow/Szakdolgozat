package model.bean;

import model.chess.TileType;

public class ChessMove {
    private TileType from;
    private TileType to;
    private int playerId;

    public ChessMove(TileType from, TileType to, int playerId) {
        this.from = from;
        this.to = to;
        this.playerId = playerId;
    }

    public TileType getFrom() {
        return from;
    }

    public void setFrom(TileType from) {
        this.from = from;
    }

    public TileType getTo() {
        return to;
    }

    public void setTo(TileType to) {
        this.to = to;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    @Override
    public String toString() {
        return "ChessMove{" +
                "from=" + from +
                ", to=" + to +
                ", playerId=" + playerId +
                '}';
    }
}
