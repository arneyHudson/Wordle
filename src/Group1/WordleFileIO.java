
package Group1;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Holds all the file input/output methods used by the wordle application.
 */
public class WordleFileIO {

    private static final String CHAR_DICT_LINE = "BEGIN_CHAR_DICT";
    private static final String END_LINE = "END";

    public static HashMap<Character, Integer> CHARACTER_FREQUENCY = new HashMap<>();
    public static Path CHAR_FREQ_PATH = Path.of("src/Group1/ADMIN_FILES/character_frequency_log.txt");

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
    public static Collection<String> validateLength(Collection<String> nonValidCollection, int length){
        Collection<String> ret = new HashSet<>();
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

}
