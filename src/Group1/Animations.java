package Group1;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;

public class Animations {
    @FXML
    private static VBox invalidWordVBox;
    @FXML
    private static Pane congratsPane;
    @FXML
    private static Pane failedPane;
    private static final ArrayList<Label> warningLabels = new ArrayList<>();
    public static void displayCongrats(Pane parent) {
        Label label = new Label("Great");
        label.setStyle("-fx-font-family: Arial; -fx-background-color: white; -fx-font-weight: bold; " +
                "-fx-font-size: 14px; -fx-text-fill: black; -fx-padding: 10px;");
        label.setAlignment(Pos.CENTER);

        congratsPane = new Pane();
        congratsPane.setStyle("-fx-background-color: white; -fx-padding: 10px;");
        congratsPane.setPrefSize(58, 50);
        congratsPane.getChildren().add(label);

        // Position the pane absolutely
        congratsPane.setManaged(false);
        // Set layout relative to parent
        congratsPane.setLayoutX((parent.getWidth() - congratsPane.getPrefWidth()) / 2);
        congratsPane.setLayoutY(-10);

        // Add warningPane to the parent
        parent.getChildren().add(congratsPane);

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(3), label);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setOnFinished(event -> parent.getChildren().remove(congratsPane));
        fadeTransition.play();
    }

    public static void displayBetterLuckNextTime(Pane parent, String correctWord) {
        Label label = new Label("Better Luck Next Time\nThe correct word was: " + correctWord.toUpperCase());
        label.setStyle("-fx-font-family: Arial; -fx-background-color: white; -fx-font-weight: bold; " +
                "-fx-font-size: 14px; -fx-text-fill: black; -fx-padding: 10px; -fx-alignment: center;");
        label.setAlignment(Pos.CENTER);

        failedPane = new Pane();
        failedPane.setStyle("-fx-background-color: white; -fx-padding: 10px;");
        failedPane.setPrefSize(225, 50);
        failedPane.getChildren().add(label);

        // Position the pane absolutely
        failedPane.setManaged(false);
        // Set layout relative to parent
        failedPane.setLayoutX((parent.getWidth() - failedPane.getPrefWidth()) / 2);
        failedPane.setLayoutY(-10);

        // Add warningPane to the parent
        parent.getChildren().add(failedPane);

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(6), label);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setOnFinished(event -> parent.getChildren().remove(failedPane));
        fadeTransition.play();
    }
    public static void showWarningPane(Pane parent) {
        Label label = new Label("Not in word list");
        label.setStyle("-fx-font-family: Arial; -fx-background-color: white; -fx-font-weight: bold; " +
                "-fx-font-size: 12px; -fx-text-fill: black; -fx-padding: 10px;");
        label.setAlignment(Pos.CENTER);
        label.setMinWidth(115);

        if (invalidWordVBox == null) {
            invalidWordVBox = new VBox();
            invalidWordVBox.setStyle("-fx-background-color: white;");
            invalidWordVBox.setPrefSize(125, 50);
            invalidWordVBox.setSpacing(10);

            // Position the pane absolutely
            invalidWordVBox.setManaged(false);
            // Set layout relative to parent
            invalidWordVBox.setLayoutX((parent.getWidth() - invalidWordVBox.getPrefWidth()) / 2);
            invalidWordVBox.setLayoutY(-10);

            // Add warningPane to the parent
            parent.getChildren().add(invalidWordVBox);
        }

        // Add the label to the warningPane
        invalidWordVBox.getChildren().add(0, label);

        // Add the label to the list of labels to be removed later
        warningLabels.add(label);

        // Fade out the label when its animation finishes
        FadeTransition fade = new FadeTransition(Duration.seconds(2), label);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(event -> {
            invalidWordVBox.getChildren().remove(label);
            warningLabels.remove(label);
            if (invalidWordVBox.getChildren().isEmpty()) {
                parent.getChildren().remove(invalidWordVBox);
                invalidWordVBox = null;
            }
        });

        // Play the fade-out animation
        fade.play();
    }
}
