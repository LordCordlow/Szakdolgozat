package jchess.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import jchess.view.controller.ChessController;

import java.io.IOException;
import java.util.List;

public class WindowUtils {
    private static WindowUtils single_instance = null;
    private ChessController fxController = null;

    private WindowUtils() { }

    public static WindowUtils getInstance() {
        if (single_instance == null) {
            single_instance = new WindowUtils();
        }

        return single_instance;
    }

    public void switchScreen(String fxmlFileName, Node fxmlElement, String cssFileName, double width, double height) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("jchess/view/layouts/" + fxmlFileName + ".fxml"));
            if (cssFileName != null) {
                root.getStylesheets().add(getClass().getClassLoader().getResource("jchess/view/css/" + cssFileName + ".css").toExternalForm());
            } else {
                root.getStylesheets().removeAll();
            }

            fxmlElement.getScene().getWindow().setHeight(height);
            fxmlElement.getScene().getWindow().setWidth(width);
            fxmlElement.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchScreen(String fxmlFileName, String cssFileName, double width, double height) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("jchess/view/layouts/" + fxmlFileName + ".fxml"));
            if (cssFileName != null) {
                root.getStylesheets().add(getClass().getClassLoader().getResource("jchess/view/css/" + cssFileName + ".css").toExternalForm());
            } else {
                root.getStylesheets().removeAll();
            }
            Scene scene = new Scene(root);
            Stage currentStage = (Stage) Stage.getWindows().get(0);
            currentStage.setHeight(height);
            currentStage.setWidth(width);
            currentStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ChessController getFxController() {
        return this.fxController;
    }

    public void setFxController(ChessController controller) {
        this.fxController = controller;
    }

    public void setFullScreen(Node fxmlElement) {
        Stage stage = (Stage) fxmlElement.getScene().getWindow();
        stage.setFullScreen(true);
        stage.setResizable(false);
    }

    public void switchScreen2(String fxmlFileName, Node fxmlElement) {
        Parent root = null;

        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("jchess/view/layouts/" + fxmlFileName + ".fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stage currentStage = (Stage) fxmlElement.getScene().getWindow();
        currentStage.close();
    }
}
