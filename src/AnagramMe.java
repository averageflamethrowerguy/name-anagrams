import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class AnagramMe {
    String name;
    String dictPath;
    List<String> dict = new ArrayList<>();

    public AnagramMe(String name, String dictPath) {
        this.name = name;
        this.dictPath = dictPath;
        try {
            loadDict();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the dictionary into an ArrayList
     * @throws IOException
     */
    private void loadDict() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(dictPath));
        String nextLine;

        while ((nextLine = reader.readLine()) != null) {
            // for now, use only 4+ letter words
            if (nextLine.length() > 3) {
                dict.add(nextLine);
            }
        }

        // sorts with largest first
        dict.sort((s1, s2) -> {
            if (s1.length() == s2.length()) {
                return 0;
            }
            else {
                return (s1.length() >= s2.length()) ? 1 : -1;
            }
        });
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
        for (Character character : name.toCharArray()) {
            frequencyMap.increment(character);
        }

        return frequencyMap;
    }

    /**
     * Returns a filtered version of the dictionary based on the submitted frequency map
     *
     * @param dict                          the original dict to filter
     * @param frequencyMap                  the frequency map of characters in the search string
     */
    private List<String> filterDict(List<String> dict, FrequencyMap frequencyMap) {
        List<String> filteredDict = new ArrayList<>();
        // we will edit only cloned, rather than original, words
        FrequencyMap clonedFreqs;

        for (String word : dict) {
            clonedFreqs = frequencyMap.copy();

            if (word.length() <= clonedFreqs.size()) {
                for (Character character : word.toCharArray()) {
                    if (clonedFreqs.contains(character)) {
                        clonedFreqs.decrement(character);
                    }
                    else {
                        // breaks the inner loop and continues to the next word
                        break;
                    }
                    // if we get here, we can add to the new dict
                    filteredDict.add(word);
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
    private List<List<String>> findAnagrams(String name) {
        List<List<String>> anagrams = new ArrayList<>();
        FrequencyMap frequencyMap = generateFrequencyMap(name);

        anagramHelper(dict, frequencyMap, new ArrayList<>(), anagrams, -1);

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
            List<String> dict,
            FrequencyMap frequencyMap,
            List<String> usedWords,
            List<List<String>> anagrams,
            int minimumIndex
    ) {
        // we filter the dictionary based on the current frequencyMap
        // we only pay attention to a subsection
        List<String> filteredDict = filterDict(dict.subList(minimumIndex, dict.size()), frequencyMap);

        // we keep track of the index to keep from evaluating duplicates
        int index = 0;
        for (String word : filteredDict) {
            // abort if the word is longer than the freq map
            if (word.length() > frequencyMap.size()) {
                continue;
            }

            List<String> newUsedWords = new ArrayList(usedWords);
            // if lengths match, we can submit an anagram
            if (word.length() == frequencyMap.size()) {
                newUsedWords.add(word);
                anagrams.add(newUsedWords);
            }

            // otherwise, we recurse
            else {
                FrequencyMap newFrequencyMap = frequencyMap.copy();
                for (Character character : word.toCharArray()) {
                    newFrequencyMap.decrement(character);
                }
                anagramHelper(filteredDict, newFrequencyMap, newUsedWords, anagrams, index);
            }

            index++;
        }
    }

    public static void main(String[] args) {

    }
}
