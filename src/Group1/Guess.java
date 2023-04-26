package Group1;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.List;

public class Guess {
    @FXML
    private VBox mainDisplay;
    @FXML
    private VBox userKeys;
    @FXML
    private WordleDisplay wordleDisplay;
    @FXML
    private Wordle wordle;
    @FXML
    private Button guessButton;
    private final List<Integer> numGuessesList;
    @FXML
    private Label numGuessesLabel;
    @FXML
    private Button playAgainButton;
    @FXML
    private Button hintButton;
    @FXML
    private Label commonLetterLabel;
    @FXML
    private Label averageNumGuessesLabel;
    @FXML
    private Label commonGuessLabel;
    @FXML
    private KeyboardDisplay keyboardDisplay;
    private Color[] colorBuffer;
    private boolean correctGuess;
    private int numGuesses;
    private int gamesPlayed;
    private int totalNumGuesses;
    private final Label hintLabel;
    private WordleController wordleController;
    private final Line line;
    private SetColor setColor;

    public Guess(VBox mainDisplay, VBox userKeys, WordleDisplay wordleDisplay, Wordle wordle, Button guessButton,
                 List<Integer> numGuessesList, Label numGuessesLabel, Button playAgainButton, Button hintButton,
                 Label commonLetterLabel, Label averageNumGuessesLabel, Label commonGuessLabel,
                 Label hintLabel, int numGuesses, boolean correctGuess, int gamesPlayed, int totalNumGuesses,
                 WordleController wordleController, Line line, KeyboardDisplay keyboardDisplay) {
        this.mainDisplay = mainDisplay;
        this.userKeys = userKeys;
        this.wordleDisplay = wordleDisplay;
        this.wordle = wordle;
        this.guessButton = guessButton;
        this.numGuessesList = numGuessesList;
        this.numGuessesLabel = numGuessesLabel;
        this.playAgainButton = playAgainButton;
        this.hintButton = hintButton;
        this.commonLetterLabel = commonLetterLabel;
        this.averageNumGuessesLabel = averageNumGuessesLabel;
        this.commonGuessLabel = commonGuessLabel;
        this.hintLabel = hintLabel;
        this.numGuesses = numGuesses;
        this.correctGuess = correctGuess;
        this.gamesPlayed = gamesPlayed;
        this.totalNumGuesses = totalNumGuesses;
        this.wordleController = wordleController;
        this.line = line;
        this.keyboardDisplay = keyboardDisplay;
        setColor = new SetColor(this.wordleDisplay, this.wordle, this.userKeys);
    }

    @FXML
    public void makeGuess() {
        StringBuilder guess = new StringBuilder();
        List<Node> children = wordleDisplay.getWordleGrid().getChildren();
        int col = wordleDisplay.getWordleGrid().getColumnCount();
        int row = wordleDisplay.getWordleGrid().getRowCount();
        int remain = wordle.getRemainingGuesses();

        for (int i = 0; i < col; i++) {
            guess.append(((TextField) children.get(i + col * (row - remain))).getText());
        }
        if (wordle.checkRealWord(guess.toString().toLowerCase())) {
            WordleFileIO.addLettersToCharacterFrequency
                    (guess.toString(), WordleFileIO.CHARACTER_FREQUENCY);
            WordleFileIO.saveMainCharacterFrequency();
            for (int i = 0; i < col; i++) {
                children.get(i + col * (row - remain)).setDisable(true);
                ((TextField) children.get(i + col * (row - remain))).setEditable(false);
            }
            colorBuffer = Wordle.perWordLetterCheck(guess.toString().toLowerCase(), wordle.getSecretWord());
            setColor.setGuessColor(Arrays.asList(colorBuffer));
            setColor.setGuessedLetterColors(wordle.checkLetters(guess.toString()));
            commonLetterLabel.setText(wordleController.commonLetters(wordle.checkLetters(guess.toString())));
            commonGuessLabel.setText(wordleController.commonGuesses(guess.toString()));
            if (wordle.getRemainingGuesses() != 1 && !wordle.getSecretWord().equals(guess.toString().toLowerCase())) {
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
            if (guess.toString().equalsIgnoreCase(wordle.getSecretWord())) {
                correctGuess = true;
            }
        } else if(guess.toString().matches("^[xX]+$")) {
            wordleController.startAdminPanel(wordleController);
        } else {
            // Shake animation for textfields
            for (int i = 0; i < col; i++) {
                TextField textField = (TextField) children.get(i + col * (row - remain));
                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.millis(0), new KeyValue(textField.translateXProperty(), 0)),
                        new KeyFrame(Duration.millis(35), new KeyValue(textField.translateXProperty(), -4)),
                        new KeyFrame(Duration.millis(70), new KeyValue(textField.translateXProperty(), 4)),
                        new KeyFrame(Duration.millis(105), new KeyValue(textField.translateXProperty(), -4)),
                        new KeyFrame(Duration.millis(140), new KeyValue(textField.translateXProperty(), 4)),
                        new KeyFrame(Duration.millis(175), new KeyValue(textField.translateXProperty(), 0))
                );
                timeline.play();
            }

            // Method call to show an invalid word was typed
            Animations.showWarningPane(wordleDisplay.getWordleGrid());

            // remove: + (row - 2) to get the start of the word
            //children.get(col * (row - remain) + (row - 2)).requestFocus();
            children.get(col * (row - remain) + (wordle.getSecretWord().length() - 1)).requestFocus();
            //children.get(col * (row - remain) + (row - col)).requestFocus();
        }
        //numGuessesList.add(numGuesses++);
        int numCurrentGuesses = numGuessesList.size();
        numGuessesLabel.setText("Current Guesses: " + numCurrentGuesses);

        if (numCurrentGuesses == 6 || correctGuess) {
            PauseTransition delay = new PauseTransition(Duration.seconds(2.5));
            if(correctGuess) {
                delay.setOnFinished(event -> Animations.displayCongrats(wordleDisplay.getWordleGrid()));
            } else {
                delay.setOnFinished(event -> Animations.displayBetterLuckNextTime(wordleDisplay.getWordleGrid(), wordle.getSecretWord()));
            }
            delay.play();
            gamesPlayed++;
            totalNumGuesses += numGuessesList.size();
            numGuessesList.clear();
            averageNumGuessesLabel.setText("Average Guesses: " + getAverageNumGuesses(totalNumGuesses, gamesPlayed));
            playAgainButton.setDisable(false);
            playAgainButton.requestFocus();
            hintButton.setDisable(true);

            playAgainButton.setOnAction(event -> restartGame());
        }
    }

    @FXML
    public void restartGame() {
        // reset keyboard and wordle display
        ObservableList<Node> children = userKeys.getChildren();
        for (Node node : children) {
            if (node instanceof HBox hbox) {
                ObservableList<Node> hboxChildren = hbox.getChildren();
                hboxChildren.removeIf(child -> child instanceof TextField);
            }
        }

        wordleDisplay.getWordleGrid().getChildren().clear();
        keyboardDisplay = new KeyboardDisplay(userKeys);
        wordle = new Wordle(); // reset the wordle
        wordleDisplay = new WordleDisplay(6, wordle.getSecretWord().length(), guessButton, wordle);
        mainDisplay.getChildren().set(1, line);
        mainDisplay.getChildren().set(2, wordleDisplay.getWordleGrid());

        numGuesses = 0; // reset the number of guesses
        numGuessesLabel.setText("Current Guesses: 0");
        correctGuess = false; // reset correct guess flag
        guessButton.setDisable(false); // enable guess button
        hintButton.setDisable(false); // enable the hint button
        colorBuffer = null; // reset color buffer to a null value
        hintLabel.setText("[_] ".repeat(wordle.getSecretWord().length())); // remove the hint label
        hintLabel.setPrefWidth(28 * wordle.getSecretWord().length());
        playAgainButton.setDisable(true); // disable play again button
        setColor = new SetColor(wordleDisplay, wordle, userKeys);
    }

    /**
     * Will print the average number of guesses after a round is finsihed.
     * @return the average
     */
    private double getAverageNumGuesses(int totalNumGuesses, int gamesPlayed) {
        double average = (double) totalNumGuesses / gamesPlayed;
        return Math.round(average * 100.0) / 100.0;
    }
}
