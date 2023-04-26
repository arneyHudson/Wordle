package Group1;/*
 * Course:     SE 2800
 * Term:       Spring 2023
 * Assignment: Lab 1: User Stories
 * Authors:    Hudson Arney
 * Date:       3/13/2023
 */

import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;

import java.io.*;
import java.util.*;
import java.util.List;

/**
 A class that represents the Wordle game which can generate a
 random word Rand let the user guess up to 6 guesses.
 */
public class Wordle {

    public static final Color DIRECT_COLOR = Color.web("#6ca965");
    public static final Color INDIRECT_COLOR = Color.web("#c8b653");
    public static final Color NONE_COLOR = Color.web("#363636");

    private static final int MAX_GUESSES = 6;
    private int remainingGuesses;
    private final Map<Character, Paint> lettersGuessed;
    private final List<String> words = new ArrayList<>();
    private final String secretWord;
    private Color[] colorBuffer;

    public Wordle() {
        this.remainingGuesses = MAX_GUESSES;
        this.secretWord = generateSecretWord(AdminController.getFile());
        lettersGuessed = new HashMap<>();
    }

    public String getSecretWord(){
        return secretWord;
    }

    /**
     Generates a secret word from the given text file of words
     @return the secret word
     */
    private String generateSecretWord(File wordListFile) {
        try {
            FileInputStream fileInputStream;
            if (wordListFile != null) {
                fileInputStream = new FileInputStream(wordListFile);
            } else {
                // Defaults to the word list at the start
                fileInputStream = new FileInputStream("src/Group1/wordle-official.txt");
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String word;
            while ((word = bufferedReader.readLine()) != null) {
                words.add(word.toLowerCase());
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
     * Checks the word list for the existence of the guess.
     * @param guess The guessed word
     * @return True if the guess appears within the word list, false otherwise.
     * @author NZawarus
     */
    public boolean checkRealWord(String guess){
        return words.contains(guess);
    }


    public Color[] perWordLetterCheck(String theGuess, String theTruth, boolean saveBuffer){
        Color[] ret = perWordLetterCheck(theGuess, theTruth);
        if(saveBuffer){
            colorBuffer = ret;
        }
        return ret;
    }

    /**
     * Returns a 1xn array of colors to use in the GUI representation of the letters.
     * theGuess and theTruth MUST be the same length.
     * @param theGuess The guess word to compare against the true word
     * @param theTruth The true word to be compared against
     * @return A 1xn Array where each color is respective to the letter of the guess word
     */
    public static Color[] perWordLetterCheck(String theGuess, String theTruth){

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

    public String getLetterHint(String theTruth){
        return getLetterHint(theTruth, colorBuffer);
    }

    /**
     * For User Story 8, returns a String with a single correct letter in its correct place
     * All other strings as asterisks
     * NOTE: The color array MUST be the same length as the word.
     * @param theTruth The correct word, used to create a hint from.
     * @param colors Optionally passed in, in order to avoid making hints for 'green' spaces
     * @throws IllegalArgumentException when the length of the color array is not equal to the
     * length of the 'true' string.
     * @return A string containing the hint.
     */
    public String getLetterHint(String theTruth, Color[] colors){

        if (colors == null){
            colors = new Color[theTruth.length()];
            Arrays.fill(colors, NONE_COLOR); // Colors technically does not matter
        }


        List<Integer> possiblePositions = new ArrayList<>();
        for(int i = 0; i < colors.length; ++i){
            if(colors[i] != DIRECT_COLOR){
                possiblePositions.add(i);
            }
        }
        final int hintPosition = possiblePositions.get((int)(Math.random()*possiblePositions.size()));
        final char hintChar = theTruth.charAt(hintPosition);
        final String hiddenChar = "[_] ";
        StringBuilder ret = new StringBuilder();
        for(int i = 0; i < theTruth.length(); ++i){
            if(i == hintPosition){
                ret.append("[").append(hintChar).append("] ");
            } else {
                ret.append(hiddenChar);
            }
        }
        return ret.toString();
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

    public void setColorBuffer(Color[] colorBuffer) {
        this.colorBuffer = colorBuffer;
    }
    public void setRemainingGuesses(int numGuesses){
        this.remainingGuesses = numGuesses;
    }
    public int getRemainingGuesses() {
        return remainingGuesses;
    }

}