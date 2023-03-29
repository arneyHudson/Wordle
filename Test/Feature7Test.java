import Group1.Wordle;
import Group1.wordleController;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class Feature7Test {
    private Wordle wordle;
    private wordleController controller;
    private final Color DIRECT_COLOR = Color.web("#6ca965");
    private final Color INDIRECT_COLOR = Color.web("#c8b653");
    private final Color NONE_COLOR = Color.web("#363636");
    @BeforeEach
    public void setup(){
        wordle = new Wordle();
        controller = new wordleController();
    }
    @Test
    public void letterTest(){
        Map<Character, Paint> lettersGuessed = new HashMap<>();
        String commonText;
        commonText = controller.commonLetters(lettersGuessed);
        Assertions.assertEquals(commonText,"Common Letters: * * * * * ");
        lettersGuessed.put('M',DIRECT_COLOR);
        lettersGuessed.put('O',DIRECT_COLOR);
        lettersGuessed.put('U',DIRECT_COLOR);
        lettersGuessed.put('S',DIRECT_COLOR);
        lettersGuessed.put('E',DIRECT_COLOR);
        commonText = controller.commonLetters(lettersGuessed);
        Assertions.assertEquals(commonText,"Common Letters: S E U M O ");
        lettersGuessed.clear();
        lettersGuessed.put('A',NONE_COLOR);
        lettersGuessed.put('P',NONE_COLOR);
        lettersGuessed.put('P',NONE_COLOR);
        lettersGuessed.put('L',NONE_COLOR);
        lettersGuessed.put('E',DIRECT_COLOR);
        commonText = controller.commonLetters(lettersGuessed);
        Assertions.assertEquals(commonText, "Common Letters: E S U M O ");



    }
    @Test
    public void wordTest(){
        controller.commonGuesses("apple");
        controller.commonGuesses("mouse");
        controller.commonGuesses("cabin");
        String guesses = controller.commonGuesses("mouse");
        Assertions.assertEquals(guesses, "Common Guesses: mouse apple cabin * * ");

    }
}
