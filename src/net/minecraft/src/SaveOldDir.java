package net.minecraft.src;

import java.io.File;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SaveOldDir
extends PlayerNBTManager {
    public SaveOldDir(File file, String string, boolean bl) {
        super(file, string, bl);
    }

    @Override
    public IChunkLoader getChunkStorage(WorldProvider dimension) {
        File file = this.getWorldDir();
        if (dimension instanceof WorldProviderHell) {
            File file2 = new File(file, "DIM-1");
            file2.mkdirs();
            return new McRegionChunkLoader(file2);
        }
        return new McRegionChunkLoader(file);
    }

    @Override
    public void saveData(WorldInfo data, List<EntityPlayer> players) {
        data.setVersion(19132);
        super.saveData(data, players);
    }

    @Override
    public void forceSave() {
        RegionFileCache.flush();
    }
}

