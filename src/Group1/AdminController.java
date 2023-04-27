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

    public void fillRecommendations() {
        Map<String, Integer> sorted = new TreeMap<>(WordleFileIO.WORD_FREQUENCY);
        List<Map.Entry<String, Integer>> save = new ArrayList<>(sorted.entrySet());
        save.sort(Map.Entry.comparingByValue(Comparator.naturalOrder()));

        StringBuilder builder = new StringBuilder();
        int i = 0;
        int n = 0;
        while(n < 5 && i < save.size()) {
            if(save.get(i).getKey().length() == wordLength) {
                builder.append(save.get(i).getKey());
                builder.append('\n');
            } else {
                n--;
            }
            n++;
            i++;
        }
        recommended.setText(builder.toString());

        StringBuilder builder1 = new StringBuilder();
        i = save.size() - 1;
        n = 5;
        while(save.size() > save.size() - n && 0 <= i) {
            if(save.get(i).getKey().length() == wordLength && save.get(i).getValue() != 0) {
                builder1.append(save.get(i).getKey());
                builder1.append('\n');
            } else {
                n++;
            }
            n--;
            i--;
        }
        notRecommended.setText(builder1.toString());
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
}
