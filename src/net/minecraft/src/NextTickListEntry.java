package net.minecraft.src;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class NextTickListEntry
implements Comparable<NextTickListEntry> {
    private static long nextTickEntryID = 0L;
    public int xCoord;
    public int yCoord;
    public int zCoord;
    public int blockID;
    public long scheduledTime;
    private long tickEntryID = nextTickEntryID++;

    public NextTickListEntry(int x, int y, int z, int block) {
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
        this.blockID = block;
    }

    public boolean equals(Object object) {
        if (object instanceof NextTickListEntry) {
            NextTickListEntry nextTickListEntry = (NextTickListEntry)object;
            return this.xCoord == nextTickListEntry.xCoord && this.yCoord == nextTickListEntry.yCoord && this.zCoord == nextTickListEntry.zCoord && this.blockID == nextTickListEntry.blockID;
        }
        return false;
    }

    public int hashCode() {
        return (this.xCoord * 128 * 1024 + this.zCoord * 128 + this.yCoord) * 256 + this.blockID;
    }

    public NextTickListEntry setScheduledTime(long time) {
        this.scheduledTime = time;
        return this;
    }

    @Override
    public int compareTo(NextTickListEntry nextTickListEntry) {
        if (this.scheduledTime < nextTickListEntry.scheduledTime) {
            return -1;
        }
        if (this.scheduledTime > nextTickListEntry.scheduledTime) {
            return 1;
        }
        if (this.tickEntryID < nextTickListEntry.tickEntryID) {
            return -1;
        }
        if (this.tickEntryID > nextTickListEntry.tickEntryID) {
            return 1;
        }
        return 0;
    }
}

