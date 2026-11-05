// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChunkProviderServer implements IChunkProvider
{
    private Set<Integer> chunksToUnload;
    private Chunk dummyChunk;
    private IChunkProvider serverChunkGenerator;
    private IChunkLoader storage;
    public boolean chunkLoadOverride;
    private Map<Integer, Chunk> id2ChunkMap;
    private List<Chunk> chunks;
    private WorldServer world;
    
    public ChunkProviderServer(final WorldServer world, final IChunkLoader storage, final IChunkProvider generator) {
        this.chunksToUnload = new HashSet<Integer>();
        this.chunkLoadOverride = false;
        this.id2ChunkMap = new HashMap<Integer, Chunk>();
        this.chunks = new ArrayList<Chunk>();
        this.dummyChunk = new EmptyChunk(world, new byte[32768], 0, 0);
        this.world = world;
        this.storage = storage;
        this.serverChunkGenerator = generator;
    }
    
    public boolean chunkExists(final int chunkX, final int chunkZ) {
        return this.id2ChunkMap.containsKey(ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ));
    }
    
    public void unloadChunk(int chunkX, int chunkZ) {
        ChunkCoordinates chunkCoordinates = this.world.getSpawnPoint();
        int n = chunkX * 16 + 8 - chunkCoordinates.posX;
        int n2 = chunkZ * 16 + 8 - chunkCoordinates.posZ;
        int n3 = 128;
        if (n < -n3 || n > n3 || n2 < -n3 || n2 > n3) {
            this.chunksToUnload.add(ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ));
        }
    }
    
    public Chunk loadChunk(final int chunkX, final int chunkZ) {
        final int chunkXZ2Int = ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ);
        this.chunksToUnload.remove(chunkXZ2Int);
        Chunk chunk = this.id2ChunkMap.get(chunkXZ2Int);
        if (chunk == null) {
            chunk = this.loadChunkFromStorage(chunkX, chunkZ);
            if (chunk == null) {
                if (this.serverChunkGenerator == null) {
                    chunk = this.dummyChunk;
                }
                else {
                    chunk = this.serverChunkGenerator.provideChunk(chunkX, chunkZ);
                }
            }
            this.id2ChunkMap.put(chunkXZ2Int, chunk);
            this.chunks.add(chunk);
            if (chunk != null) {
                chunk.populateBlockLight();
                chunk.onChunkLoad();
            }
            if (!chunk.isTerrainPopulated && this.chunkExists(chunkX + 1, chunkZ + 1) && this.chunkExists(chunkX, chunkZ + 1) && this.chunkExists(chunkX + 1, chunkZ)) {
                this.populate(this, chunkX, chunkZ);
            }
            if (this.chunkExists(chunkX - 1, chunkZ) && !this.provideChunk(chunkX - 1, chunkZ).isTerrainPopulated && this.chunkExists(chunkX - 1, chunkZ + 1) && this.chunkExists(chunkX, chunkZ + 1) && this.chunkExists(chunkX - 1, chunkZ)) {
                this.populate(this, chunkX - 1, chunkZ);
            }
            if (this.chunkExists(chunkX, chunkZ - 1) && !this.provideChunk(chunkX, chunkZ - 1).isTerrainPopulated && this.chunkExists(chunkX + 1, chunkZ - 1) && this.chunkExists(chunkX, chunkZ - 1) && this.chunkExists(chunkX + 1, chunkZ)) {
                this.populate(this, chunkX, chunkZ - 1);
            }
            if (this.chunkExists(chunkX - 1, chunkZ - 1) && !this.provideChunk(chunkX - 1, chunkZ - 1).isTerrainPopulated && this.chunkExists(chunkX - 1, chunkZ - 1) && this.chunkExists(chunkX, chunkZ - 1) && this.chunkExists(chunkX - 1, chunkZ)) {
                this.populate(this, chunkX - 1, chunkZ - 1);
            }
        }
        return chunk;
    }
    
    public Chunk provideChunk(int chunkX, int chunkZ) {
        Chunk chunk = this.id2ChunkMap.get(ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ));
        if (chunk == null) {
            if (this.world.worldChunkLoadOverride || this.chunkLoadOverride) {
                return this.loadChunk(chunkX, chunkZ);
            }
            return this.dummyChunk;
        }
        return chunk;
    }
    
    private Chunk loadChunkFromStorage(final int chunkX, final int chunkZ) {
        if (this.storage == null) {
            return null;
        }
        try {
            final Chunk loadChunk = this.storage.loadChunk(this.world, chunkX, chunkZ);
            if (loadChunk != null) {
                loadChunk.lastSaveTime = this.world.getWorldTime();
            }
            return loadChunk;
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    private void saveEntities(final Chunk chunk) {
        if (this.storage == null) {
            return;
        }
        try {
            this.storage.saveExtraChunkData(this.world, chunk);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void saveChunk(final Chunk chunk) {
        if (this.storage == null) {
            return;
        }
        try {
            chunk.lastSaveTime = this.world.getWorldTime();
            this.storage.saveChunk(this.world, chunk);
        }
        catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void populate(final IChunkProvider source, final int chunkX, final int chunkZ) {
        final Chunk provideChunk = this.provideChunk(chunkX, chunkZ);
        if (!provideChunk.isTerrainPopulated) {
            provideChunk.isTerrainPopulated = true;
            if (this.serverChunkGenerator != null) {
                this.serverChunkGenerator.populate(source, chunkX, chunkZ);
                provideChunk.setChunkModified();
            }
        }
    }
    
    public boolean saveChunks(final boolean saveEntities, final IProgressUpdate listener) {
        int n = 0;
        for (int i = 0; i < this.chunks.size(); ++i) {
            final Chunk chunk = this.chunks.get(i);
            if (saveEntities && !chunk.neverSave) {
                this.saveEntities(chunk);
            }
            if (chunk.needsSaving(saveEntities)) {
                this.saveChunk(chunk);
                chunk.isModified = false;
                if (++n == 24 && !saveEntities) {
                    return false;
                }
            }
        }
        if (saveEntities) {
            if (this.storage == null) {
                return true;
            }
            this.storage.saveExtraData();
        }
        return true;
    }
    
    public boolean tick() {
        if (!this.world.levelSaving) {
            for (int i = 0; i < 100; ++i) {
                if (!this.chunksToUnload.isEmpty()) {
                    final Integer n = this.chunksToUnload.iterator().next();
                    final Chunk chunk = this.id2ChunkMap.get(n);
                    chunk.onChunkUnload();
                    this.saveChunk(chunk);
                    this.saveEntities(chunk);
                    this.chunksToUnload.remove(n);
                    this.id2ChunkMap.remove(n);
                    this.chunks.remove(chunk);
                }
            }
            if (this.storage != null) {
                this.storage.tick();
            }
        }
        return this.serverChunkGenerator.tick();
    }
    
    public boolean shouldSave() {
        return !this.world.levelSaving;
    }
}
