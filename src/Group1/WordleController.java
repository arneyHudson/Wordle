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
public class WordleController implements Initializable {

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
    private int gamesPlayed;
    private int numGuesses;
    private int totalNumGuesses;
    private boolean correctGuess;
    private Color[] colorBuffer;
    private final Map<Character, Integer> letterFrequency = new HashMap<>();
    private final Map<String, Integer> wordFrequency = new HashMap<>();
    private Boolean adminPanelOpen;
    private Guess guess;

    /**
     * Runs at the startup of the application setting up all the main parts
     *
     * @author Collin Schmocker
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        wordleDisplay = new GridPane();
        gamesPlayed = 0;
        numGuesses = 0;
        totalNumGuesses = 0;
        correctGuess = false;
        setUpWordleDisplay(6, 5, wordleDisplay);
        setUpKeyboard();
        line = new Line();
        line.setStroke(Color.GRAY);
        line.setStartX(0);
        line.setEndX(450);
        line.setStrokeWidth(1.5);
        mainDisplay.getChildren().add(1, line);
        mainDisplay.getChildren().add(2, wordleDisplay);
        wordle = new Wordle();
        playAgainButton.setDisable(true);
        adminPanelOpen = false;
        guess = new Guess(mainDisplay, userKeys, wordleDisplay, wordle, guessButton,
                numGuessesList, numGuessesLabel, playAgainButton, hintButton,
                commonLetterLabel, averageNumGuessesLabel, commonGuessLabel, colorBuffer,
                hintLabel, numGuesses, correctGuess, gamesPlayed, totalNumGuesses, this, line);
    }

    /**
     * The setUpWordleDisplay sets up the section where the user guesses the word
     * @param maxGuesses the maximum number of guesses
     * @param secretWordLength the length of the secret word
     * @author Collin Schmocker
     */
    void setUpWordleDisplay(int maxGuesses, int secretWordLength, GridPane wordleDisplay) {
        //wordleDisplay = new GridPane();
        wordleDisplay.setHgap(5);
        wordleDisplay.setVgap(5);
        wordleDisplay.setAlignment(Pos.CENTER);

        for (int i = 0; i < secretWordLength; i++) {
            wordleDisplay.addColumn(0);
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
                textField.setOnKeyTyped(keyEvent -> {
                    String input = textField.getText();
                    if (input.length() >= 1) {
                        if (Character.isLetter(input.charAt(0))) {
                            List<Node> children = wordleDisplay.getChildren();
                            int col = wordleDisplay.getColumnCount();
                            int row = wordleDisplay.getRowCount();
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
                        List<Node> children = wordleDisplay.getChildren();
                        int index = children.indexOf(textField);
                        if (index > 0 && textField.getText().equals("")) {
                            children.get(index - 1).requestFocus();
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
    void setUpKeyboard() {
        String[] keyboard = {"QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM⌫"};
        List<Node> keyboardDisplay = userKeys.getChildren();
        for(int i = 0; i < keyboardDisplay.size(); i++) {
            for(char letter: keyboard[i].toCharArray()) {
                TextField textField = new TextField();
                textField.setDisable(true);
                textField.setPrefSize(32, 32);
                textField.setText(String.valueOf(letter));
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
        guess.makeGuess();


        /*
        StringBuilder guess = new StringBuilder();
        List<Node> children = wordleDisplay.getChildren();
        int col = wordleDisplay.getColumnCount();
        int row = wordleDisplay.getRowCount();
        int remain = wordle.getRemainingGuesses();

        for (int i = 0; i < col; i++) {
            guess.append(((TextField) children.get(i + col * (row - remain))).getText());
        }
        if (wordle.checkRealWord(guess.toString().toLowerCase())) {
            for (int i = 0; i < col; i++) {
                children.get(i + col * (row - remain)).setDisable(true);
                ((TextField) children.get(i + col * (row - remain))).setEditable(false);
            }
            colorBuffer = Wordle.perWordLetterCheck(guess.toString().toLowerCase(), wordle.getSecretWord());
            setGuessColor(Arrays.asList(colorBuffer));
            setGuessedLetterColors(wordle.checkLetters(guess.toString()));
            commonLetterLabel.setText(commonLetters(wordle.checkLetters(guess.toString())));
            commonGuessLabel.setText(commonGuesses(guess.toString()));
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
        } else if(guess.toString().toLowerCase().equals("xxxxx")) {
            startAdminPanel();
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
            showWarningPane(wordleDisplay);

            // remove: + (row - 2) to get the start of the word
            children.get(col * (row - remain) + (row - 2)).requestFocus();
        }
        //numGuessesList.add(numGuesses++);
        int numCurrentGuesses = numGuessesList.size();
        numGuessesLabel.setText("Current Guesses: " + numCurrentGuesses);

        if (numCurrentGuesses == 6 || correctGuess) {
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            if(correctGuess) {
                delay.setOnFinished(event -> displayCongrats(wordleDisplay));
            } else {
                delay.setOnFinished(event -> displayBetterLuckNextTime(wordleDisplay, wordle.getSecretWord()));
            }
            delay.play();
            gamesPlayed++;
            totalNumGuesses += numGuessesList.size();
            numGuessesList.clear();
            averageNumGuessesLabel.setText("Average Guesses: " + getAverageNumGuesses());
            playAgainButton.setDisable(false);
            playAgainButton.requestFocus();
            hintButton.setDisable(true);

            playAgainButton.setOnAction(event -> restartGame());
        } */
    }

    /*
    private void displayCongrats(Pane parent) {
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

    private void displayBetterLuckNextTime(Pane parent, String correctWord) {
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

    private void showWarningPane(Pane parent) {
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
    } */

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

    /*
     * Restarts the game when the Play Again button is pressed.

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
        mainDisplay.getChildren().set(1, wordleDisplay);
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
    */

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
     *
     * @param colors a list of JavaFX Paint objects the same size as the length of the secretWords
     * @author Collin Schmocker
     */
    void setGuessColor(List<Paint> colors) {
        int col = wordleDisplay.getColumnCount();
        int row = (wordleDisplay.getRowCount() - wordle.getRemainingGuesses());
        SequentialTransition sequentialTransition = new SequentialTransition();
        for (int i = 0; i < col; i++) {
            TextField textField = (TextField) wordleDisplay.getChildren().get(i + col * row);

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
    void setGuessedLetterColors(Map<Character, Paint> lettersGuessed) {
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


    public String commonLetters(Map<Character, Paint> lettersGuessed) {
        for (char c : lettersGuessed.keySet()) {
            if (lettersGuessed.get(c).equals(Color.web("#6ca965")) ||
                    lettersGuessed.get(c).equals(Color.web("#c8b653"))) {
                addToFrequency(c);
            }
        }
        StringBuilder commonText = new StringBuilder("Common Letters: ");
        ArrayList<Character> topFiveLetters = sortLetters(letterFrequency);
        for (int i = 0; i < 5; i++) {
            commonText.append(topFiveLetters.get(i)).append(" ");
        }
        return commonText.toString();
    }
    public String commonGuesses(String word){
        wordFrequency.merge(word, 1, Integer::sum);
        sortGuesses(wordFrequency);
        StringBuilder commonText = new StringBuilder("Common Guesses: ");
        ArrayList<String> topFiveGuesses = sortGuesses(wordFrequency);
        for (int i = 0; i < 5; i++) {
            commonText.append(topFiveGuesses.get(i)).append(" ");
        }
        return commonText.toString();
    }
    private void addToFrequency(Character c) {
        letterFrequency.merge(c, 1, Integer::sum);
    }

    private static ArrayList<Character> sortLetters(Map<Character, Integer> letterFrequency){
        ArrayList<Character> mostCommonLetters = new ArrayList<>();
        for(int i = 0; i<5; i++) {
            int mostCommon = 0;
            char mostCommonLetter = '*';
            for (char c : letterFrequency.keySet()) {
                if(letterFrequency.get(c) > mostCommon && !mostCommonLetters.contains(c)){
                    mostCommon = letterFrequency.get(c);
                    mostCommonLetter = c;
                }
            }
            mostCommonLetters.add(mostCommonLetter);
        }
        return mostCommonLetters;
    }

    private static ArrayList<String> sortGuesses(Map<String, Integer> wordFrequency){
        ArrayList<String> mostCommonGuesses = new ArrayList<>();
        for(int i = 0; i<5; i++) {
            int mostCommon = 0;
            String mostCommonWord = "*";
            for (String s : wordFrequency.keySet()) {
                if(wordFrequency.get(s) > mostCommon && !mostCommonGuesses.contains(s)){
                    mostCommon = wordFrequency.get(s);
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