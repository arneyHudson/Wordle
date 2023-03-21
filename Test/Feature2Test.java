import Group1.Wordle;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Feature2Test {

    @BeforeEach
    public void startUp(){
        // neccesary?
    }

    @Test
    /**
     * @author Golvachi
     * @version 1.0
     */
    public void greenTest(){
        final int wordLength = 5;
        final String guessString = "truth";
        final String truthString = "truth";
        final Color[] trueArray = {Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN};
        Color[] colorArray = Wordle.perWordLetterCheck(guessString, truthString);
        for(int i = 0; i < wordLength; ++i){
            Assertions.assertEquals(trueArray[i], colorArray[i]);
        }
    }

    @Test
    /**
     * @author Golvachi
     * @version 1.0
     */
    public void grayTest(){
        final int wordLength = 5;
        final String guessString = "mango";
        final String truthString = "wreck";
        final Color[] trueArray ={Color.GRAY,Color.GRAY,Color.GRAY,Color.GRAY,Color.GRAY};
        Color[] colorArray = Wordle.perWordLetterCheck(guessString, truthString);
        for(int i = 0; i < wordLength; ++i){
            Assertions.assertEquals(trueArray[i], colorArray[i]);
        }
    }

    @Test
    /**
     * @author Golvachi
     * @version 1.0
     */
    public void yellowTest(){
        final int wordLength = 5;
        final String guessString = "throb";
        final String truthString = "broth";
        final Color[] trueArray = {Color.YELLOW, Color.YELLOW,Color.YELLOW,Color.YELLOW,Color.YELLOW};
        Color[] colorArray = Wordle.perWordLetterCheck(guessString, truthString);
        for(int i = 0; i < wordLength; ++i){
            Assertions.assertEquals(trueArray[i], colorArray[i]);
        }
    }


}
