package jchess.model;

import javafx.beans.property.StringProperty;

import java.sql.Timestamp;

public class VMatch {
    private String whitePlayerName;
    private String blackPlayerName;
    private int winner;
    private Timestamp startTime;
    private Timestamp endTime;

    public VMatch(String whitePlayerName, String blackPlayerName, int winner, Timestamp startTime, Timestamp endTime) {
        this.whitePlayerName = whitePlayerName;
        this.blackPlayerName = blackPlayerName;
        this.winner = winner;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public VMatch() { }

    public String getWhitePlayerName() {
        return whitePlayerName;
    }

    public void setWhitePlayerName(String whitePlayerName) {
        this.whitePlayerName = whitePlayerName;
    }

    public String getBlackPlayerName() {
        return blackPlayerName;
    }

    public void setBlackPlayerName(String blackPlayerName) {
        this.blackPlayerName = blackPlayerName;
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
        return "VMatch{" +
                "whitePlayerName='" + whitePlayerName + '\'' +
                ", blackPlayerName='" + blackPlayerName + '\'' +
                ", winner=" + winner +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
