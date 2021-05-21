package jchess.view.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import jchess.controller.connection.RESTAccessController;
import jchess.controller.connection.SocketController;
import jchess.utils.SystemUtils;
import jchess.utils.WindowUtils;

import java.io.IOException;

public class LoginController {
    @FXML public TextField usernameTextField;
    @FXML public TextField passwordTextField;
    @FXML public Label errorMessage;
    private static final String REGEX = "^[a-z0-9_-]{3,16}$";

    public void onLogin(ActionEvent actionEvent) {
        errorMessage.setVisible(false);
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorMessage.setText("Username and password cannot be empty!");
            errorMessage.setVisible(true);
            return;
        }

        if (!username.matches(REGEX) || !password.matches(REGEX)) {
            errorMessage.setText("Invalid characters in username or password!");
            errorMessage.setVisible(true);
            return;
        }

        int statusCode = 0;
        try {
            statusCode = RESTAccessController.login(usernameTextField.getText(), passwordTextField.getText(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        switch (statusCode) {
            case 200: {
                SocketController.getInstance().openSocketConnection();

                WindowUtils.getInstance().switchScreen("lobby", errorMessage, "lobby", 800, 500);
                break;
            }
            case 412: {
                errorMessage.setText("Invalid username or password!");
                errorMessage.setVisible(true);
                break;
            }
            default: {
                errorMessage.setText("Something went wrong. Try again!");
                errorMessage.setVisible(true);
            }
        }
    }

    public void switchToRegister(ActionEvent actionEvent) {
        WindowUtils.getInstance().switchScreen("register", errorMessage, null, 800, 500);
    }
}
