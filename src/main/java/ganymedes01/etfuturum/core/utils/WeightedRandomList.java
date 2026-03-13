package ganymedes01.etfuturum.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * Creates weighted RNG to simulate the weights on Nether ambience sounds in 1.16
 * Author Philipp from Stack Overflow
 */
public class WeightedRandomList<T> {

    private class Entry {

        double accumulatedWeight;
        T object;
    }

    private final List<Entry> entries = new ArrayList<>();
    private double accumulatedWeight;

    public void addEntry(T object, double weight) {
        accumulatedWeight += weight;
        Entry e = new Entry();
        e.object = object;
        e.accumulatedWeight = accumulatedWeight;
        entries.add(e);
    }

    public T getRandom(Random rand) {
        if (entries.size() == 1) {
            return entries.get(0).object;
        }

        double r = rand.nextDouble() * accumulatedWeight;

        for (Entry entry : entries) {
            if (entry.accumulatedWeight >= r) {
                return entry.object;
            }
        }
        return entries.get(entries.size() - 1).object;
    }
}
