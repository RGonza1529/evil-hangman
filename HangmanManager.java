import java.util.*;
import java.lang.*;

/**
 * Manages the details of EvilHangman. This class keeps
 * tracks of the possible words from a dictionary during
 * rounds of hangman, based on guesses so far.
 *
 */
public class HangmanManager 
{
    // instance variables / fields
    private Set<String> words;
    private HashMap<Integer, ArrayList<String>> mapOfWords;
    private ArrayList<String> currWords;
    private ArrayList<String> lettersGuessed;
    private String currPattern;
    private boolean debugOn;
    private int numGuesses;
    private HangmanDifficulty diff;


    /**
     * Create a new HangmanManager from the provided set of words and phrases.
     * pre: words != null, words.size() > 0
     * @param words A set with the words for this instance of Hangman.
     * @param debugOn true if we should print out debugging to System.out.
     */
    public HangmanManager(Set<String> words, boolean debugOn) 
    {
        //checking precondition
        if (words == null || words.size() <= 0)
        {
            throw new IllegalArgumentException("words cannot be null or words.size() <= 0");
        }

        //establishing variables
        this.debugOn = debugOn;
        this.mapOfWords = new HashMap<>();

        //iterating through the dictionary and adding words to this.mapOfWords
        Iterator<String> i = words.iterator();
        while (i.hasNext())
        {
            String temp = i.next();
            int length = temp.length();
            ArrayList<String> list = mapOfWords.get(length);
            if (list == null)
            {
                list = new ArrayList<>();
                mapOfWords.put(length, list);
            }
            mapOfWords.get(length).add(temp);
        }
    }

    /**
     * Create a new HangmanManager from the provided set of words and phrases.
     * Debugging is off.
     * pre: words != null, words.size() > 0
     * @param words A set with the words for this instance of Hangman.
     */
    public HangmanManager(Set<String> words) 
    {
        //checking precondition
        if (words == null || words().size <= 0)
        {
            throw new IllegalArgumentException("words cannot be null or words.size() <= 0");
        }

        //establishing variables
        this.debugOn = false;
        this.mapOfWords = new HashMap<>();

        //iterating through the dictionary and adding words to this.mapOfWords
        Iterator<String> i = words.iterator();
        while (i.hasNext())
        {
            String temp = i.next();
            int length = temp.length();
            ArrayList<String> list = mapOfWords.get(length);
            if (list == null)
            {
                list = new ArrayList<>();
                mapOfWords.put(length, list);
            }
            mapOfWords.get(length).add(temp);
        }
    }


    /**
     * Get the number of words in this HangmanManager of the given length.
     * pre: none
     * @param length The given length to check.
     * @return the number of words in the original Dictionary
     * with the given length
     */
    public int numWords(int length) 
    {
        if (this.mapOfWords.get(length) == null)
        {
            return 0;
        }
        return this.mapOfWords.get(length).size();
    }


    /**
     * Get for a new round of Hangman. Think of a round as a
     * complete game of Hangman.
     * @param wordLen the length of the word to pick this time.
     * numWords(wordLen) > 0
     * @param numGuesses the number of wrong guesses before the
     * player loses the round. numGuesses >= 1
     * @param diff The difficulty for this round.
     */
    public void prepForRound(int wordLen, int numGuesses, HangmanDifficulty diff) 
    {
        //establishing variables
        StringBuilder b = new StringBuilder();
        this.numGuesses = numGuesses;
        this.diff = diff;
        this.currWords = this.mapOfWords.get(wordLen); //adding words of wordLen Length to currWords ArrayList
        this.lettersGuessed = new ArrayList<>();
        
        //creating secret pattern
        for (int i = 0; i < wordLen; i++)
        {
            b.append("-");
        }
        this.currPattern = b.toString();
    }


    /**
     * The number of words still possible (live) based on the guesses so far.
     *  Guesses will eliminate possible words.
     * @return the number of words that are still possibilities based on the
     * original dictionary and the guesses so far.
     */
    public int numWordsCurrent() 
    {
        return this.currWords.size();
    }


    /**
     * Get the number of wrong guesses the user has left in
     * this round (game) of Hangman.
     * @return the number of wrong guesses the user has left
     * in this round (game) of Hangman.
     */
    public int getGuessesLeft() 
    {
        return this.numGuesses;
    }


    /**
     * Return a String that contains the letters the user has guessed
     * so far during this round.
     * The characters in the String are in alphabetical order.
     * The String is in the form [let1, let2, let3, ... letN].
     * For example [a, c, e, s, t, z]
     * @return a String that contains the letters the user
     * has guessed so far during this round.
     */
    public String getGuessesMade() 
    {
        Collections.sort(this.lettersGuessed);
        return this.lettersGuessed.toString();
    }


    /**
     * Check the status of a character.
     * @param guess The character to check.
     * @return true if guess has been used or guessed this round of Hangman,
     * false otherwise.
     */
    public boolean alreadyGuessed(char guess) 
    {
        return this.lettersGuessed.contains(Character.toString(guess));
    }


    /**
     * Get the current pattern. The pattern contains '-''s for
     * unrevealed (or guessed) characters and the actual character 
     * for "correctly guessed" characters.
     * @return the current pattern.
     */
    public String getPattern() 
    {
        return this.currPattern;
    }


    /**
     * Update the game status (pattern, wrong guesses, word list),
     * based on the give guess.
     * @param guess pre: !alreadyGuessed(ch), the current guessed character
     * @return return a tree map with the resulting patterns and the number of
     * words in each of the new patterns.
     * The return value is for testing and debugging purposes.
     */
    public TreeMap<String, Integer> makeGuess(char guess) 
    {
        //checking precondition
        if (alreadyGuessed(guess))
        {
            throw new IllegalStateException(""); //if char has already been guessed
        }

        //establishing/updating variables
        TreeMap <String, Integer> m = new TreeMap<>(); //stores patterns and # of words in each pattern
        lettersGuessed.add(Character.toString(guess));
        TreeMap<String, ArrayList<String>> words = fillTree(guess);
        updateCurrWords(words);

        //creating return TreeMap that stores patterns and number of words in each pattern
        for (Map.Entry<String, ArrayList<String>> entry : words.entrySet())
        {
            m.put(entry.getKey(), entry.getValue().size());
        }

        //decrementing numGuesses if the guess revealed no characters
        if (!this.currPattern.contains(Character.toString(guess)))
        {
            this.numGuesses--;
        }
        return m;
    }


    /**
     * Fills TreeMap with patterns and ArrayLists of words in each pattern
     * @param guess char representing the letter the player guessed
     * @return TreeMap<String, ArrayList<String>>
     */
    private TreeMap<String, ArrayList<String>> fillTree (char guess)
    {
        //establishing variables
        StringBuilder b = new StringBuilder(currPattern);
        TreeMap<String, ArrayList<String>> words = new TreeMap<>();

        //looping through current list of words and checking its pattern
        //relative to the player's guess then adds to TreeMap
        for (int i = 0; i < currWords.size(); i++)
        {
            //checking pattern of String
            for (int j = 0; j < currWords.get(i).length(); j++)
            {
                if (currWords.get(i).charAt(j) == guess)
                {
                    b.setCharAt(j, guess);
                }
            }

            //adding word and pattern to TreeMap
            ArrayList<String> list = words.get(b.toString());
            if (list == null)
            {
                list = new ArrayList<>();
                words.put(b.toString(), list);
            }
            words.get(b.toString()).add(currWords.get(i));
            b = new StringBuilder(currPattern);
        }
        return words;
    }


    /**
     * Update the list of viable words based off of the player's guess
     * @param words TreeMap containing all patterns and ArrayLists of words
     * @return N/A
     */
    private void updateCurrWords (TreeMap<String, ArrayList<String>> words)
    {
        //Created a new TreeMap but with keys and values swapped to make it 
        //easier to find the longest word list.
        TreeMap<Integer, String> wordsSorted = new TreeMap<>();
        for (Map.Entry<String, ArrayList<String>> entry : words.entrySet())
        {
            wordsSorted.put(entry.getValue().size(), entry.getKey());
        }
        ArrayList<String> patternsSorted = new ArrayList<>(wordsSorted.values());

        //picking currWord list based off of difficulty
        if (this.diff == HangmanDifficulty.EASY)
        {
            easyCurrWords(words, patternsSorted);
        }
        else if (this.diff == HangmanDifficulty.MEDIUM)
        {
            mediumCurrWords(words, patternsSorted);
        }
        else 
        {
            hardCurrWords(words);
        }
    }


    /**
     * Update the list of viable names based off of easy difficulty
     * @param TreeMap<String, ArrayList<String>> words, represents the pool of all words,
     * and ArrayList<String> patternsSorted which represents the patters in sorted order based 
     * off of the size of the ArrayList<String>
     * @return N/A
     */
    private void easyCurrWords (TreeMap<String, ArrayList<String>> words, ArrayList<String> patternsSorted)
    {
        int guessCount = this.lettersGuessed.size();

        //gets hardest pattern/word list every other round
        if (guessCount % 2 == 0 && patternsSorted.size() > 1)
        {
            this.currWords = words.get(patternsSorted.get(patternsSorted.size() - 2));
            this.currPattern = patternsSorted.get(patternsSorted.size() - 2);
        }
        else 
        {
            this.currWords = words.get(patternsSorted.get(patternsSorted.size() - 1));
            this.currPattern = patternsSorted.get(patternsSorted.size() - 1);
        }

        //loop that checks for most "optimal" word list
        //i.e. lexicographic order
        for (Map.Entry<String, ArrayList<String>> entry : words.entrySet())
        {
            if (entry.getValue().size() == currWords.size() && entry.getKey().compareTo(this.currPattern) < 0)
            {
                this.currPattern = entry.getKey();
                this.currWords = entry.getValue();
            }
        }
    }


    /**
     * Update the list of viable names based off of medium difficulty
     * @param TreeMap<String, ArrayList<String>> words, represents the pool of all words,
     * and ArrayList<String> patternsSorted which represents the patters in sorted order based 
     * off of the size of the ArrayList<String>
     * @return N/A
     */
    private void mediumCurrWords (TreeMap<String, ArrayList<String>> words, ArrayList<String> patternsSorted)
    {
        int guessCount = this.lettersGuessed.size();

        //gets hardest pattern/word list every fourth round
        if (guessCount % 4 == 0 && patternsSorted.size() > 1)
        {
            this.currWords = words.get(patternsSorted.get(patternsSorted.size() - 2));
            this.currPattern = patternsSorted.get(patternsSorted.size() - 2);
        }
        else 
        {
            this.currWords = words.get(patternsSorted.get(patternsSorted.size() - 1));
            this.currPattern = patternsSorted.get(patternsSorted.size() - 1);
        }

        //loop that checks for most "optimal" word list
        //i.e. lexicographic order
        for (Map.Entry<String, ArrayList<String>> entry : words.entrySet())
        {
            if (entry.getValue().size() == currWords.size() && entry.getKey().compareTo(this.currPattern) < 0)
            {
                this.currPattern = entry.getKey();
                this.currWords = entry.getValue();
            }
        }
    }


    /**
     * Update the list of viable names based off of hard difficulty
     * @param TreeMap<String, ArrayList<String>> words, represents the pool of all words
     * @return N/A
     */
    private void hardCurrWords (TreeMap<String, ArrayList<String>> words)
    {
        //loop to find the hardest/longest pattern/word list
        ArrayList<String> longestList = new ArrayList<>();
        for (Map.Entry<String, ArrayList<String>> entry : words.entrySet())
        {
            if (entry.getValue().size() > longestList.size())
            {
                this.currPattern = entry.getKey();
                longestList = entry.getValue();
            }
        }
        this.currWords = longestList;

        //loop that checks for most "optimal" word list
        //i.e. lexicographic order
        for (Map.Entry<String, ArrayList<String>> entry : words.entrySet())
        {
            if (entry.getValue().size() == longestList.size() && entry.getKey().compareTo(this.currPattern) < 0)
            {
                this.currPattern = entry.getKey();
                this.currWords = entry.getValue();
            }
        }
    }


    /**
     * Return the secret word this HangmanManager finally ended up
     * picking for this round.
     * If there are multiple possible words left one is selected at random.
     * <br> pre: numWordsCurrent() > 0
     * @return return the secret word the manager picked.
     */
    public String getSecretWord() 
    {
        //checking precondition
        if (numWordsCurrent() < 0)
        {
            throw new IllegalStateException("");
        }
        //return final word or random word if player runs out of guesses
        return this.currWords.get(0);
    }
}
