package jchess.view.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import jchess.controller.connection.RESTLobbyController;
import jchess.controller.connection.RESTPlayersController;
import jchess.controller.connection.RESTTandemController;
import jchess.controller.connection.SocketController;
import jchess.model.Player;
import jchess.utils.AlertUtils;
import jchess.utils.SystemUtils;
import jchess.utils.WindowUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MatchMakingController implements Initializable {
    @FXML public Label playerLabel;
    @FXML public Label opponentLabel;
    @FXML public Button startBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playerLabel.setText(SystemUtils.getInstance().getUser().getUsername());
        Player opponent = SystemUtils.getInstance().getOpponent();
        if (opponent != null) {
            opponentLabel.setText(opponent.getUsername());
        } else {
            opponentLabel.setText("...");
        }
    }

    public void onBack(ActionEvent actionEvent) throws IOException {
        int lobbyStatusCode = RESTLobbyController.refreshLobby(SystemUtils.getInstance().getCurrentLobby());
        if (lobbyStatusCode == 404) {
            WindowUtils.getInstance().switchScreen("lobby", playerLabel, null, 800, 500);
            return;
        }
        int statusCode = RESTLobbyController.leaveLobby(SystemUtils.getInstance().getCurrentLobby());
        if (statusCode == 200) {
            SystemUtils.getInstance().setCurrentLobby(null);
            WindowUtils.getInstance().switchScreen("lobby", playerLabel, null, 800, 500);
        }
    }

    public void onRefresh(ActionEvent actionEvent) throws IOException {
        int statusCode = RESTLobbyController.refreshLobby(SystemUtils.getInstance().getCurrentLobby());
        Player opponent = SystemUtils.getInstance().getOpponent();
        if (statusCode == 200) {
            if (opponent != null) {
                opponentLabel.setText(SystemUtils.getInstance().getOpponent().getUsername());
            } else {
                opponentLabel.setText("...");
            }
        } else if (statusCode == 404) {
            AlertUtils.showAlert("Lobby no longer available!");
            SystemUtils.getInstance().setCurrentLobby(null);
            RESTLobbyController.getLobbies();
            RESTTandemController.getTandems();
            RESTPlayersController.getOnlinePlayers();
            WindowUtils.getInstance().switchScreen("lobby", playerLabel, null, 800, 500);
        }
    }


    public void onStart(ActionEvent actionEvent) throws IOException {
        int statusCode = RESTLobbyController.refreshLobby(SystemUtils.getInstance().getCurrentLobby());
        Player opponent = SystemUtils.getInstance().getOpponent();
        if (statusCode == 200 && opponent != null) {
            // start match
            int matchDuration = 0;
            if (SystemUtils.getInstance().isWhite()) {
                matchDuration = AlertUtils.showTimeAlert();
            }
            SocketController.getInstance().sendReady(matchDuration);
        } else if (statusCode == 404) {
            AlertUtils.showAlert("Lobby no longer available!");
            SystemUtils.getInstance().setCurrentLobby(null);
            RESTLobbyController.getLobbies();
            RESTPlayersController.getOnlinePlayers();
            WindowUtils.getInstance().switchScreen("lobby", playerLabel, null, 800, 500);
        }
    }

    public void onStartLocal(ActionEvent actionEvent) {
        int matchDuration = AlertUtils.showTimeAlert();
        WindowUtils.getInstance().switchScreen("chess", playerLabel, null, 600, 700);
    }
}
