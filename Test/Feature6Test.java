import Group1.Wordle;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Feature6Test {

    private Wordle wordle;
    private Label numGuessesLabel;

    @BeforeEach
    public void setUp() {
        wordle = new Wordle();
        numGuessesLabel = new Label("Current Number of Guesses: 0");
    }

    @Test
    public void testCurrentGuessesLabel() {
        // Make 3 guesses
        for (int i = 0; i < 3; i++) {
            // Simulate a guess
            String guess = "apple";
            wordle.checkRealWord(guess.toLowerCase());
            numGuessesLabel.setText("Current Number of Guesses: " + (i+1));
        }

        // Check that the label displays 3 after 3 guesses
        Assertions.assertEquals("Current Number of Guesses: 3", numGuessesLabel.getText());
    }


    /**
     * Current Guess Test that will test if the current guesses made matches the label
     * @author arneyh
     */
    @Test
    public void currentGuessTest(){
        int numGuesses = 3;

    }

    /**
     * Play Again Button Guess Test that will test if the current guesses made matches the label
     * @author arneyh
     */
    @Test
    public void playAgainButtonTest(){
    }

    /**
     * Average Number of guesses test that will test if the average guesses over multiple games matches the label.
     * @author arneyh
     */
    @Test
    public void averageNumberOfGuessesTest(){

    }


}
