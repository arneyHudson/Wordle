package Group1;
import javafx.animation.*;
import javafx.collections.ObservableList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
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
    private Line line;
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
        initializeWordFreq();
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
        hintLabel.setText("[_] ".repeat(wordle.getSecretWord().length())); // create a hint label with blank spaces
        guess = new Guess(mainDisplay, userKeys, wordleDisplay, wordle, guessButton,
                numGuessesList, numGuessesLabel, playAgainButton, hintButton,
                commonLetterLabel, averageNumGuessesLabel, commonGuessLabel, hintLabel, numGuesses,
                correctGuess, gamesPlayed, totalNumGuesses, this, line, keyboardDisplay);
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
        hintLabel.setText(wordle.getLetterHint(wordle.getSecretWord()).toUpperCase());
        // Optional code to increase difficulty by only allowing one hint per game
        hintButton.setDisable(true);
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
                controller.fillTextArea();
                controller.fillWordArea();
                adminPanelOpen = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void openTextFile(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Text File");
            fileChooser.setInitialDirectory(new File("txt_files"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File selectedFile = fileChooser.showOpenDialog(new Stage());
            if (selectedFile != null) {
                // Read the contents of the selected file
                BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                String line;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                    stringBuilder.append(System.getProperty("line.separator"));
                }
                reader.close();
                String fileContent = stringBuilder.toString();
                // Do something with the file content
                System.out.println("File content: " + fileContent);
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error reading file");
            alert.showAndWait();
        }
    }

    private void initializeWordFreq(){
        for(String word: wordle.getWords()){
            wordFrequency.put(word,0);
        }
    }
}