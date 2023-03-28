import Group1.Wordle;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Collin Schmocker
 */
public class Feature4Test {

    private Wordle wordle;

    @BeforeEach
    public void setUp() {
        wordle = new Wordle();
    }

    @Test
    public void greenTest(){
        Map<Character, Paint> letterChecker = wordle.checkLetters(wordle.getSecretWord());
        for(char letter: wordle.getSecretWord().toUpperCase().toCharArray()) {
            assertEquals(Color.GREEN.toString(), letterChecker.get(letter).toString());
        }
    }

    @Test
    public void grayTest(){
        Wordle wordleOther;
        boolean valid = true;
        do {
            wordleOther = new Wordle();
            int i = 0;
            while(valid && i < wordle.getSecretWord().length()) {
                int j = 0;
                while(valid && j < wordle.getSecretWord().length()) {
                    if (wordle.getSecretWord().charAt(i) == wordleOther.getSecretWord().charAt(j)) {
                        valid = false;
                    }
                    j++;
                }
                i++;
            }
        } while (!valid);
        while(wordleOther.getSecretWord().equals(wordle.getSecretWord())) {
            wordleOther = new Wordle();
        }
        Map<Character, Paint> letterChecker = wordle.checkLetters(wordleOther.getSecretWord().toUpperCase());
        for(char letter: wordleOther.getSecretWord().toUpperCase().toCharArray()) {
            assertEquals(Color.GRAY.toString(), letterChecker.get(letter).toString());
        }
    }

    @Test
    public void yellowTest(){
        String secretWord;
        String guess;
        boolean valid = true;
        do {
            wordle = new Wordle();
            secretWord = wordle.getSecretWord().toUpperCase();
            guess = secretWord.substring(1) + secretWord.charAt(0);
            for(int i = 0; i < secretWord.length(); i++) {
                if (valid && secretWord.charAt(i) == guess.charAt(i)) {
                    valid = false;
                    break;
                }
            }
        } while (!valid);
        Map<Character, Paint> letterChecker = wordle.checkLetters(guess);
        for(char letter: wordle.getSecretWord().toUpperCase().toCharArray()) {
            assertEquals(Color.YELLOW.toString(), letterChecker.get(letter).toString());
        }
    }

}