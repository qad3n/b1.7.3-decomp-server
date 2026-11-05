package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class McRegionChunkLoader
implements IChunkLoader {
    private final File worldFolder;

    public McRegionChunkLoader(File dir) {
        this.worldFolder = dir;
    }

    public Chunk loadChunk(World world, int chunkX, int chunkZ) throws IOException {
        DataInputStream dataInputStream = RegionFileCache.getChunkInputStream(this.worldFolder, chunkX, chunkZ);
        if (dataInputStream == null) {
            return null;
        }
        NBTTagCompound nBTTagCompound = CompressedStreamTools.read(dataInputStream);
        if (!nBTTagCompound.hasKey("Level")) {
            System.out.println("Chunk file at " + chunkX + "," + chunkZ + " is missing level data, skipping");
            return null;
        }
        if (!nBTTagCompound.getCompoundTag("Level").hasKey("Blocks")) {
            System.out.println("Chunk file at " + chunkX + "," + chunkZ + " is missing block data, skipping");
            return null;
        }
        Chunk chunk = ChunkLoader.loadChunkIntoWorldFromCompound(world, nBTTagCompound.getCompoundTag("Level"));
        if (!chunk.isAtLocation(chunkX, chunkZ)) {
            System.out.println("Chunk file at " + chunkX + "," + chunkZ + " is in the wrong location; relocating. (Expected " + chunkX + ", " + chunkZ + ", got " + chunk.xPosition + ", " + chunk.zPosition + ")");
            nBTTagCompound.setInteger("xPos", chunkX);
            nBTTagCompound.setInteger("zPos", chunkZ);
            chunk = ChunkLoader.loadChunkIntoWorldFromCompound(world, nBTTagCompound.getCompoundTag("Level"));
        }
        chunk.fill();
        return chunk;
    }

    public void saveChunk(World world, Chunk chunk) throws IOException {
        world.checkSessionLock();
        try {
            DataOutputStream dataOutputStream = RegionFileCache.getChunkOutputStream(this.worldFolder, chunk.xPosition, chunk.zPosition);
            NBTTagCompound nBTTagCompound = new NBTTagCompound();
            NBTTagCompound nBTTagCompound2 = new NBTTagCompound();
            nBTTagCompound.setTag("Level", nBTTagCompound2);
            ChunkLoader.storeChunkInCompound(chunk, world, nBTTagCompound2);
            CompressedStreamTools.write(nBTTagCompound, dataOutputStream);
            dataOutputStream.close();
            WorldInfo worldInfo = world.getWorldInfo();
            worldInfo.setSizeOnDisk(worldInfo.getSizeOnDisk() + (long)RegionFileCache.getChunkSize(this.worldFolder, chunk.xPosition, chunk.zPosition));
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void saveExtraChunkData(World world, Chunk chunk) throws IOException {
    }

    public void tick() {
    }

    public void saveExtraData() {
    }
}

