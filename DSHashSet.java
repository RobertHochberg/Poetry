
/**
 * DSHashSet class
 * September 5, 2020
 */

import java.math.BigInteger;

/**
 * Hashes Strings to Integers
 */
public class DSHashSet<E> {
    // Size of the backing array
    int capacity;

    // The backing array
    DSArrayList<DSArrayList<E>> a;

    // Number of items in the Hash Set
    int numberOfItems;

    /**
     * Constructor
     */
    public DSHashSet() {
        this(5);
    }

    public DSHashSet(int initial_capacity) {
        this.capacity = initial_capacity;
        this.a = new DSArrayList<DSArrayList<E>>(capacity);
        this.numberOfItems = 0;
    }

    /**
     * Insert a new value associated with the given key, into the hash map
     */
    public void add(E item) {
        int h = hash(item);

        // Make sure there is a chain in our target slot
        if (a.get(h) == null) {
            a.set(h, new DSArrayList<E>());
        }

        // Check to see if this key is already in the chain
        DSArrayList<E> chain = a.get(h); // reference to this key's chain
        for (int i = 0; i < chain.size(); i++) {
            if (item.equals(chain.get(i))) {
                return; // Found it. Change the value and exit
            }
        }

        // Add the item
        a.get(h).add(item);
        this.numberOfItems++;

        // If there are many items in the HashSet, make more room.
        if(this.numberOfItems * 2 > capacity) rehash();
    }

    /**
     * Create a new backing array that's (at least) twice as large as the current
     * one, and then place all items back into this new backing array.
     */
    private void rehash() {
        int oldCapacity = this.capacity;
        int newCapacity = this.capacity * 2;
        newCapacity = BigInteger.valueOf(newCapacity).nextProbablePrime().intValue();
        //System.out.println("New Capacity: " + newCapacity);

        this.capacity = newCapacity;

        // Keep a reference to the old backing array
        DSArrayList<DSArrayList<E>> olda = a;

        // Build new backing array in memory
        DSArrayList<DSArrayList<E>> newa = new DSArrayList<DSArrayList<E>>(newCapacity);
        this.numberOfItems = 0;

        // Have this DSHashMap's a field point
        // to the new DSArrayList we just built
        this.a = newa;

        // Loop over the backing array, and then all chains,
        // to rehash the KVPs
        for (int i = 0; i < oldCapacity; i++) {
            if (olda.get(i) == null)
                continue;
            for (int j = 0; j < olda.get(i).size(); j++) {
                this.add(olda.get(i).get(j));
            }
        }
    }

    /**
     * Determines if a given value has been added to the Hash Set
     * 
     * @param key The item to search for
     * @return true if the item is in the hash set, otherwise false.
     */
    public Boolean contains(E item) {
        int h = hash(item);
        DSArrayList<E> chain = a.get(h); // a direct reference to the chain we're looking at
        if (chain == null)
            return false;

        // Loop over the chain until we find the key
        for (int i = 0; i < chain.size(); i++)
            if (chain.get(i).equals(item))
                return true; // Found the key. Return the value.

        // If we make it here, then the key was not in the hash map
        return false;
    }

    /**
     * Returns a DSArrayList of all the keys in the DSHashMap
     */
    public DSArrayList<E> toDSArrayList() {
        DSArrayList<E> rv = new DSArrayList<E>();

        // Loop over all slots in the backing array
        for (int i = 0; i < this.capacity; i++) {
            DSArrayList<E> chain = a.get(i);
            if (chain == null)
                continue;
            // Loop over all KVPs in the chain
            for (int j = 0; j < chain.size(); j++)
                rv.add(chain.get(j));
        }

        return rv;
    }

    /**
     * Size of the HashSet
     * 
     * @return the number of items in this hash set
     */
    public int size(){
        return this.numberOfItems;
    }

    /**
     * Print map info
     */
    public void printStats() {
        int max = 0, min = 1000000;
        for (int i = 0; i < this.capacity; i++) {
            if (a.get(i) != null) {
                int s = a.get(i).size();
                System.out.println(i + ": " + s);
                if (s > max)
                    max = s;
                if (s < min)
                    min = s;
            }
        }
        System.out.println("Max = " + max + ", min = " + min);
    }

    /**
     * @return a string representation of the DSHashMap
     */
    public String toString() {
        DSArrayList<E> keys = this.toDSArrayList();
        keys.sort();
        String rv = "{";
        for (int i = 0; i < keys.size(); i++) {
            E key = keys.get(i);
            rv = rv + key;
            if (i != keys.size() - 1)
                rv += ", ";
        }
        rv += "}";
        return rv;
    }

    /**
     * Hash function, returning the index of the chain that this key should be in.
     */
    private int hash(E s) {
        return Math.abs(s.hashCode()) % this.capacity;
    }
}
