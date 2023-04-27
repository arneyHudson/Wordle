import Group1.Wordle;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Feature8Test {

    Wordle wordle = new Wordle();

    // TODO: Figure out how to incorpate switching of incorrect character from [_], *

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
        final int testNumber = 10_000;
        final Color[] baseArray = {Wordle.NONE_COLOR,Wordle.NONE_COLOR,Wordle.NONE_COLOR,Wordle.NONE_COLOR,Wordle.NONE_COLOR};
        boolean greenFound;
        for(int i = 0; i < testNumber; ++i){
            greenFound = false;
            Color[] colorArray = Wordle.perWordLetterCheck(
                    wordle.getLetterHint(THE_TRUTH, baseArray) ,THE_TRUTH
            );
            for(int j = 0; j < WORD_LENGTH; ++j){
                if(colorArray[j] == Wordle.DIRECT_COLOR){
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
    public void grayInOne(){
        final int WORD_LENGTH = 5;
        final String THE_TRUTH = "funny";
        final Color[] oneArray = {Wordle.NONE_COLOR,Wordle.DIRECT_COLOR,Wordle.DIRECT_COLOR,Wordle.DIRECT_COLOR,Wordle.DIRECT_COLOR};
        final Color[] twoArray = {Wordle.DIRECT_COLOR,Wordle.NONE_COLOR,Wordle.DIRECT_COLOR,Wordle.DIRECT_COLOR,Wordle.DIRECT_COLOR};
        final Color[] threeArray = {Wordle.DIRECT_COLOR,Wordle.DIRECT_COLOR,Wordle.NONE_COLOR,Wordle.DIRECT_COLOR,Wordle.DIRECT_COLOR};
        final Color[] fourArray = {Wordle.DIRECT_COLOR,Wordle.DIRECT_COLOR,Wordle.DIRECT_COLOR,Wordle.NONE_COLOR,Wordle.DIRECT_COLOR};
        final Color[] fiveArray = {Wordle.DIRECT_COLOR,Wordle.DIRECT_COLOR,Wordle.DIRECT_COLOR,Wordle.DIRECT_COLOR,Wordle.NONE_COLOR};
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
                            wordle.getLetterHint(THE_TRUTH, theHorror[i]), THE_TRUTH)[i],
                    Wordle.DIRECT_COLOR);
        }

    }

    /**
     * @author Golvachi
     * @version 1.0
     */
    @Test
    public void highIntensityOcclusion(){
        final String THE_TRUTH = "aeiou";
        final int testNumber = 10_000;
        final Color[] oneArray = {Wordle.DIRECT_COLOR,Wordle.NONE_COLOR,Wordle.NONE_COLOR,Wordle.NONE_COLOR,Wordle.NONE_COLOR};
        final Color[] twoArray = {Wordle.NONE_COLOR,Wordle.DIRECT_COLOR,Wordle.NONE_COLOR,Wordle.NONE_COLOR,Wordle.NONE_COLOR};
        final Color[] threeArray = {Wordle.NONE_COLOR,Wordle.NONE_COLOR,Wordle.DIRECT_COLOR,Wordle.NONE_COLOR,Wordle.NONE_COLOR};
        final Color[] fourArray = {Wordle.NONE_COLOR,Wordle.NONE_COLOR,Wordle.NONE_COLOR,Wordle.DIRECT_COLOR,Wordle.NONE_COLOR};
        final Color[] fiveArray = {Wordle.NONE_COLOR,Wordle.NONE_COLOR,Wordle.NONE_COLOR,Wordle.NONE_COLOR,Wordle.DIRECT_COLOR};
        final Color[][] theHorror = {oneArray, twoArray, threeArray, fourArray, fiveArray};
        /*
         * This is similar to the previous test, however, it tests the exact opposite
         * Instead of confirming that the correct letter is in the only place it can be,
         * this confirms that an asterisk is always in the 'forbidden' place, which is
         * marked by a green coloring in the input color array.
         *
         * The test in then repeated a large number of times in order to confirm that
         * the asterisk is intentionally placed there and not a result of luck
         *
         * At a test number of 10,000, the probability of the code passing the test unintentionally
         * with a word of length 5 is 0.80 to the ten-thousandth power, which is about 7.94x10^-970
         */
        for (int i = 0; i < THE_TRUTH.length(); ++i){
            for(int j = 0; j < testNumber; ++j){
                Assertions.assertEquals(
                        wordle.getLetterHint(THE_TRUTH, theHorror[i]).charAt(i), '*');
            }
        }
    }
}
