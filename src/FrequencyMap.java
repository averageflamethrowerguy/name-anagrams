import java.util.HashMap;
import java.util.Map;

/**
 * A wrapper class that allows for an unusual size implementation (records all characters in string, not just unique
 * characters)
 */
public class FrequencyMap {
    private Map<Character, Integer> backingMap = new HashMap<>();
    private Integer size = 0;

    public FrequencyMap() {

    }

    public FrequencyMap(Map<Character, Integer> backingMap, Integer size) {
        this.backingMap = backingMap;
        this.size = size;
    }

    /**
     * Updates a cell of the freqMap
     *
     * @param character
     * @param integer
     */
    public void put(Character character, Integer integer) {
        int previousInteger = 0;
        if (backingMap.containsKey(character)) {
            previousInteger = backingMap.get(character);
        }

        int difference = integer - previousInteger;
        size += difference;
        backingMap.put(character, integer);
    }

    /**
     * Increases the value of the backingMap by 1
     *
     * @param character
     */
    public void increment(Character character) {
        if (!backingMap.containsKey(character)) {
            backingMap.put(character, 0);
        }
        backingMap.put(character, backingMap.get(character)+1);
        size++;
    }

    /**
     * Decreases the value of the backingMap by 1, or deletes the entry if underflow
     *
     * @param character
     */
    public void decrement(Character character) {
        // this will throw an NPE if character is not in the map. This is desirable
        int count = backingMap.get(character);
        count--;
        if (count <= 0) {
            backingMap.remove(character);
        }
        else {
            backingMap.put(character, count);
        }
        size--;
    }

    /**
     * Makes a copy of the FrequencyMap
     * @return                  the copied FrequencyMap
     */
    public FrequencyMap copy() {
        return new FrequencyMap(backingMap, size);
    }

    public boolean contains(Character character) {
        return backingMap.containsKey(character);
    }

    public int size() {
        return size;
    }
}
