package jchess.view.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import jchess.model.VMatch;
import jchess.model.VTMatch;
import jchess.utils.SystemUtils;
import jchess.utils.WindowUtils;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MatchesController implements Initializable {
    // normal matches
    @FXML TableView<VMatch> matchesTable;
    @FXML TableColumn<VMatch, String> whitePlayerCol;
    @FXML TableColumn<VMatch, String> blackPlayerCol;
    @FXML TableColumn<VMatch, Integer> winnerCol;
    @FXML TableColumn<VMatch, String> startCol;
    @FXML TableColumn<VMatch, String> endCol;

    // tandem matches
    @FXML TableView<VTMatch> tandemsTable;
    @FXML TableColumn<VTMatch, String> hostTeamCol;
    @FXML TableColumn<VTMatch, String> oppTeamCol;
    @FXML TableColumn<VTMatch, Integer> tandemWinnerCol;
    @FXML TableColumn<VTMatch, String> tandemStartCol;
    @FXML TableColumn<VTMatch, String> tandemEndCol;

    @FXML Button switchBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<VMatch> vMatches = SystemUtils.getInstance().getMatches();
        List<VTMatch> vTandems = SystemUtils.getInstance().getVTandems();

        matchesTable.setItems(FXCollections.observableList(vMatches));
        matchesTable.setEditable(false);

        whitePlayerCol.setCellValueFactory(new PropertyValueFactory<>("whitePlayerName"));
        blackPlayerCol.setCellValueFactory(new PropertyValueFactory<>("blackPlayerName"));
        winnerCol.setCellValueFactory(new PropertyValueFactory<>("winner"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));

        tandemsTable.setItems(FXCollections.observableList(vTandems));
        tandemsTable.setEditable(false);

        hostTeamCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHostWhiteName() + ", " + c.getValue().getHostBlackName()));
        oppTeamCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOppWhiteName() + ", " + c.getValue().getOppBlackName()));
        tandemWinnerCol.setCellValueFactory(new PropertyValueFactory<>("winner"));
        tandemStartCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        tandemEndCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));

        matchesTable.setVisible(true);
        tandemsTable.setVisible(false);
    }

    public void onSwitch(ActionEvent actionEvent) {
        matchesTable.setVisible(!matchesTable.isVisible());
        tandemsTable.setVisible(!tandemsTable.isVisible());
    }

    public void onBack(ActionEvent actionEvent) {
        WindowUtils.getInstance().switchScreen("profile", switchBtn, "profile", 500, 500);
    }
}
