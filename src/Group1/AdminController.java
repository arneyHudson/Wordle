package Group1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class AdminController {

    private Stage stage;
    private Map<Character, Integer> letterFrequency;
    private Map<String, Integer> wordFrequency;
    private WordleController wordleController;

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
        TreeMap<String, Integer> sorted = new TreeMap<>(wordFrequency);
        Iterator<String> it = sorted.descendingKeySet().iterator();
        StringBuilder builder = new StringBuilder();
        int i = 5;
        while(it.hasNext() && i > 0) {
            builder.append(it.next());
            builder.append('\n');
            i--;
        }
        recommended.setText(builder.toString());
        ListIterator<String> li = sorted.descendingKeySet().stream().toList().listIterator();
        builder = new StringBuilder();
        i = 5;
        while(li.hasPrevious() && i > 0) {
            builder.append(li.previous());
            builder.append('\n');
            i--;
        }
        notRecommended.setText(builder.toString());
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
                close(); // Close the Admin Panel
                wordleController.getGuess().restartGame(); // Restart the game in the Guess instance
                String fileContent = stringBuilder.toString();
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
