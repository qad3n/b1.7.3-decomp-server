package net.minecraft.src;

import java.io.IOException;

public interface IChunkLoader {
    public Chunk loadChunk(World var1, int var2, int var3) throws IOException;

    public void saveChunk(World var1, Chunk var2) throws IOException;

    public void saveExtraChunkData(World var1, Chunk var2) throws IOException;

    public void tick();

    public void saveExtraData();
}

