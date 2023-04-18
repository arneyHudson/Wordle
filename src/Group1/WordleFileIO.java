
package Group1;

import javafx.scene.control.Alert;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Scanner;

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
                out.write(c + ' ' + frequencyDict.get(c) + '\n');
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

}
