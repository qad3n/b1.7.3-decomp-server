package net.minecraft.src;

import java.io.File;
import java.util.regex.Matcher;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class ChunkFile
implements Comparable<ChunkFile> {
    private final File file;
    private final int chunkX;
    private final int chunkZ;

    public ChunkFile(File file) {
        this.file = file;
        Matcher matcher = SaveConverterMcRegion.ChunkFilePattern.PATTERN.matcher(file.getName());
        if (matcher.matches()) {
            this.chunkX = Integer.parseInt(matcher.group(1), 36);
            this.chunkZ = Integer.parseInt(matcher.group(2), 36);
        } else {
            this.chunkX = 0;
            this.chunkZ = 0;
        }
    }

    @Override
    public int compareTo(ChunkFile chunkFile) {
        int n = this.chunkX >> 5;
        int n2 = chunkFile.chunkX >> 5;
        if (n == n2) {
            int n3 = this.chunkZ >> 5;
            int n4 = chunkFile.chunkZ >> 5;
            return n3 - n4;
        }
        return n - n2;
    }

    public File getFile() {
        return this.file;
    }

    public int getChunkX() {
        return this.chunkX;
    }

    public int getChunkZ() {
        return this.chunkZ;
    }
}

