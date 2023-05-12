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
    private final List<String> guesses = new ArrayList<>();
    private final String secretWord;
    private Color[] colorBuffer;
    private File currentGuessFile;


    public Wordle() {
        this.remainingGuesses = MAX_GUESSES;
        this.secretWord = generateSecretWord(AdminController.getFile());
        lettersGuessed = new HashMap<>();
        try(BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("src/guess_lists/wordle-official.txt")))){
            String word = in.readLine();
            while(word != null){
                guesses.add(word.toLowerCase());
                word = in.readLine();
            }
        }catch(IOException e){
            System.out.println("Error: could not find guess list");
        }
    }
    public Wordle(File guessListFile){
        currentGuessFile = guessListFile;
        this.remainingGuesses = MAX_GUESSES;
        this.secretWord = generateSecretWord(AdminController.getFile());
        lettersGuessed = new HashMap<>();
        try(BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(guessListFile)))){
            String word = in.readLine();
            while(word != null){
                guesses.add(word.toLowerCase());
                word = in.readLine();
            }
        }catch(IOException e){
            System.out.println("Error: could not find guess list");
        }
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
                fileInputStream = new FileInputStream("src/word_lists/wordle-official.txt");
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
        return guesses.contains(guess);
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
    public List<String> getWords(){
        return words;
    }
    public File getCurrentGuessFile(){
        return currentGuessFile;
    }
    public void setCurrentGuessFile(File guessFile){
        this.currentGuessFile = guessFile;
    }


    /**
     * Checks to make sure that the given word could theoretically fit in the hint.
     * @return If the word could be a hint
     */
    private static boolean eligibleForHint(String theHint, String theTruth, Color[] colorBuffer){
        Color[] compareBuffer = perWordLetterCheck(theHint, theTruth);
        boolean ret = true;
        int mimimumMatches = 0;
        int compareMatches = 0;
        for(int i = 0; i < colorBuffer.length && ret; ++i){
            if(colorBuffer[i] == DIRECT_COLOR){
                ret = compareBuffer[i] == DIRECT_COLOR;
            } else {
                if(colorBuffer[i] == INDIRECT_COLOR){
                    ++mimimumMatches;
                }
                if(compareBuffer[i] == INDIRECT_COLOR || compareBuffer[i] == DIRECT_COLOR){
                    ++compareMatches;
                }
            }
        }
        return ret && compareMatches >= mimimumMatches;
    }


    /**
     * Returns a list of words hints from the static method of the same name, using the instance variables
     *
     * As of note with the static method, the secret word will always be placed at the end of this list
     * It is therefore expected that the program will shuffle the list itself after calling this method
     * @param numHints The number of hints to include, does not include the secret word.
     * @return The list of hints including the secret word, unshuffled.
     */
    public List<String> getWordHints(int numHints){
        return getWordHints(secretWord, colorBuffer, words, numHints);
    }

    /**
     * For User Story 20, returns a list of words that fill the criteria of the given buffer, as well as
     * the true word itself (passed in so that there is not unintentionally a repeat)
     *
     * Of note, the list is not shuffled so the 'true' word will always be the last entry
     * Therefore, it is expected that the user actually shuffle the list of hints.
     * @param theTruth The secret word
     * @param colors The color buffer used to block out obvious fails
     * @param words The list of word to pull the hints from
     * @param numHints The number of hint words to put in the collection, not including the true word
     * @return A list containing the hint words and the secret word
     */
    public static List<String> getWordHints(String theTruth, Color[] colors,
                                                  List<String> words, int numHints){
        final int CHANCE_DIVISOR = 100; // change as desired, performance vs difficulty

        if (colors == null){
            colors = new Color[theTruth.length()];
            for(int i = 0; i < theTruth.length(); ++i){
                colors[i] = NONE_COLOR;
            }
        }

        List<String> ret = new ArrayList<>();
        while(ret.size() < numHints - 1){
            for(int i = 0; i < words.size() && ret.size() < numHints - 1; ++i){
                if(eligibleForHint(words.get(i), theTruth, colors)){
                    if(Math.random() < (float)1/CHANCE_DIVISOR){
                        ret.add(words.get(i));
                    }
                }
            }
        }

        ret.add(theTruth);
        return ret;

    }
}