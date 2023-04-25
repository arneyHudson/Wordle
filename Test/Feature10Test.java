import Group1.WordleFileIO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * @author golvachi
 */
public class Feature10Test {

    private HashMap<Character, Integer> charFreqDict;
    private HashMap<Character, Integer> preloadedDict;
    private static final Path defaultCharList =
            Path.of("src/Group1/ADMIN_FILES/backup_char_frequency_log.txt");
    private static final Path fibbonaciCharList =
            Path.of("src/Group1/ADMIN_FILES/fib_char_frequency_log.txt");
    private static final Path saveToPath =
            Path.of("src/Group1/ADMIN_FILES/save_to_file.txt");

    @BeforeEach
    public void initDict(){
        charFreqDict = new HashMap<>();
        preloadedDict = new HashMap<>();
    }

    /**
     * Tests the basic loading capabilities of the WordleFileIO's char frequency method
     * @throws IOException Thrown if the path is incorrect or there is an issue loading, should not throw
     */
    @Test
    public void loadInFromDefaultList() throws IOException {
        charFreqDict = WordleFileIO.loadCharacterFrequency(defaultCharList);
        for(char c: charFreqDict.keySet()){
            Assertions.assertEquals(0, charFreqDict.get(c));
        }
    }

    /**
     * Ensures that the loading capabilities load in the actual numbers and not just zeros
     * @throws IOException Thrown if the path is invalid or there is an issue loading in the data
     */
    @Test
    public void loadInFromFibList() throws IOException {
        charFreqDict = WordleFileIO.loadCharacterFrequency(fibbonaciCharList);
        int prev = 0;
        int temp;
        int current = 1;
        char theChar = 'A';
        for(int i = 0; i < 26; ++i){
            Assertions.assertEquals(current, charFreqDict.get(theChar++));
            temp = current;
            current = current + prev;
            prev = temp;
        }
    }

    @Test
    public void saveAndLoad() throws IOException{
        saveToPath.toFile().delete(); // to ensure the test is the same
        char theChar = 'A';
        for(int i = 0; i < 26; ++i){
            preloadedDict.put(theChar++, i);
        }
        WordleFileIO.saveCharacterFrequency(saveToPath, preloadedDict);
        charFreqDict = WordleFileIO.loadCharacterFrequency(saveToPath);
        theChar = 'A';
        for(int i = 0; i < 26; ++i){
            Assertions.assertEquals(preloadedDict.get(theChar), charFreqDict.get(theChar));
            ++theChar;
        }
    }
}
