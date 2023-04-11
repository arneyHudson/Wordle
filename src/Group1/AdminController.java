package Group1;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class AdminController {

    private Stage stage;
    private Map<Character, Integer> letterFrequency;
    private Map<String, Integer> wordFrequency;
    private WordleController wordleController;

    @FXML
    public void close() {
        wordleController.closeAdmin(stage);
        stage.close();
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
}
