package jchess.view.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import jchess.controller.connection.RESTAccessController;
import jchess.controller.connection.SocketController;
import jchess.utils.SystemUtils;
import jchess.utils.WindowUtils;

import java.io.IOException;

public class RegisterController {
    @FXML public Button registerBtn;
    @FXML public TextField usernameTextField;
    @FXML public TextField password1TextField;
    @FXML public TextField password2TextField;
    @FXML public Label errorMessage;
    private static final String REGEX = "^[a-z0-9_-]{3,16}$";

    public void onRegister(ActionEvent actionEvent) {
        errorMessage.setVisible(false);
        String username = usernameTextField.getText();
        String password1 = password1TextField.getText();
        String password2 = password2TextField.getText();

        if (username.isEmpty() || password1.isEmpty() || password2.isEmpty()) {
            errorMessage.setText("Username and password cannot be empty!");
            errorMessage.setVisible(true);
            return;
        }

        if (!password1.equals(password2)) {
            errorMessage.setText("Passwords not matching!");
            errorMessage.setVisible(true);
            return;
        }

        if (!username.matches(REGEX) || !password1.matches(REGEX)) {
            errorMessage.setText("Invalid characters in username or password!");
            errorMessage.setVisible(true);
            return;
        }

        int statusCode = 0;
        try {
            statusCode = RESTAccessController.login(usernameTextField.getText(), password1TextField.getText(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        switch (statusCode) {
            case 200: {
                System.out.println(SystemUtils.getInstance().getUser());
                SocketController.getInstance().openSocketConnection();
                WindowUtils.getInstance().switchScreen("lobby", errorMessage, null, 800, 500);
                return;
            }
            case 409: {
                errorMessage.setText("This username already exists!");
                errorMessage.setVisible(true);
                return;
            }
            default: {
                errorMessage.setText("Something went wrong. Try again!");
                errorMessage.setVisible(true);
            }
        }
    }

    public void switchToLogin(ActionEvent actionEvent) {
        WindowUtils.getInstance().switchScreen("login", registerBtn, null, 800, 500);
    }
}
