import Group1.Wordle;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Feature8Test {


    /**
     * @author Golvachi
     * @version 1.0
     * Queries the method for several guesses, checks using the Feature 2 method to make sure the
     * hint letter is in fact the correct letter in the correct place.
     */
    @Test
    public void highIntensity(){
        final int WORD_LENGTH = 5;
        final String THE_TRUTH = "funny";
        final int testNumber = 100;
        final Color[] baseArray = {Color.GRAY,Color.GRAY,Color.GRAY,Color.GRAY,Color.GRAY};
        boolean greenFound;
        for(int i = 0; i < testNumber; ++i){
            greenFound = false;
            Color[] colorArray = Wordle.perWordLetterCheck(
                    Wordle.getLetterHint(THE_TRUTH, baseArray) ,THE_TRUTH
            );
            for(int j = 0; j < WORD_LENGTH; ++j){
                if(colorArray[j] == Color.GREEN){
                    greenFound = true;
                }
            }
            Assertions.assertTrue(greenFound);
        }
    }

    /**
     * @author Golvachi
     * @version 1.0
     * Tests the specificity requirement of the method
     * Effectively, passes in an array of colors to the method: As the method is only allowed to place
     * a hint in a non-green space, and for the purposes of the test the index of the non-green is known,
     * The method tests to make sure that the hint word has the hint letter in the non-green index.
     */
    @Test
    public void greenInOne(){
        final int WORD_LENGTH = 5;
        final String THE_TRUTH = "funny";
        final Color[] oneArray = {Color.GRAY,Color.GREEN,Color.GREEN,Color.GREEN,Color.GREEN};
        final Color[] twoArray = {Color.GREEN,Color.GRAY,Color.GREEN,Color.GREEN,Color.GREEN};
        final Color[] threeArray = {Color.GREEN,Color.GREEN,Color.GRAY,Color.GREEN,Color.GREEN};
        final Color[] fourArray = {Color.GREEN,Color.GREEN,Color.GREEN,Color.GRAY,Color.GREEN};
        final Color[] fiveArray = {Color.GREEN,Color.GREEN,Color.GREEN,Color.GREEN,Color.GRAY};
        final Color[][] theHorror = {oneArray, twoArray, threeArray, fourArray, fiveArray};
        /*
         * Now this may look like the most disgusting set up of arrays you've ever seen, but it works.
         * In the loop below, because of the indexing of the color arrays into the outer array
         * It is therefore possible for the array and the desired space for the hint to be at the
         * same index, making the logic for this test more compact (if ugly).
         */
        for (int i = 0; i < WORD_LENGTH; ++i){
            Assertions.assertEquals(
                    Wordle.perWordLetterCheck(
                            Wordle.getLetterHint(THE_TRUTH, theHorror[i]), THE_TRUTH)[i],
                    Color.GREEN);
        }

    }

}
