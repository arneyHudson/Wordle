package Group1;
import javafx.animation.*;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Course:     SE 2811
 * Term:       Winter 2022-23
 * Assignment: assignment name
 * Author:     Collin Schmocker
 * Date:       date started
 */
public class WordleController<T> implements Initializable {

    @FXML
    private VBox mainDisplay;
    @FXML
    private VBox userKeys;
    @FXML
    private Button guessButton;
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
    @FXML
    private Label commonLetterLabel;
    @FXML
    private Label commonGuessLabel;
    @FXML
    private VBox invalidWordVBox;
    @FXML
    private Pane congratsPane;
    @FXML
    private Pane failedPane;
    @FXML
    private Line line;
    private final List<Integer> numGuessesList = new ArrayList<>();
    private final ArrayList<Label> warningLabels = new ArrayList<>();
    private int gamesPlayed = 0;
    private int numGuesses = 0;
    private int totalNumGuesses = 0;
    boolean correctGuess = false;
    private final Map<Character, Integer> letterFrequency = new HashMap<>();
    private final Map<String, Integer> wordFrequency = new HashMap<>();
    private Boolean adminPanelOpen;
    private WordleDisplay wordleDisplay;
    private Guess guess;

    /**
     * Runs at the startup of the application setting up all the main parts
     *
     * @author Collin Schmocker
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        wordle = new Wordle();
        keyboardDisplay = new KeyboardDisplay(userKeys);
        wordleDisplay = new WordleDisplay(6, 5, guessButton, wordle);
        line = new Line();
        line.setStroke(Wordle.NONE_COLOR);
        line.setStartX(0);
        line.setEndX(450);
        line.setStrokeWidth(1.5);
        mainDisplay.getChildren().add(1, line);
        mainDisplay.getChildren().add(2, wordleDisplay.getWordleGrid());
        playAgainButton.setDisable(true);
        adminPanelOpen = false;
        hintLabel.setText("[_]".repeat(wordle.getSecretWord().length())); // create a hint label with blank spaces
        guess = new Guess(mainDisplay, userKeys, wordleDisplay, wordle, guessButton,
                numGuessesList, numGuessesLabel, playAgainButton, hintButton,
                commonLetterLabel, averageNumGuessesLabel, commonGuessLabel, colorBuffer,
                hintLabel, numGuesses, correctGuess, gamesPlayed, totalNumGuesses, this, line);
    }

    /**
     * The guess method runs when the user inputs a valid guess and the guess button is pressed
     * @author Collin Schmocker
     */
    @FXML
    public void guess() {
        guess.makeGuess();
    }


    @FXML
    public void createHint(){
            hintLabel.setText("Hint: " + wordle.getLetterHint(wordle.getSecretWord()).toUpperCase());
        // Optional code to increase difficulty by only allowing one hint per game
        hintButton.setDisable(true);
    }

    /**
     * The setGuessColor sets the current row that was guess to a list of colors
     *
     * @param colors a list of JavaFX Paint objects the same size as the length of the secretWords
     * @author Collin Schmocker
     */
    private void setGuessColor(List<Paint> colors) {
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
    private void setGuessedLetterColors(Map<Character, Paint> lettersGuessed) {
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


    /**
     * Updates a frequency set of all guessed characters, adding based on the letters at least appearing within
     * the secret word. Sorts the frequency list from most to least frequent, and returns the top 5 most common
     * letters.
     * @param lettersGuessed A frequency set of the letters guessed, based on the color as of the current guess.
     * @return A string containing the top 5 correctly guessed letters, separated with spaces
     * @author NZawarus
     */
    public String commonLetters(Map<Character, Paint> lettersGuessed) {
        for (char c : lettersGuessed.keySet()) {
            if (lettersGuessed.get(c).equals(Wordle.DIRECT_COLOR) ||
                    lettersGuessed.get(c).equals(Wordle.INDIRECT_COLOR)) {
                letterFrequency.merge(c, 1, Integer::sum);
            }
        }
        StringBuilder commonText = new StringBuilder("Common Letters: ");
        @SuppressWarnings("unchecked") // letterFrequency will always contain a valid key type
        ArrayList<T> topFiveLetters = sort((Map<T, Integer>)letterFrequency);
        for (int i = 0; i < 5; i++) {
            if (topFiveLetters.get(i) != null) {
                commonText.append(topFiveLetters.get(i)).append(" ");
            } else {
                commonText.append("*").append(" ");
            }
        }
        return commonText.toString();
    }

    /**
     * Updates a frequency set of all guessed words, regardless of correctness. Frequencies are
     * then sorted from most to least common, and the top 5 most common guesses are returned.s
     * @param word The word guessed
     * @return A string containing the top 5 most frequently guessed words, regardless of
     *         if the guessed word was correct.
     * @author NZawarus
     */
    public String commonGuesses(String word){
        wordFrequency.merge(word, 1, Integer::sum);
        StringBuilder commonText = new StringBuilder("Common Guesses: ");
        @SuppressWarnings("unchecked") // wordFrequency will always contain a valid key type
        ArrayList<T> topFiveGuesses = sort((Map<T, Integer>) wordFrequency);
        for (int i = 0; i < 5; i++) {
            if (topFiveGuesses.get(i) != null) {
                commonText.append(topFiveGuesses.get(i)).append(" ");
            } else {
                commonText.append("*").append(" ");
            }
        }
        return commonText.toString();
    }

    /**
     * Sorts the frequency list from most common to least common.
     * @param frequency A set with a generic type for keys, and an integer for the values
     * @return An array list of the most common objects (either string or char depending on
     *         the provided frequency set)
     * @author NZawarus
     */
    private ArrayList<T> sort(Map<T, Integer> frequency){
        ArrayList<T> mostCommonGuesses = new ArrayList<>();
        for(int i = 0; i<5; i++) {
            int mostCommon = 0;
            T mostCommonWord = null;
            for (T s : frequency.keySet()) {
                if(frequency.get(s) > mostCommon && !mostCommonGuesses.contains(s)){
                    mostCommon = frequency.get(s);
                    mostCommonWord = s;
                }
            }
            mostCommonGuesses.add(mostCommonWord);
        }
        return mostCommonGuesses;
    }

    public void closeAdmin(Stage stage) {
        stage.close();
        adminPanelOpen = false;
    }

    void startAdminPanel(WordleController wordleController) {
        if(!adminPanelOpen) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(WordleController.class.getResource("/Group1/AdminPanel.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Wordle");
                stage.show();
                stage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, windowEvent -> closeAdmin(stage));
                AdminController controller = loader.getController();
                controller.setStage(stage);
                controller.setLetterFrequency(letterFrequency);
                controller.setWordFrequency(wordFrequency);
                controller.setWordleController(wordleController);
                adminPanelOpen = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}