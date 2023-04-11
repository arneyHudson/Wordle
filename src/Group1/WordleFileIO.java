
package Group1;

import java.nio.file.Path;
import java.util.Dictionary;
import java.util.HashMap;

/**
 * Holds all the file input/output methods used by the wordle application.
 */
public class WordleFileIO {


    /**
     * Returns a dictionary of each character and its frequency of being guessed in the application
     * @param characterHistoryPath A text file holding the history of the chars and their frequency
     * @return A HashMap dictionary of characters and frequency
     */
    public HashMap<Character, Integer> loadCharacterFrequency(Path characterHistoryPath){

        return null;
    }

    /*
     * Character file should be in this format
     * BEGIN_DICT
     * A <number>
     * B <number>
     * ...
     * Z <number>
     */

    /**
     * Saves a dictionary of each character and its frequency to the file
     * @param characterHistoryPath The path to save the text file to
     * @param freqDict A HashMap dictionary of characters and frequency to save to file
     */
    public void saveCharacterFrequency(Path characterHistoryPath, HashMap<Character, Integer> freqDict){

    }

}
