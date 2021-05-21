package model.bean;

import java.sql.Timestamp;

public class TMatch {
    private int id;
    private int hostWhiteId;
    private int hostBlackId;
    private int oppWhiteId;
    private int oppBlackId;
    private int winner;
    private Timestamp startTime;
    private Timestamp endTime;

    public TMatch() { }

    public TMatch(int id, int hostWhiteId, int hostBlackId, int oppWhiteId, int oppBlackId, int winner, Timestamp startTime, Timestamp endTime) {
        this.id = id;
        this.hostWhiteId = hostWhiteId;
        this.hostBlackId = hostBlackId;
        this.oppWhiteId = oppWhiteId;
        this.oppBlackId = oppBlackId;
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

    public int getHostWhiteId() {
        return hostWhiteId;
    }

    public void setHostWhiteId(int hostWhiteId) {
        this.hostWhiteId = hostWhiteId;
    }

    public int getHostBlackId() {
        return hostBlackId;
    }

    public void setHostBlackId(int hostBlackId) {
        this.hostBlackId = hostBlackId;
    }

    public int getOppWhiteId() {
        return oppWhiteId;
    }

    public void setOppWhiteId(int oppWhiteId) {
        this.oppWhiteId = oppWhiteId;
    }

    public int getOppBlackId() {
        return oppBlackId;
    }

    public void setOppBlackId(int oppBlackId) {
        this.oppBlackId = oppBlackId;
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
        return "TMatch{" +
                "id=" + id +
                ", hostWhiteId=" + hostWhiteId +
                ", hostBlackId=" + hostBlackId +
                ", oppWhiteId=" + oppWhiteId +
                ", oppBlackId=" + oppBlackId +
                ", winner=" + winner +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
