import Group1.Wordle;
import Group1.WordleController;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class Feature7Test {
    private WordleController controller;
    @BeforeEach
    public void setup(){;
        controller = new WordleController();
    }
    @Test
    public void letterTest(){
        Map<Character, Paint> lettersGuessed = new HashMap<>();
        String commonText;
        commonText = controller.commonLetters(lettersGuessed);
        Assertions.assertEquals(commonText,"Common Letters: * * * * * ");
        lettersGuessed.put('M',Wordle.DIRECT_COLOR);
        lettersGuessed.put('O',Wordle.DIRECT_COLOR);
        lettersGuessed.put('U',Wordle.DIRECT_COLOR);
        lettersGuessed.put('S',Wordle.DIRECT_COLOR);
        lettersGuessed.put('E',Wordle.DIRECT_COLOR);
        commonText = controller.commonLetters(lettersGuessed);
        Assertions.assertEquals(commonText,"Common Letters: S E U M O ");
        lettersGuessed.clear();
        lettersGuessed.put('A',Wordle.NONE_COLOR);
        lettersGuessed.put('P',Wordle.NONE_COLOR);
        lettersGuessed.put('P',Wordle.NONE_COLOR);
        lettersGuessed.put('L',Wordle.NONE_COLOR);
        lettersGuessed.put('E',Wordle.DIRECT_COLOR);
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
