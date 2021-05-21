package jchess;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jchess.controller.connection.RESTAccessController;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("jchess/view/layouts/login.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        RESTAccessController.logout();
    }

    public static void main(String[] args) {
        launch();
    }

}