import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * This class scrapes Urban Dictionary to find a frequency table of words
 * the idea is that frequency on Urban Dictionary should somehow correlate with funniness.
 */
public class UrbanDictionaryScraper {
    /**
     * The master function to scrape Urban Dictionary
     *
     * @param startingUrl                           the URL to begin from
     * @param numberWordsToCollect                  the number of unique words we want to collect
     */
    public static void scrapeUrbanDictionary(String startingUrl, Integer numberWordsToCollect, String saveLocation) {
        Queue<String> urlQueue = new LinkedList<>();
        Map<String, Integer> frequencyMap = new HashMap<>();
        Set<String> travelledUrls = new HashSet<>();

        urlQueue.add(startingUrl);

        // iterates through the queue, finding more links and words
        while (!urlQueue.isEmpty() && (frequencyMap.size() < numberWordsToCollect)) {
            String nextUrl = urlQueue.remove();
            try {
                parsePage(nextUrl, frequencyMap, urlQueue, travelledUrls);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(nextUrl);
            System.out.println("Map Size: " + frequencyMap.size());
        }

        try {
            saveFrequencyMap(frequencyMap, saveLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * A wrapper class to contain frequency information
     */
    private static class FrequencyContainer {
        public int frequency;
        public String word;

        public FrequencyContainer(int frequency, String word) {
            this.frequency = frequency;
            this.word = word;
        }
    }

    /**
     * Saves the frequency map to a file
     *
     * @param frequencyMap                    the frequencyMap to save
     * @param saveLocation                    the location to save it to
     */
    private static void saveFrequencyMap(Map<String, Integer> frequencyMap, String saveLocation) throws IOException {
        List<FrequencyContainer> freqList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
            freqList.add(new FrequencyContainer(entry.getValue(), entry.getKey()));
        }

        freqList.sort((container1, container2) -> Integer.compare(container2.frequency, container1.frequency));

        BufferedWriter writer = new BufferedWriter(new FileWriter(saveLocation));

        for (FrequencyContainer container : freqList) {
            writer.write(container.word + ":" + container.frequency + "\n");
        }

        writer.close();
    }

    /**
     * Adds the word to the frequency map if it is compatible
     *
     * @param word                           the word to add
     * @param frequencyMap                   the map to add it to
     */
    private static void addWordIfCompatible(String word, Map<String, Integer> frequencyMap) {
        // modify the word to trim out punctuation, etc
        word = word.toLowerCase().replaceAll("[.,\"!?\n()*]", "");
        // abort if we fail matches
        if (!word.matches("[a-z]+")) {
            return;
        }

        if (!frequencyMap.containsKey(word)) {
            frequencyMap.put(word, 0);
        }
        // increment up by 1
        frequencyMap.put(word, frequencyMap.get(word)+1);
    }

    /**
     * Parses the url; adds words to the frequencyMap, and updates the urlQueue
     *
     * @param url                           the url to retrieve
     * @param frequencyMap                  the freq map to add to
     * @param urlQueue                      the queue of the next URLs to visit
     * @param travelledUrls                 the urls we've already visited
     */
    public static void parsePage(
            String url,
            Map<String, Integer> frequencyMap,
            Queue<String> urlQueue,
            Set<String> travelledUrls
    ) throws IOException {
        Document doc = Jsoup.connect(url).get();

        Elements links = doc.select("a[href]");
        for (Element link : links) {
            // makes sure it is not an external link, or has been visited previously
            String linkUrl = link.attr("abs:href");
            if (linkUrl.contains("https://www.urbandictionary.com/define.php") && !travelledUrls.contains(linkUrl)) {
                travelledUrls.add(linkUrl);
                urlQueue.add(linkUrl);
            }
        }

        // meaning and examples are the classNames for the target divs in the page
        Elements meanings = doc.select(".meaning");
        Elements examples = doc.select(".example");
        for (Elements contents : new ArrayList<Elements>(){{ add(meanings); add(examples); }} ) {
            for (Element content : contents) {
                // iterate over words, add if compatible
                for (String word : content.text().split(" ")) {
                    addWordIfCompatible(word, frequencyMap);
                }
            }
        }
    }

    public static void main(String[] args) {
        scrapeUrbanDictionary(
                "https://www.urbandictionary.com/",
                1000,
                "./urban-words.txt"
        );
    }
}
