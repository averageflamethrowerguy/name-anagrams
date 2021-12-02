import java.util.List;

/**
 * A wrapper class to contain frequency information
 */

public class FrequencyContainer {
    public int frequency;
    public String word;

    public FrequencyContainer(int frequency, String word) {
        this.frequency = frequency;
        this.word = word;
    }

    /**
     * Gets the sum of frequencies for a list of containers
     * @param containers                 the list of containers
     * @return                           the frequency sum
     */
    public static int getFrequencySum(List<FrequencyContainer> containers) {
        int sum = 0;
        for (FrequencyContainer container : containers) {
            sum += container.frequency;
        }
        return sum;
    }

    @Override
    public String toString() {
        return word + ":" + frequency;
    }
}
