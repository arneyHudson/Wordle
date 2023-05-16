/**
 * Course:     SE 2811
 * Term:       Spring 2022-23
 * Author:     Collin Schmocker
 */
package Group1;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.ArrayList;
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
    private static Boolean PLAYING;
    private static Boolean FIRSTGAME;
    private static final List<String> SECRETWORDLIST = new ArrayList<>();
    private double startTime;
    private final HBox highScoreDisplay;

    public WordleDisplay(int maxGuesses, int secretWordLength, Button guessButton, Wordle wordle, HBox topBar, HBox highScoreDisplay) {
        this.wordleGrid = new GridPane();
        this.maxGuesses = maxGuesses;
        this.secretWordLength = secretWordLength;
        this.guessButton = guessButton;
        this.wordle = wordle;
        this.highScoreDisplay = highScoreDisplay;
        WordleDisplay.PLAYING = false;
        WordleDisplay.FIRSTGAME = true;
        SECRETWORDLIST.add(wordle.getSecretWord());

        if(!WordleFileIO.HIGHSCORES.containsKey(SECRETWORDLIST.get(SECRETWORDLIST.size() - 1))) {
            String[] best = {SECRETWORDLIST.get(SECRETWORDLIST.size() - 1), "60.0", "6"};
            WordleFileIO.HIGHSCORES.put(SECRETWORDLIST.get(SECRETWORDLIST.size() - 1), best);
        } else {
            Label bestDisplay = (Label)topBar.getChildren().get(2);
            bestDisplay.setText("Best: " +  WordleFileIO.HIGHSCORES.get(SECRETWORDLIST.get(SECRETWORDLIST.size() - 1))[1]);
        }

        updateScoreBoard();
        updateLabel(topBar);
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
                final int x = i;
                final int y = j;
                TextField textField = new TextField();
                textField.setEditable(i == 0);
                textField.setMaxSize(45,45);
                textField.setMinSize(45, 45);
                textField.setAlignment(Pos.CENTER);
                textField.setStyle("-fx-control-inner-background: #1b1b1b; -fx-text-fill: white; -fx-font-family: Arial;" +
                        " -fx-font-weight: bold; -fx-font-size: 20px; -fx-background-radius: 0px;");
                textField.setOnKeyTyped(keyEvent -> userInputEvent(textField, x, y));
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
    private void userInputEvent(TextField textField, int x, int y) {
        if(!(WordleDisplay.PLAYING) && x == 0 && y == 0) {
            WordleDisplay.PLAYING = true;
            startTime = System.currentTimeMillis() / 1000.0;
        }
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

    /**
     * The updateLabel method will increment the time of the label while the given boolean is true
     * @author Collin Schmocker
     */
    public void updateLabel(HBox topBar) {
        if(WordleFileIO.HIGHSCORES.containsKey(SECRETWORDLIST.get(SECRETWORDLIST.size() - 1))) {
            Label bestDisplay = (Label)topBar.getChildren().get(2);
            bestDisplay.setText("Best: " +  WordleFileIO.HIGHSCORES.get(SECRETWORDLIST.get(SECRETWORDLIST.size() - 1))[1]);
        }
        Label timerDisplay = (Label)topBar.getChildren().get(0);
        timerDisplay.setText("Time: 0.00");
        double tempLeftPadding = 55;
        topBar.setPadding(new Insets(0, 0, 0, tempLeftPadding));
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(10), event -> {
            if (WordleDisplay.PLAYING) {
                String currentTime = String.valueOf((System.currentTimeMillis() / 1000.0) - startTime);
                timerDisplay.setText("Time: " + ((currentTime.length() - currentTime.indexOf('.')) >= 3 ? currentTime.substring(0, currentTime.indexOf('.') + 3) : currentTime));
                double leftPadding = (topBar.getWidth()/2 - timerDisplay.getWidth() - 44 - topBar.getSpacing());
                topBar.setPadding(new Insets(0, 0, 0, leftPadding));
                WordleDisplay.FIRSTGAME = false;
            } else if (!WordleDisplay.FIRSTGAME) {
                Label bestTimeDisplay = (Label)topBar.getChildren().get(2);
                String currentTime = timerDisplay.getText().substring(timerDisplay.getText().indexOf(' ') + 1);
                String bestTime = bestTimeDisplay.getText().substring(timerDisplay.getText().indexOf(' ') + 1);
                if(Double.parseDouble(currentTime) != 0.0 && Double.parseDouble(bestTime) > Double.parseDouble(currentTime)) {
                    String[] best = {SECRETWORDLIST.get(SECRETWORDLIST.size() - 1), currentTime, Integer.toString(7 - wordle.getRemainingGuesses())};
                    WordleFileIO.HIGHSCORES.replace(SECRETWORDLIST.get(SECRETWORDLIST.size() - 1), best);
                    bestTimeDisplay.setText("Best: " + currentTime);
                    updateScoreBoard();
                }
            } else {
                double leftPadding = (topBar.getWidth()/2 - timerDisplay.getWidth() - 44 - topBar.getSpacing());
                topBar.setPadding(new Insets(0, 0, 0, leftPadding));
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void setPlaying(boolean playing) {
        WordleDisplay.PLAYING = playing;
    }

    /**
     * The method updateScoreBoard will update the display of the fastest times for the display
     * @author Collin Schmocker
     */
    private void updateScoreBoard() {
        TextArea textArea = (TextArea) highScoreDisplay.getChildren().get(0);
        textArea.setText("");
        for(String word: WordleFileIO.HIGHSCORES.keySet()) {
            textArea.setText("          " + WordleFileIO.HIGHSCORES.get(word)[0] + "          " +
                    WordleFileIO.HIGHSCORES.get(word)[1] + "          " +
                    WordleFileIO.HIGHSCORES.get(word)[2] + "\n" + textArea.getText());
        }
        Font font = Font.font("Monospaced", 14); // Adjust the font size as desired
        textArea.setFont(font);
    }

}
