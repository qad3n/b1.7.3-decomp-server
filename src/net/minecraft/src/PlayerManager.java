package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.MinecraftServer;

public class PlayerManager {
    public List<EntityPlayerMP> players = new ArrayList<>();
    private PlayerHash<PlayerManager.PlayerInstance> playerInstances = new PlayerHash<>();
    private List<PlayerManager.PlayerInstance> playerInstancesToUpdate = new ArrayList<>();
    private MinecraftServer mcServer;
    private int dimension;
    private int playerViewRadius;
    private final int[][] adjacentChunkCoords = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};

    public PlayerManager(MinecraftServer server, int dimension, int chunkViewDistance) {
        if (chunkViewDistance > 15) {
            throw new IllegalArgumentException("Too big view radius!");
        }

        if (chunkViewDistance < 3) {
            throw new IllegalArgumentException("Too small view radius!");
        }

        this.playerViewRadius = chunkViewDistance;
        this.mcServer = server;
        this.dimension = dimension;
    }

    public WorldServer getMinecraftServer() {
        return this.mcServer.getWorldManager(this.dimension);
    }

    public void updatePlayerInstances() {
        for (int i = 0; i < this.playerInstancesToUpdate.size(); i++) {
            this.playerInstancesToUpdate.get(i).onUpdate();
        }

        this.playerInstancesToUpdate.clear();
    }

    private PlayerManager.PlayerInstance getPlayerInstance(int chunkX, int chunkZ, boolean add) {
        long i = chunkX + 2147483647L | chunkZ + 2147483647L << 32;
        PlayerManager.PlayerInstance playermanager$playerinstance = this.playerInstances.getValueByKey(i);
        if (playermanager$playerinstance == null && add) {
            playermanager$playerinstance = new PlayerManager.PlayerInstance(chunkX, chunkZ);
            this.playerInstances.add(i, playermanager$playerinstance);
        }

        return playermanager$playerinstance;
    }

    public void markBlockNeedsUpdate(int x, int y, int z) {
        int i = x >> 4;
        int j = z >> 4;
        PlayerManager.PlayerInstance playermanager$playerinstance = this.getPlayerInstance(i, j, false);
        if (playermanager$playerinstance != null) {
            playermanager$playerinstance.markBlockNeedsUpdate(x & 15, y, z & 15);
        }
    }

    public void addPlayer(EntityPlayerMP player) {
        int i = (int)player.posX >> 4;
        int j = (int)player.posZ >> 4;
        player.trackedX = player.posX;
        player.trackedZ = player.posZ;
        int k = 0;
        int l = this.playerViewRadius;
        int i1 = 0;
        int j1 = 0;
        this.getPlayerInstance(i, j, true).addPlayer(player);

        for (int k1 = 1; k1 <= l * 2; k1++) {
            for (int l1 = 0; l1 < 2; l1++) {
                int[] aint = this.adjacentChunkCoords[k++ % 4];

                for (int i2 = 0; i2 < k1; i2++) {
                    i1 += aint[0];
                    j1 += aint[1];
                    this.getPlayerInstance(i + i1, j + j1, true).addPlayer(player);
                }
            }
        }

        k %= 4;

        for (int j2 = 0; j2 < l * 2; j2++) {
            i1 += this.adjacentChunkCoords[k][0];
            j1 += this.adjacentChunkCoords[k][1];
            this.getPlayerInstance(i + i1, j + j1, true).addPlayer(player);
        }

        this.players.add(player);
    }

    public void removePlayer(EntityPlayerMP player) {
        int i = (int)player.trackedX >> 4;
        int j = (int)player.trackedZ >> 4;

        for (int k = i - this.playerViewRadius; k <= i + this.playerViewRadius; k++) {
            for (int l = j - this.playerViewRadius; l <= j + this.playerViewRadius; l++) {
                PlayerManager.PlayerInstance playermanager$playerinstance = this.getPlayerInstance(k, l, false);
                if (playermanager$playerinstance != null) {
                    playermanager$playerinstance.removePlayer(player);
                }
            }
        }

        this.players.remove(player);
    }

    private boolean isChunkWithinView(int chunkX, int chunkZ, int playerChunkX, int playerChunkZ) {
        int i = chunkX - playerChunkX;
        int j = chunkZ - playerChunkZ;
        return i < -this.playerViewRadius || i > this.playerViewRadius ? false : j >= -this.playerViewRadius && j <= this.playerViewRadius;
    }

    public void movePlayer(EntityPlayerMP player) {
        int i = (int)player.posX >> 4;
        int j = (int)player.posZ >> 4;
        double d0 = player.trackedX - player.posX;
        double d1 = player.trackedZ - player.posZ;
        double d2 = d0 * d0 + d1 * d1;
        if (!(d2 < 64.0)) {
            int k = (int)player.trackedX >> 4;
            int l = (int)player.trackedZ >> 4;
            int i1 = i - k;
            int j1 = j - l;
            if (i1 != 0 || j1 != 0) {
                for (int k1 = i - this.playerViewRadius; k1 <= i + this.playerViewRadius; k1++) {
                    for (int l1 = j - this.playerViewRadius; l1 <= j + this.playerViewRadius; l1++) {
                        if (!this.isChunkWithinView(k1, l1, k, l)) {
                            this.getPlayerInstance(k1, l1, true).addPlayer(player);
                        }

                        if (!this.isChunkWithinView(k1 - i1, l1 - j1, i, j)) {
                            PlayerManager.PlayerInstance playermanager$playerinstance = this.getPlayerInstance(k1 - i1, l1 - j1, false);
                            if (playermanager$playerinstance != null) {
                                playermanager$playerinstance.removePlayer(player);
                            }
                        }
                    }
                }

                player.trackedX = player.posX;
                player.trackedZ = player.posZ;
            }
        }
    }

    public int getMaxTrackingDistance() {
        return this.playerViewRadius * 16 - 16;
    }

    class PlayerInstance {
        private List<EntityPlayerMP> players = new ArrayList<>();
        private int chunkX;
        private int chunkZ;
        private ChunkCoordIntPair currentChunk;
        private short[] blocksToUpdate = new short[10];
        private int numBlocksToUpdate = 0;
        private int minX;
        private int maxX;
        private int minY;
        private int maxY;
        private int minZ;
        private int maxZ;

        public PlayerInstance(int chunkX, int chunkZ) {
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            this.currentChunk = new ChunkCoordIntPair(chunkX, chunkZ);
            PlayerManager.this.getMinecraftServer().chunkProviderServer.loadChunk(chunkX, chunkZ);
        }

        public void addPlayer(EntityPlayerMP player) {
            if (this.players.contains(player)) {
                throw new IllegalStateException("Failed to add player. " + player + " already is in chunk " + this.chunkX + ", " + this.chunkZ);
            }

            player.allLoadedChunks.add(this.currentChunk);
            player.playerNetServerHandler.sendPacket(new Packet50PreChunk(this.currentChunk.chunkXPos, this.currentChunk.chunkZPos, true));
            this.players.add(player);
            player.loadedChunks.add(this.currentChunk);
        }

        public void removePlayer(EntityPlayerMP player) {
            if (!this.players.contains(player)) {
                return;
            }
            this.players.remove(player);
            if (this.players.size() == 0) {
                long l = (long) this.chunkX + Integer.MAX_VALUE | (long) this.chunkZ + Integer.MAX_VALUE << 32;
                PlayerManager.this.playerInstances.remove(l);
                if (this.numBlocksToUpdate > 0) {
                    PlayerManager.this.playerInstancesToUpdate.remove(this);
                }
                PlayerManager.this.getMinecraftServer().chunkProviderServer.unloadChunk(this.chunkX, this.chunkZ);
            }
            player.loadedChunks.remove(this.currentChunk);
            if (player.allLoadedChunks.contains(this.currentChunk)) {
                player.playerNetServerHandler.sendPacket(new Packet50PreChunk(this.chunkX, this.chunkZ, false));
            }
        }

        public void markBlockNeedsUpdate(int localX, int y, int localZ) {
            if (this.numBlocksToUpdate == 0) {
                PlayerManager.this.playerInstancesToUpdate.add(this);
                this.minX = this.maxX = localX;
                this.minY = this.maxY = y;
                this.minZ = this.maxZ = localZ;
            }

            if (this.minX > localX) {
                this.minX = localX;
            }

            if (this.maxX < localX) {
                this.maxX = localX;
            }

            if (this.minY > y) {
                this.minY = y;
            }

            if (this.maxY < y) {
                this.maxY = y;
            }

            if (this.minZ > localZ) {
                this.minZ = localZ;
            }

            if (this.maxZ < localZ) {
                this.maxZ = localZ;
            }

            if (this.numBlocksToUpdate < 10) {
                short short1 = (short)(localX << 12 | localZ << 8 | y);

                for (int i = 0; i < this.numBlocksToUpdate; i++) {
                    if (this.blocksToUpdate[i] == short1) {
                        return;
                    }
                }

                this.blocksToUpdate[this.numBlocksToUpdate++] = short1;
            }
        }

        public void sendPacketToPlayersInInstance(Packet packet) {
            for (int i = 0; i < this.players.size(); i++) {
                EntityPlayerMP entityplayermp = this.players.get(i);
                if (entityplayermp.allLoadedChunks.contains(this.currentChunk)) {
                    entityplayermp.playerNetServerHandler.sendPacket(packet);
                }
            }
        }

        public void onUpdate() {
            final WorldServer minecraftServer = PlayerManager.this.getMinecraftServer();
            if (this.numBlocksToUpdate == 0) {
                return;
            }
            if (this.numBlocksToUpdate == 1) {
                final int x = this.chunkX * 16 + this.minX;
                final int minY = this.minY;
                final int z = this.chunkZ * 16 + this.minZ;
                this.sendPacketToPlayersInInstance(new Packet53BlockChange(x, minY, z, minecraftServer));
                if (Block.isBlockContainer[minecraftServer.getBlockId(x, minY, z)]) {
                    this.updateTileEntity(minecraftServer.getBlockTileEntity(x, minY, z));
                }
            } else if (this.numBlocksToUpdate == 10) {
                this.minY = this.minY / 2 * 2;
                this.maxY = (this.maxY / 2 + 1) * 2;
                final int n = this.minX + this.chunkX * 16;
                final int minY2 = this.minY;
                final int n2 = this.minZ + this.chunkZ * 16;
                final int widthX = this.maxX - this.minX + 1;
                final int height = this.maxY - this.minY + 2;
                final int widthZ = this.maxZ - this.minZ + 1;
                this.sendPacketToPlayersInInstance(new Packet51MapChunk(n, minY2, n2, widthX, height, widthZ, minecraftServer));
                final List<TileEntity> tileEntityList = minecraftServer.getTileEntityList(n, minY2, n2, n + widthX, minY2 + height, n2 + widthZ);
                for (int i = 0; i < tileEntityList.size(); ++i) {
                    this.updateTileEntity((TileEntity) tileEntityList.get(i));
                }
            } else {
                this.sendPacketToPlayersInInstance(new Packet52MultiBlockChange(this.chunkX, this.chunkZ, this.blocksToUpdate, this.numBlocksToUpdate, minecraftServer));
                for (int j = 0; j < this.numBlocksToUpdate; ++j) {
                    final int n3 = this.chunkX * 16 + (this.numBlocksToUpdate >> 12 & 0xF);
                    final int n4 = this.numBlocksToUpdate & 0xFF;
                    final int n5 = this.chunkZ * 16 + (this.numBlocksToUpdate >> 8 & 0xF);
                    if (Block.isBlockContainer[minecraftServer.getBlockId(n3, n4, n5)]) {
                        System.out.println("Sending!");
                        this.updateTileEntity(minecraftServer.getBlockTileEntity(n3, n4, n5));
                    }
                }
            }
            this.numBlocksToUpdate = 0;
        }

        private void updateTileEntity(TileEntity blockEntity) {
            if (blockEntity != null) {
                Packet packet = blockEntity.getDescriptionPacket();
                if (packet != null) {
                    this.sendPacketToPlayersInInstance(packet);
                }
            }
        }
    }
}
