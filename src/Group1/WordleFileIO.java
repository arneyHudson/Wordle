
package Group1;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Holds all the file input/output methods used by the wordle application.
 */
public class WordleFileIO {

    private static final String CHAR_DICT_LINE = "BEGIN_CHAR_DICT";
    private static final String END_LINE = "END";
    /**
     * An EventHandler that will log the input of any keypress during the program's execution
     */
    public static final EventHandler<KeyEvent> LOG_ON_PRESS = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent keyEvent) {
            try {
                logInput(keyEvent.getCode().getName());
            } catch(IOException e){
                System.err.print("Error logging");
            }
        }
    };

    public static HashMap<Character, Integer> CHARACTER_FREQUENCY = new HashMap<>();
    public static HashMap<String, Integer> WORD_FREQUENCY = new HashMap<>();
    public static final Path CHAR_FREQ_PATH = Path.of("src/Group1/ADMIN_FILES/character_frequency_log.txt");
    public static final Path WORD_FREQ_PATH = Path.of("src/Group1/ADMIN_FILES/word_frequency_log.txt");
    public static Path LOG_PATH;

    /**
     * Returns a dictionary of each character and its frequency of being guessed in the application
     * @param characterHistoryPath A text file holding the history of the chars and their frequency
     * @return A HashMap dictionary of characters and frequency
     */
    public static HashMap<Character, Integer> loadCharacterFrequency(Path characterHistoryPath)
            throws IOException{
        HashMap<Character, Integer> ret = new HashMap<>();
        try(Scanner in = new Scanner(characterHistoryPath)){
            if(!in.next().equals(CHAR_DICT_LINE)){
                throw new IOException("Path given is not for the character frequency.");
            }
            String current = in.next();
            while (!current.equals(END_LINE)){
                if (current.length() > 1){
                    throw new IOException("File given for character frequency is corrupted!");
                }
                ret.put(current.charAt(0), Integer.parseInt(in.next()));
                current = in.next();
            }
        }
        return ret;
    }

    // Reuse code below where the exception is caught
    //catch (IOException e){
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setHeaderText("IOException");
//            alert.setContentText(e.getMessage());
//            alert.show();
    // }

    /**
     * Saves a dictionary of each character and its frequency to the file
     * @param characterHistoryPath The path to save the text file to
     * @param frequencyDict A HashMap dictionary of characters and frequency to save to file
     */
    public static void saveCharacterFrequency(Path characterHistoryPath,
                                              HashMap<Character, Integer> frequencyDict)
            throws IOException{
        try(FileWriter out = new FileWriter(characterHistoryPath.toFile())){
            out.write(CHAR_DICT_LINE + '\n');
            for(Character c: frequencyDict.keySet()){
                out.write(""+ c + ' ' + frequencyDict.get(c) + '\n');
            }
            out.write(END_LINE);
        }

    }

    //catch (IOException e) {
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setHeaderText("IOException");
//            alert.setContentText("An exception occurred while saving the character frequency dictionary");
//            alert.show();
//        }

    public static void loadMainCharacterFrequency(){
        try {
            CHARACTER_FREQUENCY = loadCharacterFrequency(CHAR_FREQ_PATH);
        } catch (IOException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("IOException");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    /**
     * Saves the static character frequency dictionary.
     */
    public static void saveMainCharacterFrequency(){
        try {
            saveCharacterFrequency(CHAR_FREQ_PATH, CHARACTER_FREQUENCY);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("IOException");
            alert.setContentText("An exception occurred while saving the character frequency dictionary");
            alert.show();
        }
    }

    /**
     * Adds all of the letters of a string to th
     * @param theGuess The Hashmap to put in, likely just WordleFileIO.CHARACTER_FREQUENCY
     */
    public static void addLettersToCharacterFrequency(String theGuess, HashMap<Character, Integer> dict){
        for(int i = 0; i < theGuess.length(); ++i){
            dict.put(
                    Character.toUpperCase(theGuess.charAt(i))
                    ,
                    dict.get(Character.toUpperCase(theGuess.charAt(i))) + 1
            );
        }
    }

    public static void fillCharacterTextArea(TextArea textArea, HashMap<Character, Integer> dict){
        StringBuilder builder = new StringBuilder();
        builder.append("Guessed Character Frequency: \n");
        HashMap<Character, Integer> dict2 = (HashMap<Character, Integer>)dict.clone();
        for(int i = 0; i < dict.size(); ++i){
            int biggestFreq = -1;
            char biggestChar = '0';
            for(Character c: dict2.keySet()){
                if( dict2.get(c) > biggestFreq){
                    biggestFreq = dict2.get(c);
                    biggestChar = c;
                }
            }
            builder.append(biggestChar);
            builder.append(": ");
            builder.append(biggestFreq);
            builder.append('\n');
            dict2.remove(biggestChar);
        }
        textArea.setText(builder.toString());
    }

    /**
     * Takes a collection of strings, and returns a collection of strings of a certain length
     * @param nonValidCollection The collection of strings to validate.
     * @param length The length to constrain all strings to.
     * @return A collection of strings all of length length.
     */
    public static ArrayList<String> validateLength(Collection<String> nonValidCollection, int length){
        ArrayList<String> ret = new ArrayList<>();
        Iterator<String> iterator = nonValidCollection.iterator();
        String next;
        while(iterator.hasNext()){
            next = iterator.next();
            if(next.length() == length){
                ret.add(next);
            }
        }
        return ret;
    }


    /**
     * Reads the word frequency log file and saves it to a frequency set
     * @param guessFreqPath The file path of the word frequency log
     * @return A set containing each word in the word list as a key, and the
     *         number of occurrences as the value
     * @author zawarusn
     */
    public static HashMap<String, Integer> loadGuessFreq(Path guessFreqPath) throws IOException{
        HashMap<String, Integer> guessFreq = new HashMap<>();
        try(Scanner in = new Scanner(guessFreqPath)){
            String cur = in.nextLine();
            while(!cur.equals("END")){
                String[] info = cur.split(" ");
                guessFreq.put(info[0], Integer.parseInt(info[1]));
                cur = in.nextLine();
            }
        } catch(IOException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("IOException");
            alert.setContentText(e.getMessage());
            alert.show();
        }
        return guessFreq;
    }

    /**
     * Writes the contents of the word frequency set to a log file for use between play sessions
     * @param guessFreqPath The file path of the word frequency log file
     * @param guessFreq The word frequency set
     * @author zawarusn
     */
    public static void saveGuessFreq(Path guessFreqPath, HashMap<String, Integer> guessFreq) throws IOException{
        try(FileWriter out = new FileWriter(guessFreqPath.toFile())){
            for(String word: guessFreq.keySet()){
                out.write(word + " " + guessFreq.get(word) + "\n");
            }
            out.write("END");
        } catch(IOException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("IOException");
            alert.setContentText("An error occurred while saving the guessed word frequency.");
            alert.show();
        }
    }

    /**
     * Increments the frequency of the given word within the wordFreq set
     * @param word The word to be added
     * @param wordFreq The Word Frequency set
     * @author zawarusn
     */
    public static void addToWordFreq(String word, HashMap<String, Integer>wordFreq){
        wordFreq.put(word.toLowerCase(), wordFreq.get(word.toLowerCase())+1);
    }

    /**
     * Calls the loadGuessFreq method with exception handling
     * @author zawarusn
     */
    public static void loadWordFreq(){
        try{
            WORD_FREQUENCY = loadGuessFreq(WORD_FREQ_PATH);
        } catch(IOException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("IOException");
            alert.setContentText("An error occurred while saving the guessed word frequency.");
            alert.show();
        }
    }

    /**
     * Calls the saveGuessFreq method with exception handling
     * @author zawarusn
     */
    public static void saveWordFreq(){
        try{
            saveGuessFreq(WORD_FREQ_PATH, WORD_FREQUENCY);
        }catch(IOException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("IOException");
            alert.setContentText("An error occurred while saving the guessed word frequency.");
            alert.show();
        }
    }

    /**
     * Writes the word frequency set to the text area in the admin panel,
     * sorted alphabetically
     * @param textArea The textArea in the admin panel
     * @author zawarusn
     */
    public static void fillWordArea(TextArea textArea){
        StringBuilder sb = new StringBuilder();
        sb.append("Guess Frequency:\n");
        Map<String, Integer> sorted = new TreeMap<>(WORD_FREQUENCY);
        List<Map.Entry<String, Integer>> save = new ArrayList<>(sorted.entrySet());
        save.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        for (Map.Entry<String, Integer> stringIntegerEntry : save) {
            String word = stringIntegerEntry.getKey();
            if (WORD_FREQUENCY.get(word) != 0) {
                sb.append(word);
                sb.append(": ");
                sb.append(WORD_FREQUENCY.get(word));
                sb.append("\n");
            }
        }
        textArea.setText(sb.toString());
    }
    public static void initializeWordFreq(List<String> wordList){
        for (String word : wordList) {
            if(!WORD_FREQUENCY.containsKey(word)) {
                WORD_FREQUENCY.put(word, 0);
            }
        }
    }

    /**
     * Creates a new log file and assigns it to the class log file for the current run of the application
     */
    public static void initializeLogFile() throws IOException{
        LOG_PATH = Path.of("src/Group1/ADMIN_FILES/LOG_FILES/"+replaceColons(LocalDateTime.now().toString())+".txt");
        appendToFile(LOG_PATH, "BEGIN_LOG\n");
    }

    /**
     * Appends the string to a given file
     * @param filePath The file path to append to;
     * @param appendTo The words to append;
     * @throws IOException Thrown if there is a problem appending
     */
    public static void appendToFile(Path filePath, String appendTo) throws IOException{
        try( FileWriter out = new FileWriter(filePath.toFile(), true)){
            out.write(appendTo);
        }
    }

    /**
     * Logs input to the current log_path using the local time.
     * @param input The input to log
     * @throws IOException Thrown if there is an issue appending to the file.
     */
    public static void logInput(String input) throws IOException{
        appendToFile(LOG_PATH, replaceColons(LocalTime.now().toString())+": "+input+'\n');
    }

    /**
     * Recursively attaches a given KeyEvent handler to every child of a given node
     * @param type The type of the KeyEvent
     * @param handler The handler to attach
     * @param parent The parent node to begin the recursive call on.
     */
    public static void attachHandlerToAllInHierarchy(EventType<KeyEvent> type,
                                                     EventHandler<KeyEvent> handler, Node parent){
        if(parent instanceof Pane){
            for (Node n:((Pane)parent).getChildren()) {
                attachHandlerToAllInHierarchy(type, handler, n);
            }
        }
        parent.addEventHandler(type, handler);
    }

    /**
     * Simple method to replace colons
     * @param in The string to replace colons for
     * @return A string with colons replaced with dashes
     */
    private static String replaceColons(String in){
        return in.replace(':','-');
    }

}
