
/**
 * DSHashMap class
 * August 24, 2020
 */

/**
 * Hashes Strings to Integers
 */
import java.math.BigInteger;

public class DSHashMap<E> {
    // Size of the backing array
    int capacity;

    // The backing array
    DSArrayList<DSArrayList<KVP>> a;

    // The number of KVPs in the hash map
    int numberOfKVPs;

    /**
     * Constructor
     */
    public DSHashMap() {
        this(5);
    }

    public DSHashMap(int initial_capacity) {
        this.capacity = initial_capacity;
        this.a = new DSArrayList<DSArrayList<KVP>>(capacity);
        this.numberOfKVPs = 0;
    }

    /**
     * Insert a new value associated with the given key, into the hash map
     */
    public void put(String key, E val) {
        int h = hash(key);

        // Make sure there is a chain in our target slot
        if (a.get(h) == null) {
            a.set(h, new DSArrayList<KVP>());
        }
        DSArrayList<KVP> hkvp = a.get(h); // kvps in chain at index h of backing array a

        // If input key is already in the hash map, replace value with input val
        for (int i = 0; i < hkvp.size(); i++) {
            if (hkvp.get(i).key.equals(key)) {
                hkvp.set(i, new KVP(key, val));
                return;
            }
        }

        // Add the item
        a.get(h).add(new KVP(key, val));
        this.numberOfKVPs++;

        // If the hash map is getting kind of full, make more room
        if (this.numberOfKVPs * 2 > this.capacity)
            rehash();
    }

    /**
     * Create a new backing array that's (at least) twice as large as the current
     * one, and then place all items back into this new backing array.
     */
    private void rehash() {
        int oldCapacity = this.capacity;
        int newCapacity = this.capacity * 2;
        newCapacity = BigInteger.valueOf(newCapacity).nextProbablePrime().intValue();

        this.capacity = newCapacity;

        // Keep a reference to the old backing array
        DSArrayList<DSArrayList<KVP>> olda = a;

        // Build new backing array in memory
        DSArrayList<DSArrayList<KVP>> newa = new DSArrayList<DSArrayList<KVP>>(newCapacity);
        this.numberOfKVPs = 0;

        // Have this DSHashMap's a field point
        // to the new DSArrayList we just built
        this.a = newa;

        // Loop over the backing array, and then all chains,
        // to rehash the KVPs
        for (int i = 0; i < oldCapacity; i++) {
            if (olda.get(i) == null)
                continue;
            for (int j = 0; j < olda.get(i).size(); j++) {
                KVP kvp = olda.get(i).get(j);
                this.put(kvp.key, kvp.value);
            }
        }
    }

    /**
     * Return the value associated with a given key in the hash map Or null if the
     * key is not found
     */
    public E get(String key) {
        int h = hash(key);

        if (a.get(h) == null)
            return null;

        DSArrayList<KVP> hkvp = a.get(h); // kvps in chain at index h of backing array a
        for (int i = 0; i < hkvp.size(); i++) {
            if (key.equals(hkvp.get(i).key))
                return hkvp.get(i).value;
        }
        return null;
    }

    /**
     * Return true if the given key is in the hash map, otherwise false (Therese
     * Aglialoro)
     */
    public boolean containsKey(String key) {
        return this.get(key) != null;
    }

    /**
     * Returns a DSArrayList of all the keys in the DSHashMap (Andy Ferguson)
     */
    public DSArrayList<String> getKeys() {
        DSArrayList<String> rv = new DSArrayList<String>();

        for (int i = 0; i < capacity; i++) {
            if (a.get(i) != null) {
                int chainSize = a.get(i).size();
                for (int j = 0; j < chainSize; j++) {
                    KVP CurrentKVP = a.get(i).get(j);
                    String CurrentKey = CurrentKVP.key;
                    rv.add(CurrentKey);
                }
            }

        }
        return rv;
    }

    /**
     * Returns a DSArrayList of all the values in the DSHashMap
     */
    public DSArrayList<E> values() {
        DSArrayList<E> values = new DSArrayList<E>();

        for (String key : this.getKeys())
            values.add(this.get(key));

        return values;
    }

    /**
     * Print map info
     */
    public void printStats() {
        int max = 0, min = 1000000;
        for (int i = 0; i < this.capacity; i++) {
            if (a.get(i) == null)
                continue;
            int s = a.get(i).size();
            System.out.println(i + ": " + s);
            if (s > max)
                max = s;
            if (s < min)
                min = s;
        }
        System.out.println("Max = " + max + ", min = " + min);
    }

    /**
     * Hash function, returning the index of the chain that this key should be in.
     */
    private int hash(String s) {
        int val = 0;
        int multiplier = 2;
        for (int i = 0; i < s.length(); i++)
            val = (val * multiplier + (int) (s.charAt(i))) % capacity;

        return val;
    }

    /**
     * Inner calss to hold key-value pairs.
     **/
    private class KVP {
        String key;
        E value;

        public KVP(String key, E value) {
            this.key = key;
            this.value = value;
        }
    }
}
