package jchess.chess.clock;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.util.Duration;
import jchess.controller.connection.RESTMatchesController;
import jchess.controller.connection.RESTTMatchesController;
import jchess.controller.connection.SocketController;
import jchess.model.Player;
import jchess.model.TLobby;
import jchess.utils.SystemUtils;
import jchess.utils.WindowUtils;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

public class ChessClock {
    private int userSec;
    private int userMin;
    private int oppSec;
    private int oppMin;
    private boolean userTimeOver = false;
    private boolean oppTimeOver = false;

    private boolean playersTurn;

    private StringProperty userTime;
    private StringProperty oppTime;

    public ChessClock(int minutes, int seconds) {
        this.userSec = seconds;
        this.oppSec = seconds;
        this.userMin = minutes;
        this.oppMin = minutes;

        this.userTime = new SimpleStringProperty();
        this.oppTime = new SimpleStringProperty();
    }

    public void startClock() {
        timeline.setCycleCount(Timeline.INDEFINITE);
        this.playersTurn = SystemUtils.getInstance().isWhite();
        this.setUserTime(String.valueOf(userMin + ":" + userSec));
        this.setOppTime(String.valueOf(oppMin + ":" + oppSec));
        timeline.play();
    }

    public void stopClock(boolean showAlert) {
        timeline.stop();
        if (!showAlert) {
            return;
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        String headerText = playersTurn ? "You ran out of time!" : "Your opponent ran out of time!";
        String contentText = playersTurn ? "Your opponent won the game!" : "You won the game!";
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.show();
        SystemUtils.getInstance().setMatchEndTime(new Timestamp(new Date().getTime()));
        if (SystemUtils.getInstance().getCurrentTandem() == null) {
            if (SystemUtils.getInstance().isWhite() && SystemUtils.getInstance().getOpponent() != null) {
                int winner = playersTurn ? 2 : 1;
                try {
                    RESTMatchesController.addMatch(winner);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            TLobby current = SystemUtils.getInstance().getCurrentTandem();
            Player user = SystemUtils.getInstance().getUser();
            int winner = 0;
            if (playersTurn) {
                winner = current.isHostTeamMember(user.getId()) ? 2 : 1;
            } else {
                winner = current.isHostTeamMember(user.getId()) ? 1 : 2;
            }
            try {
                if (SystemUtils.getInstance().isWhite()) {
                    SocketController.getInstance().sendTandemEnd(winner);
                }
                if (current.getHost().getId() == user.getId()) {
                    RESTTMatchesController.addMatch(winner);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        SystemUtils.getInstance().clearLobbyData();
        WindowUtils.getInstance().switchScreen("lobby", "lobby", 800, 500);
    }

    public void switchTurn() {
        playersTurn = !playersTurn;
    }

    private final Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            if (playersTurn) {
                if (userSec > 0) {
                    userSec--;
                    setUserTime(String.valueOf(userMin + ":" + userSec));
                } else if (userSec == 0 && userMin > 0) {
                    userMin--;
                    userSec = 59;
                    setUserTime(String.valueOf(userMin + ":" + userSec));
                } else if (userSec == 0 && userMin == 0) {
                    setUserTime(String.valueOf(userMin + ":" + userSec));
                    userTimeOver = true;
                    // stop clock
                    stopClock(true);
                    return;
                }
            } else {
                if (oppSec > 0) {
                    oppSec--;
                    setOppTime(String.valueOf(oppMin + ":" + oppSec));
                } else if (oppSec == 0 && oppMin > 0) {
                    oppMin--;
                    oppSec = 59;
                    setOppTime(String.valueOf(oppMin + ":" + oppSec));
                } else if (oppSec == 0 && oppMin == 0) {
                    setOppTime(String.valueOf(oppMin + ":" + oppSec));
                    oppTimeOver = true;
                    // stop clock
                    stopClock(true);
                    return;
                }
            }
        }
    }));

    public String getUserTime() {
        return userTime.get();
    }

    public StringProperty userTimeProperty() {
        return userTime;
    }

    public void setUserTime(String userTime) {
        this.userTime.set(userTime);
    }

    public String getOppTime() {
        return oppTime.get();
    }

    public StringProperty oppTimeProperty() {
        return oppTime;
    }

    public void setOppTime(String oppTime) {
        this.oppTime.set(oppTime);
    }
}
