package net.minecraft.src;

public class MCHash<V> {
    private transient MCHash.MCHashEntry<V>[] slots;
    private transient int count;
    private int threshold;
    private final float growFactor;
    private transient volatile int versionStamp;

    public MCHash() {
        this.growFactor = 0.75F;
        this.threshold = 12;
        this.slots = new MCHash.MCHashEntry[16];
    }

    private static int computeHash(int key) {
        key ^= key >>> 20 ^ key >>> 12;
        return key ^ key >>> 7 ^ key >>> 4;
    }

    private static int getSlotIndex(int hash, int cap) {
        return hash & cap - 1;
    }

    public V lookup(int key) {
        int i = computeHash(key);

        for (MCHash.MCHashEntry<V> mchash$mchashentry = this.slots[getSlotIndex(i, this.slots.length)];
            mchash$mchashentry != null;
            mchash$mchashentry = mchash$mchashentry.nextEntry
        ) {
            if (mchash$mchashentry.hashEntry == key) {
                return mchash$mchashentry.valueEntry;
            }
        }

        return null;
    }

    public boolean containsItem(int key) {
        return this.lookupEntry(key) != null;
    }

    final MCHash.MCHashEntry<V> lookupEntry(int key) {
        int i = computeHash(key);

        for (MCHash.MCHashEntry<V> mchash$mchashentry = this.slots[getSlotIndex(i, this.slots.length)];
            mchash$mchashentry != null;
            mchash$mchashentry = mchash$mchashentry.nextEntry
        ) {
            if (mchash$mchashentry.hashEntry == key) {
                return mchash$mchashentry;
            }
        }

        return null;
    }

    public void addKey(int key, V value) {
        int i = computeHash(key);
        int j = getSlotIndex(i, this.slots.length);

        for (MCHash.MCHashEntry<V> mchash$mchashentry = this.slots[j]; mchash$mchashentry != null; mchash$mchashentry = mchash$mchashentry.nextEntry) {
            if (mchash$mchashentry.hashEntry == key) {
                mchash$mchashentry.valueEntry = (V)value;
            }
        }

        this.versionStamp++;
        this.insert(i, key, (V)value, j);
    }

    private void grow(int size) {
        MCHash.MCHashEntry[] amchash$mchashentry = this.slots;
        int i = amchash$mchashentry.length;
        if (i == 1073741824) {
            this.threshold = Integer.MAX_VALUE;
        } else {
            MCHash.MCHashEntry[] amchash$mchashentry1 = new MCHash.MCHashEntry[size];
            this.copyTo(amchash$mchashentry1);
            this.slots = amchash$mchashentry1;
            this.threshold = (int)(size * this.growFactor);
        }
    }

    private void copyTo(final MCHashEntry<V>[] nodes) {
        final MCHashEntry<V>[] slots = this.slots;
        final int length = nodes.length;
        for (int i = 0; i < slots.length; ++i) {
            MCHashEntry<V> mcHashEntry = slots[i];
            if (mcHashEntry != null) {
                slots[i] = null;
                do {
                    final MCHashEntry<V> nextEntry = mcHashEntry.nextEntry;
                    final int slotIndex = getSlotIndex(mcHashEntry.slotHash, length);
                    mcHashEntry.nextEntry = nodes[slotIndex];
                    nodes[slotIndex] = mcHashEntry;
                    mcHashEntry = nextEntry;
                } while (mcHashEntry != null);
            }
        }
    }

    public V removeObject(int key) {
        MCHash.MCHashEntry<V> mchash$mchashentry = this.removeEntry(key);
        return mchash$mchashentry == null ? null : mchash$mchashentry.valueEntry;
    }

    final MCHash.MCHashEntry<V> removeEntry(int key) {
        int i = computeHash(key);
        int j = getSlotIndex(i, this.slots.length);
        MCHash.MCHashEntry<V> mchash$mchashentry = this.slots[j];
        MCHash.MCHashEntry<V> mchash$mchashentry1 = mchash$mchashentry;

        while (mchash$mchashentry1 != null) {
            MCHash.MCHashEntry<V> mchash$mchashentry2 = mchash$mchashentry1.nextEntry;
            if (mchash$mchashentry1.hashEntry == key) {
                this.versionStamp++;
                this.count--;
                if (mchash$mchashentry == mchash$mchashentry1) {
                    this.slots[j] = mchash$mchashentry2;
                } else {
                    mchash$mchashentry.nextEntry = mchash$mchashentry2;
                }

                return mchash$mchashentry1;
            }

            mchash$mchashentry = mchash$mchashentry1;
            mchash$mchashentry1 = mchash$mchashentry2;
        }

        return mchash$mchashentry1;
    }

    public void clearMap() {
        this.versionStamp++;
        MCHash.MCHashEntry[] amchash$mchashentry = this.slots;

        for (int i = 0; i < amchash$mchashentry.length; i++) {
            amchash$mchashentry[i] = null;
        }

        this.count = 0;
    }

    private void insert(int hash, int key, V value, int index) {
        MCHash.MCHashEntry<V> mchash$mchashentry = this.slots[index];
        this.slots[index] = new MCHash.MCHashEntry<>(hash, key, (V)value, mchash$mchashentry);
        if (this.count++ >= this.threshold) {
            this.grow(2 * this.slots.length);
        }
    }

    static class MCHashEntry<V> {
        final int hashEntry;
        V valueEntry;
        MCHash.MCHashEntry<V> nextEntry;
        final int slotHash;

        MCHashEntry(int hash, int key, V value, MCHash.MCHashEntry<V> next) {
            this.valueEntry = (V)value;
            this.nextEntry = next;
            this.hashEntry = key;
            this.slotHash = hash;
        }

        public final int getHash() {
            return this.hashEntry;
        }

        public final V getValue() {
            return this.valueEntry;
        }

        @Override
        public final boolean equals(Object var1) {
            if (!(var1 instanceof MCHash.MCHashEntry)) {
                return false;
            }

            MCHash.MCHashEntry<V> mchash$mchashentry = (MCHash.MCHashEntry)var1;
            Integer integer = this.getHash();
            Integer integer1 = mchash$mchashentry.getHash();
            if (integer == integer1 || integer != null && integer.equals(integer1)) {
                Object object = this.getValue();
                Object object1 = mchash$mchashentry.getValue();
                if (object == object1 || object != null && object.equals(object1)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public final int hashCode() {
            return MCHash.computeHash(this.hashEntry);
        }

        @Override
        public final String toString() {
            return this.getHash() + "=" + this.getValue();
        }
    }
}
