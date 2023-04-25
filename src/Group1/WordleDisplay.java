/**
 * Course:     SE 2811
 * Term:       Spring 2022-23
 * Author:     Collin Schmocker
 */
package Group1;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.List;

/**
 * Sets up and holds all information related to the main wordle display to the user
 * @author Collin Schmocker
 */
public class WordleDisplay {

    private GridPane wordleGrid;
    private final int maxGuesses;
    private final int secretWordLength;

    private final Button guessButton;
    private final Wordle wordle;

    public WordleDisplay(int maxGuesses, int secretWordLength, Button guessButton, Wordle wordle) {
        this.wordleGrid = new GridPane();
        this.maxGuesses = maxGuesses;
        this.secretWordLength = secretWordLength;
        this.guessButton = guessButton;
        this.wordle = wordle;
        setUpWordleDisplay();
    }

    /**
     * The setUpWordleDisplay sets up the section where the user guesses the word
     * @author Collin Schmocker
     */
    private void setUpWordleDisplay() {
        wordleGrid = new GridPane();
        wordleGrid.setHgap(5);
        wordleGrid.setVgap(5);
        wordleGrid.setAlignment(Pos.CENTER);

        for (int i = 0; i < secretWordLength; i++) {
            wordleGrid.addColumn(0);
        }

        for (int i = 0; i < maxGuesses; i++) {
            for (int j = 0; j < secretWordLength; j++) {
                TextField textField = new TextField();
                textField.setEditable(i == 0);
                textField.setMaxSize(45,45);
                textField.setMinSize(45, 45);
                textField.setAlignment(Pos.CENTER);
                textField.setStyle("-fx-control-inner-background: #1b1b1b; -fx-text-fill: white; -fx-font-family: Arial;" +
                        " -fx-font-weight: bold; -fx-font-size: 20px; -fx-background-radius: 0px;");
                textField.setOnKeyTyped(keyEvent -> userInputEvent(textField));
                wordleGrid.add(textField, j, i);
            }
            if (i != 0) {
                wordleGrid.addRow(i);
            }
        }
    }

    /**
     * The meathod userInputEvent will be run every time a textField is typed into by a user
     * @param textField the textField the user typed into
     * @author Collin Schmocker
     */
    private void userInputEvent(TextField textField) {
        String input = textField.getText();
        if (input.length() >= 1) {
            if (Character.isLetter(input.charAt(0))) {
                List<Node> children = wordleGrid.getChildren();
                int col = wordleGrid.getColumnCount();
                int row = wordleGrid.getRowCount();
                int remain = wordle.getRemainingGuesses();
                textField.setText(input.substring(0, 1).toUpperCase());
                int index = children.indexOf(textField);
                boolean full = true;
                for (int k = 0; k < col; k++) {
                    if (full && ((TextField) children.get(k + col * (row - remain))).getText().equals("")) {
                        full = false;
                    }
                }
                guessButton.setDisable(!full);
                if (((index != 0) && ((index + 1) % secretWordLength == 0))) {
                    guessButton.requestFocus();
                } else {
                    children.get(index + 1).requestFocus();
                }
            } else {
                textField.setText("");
            }
        } else {
            List<Node> children = wordleGrid.getChildren();
            int index = children.indexOf(textField);
            if (index > 0 && textField.getText().equals("")) {
                children.get(index - 1).requestFocus();
            }
        }
    }

    public GridPane getWordleGrid() {
        return wordleGrid;
    }
}
