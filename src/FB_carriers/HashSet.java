package FB_carriers;

public class HashSet<E> {

    /**
     * The initial capacity of the LinearProbingHashMap when created with the
     * default constructor.
     *
     * DO NOT MODIFY THIS VARIABLE!
     */
    public static final int INITIAL_CAPACITY = 13;

    /**
     * The max load factor of the LinearProbingHashMap
     *
     * DO NOT MODIFY THIS VARIABLE!
     */
    public static final double MAX_LOAD_FACTOR = 0.67;

    // Do not add new instance variables or modify existing ones.
    private E[] table;
    private boolean[]removed;
    private int size;

    /**
     * Constructs a new LinearProbingHashMap.
     *
     * The backing array should have an initial capacity of INITIAL_CAPACITY.
     *
     * Use constructor chaining.
     */
    public HashSet() {
        this(INITIAL_CAPACITY);
    }

    /**
     * Constructs a new LinearProbingHashMap.
     *
     * The backing array should have an initial capacity of initialCapacity.
     *
     * You may assume initialCapacity will always be positive.
     *
     * @param initialCapacity the initial capacity of the backing array
     */
    public HashSet(int initialCapacity) {
        table = (E[])
                new Object[initialCapacity];
        removed = new boolean[initialCapacity];
        size = 0;
    }

    /**
     * Adds the given key-value pair to the map. If an entry in the map already
     * has this key, replace the entry's value with the new one passed in.
     *
     * In the case of a collision, use linear probing as your resolution
     * strategy.
     *
     * Before actually adding any data to the HashMap, you should check to see
     * if the array would violate the max load factor if the data was added. For
     * example, let's say the array is of length 5 and the current size is 3 (LF
     * = 0.6). For this example, assume that no elements are removed in between
     * steps. If another entry is attempted to be added, before doing anything
     * else, you should check whether (3 + 1) / 5 = 0.8 is larger than the max
     * LF. It is, so you would trigger a resize before you even attempt to add
     * the data or figure out if it's a duplicate. Be careful to consider the
     * differences between integer and double division when calculating load
     * factor.
     *
     * When regrowing, resize the length of the backing table to 2 * old length
     * + 1. You must use the resizeBackingTable method to do so.
     *
     * Return null if the key was not already in the map. If it was in the map,
     * return the old value associated with it.
     *
     * @param key   the key to add
     * @return null if the key was not already in the map. If it was in the map,
     *         return the old value associated with it
     * @throws IllegalArgumentException if key or value is null
     */
    public void add(E key) {
        if (key == null) {
            throw new IllegalArgumentException(
                    "Tried to add null key into LienarProbingHashMap");
        }


        // case for resizing when projected above load factor
        if ((size + 1.0) / table.length > MAX_LOAD_FACTOR) {
            int newSize = 2 * table.length + 1;
            resizeBackingTable(newSize);
            removed = new boolean[newSize];
        }

        int hash = Math.abs(key.hashCode() % table.length);
        int probe = 0;
        int insert = -1;
        int entry = hash % table.length;
        while (probe < table.length && table[entry] != null) {
            if (removed[entry]) {
                // case for seeing a DEL marker
                if (insert == -1) {
                    insert = probe + hash;
                }
            } else {
                // duplicate case
                if (table[entry].equals(key)) {
                    return;
                }
            }
            entry = (++probe + hash) % table.length;
        }

        // insertion at first DEL marker or null
        if (insert == -1) {
            table[entry] = key;
        } else {
            table[insert] = key;
        }
        size++;

    }

    /**
     * Removes the entry with a matching key from map by marking the entry as
     * removed.
     *
     * @param key the key to remove
     * @return the value previously associated with the key
     * @throws IllegalArgumentException if key is null
     * @throws java.util.NoSuchElementException   if the key is not in the map
     */
    public void remove(E key) {
        if (key == null) {
            throw new IllegalArgumentException(
                    "Tried to remove null key from LinearProbingHashMap");
        }

        int hash = Math.abs(key.hashCode() % table.length);
        int probe = 0;
        int entry = hash % table.length;
        while (probe < table.length && table[entry] != null) {
            // mark DEL on finding key
            if (!removed[entry] && table[entry].equals(key)) {
                removed[entry] = true;
                size--;
            }
            entry = (++probe + hash) % table.length;
        }
    }



    /**
     * Returns whether or not the key is in the map.
     *
     * @param key the key to search for in the map
     * @return true if the key is contained within the map, false otherwise
     * @throws IllegalArgumentException if key is null
     */
    public boolean contains(E key) {
        if (key == null) {
            throw new IllegalArgumentException(
                    "Tried to query if LinearProbingHashMap contains null key");
        }

        int hash = Math.abs(key.hashCode() % table.length);
        int probe = 0;
        int entry = hash % table.length;
        while (probe < table.length && table[entry] != null) {
            // return true on finding entry with same key
            if (!removed[entry] && table[entry].equals(key)) {
                return true;
            }
            entry = (++probe + hash) % table.length;

        }
        return false;
    }



    /**
     * Resize the backing table to length.
     *
     * Disregard the load factor for this method. So, if the passed in length is
     * smaller than the current capacity, and this new length causes the table's
     * load factor to exceed MAX_LOAD_FACTOR, you should still resize the table
     * to the specified length and leave it at that capacity.
     *
     * You should iterate over the old table in order of increasing index and
     * add entries to the new table in the order in which they are traversed.
     * You should NOT copy over removed elements to the resized backing table.
     *
     * Since resizing the backing table is working with the non-duplicate data
     * already in the table, you shouldn't explicitly check for duplicates.
     *
     * Hint: You cannot just simply copy the entries over to the new array.
     *
     * @param length new length of the backing table
     * @throws IllegalArgumentException if length is less than the
     *                                            number of items in the hash
     *                                            map
     */
    public void resizeBackingTable(int length) {
        if (size > length) {
            throw new IllegalArgumentException(
                    "Tried to resize to a backing array smaller than "
                            + "number of elements in LinearProbingHashMap");
        }
        E[] temp = (E[])
                new Object[length];

        int count = 0;
        int index = 0;
        while (count < size) {
            if (table[index] != null && !removed[index]) {
                int hash = Math.abs(table[index].hashCode() % length);
                int probe = hash;
                while (temp[probe] != null) {
                    probe = (probe + 1) % length;
                }
                temp[probe] = table[index];
                count++;
            }
            index++;
        }
        table = temp;

    }

    /**
     * Clears the map.
     *
     * Resets the table to a new array of the INITIAL_CAPACITY and resets the
     * size.
     *
     * Must be O(1).
     */
    public void clear() {
        table = (E[])
                new Object[INITIAL_CAPACITY];
        removed = new boolean[INITIAL_CAPACITY];
        size = 0;
    }

    /**
     * Returns the size of the map.
     *
     * For grading purposes only. You shouldn't need to use this method since
     * you have direct access to the variable.
     *
     * @return the size of the map
     */
    public int size() {
        // DO NOT MODIFY THIS METHOD!
        return size;
    }
}

