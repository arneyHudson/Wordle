package Group1;

import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;

public class SetColor {
    private WordleDisplay wordleDisplay;
    private Wordle wordle;
    private VBox userKeys;

    public SetColor(WordleDisplay wordleDisplay, Wordle wordle, VBox userKeys) {
        this.wordleDisplay = wordleDisplay;
        this.wordle = wordle;
        this.userKeys = userKeys;
    }

    /**
     * The setGuessColor sets the current row that was guess to a list of colors
     *
     * @param colors a list of JavaFX Paint objects the same size as the length of the secretWords
     * @author Collin Schmocker
     */
    public void setGuessColor(List<Paint> colors) {
        int col = wordleDisplay.getWordleGrid().getColumnCount();
        int row = (wordleDisplay.getWordleGrid().getRowCount() - wordle.getRemainingGuesses());
        SequentialTransition sequentialTransition = new SequentialTransition();
        for (int i = 0; i < col; i++) {
            TextField textField = (TextField) wordleDisplay.getWordleGrid().getChildren().get(i + col * row);

            String style = "-fx-control-inner-background: #" + colors.get(i).toString().substring(2);
            // Create a ScaleTransition to flip the TextField vertically
            RotateTransition flipTransition = new RotateTransition(Duration.seconds(0.2), textField);
            flipTransition.setAxis(Rotate.X_AXIS);
            flipTransition.setFromAngle(0);
            flipTransition.setToAngle(90);
            RotateTransition flipTransition2 = new RotateTransition(Duration.seconds(0.2), textField);
            flipTransition.setOnFinished(event -> {
                flipTransition2.setAxis(Rotate.X_AXIS);
                flipTransition2.setFromAngle(90);
                flipTransition2.setToAngle(0);
                textField.setStyle(style + "; -fx-text-fill: white; " +
                        "-fx-font-family: Arial; -fx-opacity: 1.0; -fx-font-weight: bold; " +
                        "-fx-font-size: 20px; -fx-background-radius: 0px;");
            });
            // Add the flipTransition to the sequentialTransition
            sequentialTransition.getChildren().addAll(flipTransition, flipTransition2);
        }

        sequentialTransition.play(); // Play the sequential transition to animate textfields one at a time
    }

    /**
     * The setGuessedLetterColors sets the color of the keyboard display with the HashMap
     * @param lettersGuessed letters that have been guessed mapped to the accuracy of them by color
     * @author Collin Schmocker
     */
    public void setGuessedLetterColors(Map<Character, Paint> lettersGuessed) {
        List<Node> keyboardDisplay = userKeys.getChildren();
        for (Node value : keyboardDisplay) {
            List<Node> keyboardRow = ((HBox) value).getChildren();
            for (Node node : keyboardRow) {
                TextField textField = (TextField) node;
                if (lettersGuessed.containsKey(textField.getText().toCharArray()[0])) {
                    String style = "-fx-control-inner-background: #" + lettersGuessed.get(textField.getText()
                            .toCharArray()[0]).toString().substring(2);
                    textField.setStyle(style + "; -fx-text-fill: white; " +
                            "-fx-font-family: Arial; -fx-opacity: 1.0; -fx-font-weight: bold;");
                    textField.setDisable(false);
                    textField.setEditable(false);
                }
            }
        }
    }
}
