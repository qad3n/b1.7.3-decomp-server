package net.minecraft.src;

import java.io.File;
import java.io.FileInputStream;

public class SaveFormatOld implements ISaveFormat {
    protected final File dir;

    public SaveFormatOld(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }

        this.dir = dir;
    }

    public WorldInfo getWorldInfo(String saveName) {
        File file1 = new File(this.dir, saveName);
        if (!file1.exists()) {
            return null;
        }

        File file2 = new File(file1, "level.dat");
        if (file2.exists()) {
            try {
                NBTTagCompound nbttagcompound2 = CompressedStreamTools.readCompressed(new FileInputStream(file2));
                NBTTagCompound nbttagcompound3 = nbttagcompound2.getCompoundTag("Data");
                return new WorldInfo(nbttagcompound3);
            } catch (Exception exception1) {
                exception1.printStackTrace();
            }
        }

        file2 = new File(file1, "level.dat_old");
        if (file2.exists()) {
            try {
                NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(new FileInputStream(file2));
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("Data");
                return new WorldInfo(nbttagcompound1);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        return null;
    }

    protected static void deleteFilesAndDirs(File[] files) {
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                deleteFilesAndDirs(files[i].listFiles());
            }

            files[i].delete();
        }
    }

    public ISaveHandler get(String saveName, boolean createPlayerDataDir) {
        return new PlayerNBTManager(this.dir, saveName, createPlayerDataDir);
    }

    @Override
    public boolean isOldSaveType(String saveName) {
        return false;
    }

    @Override
    public boolean converMapToMCRegion(String saveName, IProgressUpdate listener) {
        return false;
    }
}
