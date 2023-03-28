package Group1;/*
 * Course:     SE 2800
 * Term:       Spring 2023
 * Assignment: Lab 1: User Stories
 * Authors:    Hudson Arney
 * Date:       3/13/2023
 */

import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;

import java.awt.*;
import java.io.*;
import java.security.Guard;
import java.util.*;
import java.util.List;

/**
 A class that represents the Wordle game which can generate a
 random word Rand let the user guess up to 6 guesses.
 */
public class Wordle {
    private static final int MAX_GUESSES = 6;
    private int remainingGuesses;
    private Map<String, Integer> previousGuesses;
    private Map<Character, Paint> lettersGuessed;
    private List<String> words = new ArrayList<String>();
    private String secretWord;

    public Wordle() {
        this.remainingGuesses = MAX_GUESSES;
        this.previousGuesses = new HashMap<String, Integer>();
        this.secretWord = generateSecretWord();
        lettersGuessed = new HashMap<>();
    }

    public String getSecretWord(){
        return secretWord;
    }
    /**
     * Contains the basics for playing the game of wordle
     */
    public void play() {
        System.out.println("Welcome to Wordle!");
        System.out.println("You have " + MAX_GUESSES + " guesses to find the secret word.");
        System.out.println("The secret word is a 5-letter word. Good luck!");
        String secretWord = generateSecretWord();

        Map<Character, Color> lettersGuessed = new HashMap<>();

        while (remainingGuesses > 0) {
            System.out.println("Guesses remaining: " + remainingGuesses);
            String guess = getValidGuess();

            if (guess.equals(secretWord)) {
                System.out.println("Congratulations, you found the secret word!");
                return;
            }
            previousGuesses.put(guess, 1);
            remainingGuesses--;
        }
        System.out.println("Sorry, you ran out of guesses. The secret word was " + secretWord + ".");
    }

    /**
     Generates a secret word from the given text file of words
     @return the secret word
     */
    private String generateSecretWord() {
        try {
            FileInputStream fileInputStream = new FileInputStream("src/Group1/wordle-official.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String word;
            while ((word = bufferedReader.readLine()) != null) {
                if (word.length() == 5) {
                    words.add(word.toLowerCase());
                }
            }
            bufferedReader.close();

            int randomIndex = (int) (Math.random() * words.size());
            System.out.println("The secret word = " + words.get(randomIndex));
            return words.get(randomIndex);
        } catch (FileNotFoundException e) {
            System.out.println("Error: wordle-official.txt not found.");
        } catch (IOException e) {
            System.out.println("Error reading wordle-official.txt.");
        }
        return "EMPTY";
    }

    /**
     Makes sure the user is inputting a 5-letter word. If so the guess will be counted.
     Lets the user know if their guess is correct.
     @return the valid guess
     */
    private String getValidGuess() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter your guess: ");
            String guess = scanner.next().toLowerCase();
            if (guess.length() != 5) {
                System.out.println("Invalid guess. Please enter a 5-letter word.");
            } else if(!checkRealWord(guess)){
                System.out.println("Not in word list.");
            } else if (previousGuesses.containsKey(guess)) {
                System.out.println("You already guessed that word. Please enter a new word.");
            } else {
                return guess;
            }
        }
    }

    public boolean checkRealWord(String guess){
        return words.contains(guess);
    }


    /**
     * Returns a 1xn array of colors to use in the GUI representation of the letters.
     * theGuess and theTruth MUST be the same length.
     * @param theGuess The guess word to compare against the true word
     * @param theTruth The true word to be compared against
     * @return A 1xn Array where each color is respective to the letter of the guess word
     */
    public static Color[] perWordLetterCheck(String theGuess, String theTruth){
        if(theGuess.length() != theTruth.length()){
          throw new IllegalArgumentException("String lengths are not equal");
        }
        Color DIRECT_COLOR = Color.GREEN;
        Color INDIRECT_COLOR = Color.YELLOW;
        Color NONE_COLOR = Color.GRAY;
        Color[] ret = new Color[theGuess.length()];
        List<Character> guessNonDirectLetters = new ArrayList<>();
        List<Character> truthNonDirectLetters = new ArrayList<>();
        /*
         * Phase 1: Direct Check (check for perfect guesses, then remove those letters from both
         * Phase 2: Indirect Check (check remaining letters, removing when an indirect is found
         */

        // Direct Check

        for(int i = 0; i < theGuess.length(); ++i){
            if(theGuess.charAt(i) == theTruth.charAt(i)){
                guessNonDirectLetters.add('0'); // to maintain spacing, no word has '0' in it.
                ret[i] = DIRECT_COLOR;
            } else {
                guessNonDirectLetters.add(theGuess.charAt(i));
                truthNonDirectLetters.add(theTruth.charAt(i));
            }
        }

        // Indirect Check

        for(int i = 0; i < theGuess.length(); ++i){
            if(truthNonDirectLetters.contains(guessNonDirectLetters.get(i))){
                ret[i] = INDIRECT_COLOR;
                truthNonDirectLetters.remove(guessNonDirectLetters.get(i));
            } else if ((!truthNonDirectLetters.contains(guessNonDirectLetters.get(i))) &&
            ret[i] == null){
                ret[i] = NONE_COLOR;
            }
        }
        return ret;
    }

    /**
     * Overloaded method for getLetterHint(String, int, Color[])
     * Color is default all gray
     * @param theTruth The reference word to make a hint for
     * @return A hint based off the reference word
     */
    public static String getLetterHint(String theTruth){
        Color[] colorArray = new Color[theTruth.length()];
        Arrays.fill(colorArray, Color.GRAY);
        return getLetterHint(theTruth, colorArray);
    }

    /**
     * For User Story 8, returns a String with a single correct letter in its correct place
     * All other strings as asterisks
     *
     * NOTE: The color array MUST be the same length as the word.
     * @param theTruth The correct word, used to create a hint from.
     * @param colors Optionally passed in, in order to avoid making hints for 'green' spaces
     * @throws IllegalArgumentException when the length of the color array is not equal to the
     * length of the 'true' string.
     * @return A string containing the hint.
     */
    public static String getLetterHint(String theTruth, Color[] colors){
        // if all green, disable the button
        /*
         * Figure out which positions /can/ be hints
         * Use math.random to pick one of those positions
         * Use the code succeeding to come up with the hint.
         */
        if(colors.length != theTruth.length()){
            throw new IllegalArgumentException("Color Array and String length are not equal!");
        }

        List<Integer> possiblePositions = new ArrayList<>();
        for(int i = 0; i < colors.length; ++i){
            if(colors[i] != Color.GREEN){
                possiblePositions.add(i);
            }
        }
        final int hintPosition = possiblePositions.get((int)(Math.random()*possiblePositions.size()));
        final char hintChar = theTruth.charAt(hintPosition);
        final char hiddenChar = '*';
        String ret = "";
        for(int i = 0; i < theTruth.length(); ++i){
            if(i == hintPosition){
                ret = ret + hintChar;
            } else {
                ret = ret + hiddenChar;
            }
        }
        return ret;
    }

    /**
     * Meathod that takes in a guess and a secret word and checks if
     * any of the letters are in the secret word and adds them to the Map.
     * If the letter is in the word its saved as Color.GREEN and Color.GRAY
     * if it isn't
     * @param guess the word the user did guess
     * @author Collin Schmocker
     */
    public Map<Character, Paint> checkLetters(String guess) {
        Color[] check = perWordLetterCheck(guess.toLowerCase(), secretWord);
        char[] characters = guess.toUpperCase().toCharArray();
        for(int i = 0; i < characters.length; i++) {
            lettersGuessed.put(characters[i], check[i]);
            //System.out.println(characters[i] + ": " + check[i]);
        }
        return lettersGuessed;
    }

    public void setRemainingGuesses(int numGuesses){
        this.remainingGuesses = numGuesses;
    }
    public int getRemainingGuesses() {
        return remainingGuesses;
    }

    public boolean isGameOver() {
        return remainingGuesses == 0;
    }

    public int getNumGuesses() {
        return previousGuesses.size();
    }
}