/*
 * Course:     SE 2800
 * Term:       Spring 2023
 * Assignment: Lab 1: User Stories
 * Authors:    Hudson Arney
 * Date:       3/13/2023
 */

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 A class that represents the Wordle game which can generate a
 random word and let the user guess up to 6 guesses.
 */
public class Wordle {
    private static final int MAX_GUESSES = 6;
    private int remainingGuesses;
    private Map<String, Integer> previousGuesses;
    private List<String> words = new ArrayList<String>();

    public Wordle() {
        this.remainingGuesses = MAX_GUESSES;
        this.previousGuesses = new HashMap<String, Integer>();
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

            checkLetters(secretWord, guess, lettersGuessed);

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
            FileInputStream fileInputStream = new FileInputStream("src\\wordle-official.txt");
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

    private boolean checkRealWord(String guess){
        return words.contains(guess);
    }

    /**
     * Meathod that takes in a guess and a secret word and checks if
     * any of the letters are in the secret word and adds them to the Map.
     * If the letter is in the word its saved as Color.GREEN and Color.GRAY
     * if it isn't
     * @param secretWord the word the user is trying to guess
     * @param guess the word the user did guess
     * @param guessedLetters the map of guessed letters
     * @author Collin Schmocker
     */
    private void checkLetters(String secretWord, String guess, Map<Character, Color> guessedLetters) {
        for(char character: guess.toUpperCase().toCharArray()) {
            guessedLetters.put(character, secretWord.indexOf(character) == -1 ? Color.GREEN: Color.GRAY);
        }
    }
}