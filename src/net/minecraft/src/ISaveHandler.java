package net.minecraft.src;

import java.io.File;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ISaveHandler {
    public WorldInfo loadData();

    public void checkSessionLock();

    public IChunkLoader getChunkStorage(WorldProvider var1);

    public void saveData(WorldInfo var1, List<EntityPlayer> var2);

    public void saveData(WorldInfo var1);

    public IPlayerFileData getPlayerDataStorage();

    public void forceSave();

    public File getDataFile(String var1);
}

