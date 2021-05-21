package jchess.view.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import jchess.controller.connection.RESTMatchesController;
import jchess.controller.connection.RESTTMatchesController;
import jchess.model.VMatch;
import jchess.model.VTMatch;
import jchess.utils.SystemUtils;
import jchess.utils.WindowUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {
    @FXML Label nameLabel;
    @FXML Label matchesLabel;
    @FXML Label winsLabel;
    @FXML Label losesLabel;
    @FXML Label drawsLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameLabel.setText("Name: " + SystemUtils.getInstance().getUser().getUsername());
        try {
            setStats();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setStats() throws IOException {
        RESTMatchesController.getMatchesByPlayerId();
        RESTTMatchesController.getMatchesByPlayerId();

        matchesLabel.setText("Matches: " + SystemUtils.getInstance().getMatches().size());
        int wins = 0, loses = 0, draws = 0;
        for (VMatch m : SystemUtils.getInstance().getMatches()) {
            switch (m.getWinner()) {
                case 0: draws++; break;
                case 1: wins++; break;
                case 2: loses++; break;
            }
        }

        for (VTMatch m : SystemUtils.getInstance().getVTandems()) {
            switch (m.getWinner()) {
                case 1: wins++; break;
                case 2: loses++; break;
            }
        }
        winsLabel.setText("Wins: " + wins);
        losesLabel.setText("Loses: " + loses);
        drawsLabel.setText("Draws: " + draws);
    }

    public void onOpenMatches(ActionEvent actionEvent) {
        WindowUtils.getInstance().switchScreen("matches", winsLabel, null, 800, 500);
    }

    public void onBack(ActionEvent actionEvent) {
        WindowUtils.getInstance().switchScreen("lobby", nameLabel, "lobby", 800, 500);
    }
}
