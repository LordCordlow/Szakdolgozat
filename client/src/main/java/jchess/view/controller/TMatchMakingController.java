package jchess.view.controller;

import javafx.application.Platform;
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
import jchess.model.TLobby;
import jchess.utils.AlertUtils;
import jchess.utils.SystemUtils;
import jchess.utils.WindowUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TMatchMakingController implements Initializable {
    @FXML
    Label player1Label;
    @FXML
    Label player2Label;
    @FXML
    Label player3Label;
    @FXML
    Label player4Label;
    @FXML
    Button startBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setPlayers();
    }

    public void onRefresh(ActionEvent actionEvent) throws IOException {
        int statusCode = RESTTandemController.refreshTandem(SystemUtils.getInstance().getCurrentTandem().getId());
        if (statusCode == 200) {
            setPlayers();
        } else if (statusCode == 404) {
            AlertUtils.showAlert("Lobby no longer available!");
            SystemUtils.getInstance().setCurrentTandem(null);
            RESTLobbyController.getLobbies();
            RESTTandemController.getTandems();
            RESTPlayersController.getOnlinePlayers();
            WindowUtils.getInstance().switchScreen("lobby", player1Label, null, 800, 500);
        }
    }

    public void onStart(ActionEvent actionEvent) throws IOException {
        int statusCode = RESTTandemController.refreshTandem(SystemUtils.getInstance().getCurrentTandem().getId());
        if (statusCode == 200 && SystemUtils.getInstance().getCurrentTandem().isOnFull()) {
            int matchDuration = 0;
            if (SystemUtils.getInstance().getCurrentTandem().getHost().getId() == SystemUtils.getInstance().getUser().getId()) {
                matchDuration = AlertUtils.showTimeAlert();
            }
            assignOpponent();
            SocketController.getInstance().sendTandemReady(matchDuration);
        } else if (statusCode == 404) {
            AlertUtils.showAlert("Lobby no longer available!");
            SystemUtils.getInstance().setCurrentTandem(null);
            RESTLobbyController.getLobbies();
            RESTTandemController.getTandems();
            RESTPlayersController.getOnlinePlayers();
            WindowUtils.getInstance().switchScreen("lobby", player1Label, null, 800, 500);
        } else {
            AlertUtils.showAlert("Something went wrong!");
        }
    }

    public void onCancel(ActionEvent actionEvent) throws IOException {
        int lobbyStatusCode = RESTTandemController.refreshTandem(SystemUtils.getInstance().getCurrentTandem().getId());
        if (lobbyStatusCode == 404) {
            WindowUtils.getInstance().switchScreen("lobby", player1Label, null, 800, 500);
            return;
        }
        int statusCode = RESTTandemController.leaveTandem(SystemUtils.getInstance().getCurrentTandem().getId());
        if (statusCode == 200) {
            SystemUtils.getInstance().setCurrentTandem(null);
            WindowUtils.getInstance().switchScreen("lobby", player1Label, null, 800, 500);
        } else {
            AlertUtils.showAlert("Something went wrong!");
        }
    }

    private void setPlayers() {
        TLobby currentTandem = SystemUtils.getInstance().getCurrentTandem();
        player1Label.setText(currentTandem.getHost().getUsername());
        if (currentTandem.getHostTeam().size() > 1) {
            player2Label.setText(currentTandem.getHostTeam().get(1).getUsername());
        } else {
            player2Label.setText("...");
        }
        if (currentTandem.getOppTeam().size() > 0) {
            player3Label.setText(currentTandem.getOppTeam().get(0).getUsername());
        } else {
            player3Label.setText("...");
        }
        if (currentTandem.getOppTeam().size() > 1) {
            player4Label.setText(currentTandem.getOppTeam().get(1).getUsername());
        } else {
            player4Label.setText("...");
        }
    }

    private void assignOpponent() {
        TLobby current = SystemUtils.getInstance().getCurrentTandem();
        Player user = SystemUtils.getInstance().getUser();
        if (current.getHost().getId() == user.getId()) {
            SystemUtils.getInstance().setOpponent(current.getOppTeam().get(0));
            return;
        }
        if (current.getHostTeam().get(1).getId() == user.getId()) {
            SystemUtils.getInstance().setOpponent(current.getOppTeam().get(1));
            return;
        }
        if (current.getOppTeam().get(0).getId() == user.getId()) {
            SystemUtils.getInstance().setOpponent(current.getHost());
            return;
        }
        if (current.getOppTeam().get(1).getId() == user.getId()) {
            SystemUtils.getInstance().setOpponent(current.getHostTeam().get(1));
        }
    }
}
