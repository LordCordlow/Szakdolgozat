package model.bean;

import java.sql.Timestamp;

public class Match {
    private int id;
    private int whitePlayerId;
    private int blackPlayerId;
    private int winner;
    private Timestamp startTime;
    private Timestamp endTime;

    public Match() { }

    public Match(int id, int whitePlayerId, int blackPlayerId, int winner, Timestamp startTime, Timestamp endTime) {
        this.id = id;
        this.whitePlayerId = whitePlayerId;
        this.blackPlayerId = blackPlayerId;
        this.winner = winner;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Match(int whitePlayerId, int blackPlayerId, int winner, Timestamp startTime, Timestamp endTime) {
        this.whitePlayerId = whitePlayerId;
        this.blackPlayerId = blackPlayerId;
        this.winner = winner;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWhitePlayerId() {
        return whitePlayerId;
    }

    public void setWhitePlayerId(int whitePlayerId) {
        this.whitePlayerId = whitePlayerId;
    }

    public int getBlackPlayerId() {
        return blackPlayerId;
    }

    public void setBlackPlayerId(int blackPlayerId) {
        this.blackPlayerId = blackPlayerId;
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", whitePlayerId=" + whitePlayerId +
                ", blackPlayerId=" + blackPlayerId +
                ", winner=" + winner +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
