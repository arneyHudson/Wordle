package Group1;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.net.URL;
import java.util.*;

/**
 * Course:     SE 2811
 * Term:       Winter 2022-23
 * Assignment: assignment name
 * Author:     Collin Schmocker
 * Date:       date started
 */
public class wordleController implements Initializable {

    @FXML
    private VBox mainDisplay;
    @FXML
    private VBox userKeys;
    @FXML
    private Button guessButton;
    private GridPane wordleDisplay;
    private Wordle wordle;
    @FXML
    private Label numGuessesLabel;
    @FXML
    private Label averageNumGuessesLabel;
    @FXML
    private Button playAgainButton;
    @FXML
    private Button hintButton;
    @FXML
    private Label hintLabel;
    private List<Integer> numGuessesList = new ArrayList<>();
    private int gamesPlayed = 0;
    private int numGuesses = 0;
    private int totalNumGuesses = 0;
    boolean correctGuess = false;
    private Color[] colorBuffer;

    /**
     * Runs at the startup of the application setting up all the main parts
     * @author Collin Schmocker
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUpWordleDisplay(6, 5);
        setUpKeyboard();
        mainDisplay.getChildren().add(1, wordleDisplay);
        wordle = new Wordle();
        playAgainButton.setDisable(true);
    }

    /**
     * The setUpWordleDisplay sets up the section where the user guesses the word
     * @param maxGuesses the maximum number of guesses
     * @param secretWordLength the length of the secret word
     * @author Collin Schmocker
     */
    private void setUpWordleDisplay(int maxGuesses, int secretWordLength) {
        wordleDisplay = new GridPane();
        wordleDisplay.setHgap(5);
        wordleDisplay.setVgap(5);
        wordleDisplay.setAlignment(Pos.CENTER);

        for(int i = 0; i < secretWordLength; i++) {
            wordleDisplay.addColumn(0);
        }

        for(int i = 0; i < maxGuesses; i++) {
            for(int j = 0; j < secretWordLength; j++) {
                TextField textField = new TextField();
                textField.setEditable(i == 0);
                textField.setPrefSize(64, 64);
                textField.setMinSize(48, 48);
                textField.setAlignment(Pos.CENTER);
                textField.setStyle("-fx-control-inner-background: #1b1b1b; -fx-text-fill: white; -fx-font-family: Arial;" +
                        " -fx-font-weight: bold; -fx-font-size: 25px");
                textField.setOnKeyTyped(keyEvent -> {
                    String input = textField.getText();
                    if(input.length() >= 1) {
                        if (Character.isLetter(input.charAt(0))) {
                            List<Node> children = wordleDisplay.getChildren();
                            int col = wordleDisplay.getColumnCount();
                            int row = wordleDisplay.getRowCount();
                            int remain =  wordle.getRemainingGuesses();
                            textField.setText(input.substring(0, 1).toUpperCase());
                            int index = children.indexOf(textField);
                            boolean full = true;
                            for (int k = 0; k < col; k++) {
                                if(full && ((TextField)children.get(k + col * (row - remain))).getText().equals("")) {
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
                    }
                });
                wordleDisplay.add(textField, j, i);
            }
            if (i != 0) {
                wordleDisplay.addRow(i);
            }
        }
    }

    /**
     * The setUpKeyboard method the section the user sees their previous input
     * @author Collin Schmocker
     */
    private void setUpKeyboard() {
        String[] keyboard = {"QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM"};
        List<Node> keyboardDisplay = userKeys.getChildren();
        for(int i = 0; i < keyboardDisplay.size(); i++) {
            for(char letter: keyboard[i].toCharArray()) {
                TextField textField = new TextField();
                textField.setDisable(true);
                textField.setPrefSize(32, 32);
                textField.setText(letter + "");
                textField.setAlignment(Pos.CENTER);
                textField.setStyle("-fx-control-inner-background: gray; -fx-text-fill: white; -fx-opacity: 1.0;  " +
                        "-fx-font-family: Arial; -fx-font-weight: bold;");
                ((HBox)keyboardDisplay.get(i)).getChildren().add(textField);
            }
        }
    }


    /**
     * The guess method runs when the user inputs a valid guess and the guess button is pressed
     * @author Collin Schmocker
     */
    @FXML
    public void guess() {
        String guess = "";
        List<Node> children = wordleDisplay.getChildren();
        int col = wordleDisplay.getColumnCount();
        int row = wordleDisplay.getRowCount();
        int remain =  wordle.getRemainingGuesses();

        for (int i = 0; i < col; i++) {
            guess = guess + ((TextField) children.get(i + col * (row - remain))).getText();
        }
        if(wordle.checkRealWord(guess.toLowerCase())){
            for (int i = 0; i < col; i++) {
                children.get(i + col * (row -remain)).setDisable(true);
                ((TextField)children.get(i + col * (row - remain))).setEditable(false);
            }
            colorBuffer = Wordle.perWordLetterCheck(guess.toLowerCase(), wordle.getSecretWord());
            setGuessColor(Arrays.asList(colorBuffer));
            setGuessedLetterColors(wordle.checkLetters(guess));
            if (wordle.getRemainingGuesses() != 1 && !wordle.getSecretWord().equals(guess.toLowerCase())) {
                wordle.setRemainingGuesses(remain - 1);
                remain = wordle.getRemainingGuesses();
                for (int i = 0; i < col; i++) {
                    children.get(i + col * (row - remain)).setDisable(false);
                    ((TextField) children.get(i + col * (row - remain))).setEditable(true);
                }
                children.get(col * (row - remain)).requestFocus();
            }
            guessButton.setDisable(true);
            numGuessesList.add(numGuesses++);
            if(guess.equalsIgnoreCase(wordle.getSecretWord())) {
                correctGuess = true;
            }
        } else {
            Alert invalidWord = new Alert(Alert.AlertType.WARNING,"Not in word list. Please enter a valid 5-letter word.",ButtonType.CLOSE);
            invalidWord.showAndWait();
            children.get(col * (row - remain)).requestFocus();
        }
        //numGuessesList.add(numGuesses++);
        int numCurrentGuesses = numGuessesList.size();
        numGuessesLabel.setText("Current Guesses: " + numCurrentGuesses);

        if (numCurrentGuesses == 6 || correctGuess) {
            gamesPlayed++;
            totalNumGuesses += numGuessesList.size();
            numGuessesList.clear();
            averageNumGuessesLabel.setText("Average Guesses: " + getAverageNumGuesses());
            playAgainButton.setDisable(false);
            playAgainButton.requestFocus();
            hintButton.setDisable(true);

            playAgainButton.setOnAction(event -> {
                restartGame();
            });
        }
    }

    @FXML
    public void createHint(){
        if (colorBuffer == null){
            hintLabel.setText("Hint: " + Wordle.getLetterHint(wordle.getSecretWord()).toUpperCase());
        } else {
            hintLabel.setText("Hint: " + Wordle.getLetterHint(wordle.getSecretWord(), colorBuffer).toUpperCase());
        }
        // Optional code to increase difficulty by only allowing one hint per game
        hintButton.setDisable(true);
    }

    /**
     * Restarts the game when the Play Again button is pressed.
     */
    @FXML
    private void restartGame() {
        // reset keyboard and wordle display
        ObservableList<Node> children = userKeys.getChildren();
        for (Node node : children) {
            if (node instanceof HBox hbox) {
                ObservableList<Node> hboxChildren = hbox.getChildren();
                hboxChildren.removeIf(child -> child instanceof TextField);
            }
        }

        wordleDisplay.getChildren().clear();
        setUpKeyboard();
        setUpWordleDisplay(6, 5);
        mainDisplay.getChildren().add(1, wordleDisplay);
        wordle = new Wordle(); // reset the wordle

        numGuesses = 0; // reset the number of guesses
        numGuessesLabel.setText("Current Guesses: 0");
        correctGuess = false; // reset correct guess flag
        guessButton.setDisable(false); // enable guess button
        hintButton.setDisable(false); // enable the hint button
        colorBuffer = null; // reset color buffer to a null value
        hintLabel.setText(""); // remove the hint label
        playAgainButton.setDisable(true); // disable play again button
    }

    /**
     * Will print the average number of guesses after a round is finsihed.
     * @return the average
     */
    private double getAverageNumGuesses() {
        double average = (double) totalNumGuesses / gamesPlayed;
        return Math.round(average * 100.0) / 100.0;
    }

    /**
     * The setGuessColor sets the current row that was guess to a list of colors
     * @param colors a list of JavaFX Paint objects the same size as the length of the secretWords
     *               @author Collin Schmocker
     */
    private void setGuessColor(List<Paint> colors) {
        for (int i = 0; i < wordleDisplay.getColumnCount(); i++) {
            String style = "-fx-control-inner-background: #" + colors.get(i).toString().substring(2);
            wordleDisplay.getChildren().get(i + wordleDisplay.getColumnCount() * (wordleDisplay.getRowCount() -
                    wordle.getRemainingGuesses())).setStyle(style + "; -fx-text-fill: white; " +
                    "-fx-font-family: Arial; -fx-opacity: 1.0; -fx-font-weight: bold; -fx-font-size: 25px");
        }
    }

    /**
     * The setGuessedLetterColors sets the color of the keyboard display with the HashMap
     * @param lettersGuessed letters that have been guessed mapped to the accuracy of them by color
     * @author Collin Schmocker
     */
    private void setGuessedLetterColors(Map<Character, Paint> lettersGuessed) {
        List<Node> keyboardDisplay = userKeys.getChildren();
        for (Node value : keyboardDisplay) {
            List<Node> keyboardRow = ((HBox) value).getChildren();
            for (Node node : keyboardRow) {
                TextField textField = (TextField) node;
                if (lettersGuessed.containsKey(textField.getText().toCharArray()[0])) {
                    String style = "-fx-control-inner-background: #" + lettersGuessed.get(textField.getText().toCharArray()[0]).toString().substring(2);
                    textField.setStyle(style + "; -fx-text-fill: white; " +
                    "-fx-font-family: Arial; -fx-opacity: 1.0; -fx-font-weight: bold;");
                    textField.setDisable(false);
                    textField.setEditable(false);
                }
            }
        }
    }

}