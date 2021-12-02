import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class AnagramMe {
    String dictPath;
    List<FrequencyContainer> dict = new ArrayList<>();
    private int wordLengthThreshold = 3;

    public AnagramMe(String dictPath) {
        this.dictPath = dictPath;
        try {
            loadDict();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Computes the score based on the frequency and score of the word
     *
     * @param frequency             the number of times we've found this word
     * @param word                  the length of the word
     * @return                      the score of the word
     */
    private int computeScore(Integer frequency, String word) {
        Integer wordLength = word.length();
        // we try to give a bonus by cube of word length
        return frequency * wordLength * wordLength * wordLength;
    }

    /**
     * Loads the dictionary into an ArrayList
     * @throws IOException
     */
    private void loadDict() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(dictPath));
        String nextLine;

        int numberCollected = 0;
        while ((nextLine = reader.readLine()) != null && numberCollected < 4000) {
            numberCollected++;
            String[] splitLine = nextLine.split(":");
            if (splitLine.length > 1 && splitLine[0].length() >= wordLengthThreshold) {
                dict.add(
                        new FrequencyContainer(
                                computeScore(Integer.parseInt(splitLine[1]), splitLine[0]),
                                splitLine[0]
                        )
                );
            }
            else if (nextLine.length() >= wordLengthThreshold) {
                dict.add(new FrequencyContainer(1, nextLine));
            }
        }

        // sorts with largest score first
        dict.sort((s1, s2) -> {
            if (s1.frequency == s2.frequency) {
                return 0;
            }
            else {
                return (s1.frequency >= s2.frequency) ? -1 : 1;
            }
        });

        reader.close();
        System.out.println("Finished loading the dict");
    }

    /**
     * Generates a Map from characters in the string to frequencies
     *
     * @param name             the name to convert to Map representation
     * @return                 the map representation of the name
     */
    private FrequencyMap generateFrequencyMap(String name) {
        FrequencyMap frequencyMap = new FrequencyMap();

        // iterates over characters
        for (Character character : name.toLowerCase().toCharArray()) {
            // trims out whitespace
            if (character != ' ') {
                frequencyMap.increment(character);
            }
        }

        return frequencyMap;
    }

    /**
     * Returns a filtered version of the dictionary based on the submitted frequency map
     *
     * @param dict                          the original dict to filter
     * @param frequencyMap                  the frequency map of characters in the search string
     */
    private List<FrequencyContainer> filterDict(List<FrequencyContainer> dict, FrequencyMap frequencyMap) {
        List<FrequencyContainer> filteredDict = new ArrayList<>();
        // we will edit only cloned, rather than original, words
        FrequencyMap clonedFreqs;

        for (FrequencyContainer container : dict) {
            clonedFreqs = frequencyMap.copy();

            if (container.word.length() <= clonedFreqs.size()) {
                boolean didFail = false;
                for (Character character : container.word.toCharArray()) {
                    if (clonedFreqs.contains(character)) {
                        clonedFreqs.decrement(character);
                    }
                    else {
                        // breaks the inner loop and continues to the next word
                        didFail = true;
                        break;
                    }
                }
                if (!didFail) {
                    // if we get here, we can add to the new dict
                    filteredDict.add(container);
                }
            }
        }

        return filteredDict;
    }

    /**
     * Gets a list of the anagrams of a name
     *
     * @param name             the name to anagram
     * @return                 the anagrams of the name
     */
    private List<List<FrequencyContainer>> findAnagrams(String name) {
        List<List<FrequencyContainer>> anagrams = new ArrayList<>();
        FrequencyMap frequencyMap = generateFrequencyMap(name);

        anagramHelper(dict, frequencyMap, new ArrayList<>(), anagrams, 0);

        return anagrams;
    }

    /**
     * A helper function to recursively generate anagrams
     *
     * @param dict                     the currently available dictionary (from one layer up)
     * @param frequencyMap             the available characters
     * @param usedWords                the words we've already used
     * @param anagrams                 the List of anagrams we're adding to
     */
    public void anagramHelper(
            List<FrequencyContainer> dict,
            FrequencyMap frequencyMap,
            List<FrequencyContainer> usedWords,
            List<List<FrequencyContainer>> anagrams,
            int minimumIndex
    ) {
        // we filter the dictionary based on the current frequencyMap
        // we only pay attention to a subsection
        List<FrequencyContainer> filteredDict = filterDict(dict.subList(minimumIndex, dict.size()), frequencyMap);

        // we keep track of the index to keep from evaluating duplicates
        int index = 0;
        for (FrequencyContainer container : filteredDict) {
            // abort if the word is longer than the freq map
            if (container.word.length() > frequencyMap.size()) {
                continue;
            }

            List<FrequencyContainer> newUsedWords = new ArrayList<>(usedWords);
            newUsedWords.add(container);

            // if lengths match, we can submit an anagram
            if (container.word.length() == frequencyMap.size()) {
                anagrams.add(newUsedWords);
            }

            // otherwise, we recurse
            else {
                FrequencyMap newFrequencyMap = frequencyMap.copy();
                for (Character character : container.word.toCharArray()) {
                    newFrequencyMap.decrement(character);
                }
                anagramHelper(filteredDict, newFrequencyMap, newUsedWords, anagrams, index);
            }

            index++;
        }
    }

    public static void main(String[] args) {
        AnagramMe anagrammer = new AnagramMe("./urban-words.txt");
        List<List<FrequencyContainer>> anagrams = anagrammer.findAnagrams("Elliot Bayes Potter");
        System.out.println("Sorting the anagrams...");
        // sort in descending order based on score.
        anagrams.sort((anagram1, anagram2) -> {
            int score1 = FrequencyContainer.getFrequencySum(anagram1);
            int score2 = FrequencyContainer.getFrequencySum(anagram2);

            if (score1 == score2) {
                return 0;
            } else if (score1 > score2) {
                return -1;
            } else {
                return 1;
            }
        });
        System.out.println(anagrams.get(0).size());
        System.out.println(anagrams.get(anagrams.size()-1).size());
        System.out.println("Length: " + anagrams.size());
        System.out.println(anagrams.subList(0, 2000));
    }
}
