package model.bean;

import java.sql.Timestamp;

public class VTMatch {
    private String hostWhiteName;
    private String hostBlackName;
    private String oppWhiteName;
    private String oppBlackName;
    private int winner;
    private Timestamp startTime;
    private Timestamp endTime;

    public VTMatch() { }

    public VTMatch(String hostWhiteName, String hostBlackName, String oppWhiteName, String oppBlackName, int winner, Timestamp startTime, Timestamp endTime) {
        this.hostWhiteName = hostWhiteName;
        this.hostBlackName = hostBlackName;
        this.oppWhiteName = oppWhiteName;
        this.oppBlackName = oppBlackName;
        this.winner = winner;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getHostWhiteName() {
        return hostWhiteName;
    }

    public void setHostWhiteName(String hostWhiteName) {
        this.hostWhiteName = hostWhiteName;
    }

    public String getHostBlackName() {
        return hostBlackName;
    }

    public void setHostBlackName(String hostBlackName) {
        this.hostBlackName = hostBlackName;
    }

    public String getOppWhiteName() {
        return oppWhiteName;
    }

    public void setOppWhiteName(String oppWhiteName) {
        this.oppWhiteName = oppWhiteName;
    }

    public String getOppBlackName() {
        return oppBlackName;
    }

    public void setOppBlackName(String oppBlackName) {
        this.oppBlackName = oppBlackName;
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
        return "VTMatch{" +
                "hostWhiteName='" + hostWhiteName + '\'' +
                ", hostBlackName='" + hostBlackName + '\'' +
                ", oppWhiteName='" + oppWhiteName + '\'' +
                ", oppBlackName='" + oppBlackName + '\'' +
                ", winner=" + winner +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
