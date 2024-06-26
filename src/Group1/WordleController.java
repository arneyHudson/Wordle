package Group1;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
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
    private TabPane tabs;
    @FXML
    private VBox gameDisplay;
    @FXML
    private VBox mainDisplay;
    @FXML
    private VBox userKeys;
    @FXML
    private Button guessButton;
    private Wordle wordle;
    private int wordLength;
    @FXML
    private Label numGuessesLabel;
    @FXML
    private Label averageNumGuessesLabel;
    @FXML
    private Button playAgainButton;
    @FXML
    private Button hintButton;
    @FXML
    private Button multiHintButton;
    @FXML
    private Label hintLabel;
    @FXML
    private Label multiHintLabel;
    @FXML
    private Label commonLetterLabel;
    @FXML
    private Label commonGuessLabel;
    @FXML
    private Line line;
    @FXML
    private ToggleButton hardModeButton;
    @FXML
    private HBox topBar;
    @FXML
    private HBox highScoreDisplay;
    private final List<Integer> numGuessesList = new ArrayList<>();
    private int gamesPlayed;
    private int numGuesses;
    private int totalNumGuesses;
    boolean correctGuess = false;
    private final Map<Character, Integer> letterFrequency = new HashMap<>();
    private final Map<String, Integer> wordFrequency = new HashMap<>();
    private Boolean adminPanelOpen;
    private WordleDisplay wordleDisplay;
    private Guess guess;
    private KeyboardDisplay keyboardDisplay;
    boolean isHardMode;
    boolean[] disableHint = {false}; // Declare a boolean array to track the state of the hint button

    /**
     * Runs at the startup of the application setting up all the main parts
     *
     * @author Collin Schmocker
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gamesPlayed = 0;
        numGuesses = 0;
        totalNumGuesses = 0;
        wordle = new Wordle();
        WordleFileIO.initializeWordFreq(wordle.getWords());
        keyboardDisplay = new KeyboardDisplay(userKeys);
        wordleDisplay = new WordleDisplay(6, 5, guessButton, wordle, topBar, highScoreDisplay);
        wordLength = 5;
        line = new Line();
        line.setStroke(Wordle.NONE_COLOR);
        line.setStartX(0);
        line.setEndX(450);
        line.setStrokeWidth(1.5);
        gameDisplay.getChildren().add(1, line);
        gameDisplay.getChildren().add(2, wordleDisplay.getWordleGrid());
        playAgainButton.setDisable(true);
        adminPanelOpen = false;
        hintLabel.setText("[_] ".repeat(wordle.getSecretWord().length())); // create a hint label with blank spaces
        hintLabel.setPrefWidth(28 * wordle.getSecretWord().length());
        setupHardModeButton();


        guess = new Guess(gameDisplay, userKeys, wordleDisplay, wordle, guessButton,
                numGuessesList, numGuessesLabel, playAgainButton, hintButton,
                commonLetterLabel, averageNumGuessesLabel, commonGuessLabel, hintLabel, numGuesses,
                correctGuess, gamesPlayed, totalNumGuesses, this, line, keyboardDisplay, isHardMode,
                hardModeButton, topBar, highScoreDisplay, multiHintButton, multiHintLabel);

        guess.setHardMode(isHardMode); // Set the initial isHardMode value in the Guess object
        WordleFileIO.attachHandlerToAllInHierarchy(KeyEvent.KEY_PRESSED,
                WordleFileIO.LOG_ON_PRESS, gameDisplay);
    }

    /**
     * The guess method runs when the user inputs a valid guess and the guess button is pressed
     * @author Collin Schmocker
     */
    @FXML
    public void guess() {
        guess.makeGuess();
    }

    public Guess getGuess() {
        return guess;
    }

    /**
     * Used by the hard mode button that simply changes the background color of the button
     * as well as the boolean value that controls hard mode
     * @author Hudson Arney
     */
    @FXML
    public void toggleHardMode() {
        isHardMode = hardModeButton.isSelected();
        if (isHardMode) {
            hardModeButton.setStyle("-fx-background-color:red");
        } else {
            hardModeButton.setStyle("-fx-background-color:white");
        }
    }

    /**
     * Used to set up the hard mode button. Also used in guess. Will make it so that
     */
    public void setupHardModeButton() {
        hardModeButton.setOnAction(event -> {
            toggleHardMode();
            guess.setHardMode(isHardMode); // Update isHardMode in the Guess object

            int col = wordleDisplay.getWordleGrid().getColumnCount();
            int row = wordleDisplay.getWordleGrid().getRowCount();
            Node textfield;
            if(wordleDisplay.getWordleGrid().getChildren().size() > 0) {
                textfield = wordleDisplay.getWordleGrid().getChildren().get(col * (row - wordle.getRemainingGuesses()));
                textfield.requestFocus();
            }

            disableHint[0] = !disableHint[0]; // Toggle the value of disableHint
            hintButton.setDisable(disableHint[0]); // Set the disable property of the hint button
        });
    }


    @FXML
    public void createHint(){
        hintLabel.setText(getGuess().getWordle().getLetterHint(getGuess().getWordle().getSecretWord()).toUpperCase());
        hintLabel.setPrefWidth(28 * getGuess().getWordle().getSecretWord().length());
        // Optional code to increase difficulty by only allowing one hint per game
        hintButton.setDisable(true);
        multiHintButton.setDisable(true);
    }

    @FXML
    public void createMultiHint(){
        multiHintLabel.setText(listToString(shuffle(getGuess().getWordle().getWordHints(5))));
        multiHintLabel.setPrefWidth(10 * 5 * getGuess().getWordle().getSecretWord().length());
        multiHintButton.setDisable(true);
        hintButton.setDisable(true);
    }

    private String listToString(List<String> strings){
        String ret = "";
        for(int i = 0; i < strings.size(); ++i){
            ret += strings.get(i);
            if(i != strings.size() -1){
                ret+=" | ";
            }
        }
        return ret;
    }

    private List<String> shuffle(List<String> strings){
        String[] ret = new String[strings.size()];
        for(int i = strings.size()-1; i >= 0; --i){
            int index = (int)(Math.random()*strings.size());
            while(ret[index] != null){
                index = (int)(Math.random()*strings.size());
            }
            ret[index] = strings.get(i);
        }
        return Arrays.stream(ret).toList();
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

    public void setWordLength(int wordLength) {
        this.wordLength = wordLength;
    }

    public void closeAdmin(Stage stage) {
        stage.close();
        adminPanelOpen = false;
    }

    void startAdminPanel(WordleController wordleController) {
        if(!adminPanelOpen) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/Group1/AdminPanel.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Wordle");
                stage.show();
                stage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, windowEvent -> closeAdmin(stage));
                AdminController controller = loader.getController();
                controller.setStage(stage);
                controller.setLetterFrequency(WordleFileIO.CHARACTER_FREQUENCY);
                controller.setWordFrequency(WordleFileIO.WORD_FREQUENCY);
                controller.setWordleController(wordleController);
                controller.setWordLength(wordLength);
                controller.fillTextArea();
                controller.fillWordArea();
                controller.fillRecommendations();
                adminPanelOpen = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


}