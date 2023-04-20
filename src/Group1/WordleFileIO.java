
package Group1;

import javafx.scene.control.Alert;

import java.io.File;
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


    /**
     * Returns a dictionary of each character and its frequency of being guessed in the application
     * @param characterHistoryPath A text file holding the history of the chars and their frequency
     * @return A HashMap dictionary of characters and frequency
     */
    public static HashMap<Character, Integer> loadCharacterFrequency(Path characterHistoryPath){
        HashMap<Character, Integer> ret = new HashMap<>();
        try(Scanner in = new Scanner(characterHistoryPath)){
            if(!in.nextLine().equals(CHAR_DICT_LINE)){
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
        } catch (IOException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("IOException");
            alert.setContentText(e.getMessage());
            alert.show();
        }
        return ret;
    }

    /*
     * Character file should be in this format
     * BEGIN_CHAR_DICT
     * A <number>
     * B <number>
     * ...
     * Z <number>
     * END
     */

    /**
     * Saves a dictionary of each character and its frequency to the file
     * @param characterHistoryPath The path to save the text file to
     * @param frequencyDict A HashMap dictionary of characters and frequency to save to file
     */
    public static void saveCharacterFrequency(Path characterHistoryPath,
                                              HashMap<Character, Integer> frequencyDict){
        try(FileWriter out = new FileWriter(characterHistoryPath.toFile())){
            out.write(CHAR_DICT_LINE + '\n');
            for(Character c: frequencyDict.keySet()){
                out.write(c + ' ' + frequencyDict.get(c) + '\n');
            }
            out.write(END_LINE);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("IOException");
            alert.setContentText("An exception occurred while saving the character frequency dictionary");
            alert.show();
        }
    }

    public static HashMap<String, Integer> loadGuessFreq(Path guessFreqPath){
        HashMap<String, Integer> guessFreq = new HashMap<>();
        try(Scanner in = new Scanner(guessFreqPath)){
            String cur = in.nextLine();
            while(!cur.equals("END")){
                String[] info = cur.split(" ");
                if(info[0].length() != 5){ // Change to variable for User Story 13
                    throw new IOException("Frequency list used does not match word length.");
                }
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

    public static void saveGuessFreq(Path guessFreqPath, HashMap<String, Integer> guessFreq){
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
}
