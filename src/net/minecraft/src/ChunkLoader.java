package net.minecraft.src;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ChunkLoader implements IChunkLoader {
    private File saveDir;
    private boolean createIfNecessary;

    public ChunkLoader(File dir, boolean make) {
        this.saveDir = dir;
        this.createIfNecessary = make;
    }

    private File chunkFileForXZ(int chunkX, int chunkZ) {
        String s = "c." + Integer.toString(chunkX, 36) + "." + Integer.toString(chunkZ, 36) + ".dat";
        String s1 = Integer.toString(chunkX & 63, 36);
        String s2 = Integer.toString(chunkZ & 63, 36);
        File file1 = new File(this.saveDir, s1);
        if (!file1.exists()) {
            if (!this.createIfNecessary) {
                return null;
            }

            file1.mkdir();
        }

        file1 = new File(file1, s2);
        if (!file1.exists()) {
            if (!this.createIfNecessary) {
                return null;
            }

            file1.mkdir();
        }

        file1 = new File(file1, s);
        return !file1.exists() && !this.createIfNecessary ? null : file1;
    }

    @Override
    public Chunk loadChunk(World world, int chunkX, int chunkZ) throws IOException {
        File file1 = this.chunkFileForXZ(chunkX, chunkZ);
        if (file1 != null && file1.exists()) {
            try {
                FileInputStream fileinputstream = new FileInputStream(file1);
                NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(fileinputstream);
                if (!nbttagcompound.hasKey("Level")) {
                    System.out.println("Chunk file at " + chunkX + "," + chunkZ + " is missing level data, skipping");
                    return null;
                }

                if (!nbttagcompound.getCompoundTag("Level").hasKey("Blocks")) {
                    System.out.println("Chunk file at " + chunkX + "," + chunkZ + " is missing block data, skipping");
                    return null;
                }

                Chunk chunk = loadChunkIntoWorldFromCompound(world, nbttagcompound.getCompoundTag("Level"));
                if (!chunk.isAtLocation(chunkX, chunkZ)) {
                    System.out
                        .println(
                            "Chunk file at "
                                + chunkX
                                + ","
                                + chunkZ
                                + " is in the wrong location; relocating. (Expected "
                                + chunkX
                                + ", "
                                + chunkZ
                                + ", got "
                                + chunk.xPosition
                                + ", "
                                + chunk.zPosition
                                + ")"
                        );
                    nbttagcompound.setInteger("xPos", chunkX);
                    nbttagcompound.setInteger("zPos", chunkZ);
                    chunk = loadChunkIntoWorldFromCompound(world, nbttagcompound.getCompoundTag("Level"));
                }

                chunk.fill();
                return chunk;
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public void saveChunk(World world, Chunk chunk) throws IOException {
        world.checkSessionLock();
        File file1 = this.chunkFileForXZ(chunk.xPosition, chunk.zPosition);
        if (file1.exists()) {
            WorldInfo worldinfo = world.getWorldInfo();
            worldinfo.setSizeOnDisk(worldinfo.getSizeOnDisk() - file1.length());
        }

        try {
            File file2 = new File(this.saveDir, "tmp_chunk.dat");
            FileOutputStream fileoutputstream = new FileOutputStream(file2);
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound.setTag("Level", nbttagcompound1);
            storeChunkInCompound(chunk, world, nbttagcompound1);
            CompressedStreamTools.writeGzippedCompoundToOutputStream(nbttagcompound, fileoutputstream);
            fileoutputstream.close();
            if (file1.exists()) {
                file1.delete();
            }

            file2.renameTo(file1);
            WorldInfo worldinfo1 = world.getWorldInfo();
            worldinfo1.setSizeOnDisk(worldinfo1.getSizeOnDisk() + file1.length());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void storeChunkInCompound(Chunk chunk, World world, NBTTagCompound nbt) {
        world.checkSessionLock();
        nbt.setInteger("xPos", chunk.xPosition);
        nbt.setInteger("zPos", chunk.zPosition);
        nbt.setLong("LastUpdate", world.getWorldTime());
        nbt.setByteArray("Blocks", chunk.blocks);
        nbt.setByteArray("Data", chunk.data.data);
        nbt.setByteArray("SkyLight", chunk.skylightMap.data);
        nbt.setByteArray("BlockLight", chunk.blocklightMap.data);
        nbt.setByteArray("HeightMap", chunk.heightMap);
        nbt.setBoolean("TerrainPopulated", chunk.isTerrainPopulated);
        chunk.hasEntities = false;
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < chunk.entities.length; i++) {
            for (Entity entity : chunk.entities[i]) {
                chunk.hasEntities = true;
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                if (entity.addEntityID(nbttagcompound)) {
                    nbttaglist.setTag(nbttagcompound);
                }
            }
        }

        nbt.setTag("Entities", nbttaglist);
        NBTTagList nbttaglist1 = new NBTTagList();

        for (TileEntity tileentity : chunk.chunkTileEntityMap.values()) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            tileentity.writeToNBT(nbttagcompound1);
            nbttaglist1.setTag(nbttagcompound1);
        }

        nbt.setTag("TileEntities", nbttaglist1);
    }

    public static Chunk loadChunkIntoWorldFromCompound(World world, NBTTagCompound nbt) {
        int i = nbt.getInteger("xPos");
        int j = nbt.getInteger("zPos");
        Chunk chunk = new Chunk(world, i, j);
        chunk.blocks = nbt.getByteArray("Blocks");
        chunk.data = new NibbleArray(nbt.getByteArray("Data"));
        chunk.skylightMap = new NibbleArray(nbt.getByteArray("SkyLight"));
        chunk.blocklightMap = new NibbleArray(nbt.getByteArray("BlockLight"));
        chunk.heightMap = nbt.getByteArray("HeightMap");
        chunk.isTerrainPopulated = nbt.getBoolean("TerrainPopulated");
        if (!chunk.data.isValid()) {
            chunk.data = new NibbleArray(chunk.blocks.length);
        }

        if (chunk.heightMap == null || !chunk.skylightMap.isValid()) {
            chunk.heightMap = new byte[256];
            chunk.skylightMap = new NibbleArray(chunk.blocks.length);
            chunk.populateHeightMap();
        }

        if (!chunk.blocklightMap.isValid()) {
            chunk.blocklightMap = new NibbleArray(chunk.blocks.length);
            chunk.populateLight();
        }

        NBTTagList nbttaglist = nbt.getTagList("Entities");
        if (nbttaglist != null) {
            for (int k = 0; k < nbttaglist.tagCount(); k++) {
                NBTTagCompound nbttagcompound = (NBTTagCompound)nbttaglist.tagAt(k);
                Entity entity = EntityList.createEntityFromNBT(nbttagcompound, world);
                chunk.hasEntities = true;
                if (entity != null) {
                    chunk.addEntity(entity);
                }
            }
        }

        NBTTagList nbttaglist1 = nbt.getTagList("TileEntities");
        if (nbttaglist1 != null) {
            for (int l = 0; l < nbttaglist1.tagCount(); l++) {
                NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist1.tagAt(l);
                TileEntity tileentity = TileEntity.createAndLoadEntity(nbttagcompound1);
                if (tileentity != null) {
                    chunk.addTileEntity(tileentity);
                }
            }
        }

        return chunk;
    }

    @Override
    public void tick() {
    }

    @Override
    public void saveExtraData() {
    }

    @Override
    public void saveExtraChunkData(World world, Chunk chunk) throws IOException {
    }
}
