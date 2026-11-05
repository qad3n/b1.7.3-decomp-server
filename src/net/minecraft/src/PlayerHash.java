package net.minecraft.src;

public class PlayerHash<V> {
    private transient PlayerHash.PlayerHashEntry<V>[] hashArray;
    private transient int numHashElements;
    private int capacity;
    private final float percentUsable;
    private transient volatile int modCount;

    public PlayerHash() {
        this.percentUsable = 0.75F;
        this.capacity = 12;
        this.hashArray = new PlayerHash.PlayerHashEntry[16];
    }

    private static int getHashedKey(long key) {
        return hash((int)(key ^ key >>> 32));
    }

    private static int hash(int key) {
        key ^= key >>> 20 ^ key >>> 12;
        return key ^ key >>> 7 ^ key >>> 4;
    }

    private static int getHashIndex(int hash, int cap) {
        return hash & cap - 1;
    }

    public V getValueByKey(long key) {
        int i = getHashedKey(key);

        for (PlayerHash.PlayerHashEntry<V> playerhash$playerhashentry = this.hashArray[getHashIndex(i, this.hashArray.length)];
            playerhash$playerhashentry != null;
            playerhash$playerhashentry = playerhash$playerhashentry.nextEntry
        ) {
            if (playerhash$playerhashentry.key == key) {
                return playerhash$playerhashentry.value;
            }
        }

        return null;
    }

    public void add(long key, V value) {
        int i = getHashedKey(key);
        int j = getHashIndex(i, this.hashArray.length);

        for (PlayerHash.PlayerHashEntry<V> playerhash$playerhashentry = this.hashArray[j];
            playerhash$playerhashentry != null;
            playerhash$playerhashentry = playerhash$playerhashentry.nextEntry
        ) {
            if (playerhash$playerhashentry.key == key) {
                playerhash$playerhashentry.value = (V)value;
            }
        }

        this.modCount++;
        this.createKey(i, key, (V)value, j);
    }

    private void resizeTable(int cap) {
        PlayerHash.PlayerHashEntry[] aplayerhash$playerhashentry = this.hashArray;
        int i = aplayerhash$playerhashentry.length;
        if (i == 1073741824) {
            this.capacity = Integer.MAX_VALUE;
        } else {
            PlayerHash.PlayerHashEntry[] aplayerhash$playerhashentry1 = new PlayerHash.PlayerHashEntry[cap];
            this.copyHashTableTo(aplayerhash$playerhashentry1);
            this.hashArray = aplayerhash$playerhashentry1;
            this.capacity = (int)(cap * this.percentUsable);
        }
    }

    private void copyHashTableTo(final PlayerHashEntry<V>[] nodes) {
        final PlayerHashEntry<V>[] hashArray = this.hashArray;
        final int length = nodes.length;
        for (int i = 0; i < hashArray.length; ++i) {
            PlayerHashEntry<V> playerHashEntry = hashArray[i];
            if (playerHashEntry != null) {
                hashArray[i] = null;
                do {
                    final PlayerHashEntry<V> nextEntry = playerHashEntry.nextEntry;
                    final int hashIndex = getHashIndex(playerHashEntry.hash, length);
                    playerHashEntry.nextEntry = nodes[hashIndex];
                    nodes[hashIndex] = playerHashEntry;
                    playerHashEntry = nextEntry;
                } while (playerHashEntry != null);
            }
        }
    }

    public V remove(long key) {
        PlayerHash.PlayerHashEntry<V> playerhash$playerhashentry = this.removeKey(key);
        return playerhash$playerhashentry == null ? null : playerhash$playerhashentry.value;
    }

    final PlayerHash.PlayerHashEntry<V> removeKey(long key) {
        int i = getHashedKey(key);
        int j = getHashIndex(i, this.hashArray.length);
        PlayerHash.PlayerHashEntry<V> playerhash$playerhashentry = this.hashArray[j];
        PlayerHash.PlayerHashEntry<V> playerhash$playerhashentry1 = playerhash$playerhashentry;

        while (playerhash$playerhashentry1 != null) {
            PlayerHash.PlayerHashEntry<V> playerhash$playerhashentry2 = playerhash$playerhashentry1.nextEntry;
            if (playerhash$playerhashentry1.key == key) {
                this.modCount++;
                this.numHashElements--;
                if (playerhash$playerhashentry == playerhash$playerhashentry1) {
                    this.hashArray[j] = playerhash$playerhashentry2;
                } else {
                    playerhash$playerhashentry.nextEntry = playerhash$playerhashentry2;
                }

                return playerhash$playerhashentry1;
            }

            playerhash$playerhashentry = playerhash$playerhashentry1;
            playerhash$playerhashentry1 = playerhash$playerhashentry2;
        }

        return playerhash$playerhashentry1;
    }

    private void createKey(int hash, long key, V value, int index) {
        PlayerHash.PlayerHashEntry<V> playerhash$playerhashentry = this.hashArray[index];
        this.hashArray[index] = new PlayerHash.PlayerHashEntry<>(hash, key, (V)value, playerhash$playerhashentry);
        if (this.numHashElements++ >= this.capacity) {
            this.resizeTable(2 * this.hashArray.length);
        }
    }

    static class PlayerHashEntry<V> {
        final long key;
        V value;
        PlayerHash.PlayerHashEntry<V> nextEntry;
        final int hash;

        PlayerHashEntry(int hash, long key, V value, PlayerHash.PlayerHashEntry<V> next) {
            this.value = (V)value;
            this.nextEntry = next;
            this.key = key;
            this.hash = hash;
        }

        public final long getKey() {
            return this.key;
        }

        public final V getValue() {
            return this.value;
        }

        @Override
        public final boolean equals(Object var1) {
            if (!(var1 instanceof PlayerHash.PlayerHashEntry)) {
                return false;
            }

            PlayerHash.PlayerHashEntry<V> playerhash$playerhashentry = (PlayerHash.PlayerHashEntry)var1;
            Long olong = this.getKey();
            Long olong1 = playerhash$playerhashentry.getKey();
            if (olong == olong1 || olong != null && olong.equals(olong1)) {
                Object object = this.getValue();
                Object object1 = playerhash$playerhashentry.getValue();
                if (object == object1 || object != null && object.equals(object1)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public final int hashCode() {
            return PlayerHash.getHashedKey(this.key);
        }

        @Override
        public final String toString() {
            return this.getKey() + "=" + this.getValue();
        }
    }
}
