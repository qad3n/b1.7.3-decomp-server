package net.minecraft.src;

import net.minecraft.server.MinecraftServer;

public class WorldServerMulti
extends WorldServer {
    public WorldServerMulti(MinecraftServer server, ISaveHandler storage, String saveName, int dimension, long seed, WorldServer delegate) {
        super(server, storage, saveName, dimension, seed);
        this.savedDataStorage = delegate.savedDataStorage;
    }
}

