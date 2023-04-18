import Group1.WordleFileIO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

public class Feature10Test {

    private HashMap<Character, Integer> charFreqDict;
    private static final Path defaultCharList = Path.of("src/Group1/ADMIN_FILES/backup_char_frequency_log.txt");

    @BeforeEach
    public void initDict(){
        charFreqDict = new HashMap<>();
    }

    @Test
    public void loadInFromDefaultList() throws IOException {
        charFreqDict = WordleFileIO.loadCharacterFrequency(defaultCharList);
        for(char c: charFreqDict.keySet()){
            Assertions.assertEquals(0, charFreqDict.get(c));
        }
    }


}
