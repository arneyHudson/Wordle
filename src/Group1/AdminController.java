package Group1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class AdminController {

    private Stage stage;
    private Map<Character, Integer> letterFrequency;
    private Map<String, Integer> wordFrequency;
    private WordleController wordleController;
    private int wordLength;

    @FXML
    private TextArea charFreqArea;

    @FXML
    private TextArea wordFreqArea;
    @FXML
    private Button wordListSelection;
    private static File selectedFile;
    @FXML
    private TextArea notRecommended;
    @FXML
    private TextArea recommended;

    @FXML
    public void close() {
        wordleController.closeAdmin(stage);
        stage.close();
    }

    public void fillTextArea(){
        WordleFileIO.fillCharacterTextArea(charFreqArea, WordleFileIO.CHARACTER_FREQUENCY);
    }

    public void fillWordArea(){
        WordleFileIO.fillWordArea(wordFreqArea);
    }

    /**
     * This method will look through the frequencies and display to the
     * user the best and worst words to use as secret words to guess
     * @author Collin Schmocker
     */
    public void fillRecommendations() {
        Map<String, Integer> updatedScore = new HashMap<>();

        for(String word: WordleFileIO.WORD_FREQUENCY.keySet()) {
            int score = WordleFileIO.WORD_FREQUENCY.get(word);
            for(char letter: word.toCharArray()) {
                letter =  Character.toUpperCase(letter);
                if(WordleFileIO.CHARACTER_FREQUENCY.containsKey(letter)) {
                    score += WordleFileIO.CHARACTER_FREQUENCY.get(letter);
                }
            }
            updatedScore.put(word, score);
        }

        Map<String, Integer> sorted = new TreeMap<>(updatedScore);
        List<Map.Entry<String, Integer>> save = new ArrayList<>(sorted.entrySet());
        save.sort(Map.Entry.comparingByValue(Comparator.naturalOrder()));

        StringBuilder builder = new StringBuilder();
        int i = 0;
        int n = 0;
        while(n < 8 && i < save.size()) {
            if(save.get(i).getKey().length() == wordLength) {
                builder.append(save.get(i).getKey());
                builder.append('\n');
            } else {
                n--;
            }
            n++;
            i++;
        }
        recommended.setText(builder.substring(0, builder.toString().length() - 1));

        StringBuilder builder1 = new StringBuilder();
        i = save.size() - 1;
        n = 8;
        while(save.size() > save.size() - n && 0 <= i) {
            if(save.get(i).getKey().length() == wordLength) {
                builder1.append(save.get(i).getKey());
                builder1.append('\n');
            } else {
                n++;
            }
            n--;
            i--;
        }
        notRecommended.setText(builder1.substring(0, builder1.toString().length() - 1));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setLetterFrequency(Map<Character, Integer> letterFrequency) {
        this.letterFrequency = letterFrequency;
    }

    public void setWordFrequency(Map<String, Integer> wordFrequency) {
        this.wordFrequency = wordFrequency;
    }

    public void setWordleController(WordleController wordleController) {
        this.wordleController = wordleController;
    }

    public void setWordLength(int wordLength) {
        this.wordLength = wordLength;
    }

    @FXML
    private void handleWordListSelection(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Text File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            fileChooser.setInitialDirectory(new File("txt_files"));
            selectedFile = fileChooser.showOpenDialog(new Stage());
            if (selectedFile != null) {
                // Read the contents of the selected file
                BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                String line;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    String[] wordsInLine = line.toLowerCase().split("\\s+");
                    for (String word : wordsInLine) {
                        // Check if the word contains only letters
                        if (word.matches("[a-zA-Z]+")) {
                            stringBuilder.append(word);
                            stringBuilder.append(System.getProperty("line.separator"));
                        }
                    }
                }
                reader.close(); // Close the input reader
                wordleController.getGuess().restartGame(); // Restart the game in the Guess instance
                String fileContent = stringBuilder.toString();
                wordleController.closeAdmin(stage); // Close the Admin Panel
                // System.out.println("File content: " + fileContent);
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error reading file");
            alert.showAndWait();
        }
    }

    public static File getFile() {
        return selectedFile;
    }

    @FXML
    public void textFileTest(){
        /*
         * Get the file with a filechooser
         * Run a local version of wordle with the selected SEED word
         * Assume that the SEED and the other ones are of the same length
         * Test input files will follow the similar formats
         * BEGIN_TEST <- start of file
         * BEGIN_RUN <run name> <- Creation of wordle object and name for log
         * SEED <seed word> <- Seed word to use for all other tests
         * WORD <word> <- word to test against the seed word
         * ...
         * END_RUN <- End of run, wordle object is popped off of stack and eaten by garbage collector
         * BEGIN_RUN <run name>
         * SEED <seed word>
         * WORD <word>
         * ...
         * END_RUN
         * END_TEST <- End of test file
         *
         * The output file produce after a run should look something akin to this
         * Test:
         * Run: <run name>
         * Seed: <seed word>
         * <word> <representation of color array output from comparison>
         * ...
         * Run: <run name>
         * Seed: <seed word>
         * <word> <representation of color array output from comparison>
         *
         * The representation of the color array output will be as follows
         * X - Gray Color (miss)
         * Y - Yellow Color (indirect hit)
         * G - Green Color (direct hit)
         */
    }
}
