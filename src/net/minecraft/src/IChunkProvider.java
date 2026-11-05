package net.minecraft.src;


public interface IChunkProvider {
    public boolean chunkExists(int var1, int var2);

    public Chunk provideChunk(int var1, int var2);

    public Chunk loadChunk(int var1, int var2);

    public void populate(IChunkProvider var1, int var2, int var3);

    public boolean saveChunks(boolean var1, IProgressUpdate var2);

    public boolean tick();

    public boolean shouldSave();
}

