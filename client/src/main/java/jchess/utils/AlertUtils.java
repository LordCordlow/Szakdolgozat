package jchess.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AlertUtils {
    public static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static int showTimeAlert() {
        int minutes = 5;

        List<Integer> choices = new ArrayList<>();
        choices.add(1);
        choices.add(5);
        choices.add(10);
        choices.add(20);
        choices.add(30);
        choices.add(40);
        choices.add(50);
        choices.add(60);

        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(5, choices);
        dialog.setTitle("Match duration");
        dialog.setHeaderText("Choose match duration!");
        dialog.setContentText("Minutes:");

        Optional<Integer> result = dialog.showAndWait();
        if (result.isPresent()) {
            minutes = result.get();
        }
        return minutes;
    }

    public static boolean showDrawOfferAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Draw");
        alert.setHeaderText("Your opponent offering you a draw!");
        alert.setContentText("Do you agree?");

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static void showDrawReplyAlert(boolean choice) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Draw");
        String headerText = choice ? "Your opponent accepted the draw request!" : "Your opponent declined the draw request!";
        String contentText = choice ? "It's a draw!" : "Keep it up!";
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public static void showGiveUpAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Give up");
        alert.setHeaderText("Your opponent gave up the match!");
        alert.setContentText("You won the match!");
        alert.showAndWait();
    }

    public static boolean showLobbyCreateAlert() {
        boolean isNormal = true;
        List<String> choices = new ArrayList<>();
        choices.add("Normal");
        choices.add("Tandem");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("Normal", choices);
        dialog.setTitle("Match type");
        dialog.setHeaderText("Choose match type!");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            isNormal = result.get().equals("Normal");
        }

        return isNormal;
    }
}
