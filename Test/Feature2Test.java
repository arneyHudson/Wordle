import Group1.Wordle;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Feature2Test {

    final Color DIRECT_COLOR = Color.web("#6ca965");
    final Color INDIRECT_COLOR = Color.web("#c8b653");
    final Color NONE_COLOR = Color.web("#363636");

    /**
     * @author Golvachi
     * @version 1.0
     */
    @Test
    public void greenTest(){
        final int wordLength = 5;
        final String guessString = "truth";
        final String truthString = "truth";
        final Color[] trueArray = {DIRECT_COLOR, DIRECT_COLOR, DIRECT_COLOR, DIRECT_COLOR, DIRECT_COLOR};
        Color[] colorArray = Wordle.perWordLetterCheck(guessString, truthString);
        for(int i = 0; i < wordLength; ++i){
            Assertions.assertEquals(trueArray[i], colorArray[i]);
        }
    }


    /**
     * @author Golvachi
     * @version 1.0
     */
    @Test
    public void grayTest(){
        final int wordLength = 5;
        final String guessString = "mango";
        final String truthString = "wreck";
        final Color[] trueArray ={NONE_COLOR,NONE_COLOR,NONE_COLOR,NONE_COLOR,NONE_COLOR};
        Color[] colorArray = Wordle.perWordLetterCheck(guessString, truthString);
        for(int i = 0; i < wordLength; ++i){
            Assertions.assertEquals(trueArray[i], colorArray[i]);
        }
    }


    /**
     * @author Golvachi
     * @version 1.0
     */
    @Test
    public void yellowTest(){
        final int wordLength = 5;
        final String guessString = "throb";
        final String truthString = "broth";
        final Color[] trueArray = {INDIRECT_COLOR, INDIRECT_COLOR,INDIRECT_COLOR,INDIRECT_COLOR,INDIRECT_COLOR};
        Color[] colorArray = Wordle.perWordLetterCheck(guessString, truthString);
        for(int i = 0; i < wordLength; ++i){
            Assertions.assertEquals(trueArray[i], colorArray[i]);
        }
    }

    /**
     * @author golvachi
     * @version 1.0
     */
    @Test
    public void multiplicities(){
        final String threeo = "coolio";
        final String oneo = "creton";
        final Color[] oArray = {DIRECT_COLOR, INDIRECT_COLOR,
                NONE_COLOR, NONE_COLOR, NONE_COLOR, NONE_COLOR};
        Color[] colorArray = Wordle.perWordLetterCheck(threeo, oneo);
        for(int i = 0; i < oArray.length; ++i){
            Assertions.assertEquals(oArray[i], colorArray[i]);
        }
        // Only one of the 'o' in 'coolio' is necessary, and it is not in the correct place.
        // Therefore, only the first o will be colored, the rest will be gray.

        final String hurry = "hurry";
        final String rumor = "rumor";
        final Color[] rArray = {NONE_COLOR, DIRECT_COLOR, INDIRECT_COLOR, INDIRECT_COLOR, NONE_COLOR};
        colorArray = Wordle.perWordLetterCheck(hurry, rumor);
        for(int i = 0; i < rArray.length; ++i){
            Assertions.assertEquals(rArray[i], colorArray[i]);
        }
        // Both rs should be colored yellow, as the first r takes up one possible yellow,
        // the second r takes the other.

        final String hurri = "hurri";
        final String rumod = "rumod";
        final Color[] rArray2 = {NONE_COLOR, DIRECT_COLOR, INDIRECT_COLOR, NONE_COLOR, NONE_COLOR};
        colorArray = Wordle.perWordLetterCheck(hurry, rumod);
        for(int i = 0; i < rArray2.length; ++i){
            Assertions.assertEquals(rArray2[i], colorArray[i]);
        }
        // Now that the 'true' word only has one r, the second r should be grayed out

        final String apple = "apple";
        final String apoge = "apoge";
        final Color[] pArray = {DIRECT_COLOR, DIRECT_COLOR, NONE_COLOR, NONE_COLOR, DIRECT_COLOR};
        colorArray = Wordle.perWordLetterCheck(apple, apoge);
        for(int i = 0; i < pArray.length; ++i){
            Assertions.assertEquals(pArray[i], colorArray[i]);
        }
        // Because the only p in the truth is already found, the other p is grayed out.

        final String apropos = "apropos";
        final String applely = "applely";
        final Color[] pArray2 = {DIRECT_COLOR, DIRECT_COLOR, NONE_COLOR, NONE_COLOR, INDIRECT_COLOR,
                NONE_COLOR, NONE_COLOR};
        colorArray = Wordle.perWordLetterCheck(apropos, applely);
        for(int i = 0; i < pArray2.length; ++i){
            Assertions.assertEquals(pArray2[i], colorArray[i]);
        }
        // There are two 'p' in the truth word, and one correct and one misplaced
        // The first will be green, and the second will be yellow.

    }


}
