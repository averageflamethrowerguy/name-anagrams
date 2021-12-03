import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogTableConstructor {
    public static int loadTable(String path, Map<String, Integer> map) throws IOException {
        BufferedReader reader  = new BufferedReader(new FileReader(path));
        int sum = 0;

        String nextLine;
        while ((nextLine = reader.readLine()) != null) {
            String[] splitLine = nextLine.split(":");
            map.put(splitLine[0], Integer.parseInt(splitLine[1]));
            sum += Integer.parseInt(splitLine[1]);
        }

        reader.close();
        return sum;
    }

    /**
     * We compute a log score
     *
     * @param word                        the word to score
     * @param frequency                   the frequency of the word
     * @param urbanWordsCount             the total count of urban words
     * @param wikipediaWordsMap           the Wikipedia words
     * @param wikipediaWordsCount         the count of Wikipedia words
     * @return                            the log score
     */
    public static double computeScore(
            String word,
            int frequency,
            int urbanWordsCount,
            Map<String, Integer> wikipediaWordsMap,
            int wikipediaWordsCount
    ) {
        if (!wikipediaWordsMap.containsKey(word)) {
            return Math.log(((double) frequency) / urbanWordsCount * wikipediaWordsCount);
        } else {
            int countInWikipedia = wikipediaWordsMap.get(word);

            if (word.equals("fuck")) {
                System.out.println((((double) frequency) / urbanWordsCount));
                System.out.println(((double) countInWikipedia) / wikipediaWordsCount);
            }

            // we strongly discount the value if it appears in Wikipedia
            return Math.log(
                    (((double) frequency) / urbanWordsCount)
                    / (((double) countInWikipedia) / wikipediaWordsCount)
            );
        }
    }

    public static int makeLogToInteger(double log) {
        return (int) (1000000 * log);
    }

    public static void constructLogTable(
            String urbanWordsPath,
            String wikipediaWordsPath,
            String outputWordsPath
    ) {
        Map<String, Integer> urbanWordsMap = new HashMap<>();
        Map<String, Integer> wikipediaWordsMap = new HashMap<>();
        int urbanWordsCount = 0;
        int wikipediaWordsCount = 0;

        try {
            urbanWordsCount = loadTable(urbanWordsPath, urbanWordsMap);
            wikipediaWordsCount = loadTable(wikipediaWordsPath, wikipediaWordsMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<FrequencyContainer> adjUrbanWordList = new ArrayList<>();

        // iterates over the urban words and computes their corresponding adjusted log representations
        for (Map.Entry<String, Integer> urbanEntry : urbanWordsMap.entrySet()) {
            adjUrbanWordList.add(new FrequencyContainer(
                    // computes a log score and intifies it.
                    makeLogToInteger(computeScore(
                            urbanEntry.getKey(),
                            urbanEntry.getValue(),
                            urbanWordsCount,
                            wikipediaWordsMap,
                            wikipediaWordsCount
                    )),
                    urbanEntry.getKey()
            ));
        }

        // sorts the list to have the most frequent words first
        adjUrbanWordList.sort((container1, container2) -> Integer.compare(container2.frequency, container1.frequency));


        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputWordsPath));

            for (FrequencyContainer container : adjUrbanWordList) {
                writer.write(container.word + ":" + container.frequency + "\n");
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        constructLogTable("./urban-words.txt", "./wikipedia-words.txt", "./log-words.txt");
    }
}
