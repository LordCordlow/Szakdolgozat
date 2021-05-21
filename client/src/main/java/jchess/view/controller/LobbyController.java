package jchess.view.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Popup;
import javafx.stage.Window;
import jchess.controller.connection.RESTLobbyController;
import jchess.controller.connection.RESTPlayersController;
import jchess.controller.connection.RESTTandemController;
import jchess.controller.connection.SocketController;
import jchess.model.Lobby;
import jchess.model.Player;
import jchess.model.TLobby;
import jchess.utils.AlertUtils;
import jchess.utils.SystemUtils;
import jchess.utils.WindowUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LobbyController implements Initializable {
    @FXML Button refreshBtn;
    @FXML Button profileBtn;
    @FXML Button plusBtn;
    @FXML Button logoutBtn;
    @FXML Label noLobbyLabel;
    @FXML ListView<String> onlinePlayers;
    @FXML ListView<String> lobbies;
    @FXML AnchorPane anchorPane;

    List<Lobby> lobbiesInList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Image refreshImage = new Image(String.valueOf(getClass().getResource("/jchess/view/icons/refresh.png")), 30, 30, false, false);
        Image profileImage = new Image(String.valueOf(getClass().getResource("/jchess/view/icons/user.png")), 30, 30, false, false);
        Image plusImage = new Image(String.valueOf(getClass().getResource("/jchess/view/icons/plus.png")), 30, 30, false, false);
        Image logoutImage = new Image(String.valueOf(getClass().getResource("/jchess/view/icons/logout.png")), 30, 30, false, false);
        ImageView logoutImageView = new ImageView(logoutImage);
        logoutImageView.setRotate(180);
        refreshBtn.setGraphic(new ImageView(refreshImage));
        profileBtn.setGraphic(new ImageView(profileImage));
        plusBtn.setGraphic(new ImageView(plusImage));
        logoutBtn.setGraphic(logoutImageView);

        lobbiesInList = new ArrayList<>();

        setOnlinePlayersListView();
        setLobbies();
    }

    private void setOnlinePlayersListView() {
        List<String> usernames = new ArrayList<>();
        for (Player player : SystemUtils.getInstance().getOnlineUsers()) {
            usernames.add(player.getUsername());
        }
        onlinePlayers.setItems(FXCollections.observableList(usernames));
    }

    private void setLobbies() {
        lobbiesInList.clear();
        List<String> lobbies = new ArrayList<>();
        for (Lobby lobby : SystemUtils.getInstance().getLobbies()) {
            lobbies.add("Host: " + lobby.getHost().getUsername() + ", lobby type: normal");
            lobbiesInList.add(lobby);
        }
        for (TLobby l : SystemUtils.getInstance().getTandems()) {
            lobbies.add("Host: " + l.getHost().getUsername() + ", lobby type: tandem");
            lobbiesInList.add(l);
        }
        noLobbyLabel.setVisible(lobbies.size() <= 0);
        this.lobbies.setItems(FXCollections.observableList(lobbies));
    }

    public void onRefresh(ActionEvent actionEvent) throws IOException {
        RESTPlayersController.getOnlinePlayers();
        RESTLobbyController.getLobbies();
        RESTTandemController.getTandems();
        setOnlinePlayersListView();
        setLobbies();
    }

    public void onCreateLobby(ActionEvent actionEvent) throws IOException {
        boolean isNormalType = AlertUtils.showLobbyCreateAlert();
        if (isNormalType) {
            int statusCode = RESTLobbyController.createLobby();
            if (statusCode == 200) {
                WindowUtils.getInstance().switchScreen("match_making", logoutBtn, null, 500, 500);
            } else {
                AlertUtils.showAlert("Something went wrong!");
            }
        } else {
            int statusCode = RESTTandemController.createTLobby();
            if (statusCode == 200) {
                WindowUtils.getInstance().switchScreen("tandem_match_making", logoutBtn, null, 500, 500);
            } else {
                AlertUtils.showAlert("Something went wrong!");
            }
        }
    }

    public void onClickedLobby(MouseEvent mouseEvent) throws IOException {
        int selectedLobby = lobbies.getSelectionModel().getSelectedIndex();
        Lobby lobbyToJoin = lobbiesInList.get(selectedLobby);
        int statusCode = 0;
        if (lobbyToJoin != null) {
            if (lobbyToJoin instanceof TLobby) {
                // joining tandem match
                statusCode = RESTTandemController.joinTandem((TLobby) lobbyToJoin);
                if (statusCode == 200) {
                    WindowUtils.getInstance().switchScreen("tandem_match_making", logoutBtn, null, 500, 500);
                } else {
                    AlertUtils.showAlert("Something went wrong!");
                }
            } else {
                // joining normal match
                statusCode = RESTLobbyController.joinLobby(lobbyToJoin);
                if (statusCode == 200) {
                    SystemUtils.getInstance().setOpponent(lobbyToJoin.getHost());
                    WindowUtils.getInstance().switchScreen("match_making", logoutBtn, null, 500, 500);
                } else {
                    AlertUtils.showAlert("Something went wrong!");
                }
            }
        }
    }

    public void onOpenProfile(ActionEvent actionEvent) {
        WindowUtils.getInstance().switchScreen("profile", logoutBtn, "profile", 500, 500);
    }

    public void onLogout(ActionEvent actionEvent) {
    }
}
