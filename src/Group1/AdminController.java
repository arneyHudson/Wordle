package Group1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Path;
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
                letter = Character.toUpperCase(letter);
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
            fileChooser.setInitialDirectory(new File("src/word_lists"));
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
                String guessPath = selectedFile.getAbsolutePath().replace("word_lists","guess_lists");
                wordleController.getGuess().getWordle().setCurrentGuessFile(new File(guessPath));
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

    /**
     * A simple method that runs through an entire
     * Files inputted must follow a simple format
     * BEGIN_TEST
     * BEGIN_RUN (run_name)
     * SEED (seed_word)
     * WORD (test_word)
     * WORD (test_word)
     * ...
     * END_RUN
     * BEGIN_RUN (run_name)
     * ...
     * END_RUN
     * END_TEST
     * furthermore, the test words must always be the same length as the seed words
     * The method will create a text file with _out appended to the name of the file
     * Inside the output file, the test words will be accompanied by an ASCII representation of the
     * graphics the user would see:
     *  G - Green, direct hit
     *  Y - Yellow, indirect hit
     *  X - Gray, miss
     */
    @FXML
    public void textFileTest() {
        final String BEGIN_TEST = "BEGIN_TEST";
        final String OUT_TEST = "Test:\n";
        final String BEGIN_RUN = "BEGIN_RUN";
        final String OUT_RUN = "Run: ";
        final String END_RUN = "END_RUN";
        final String END_TEST = "END_TEST";
        final String WORD = "WORD";
        final String SEED = "SEED";
        final String OUT_SEED = "Seed: ";
        String current_line;
        String seed_word;
        String test_word;


        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose input file(s)");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        chooser.setInitialDirectory(new File("src/Group1/ADMIN_FILES/INPUT_FILES"));
        List<File> files = chooser.showOpenMultipleDialog(null);
        if(files != null && files.size() > 0){
            for(File f: files){
                try(Scanner in = new Scanner(f);
                    FileWriter out = new FileWriter(
                            Path.of("src/Group1/ADMIN_FILES/INPUT_FILES/"
                                    +f.getName().replace(".txt","")+"_out.txt").toFile())){
                    if(!in.nextLine().equals(BEGIN_TEST)){
                        throw new IOException("Test file corrupted at BEGIN_TEST");
                    }
                    out.write(OUT_TEST);
                    current_line = in.nextLine();
                    while (!current_line.equals(END_TEST)){
                        if (!current_line.substring(0, 9).equals(BEGIN_RUN)){
                            throw new IOException("Test file corrupted at "+current_line);
                        }
                        out.write(OUT_RUN + current_line.substring(10) +'\n');
                        current_line = in.nextLine();
                        if (!current_line.substring(0,4).equals(SEED)){
                            throw new IOException("Test file corrupted at "+current_line);
                        }
                        seed_word = current_line.substring(5);
                        out.write(OUT_SEED + seed_word +'\n');
                        current_line = in.nextLine();
                        while (!current_line.equals(END_RUN)){
                            if(!current_line.substring(0,4).equals(WORD)){
                                throw new IOException("Test file corrupted at "+current_line);
                            }
                            test_word = current_line.substring(5);
                            if(test_word.length() != seed_word.length()){
                                throw new IOException(test_word+" has different length than "+seed_word);
                            }
                            out.write(test_word + ' ');
                            for(Color c: Wordle.perWordLetterCheck
                                    (test_word.toLowerCase(), seed_word.toLowerCase())){
                                if(c.equals(Wordle.DIRECT_COLOR)){
                                    out.write('G');
                                } else if (c.equals(Wordle.INDIRECT_COLOR)){
                                    out.write('Y');
                                } else {
                                    out.write('X');
                                }
                            }
                            out.write('\n');
                            current_line = in.nextLine();
                        }
                        current_line = in.nextLine();
                    }

                }
                catch (IOException e){
                    Alert error = new Alert(Alert.AlertType.ERROR, e.getMessage());
                    error.setHeaderText("Error while parsing input file!");
                    error.show();
                }
            }
        } else {
            Alert info = new Alert(Alert.AlertType.INFORMATION, "No action will be taken.");
            info.setHeaderText("No files were chosen.");
            info.show();
        }
    }
}
