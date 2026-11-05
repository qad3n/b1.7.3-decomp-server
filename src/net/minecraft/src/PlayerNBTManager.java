package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class PlayerNBTManager implements IPlayerFileData, ISaveHandler {
    private static final Logger logger = Logger.getLogger("Minecraft");
    private final File worldDir;
    private final File worldFile;
    private final File dataDir;
    private final long startTime = System.currentTimeMillis();

    public PlayerNBTManager(File savesDir, String name, boolean createPlayerDataDir) {
        this.worldDir = new File(savesDir, name);
        this.worldDir.mkdirs();
        this.worldFile = new File(this.worldDir, "players");
        this.dataDir = new File(this.worldDir, "data");
        this.dataDir.mkdirs();
        if (createPlayerDataDir) {
            this.worldFile.mkdirs();
        }

        this.writeSessionLock();
    }

    private void writeSessionLock() {
        try {
            File file1 = new File(this.worldDir, "session.lock");
            DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(file1));

            try {
                dataoutputstream.writeLong(this.startTime);
            } finally {
                dataoutputstream.close();
            }
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
            throw new RuntimeException("Failed to check session lock, aborting");
        }
    }

    protected File getWorldDir() {
        return this.worldDir;
    }

    @Override
    public void checkSessionLock() {
        try {
            File file1 = new File(this.worldDir, "session.lock");
            DataInputStream datainputstream = new DataInputStream(new FileInputStream(file1));

            try {
                if (datainputstream.readLong() != this.startTime) {
                    throw new MinecraftException("The save is being accessed from another location, aborting");
                }
            } finally {
                datainputstream.close();
            }
        } catch (IOException ioexception) {
            throw new MinecraftException("Failed to check session lock, aborting");
        }
    }

    @Override
    public IChunkLoader getChunkStorage(WorldProvider dimension) {
        if (dimension instanceof WorldProviderHell) {
            File file1 = new File(this.worldDir, "DIM-1");
            file1.mkdirs();
            return new ChunkLoader(file1, true);
        } else {
            return new ChunkLoader(this.worldDir, true);
        }
    }

    @Override
    public WorldInfo loadData() {
        File file1 = new File(this.worldDir, "level.dat");
        if (file1.exists()) {
            try {
                NBTTagCompound nbttagcompound2 = CompressedStreamTools.readCompressed(new FileInputStream(file1));
                NBTTagCompound nbttagcompound3 = nbttagcompound2.getCompoundTag("Data");
                return new WorldInfo(nbttagcompound3);
            } catch (Exception exception1) {
                exception1.printStackTrace();
            }
        }

        file1 = new File(this.worldDir, "level.dat_old");
        if (file1.exists()) {
            try {
                NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(new FileInputStream(file1));
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("Data");
                return new WorldInfo(nbttagcompound1);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public void saveData(WorldInfo data, List<EntityPlayer> players) {
        NBTTagCompound nbttagcompound = data.toNbt(players);
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        nbttagcompound1.setTag("Data", nbttagcompound);

        try {
            File file1 = new File(this.worldDir, "level.dat_new");
            File file2 = new File(this.worldDir, "level.dat_old");
            File file3 = new File(this.worldDir, "level.dat");
            CompressedStreamTools.writeGzippedCompoundToOutputStream(nbttagcompound1, new FileOutputStream(file1));
            if (file2.exists()) {
                file2.delete();
            }

            file3.renameTo(file2);
            if (file3.exists()) {
                file3.delete();
            }

            file1.renameTo(file3);
            if (file1.exists()) {
                file1.delete();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void saveData(WorldInfo data) {
        NBTTagCompound nbttagcompound = data.toNbt();
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        nbttagcompound1.setTag("Data", nbttagcompound);

        try {
            File file1 = new File(this.worldDir, "level.dat_new");
            File file2 = new File(this.worldDir, "level.dat_old");
            File file3 = new File(this.worldDir, "level.dat");
            CompressedStreamTools.writeGzippedCompoundToOutputStream(nbttagcompound1, new FileOutputStream(file1));
            if (file2.exists()) {
                file2.delete();
            }

            file3.renameTo(file2);
            if (file3.exists()) {
                file3.delete();
            }

            file1.renameTo(file3);
            if (file1.exists()) {
                file1.delete();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void writePlayerData(EntityPlayer player) {
        try {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            player.writeToNBT(nbttagcompound);
            File file1 = new File(this.worldFile, "_tmp_.dat");
            File file2 = new File(this.worldFile, player.username + ".dat");
            CompressedStreamTools.writeGzippedCompoundToOutputStream(nbttagcompound, new FileOutputStream(file1));
            if (file2.exists()) {
                file2.delete();
            }

            file1.renameTo(file2);
        } catch (Exception exception) {
            logger.warning("Failed to save player data for " + player.username);
        }
    }

    @Override
    public void readPlayerData(EntityPlayer player) {
        NBTTagCompound nbttagcompound = this.getPlayerData(player.username);
        if (nbttagcompound != null) {
            player.readFromNBT(nbttagcompound);
        }
    }

    public NBTTagCompound getPlayerData(String playerName) {
        try {
            File file1 = new File(this.worldFile, playerName + ".dat");
            if (file1.exists()) {
                return CompressedStreamTools.readCompressed(new FileInputStream(file1));
            }
        } catch (Exception exception) {
            logger.warning("Failed to load player data for " + playerName);
        }

        return null;
    }

    @Override
    public IPlayerFileData getPlayerDataStorage() {
        return this;
    }

    @Override
    public void forceSave() {
    }

    @Override
    public File getDataFile(String name) {
        return new File(this.dataDir, name + ".dat");
    }
}
