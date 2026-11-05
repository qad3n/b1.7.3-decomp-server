// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockRail extends Block
{
    private final boolean alwaysStraight;
    
    public static final boolean isRail(final World world, final int x, final int y, final int z) {
        final int blockId = world.getBlockId(x, y, z);
        return blockId == Block.minecartTrack.blockID || blockId == Block.railPowered.blockID || blockId == Block.railDetector.blockID;
    }
    
    public static final boolean isRail(final int block) {
        return block == Block.minecartTrack.blockID || block == Block.railPowered.blockID || block == Block.railDetector.blockID;
    }
    
    protected BlockRail(final int id, final int sprite, final boolean alwaysStraight) {
        super(id, sprite, Material.circuits);
        this.alwaysStraight = alwaysStraight;
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.125f, 1.0f);
    }
    
    public boolean isAlwaysStraight() {
        return this.alwaysStraight;
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(final World world, final int x, final int y, final int z) {
        return null;
    }
    
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    
    @Override
    public MovingObjectPosition collisionRayTrace(final World world, final int x, final int y, final int z, final Vec3D from, final Vec3D to) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.collisionRayTrace(world, x, y, z, from, to);
    }
    
    @Override
    public void setBlockBoundsBasedOnState(final IBlockAccess world, final int x, final int y, final int z) {
        final int blockMetadata = world.getBlockMetadata(x, y, z);
        if (blockMetadata >= 2 && blockMetadata <= 5) {
            this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.625f, 1.0f);
        }
        else {
            this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.125f, 1.0f);
        }
    }
    
    @Override
    public int getBlockTextureFromSideAndMetadata(final int face, final int metadata) {
        if (this.alwaysStraight) {
            if (this.blockID == Block.railPowered.blockID && (metadata & 0x8) == 0x0) {
                return this.blockIndexInTexture - 16;
            }
        }
        else if (metadata >= 6) {
            return this.blockIndexInTexture - 16;
        }
        return this.blockIndexInTexture;
    }
    
    @Override
    public boolean isACube() {
        return false;
    }
    
    @Override
    public int quantityDropped(final Random random) {
        return 1;
    }
    
    @Override
    public boolean canPlaceBlockAt(final World world, final int x, final int y, final int z) {
        return world.isBlockNormalCube(x, y - 1, z);
    }
    
    @Override
    public void onBlockAdded(final World world, final int x, final int y, final int z) {
        if (!world.singleplayerWorld) {
            this.updateShape(world, x, y, z, true);
        }
    }
    
    @Override
    public void onNeighborBlockChange(final World world, final int x, final int y, final int z, final int neighborBlock) {
        if (world.singleplayerWorld) {
            return;
        }
        int blockMetadata;
        final int n = blockMetadata = world.getBlockMetadata(x, y, z);
        if (this.alwaysStraight) {
            blockMetadata &= 0x7;
        }
        boolean b = false;
        if (!world.isBlockNormalCube(x, y - 1, z)) {
            b = true;
        }
        if (blockMetadata == 2 && !world.isBlockNormalCube(x + 1, y, z)) {
            b = true;
        }
        if (blockMetadata == 3 && !world.isBlockNormalCube(x - 1, y, z)) {
            b = true;
        }
        if (blockMetadata == 4 && !world.isBlockNormalCube(x, y, z - 1)) {
            b = true;
        }
        if (blockMetadata == 5 && !world.isBlockNormalCube(x, y, z + 1)) {
            b = true;
        }
        if (b) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.setBlockWithNotify(x, y, z, 0);
        }
        else if (this.blockID == Block.railPowered.blockID) {
            final boolean b2 = world.isBlockIndirectlyGettingPowered(x, y, z) || world.isBlockIndirectlyGettingPowered(x, y + 1, z) || this.isPoweredByConnectedRails(world, x, y, z, n, true, 0) || this.isPoweredByConnectedRails(world, x, y, z, n, false, 0);
            boolean b3 = false;
            if (b2 && (n & 0x8) == 0x0) {
                world.setBlockMetadataWithNotify(x, y, z, blockMetadata | 0x8);
                b3 = true;
            }
            else if (!b2 && (n & 0x8) != 0x0) {
                world.setBlockMetadataWithNotify(x, y, z, blockMetadata);
                b3 = true;
            }
            if (b3) {
                world.notifyBlocksOfNeighborChange(x, y - 1, z, this.blockID);
                if (blockMetadata == 2 || blockMetadata == 3 || blockMetadata == 4 || blockMetadata == 5) {
                    world.notifyBlocksOfNeighborChange(x, y + 1, z, this.blockID);
                }
            }
        }
        else if (neighborBlock > 0 && Block.blocksList[neighborBlock].canProvidePower() && !this.alwaysStraight && new RailLogic(world, x, y, z).getAdjacentTracks() == 3) {
            this.updateShape(world, x, y, z, false);
        }
    }
    
    private void updateShape(final World world, final int x, final int y, final int z, final boolean force) {
        if (world.singleplayerWorld) {
            return;
        }
        new RailLogic(world, x, y, z).updateState(world.isBlockIndirectlyGettingPowered(x, y, z), force);
    }
    
    private boolean isPoweredByConnectedRails(final World world, int x, int y, int z, final int metadata, final boolean towardsNegative, final int depth) {
        if (depth >= 8) {
            return false;
        }
        int n = metadata & 0x7;
        boolean b = true;
        switch (n) {
            case 0: {
                if (towardsNegative) {
                    ++z;
                    break;
                }
                --z;
                break;
            }
            case 1: {
                if (towardsNegative) {
                    --x;
                    break;
                }
                ++x;
                break;
            }
            case 2: {
                if (towardsNegative) {
                    --x;
                }
                else {
                    ++x;
                    ++y;
                    b = false;
                }
                n = 1;
                break;
            }
            case 3: {
                if (towardsNegative) {
                    --x;
                    ++y;
                    b = false;
                }
                else {
                    ++x;
                }
                n = 1;
                break;
            }
            case 4: {
                if (towardsNegative) {
                    ++z;
                }
                else {
                    --z;
                    ++y;
                    b = false;
                }
                n = 0;
                break;
            }
            case 5: {
                if (towardsNegative) {
                    ++z;
                    ++y;
                    b = false;
                }
                else {
                    --z;
                }
                n = 0;
                break;
            }
        }
        return this.isPoweredByRail(world, x, y, z, towardsNegative, depth, n) || (b && this.isPoweredByRail(world, x, y - 1, z, towardsNegative, depth, n));
    }
    
    private boolean isPoweredByRail(World world, int x, int y, int z, boolean towardsNegative, int depth, int shape) {
        int n = world.getBlockId(x, y, z);
        if (n == Block.railPowered.blockID) {
            int n2 = world.getBlockMetadata(x, y, z);
            int n3 = n2 & 7;
            if (shape == 1 && (n3 == 0 || n3 == 4 || n3 == 5)) {
                return false;
            }
            if (shape == 0 && (n3 == 1 || n3 == 2 || n3 == 3)) {
                return false;
            }
            if ((n2 & 8) != 0) {
                if (world.isBlockIndirectlyGettingPowered(x, y, z) || world.isBlockIndirectlyGettingPowered(x, y + 1, z)) {
                    return true;
                }
                return this.isPoweredByConnectedRails(world, x, y, z, n2, towardsNegative, depth + 1);
            }
        }
        return false;
    }
    
    @Override
    public int getMobilityFlag() {
        return 0;
    }
    
    public class RailLogic
    {
        private World worldObj;
        private int trackX;
        private int trackY;
        private int trackZ;
        private final boolean alwaysStraight;
        private List<ChunkPosition> connectedTracks;
        
        public RailLogic(final World world, final int x, final int y, final int z) {
            this.connectedTracks = new ArrayList<ChunkPosition>();
            this.worldObj = world;
            this.trackX = x;
            this.trackY = y;
            this.trackZ = z;
            final int blockId = world.getBlockId(x, y, z);
            int blockMetadata = world.getBlockMetadata(x, y, z);
            if (((BlockRail)Block.blocksList[blockId]).alwaysStraight) {
                this.alwaysStraight = true;
                blockMetadata &= 0xFFFFFFF7;
            }
            else {
                this.alwaysStraight = false;
            }
            this.updateConnections(blockMetadata);
        }
        
        private void updateConnections(final int metadata) {
            this.connectedTracks.clear();
            if (metadata == 0) {
                this.connectedTracks.add(new ChunkPosition(this.trackX, this.trackY, this.trackZ - 1));
                this.connectedTracks.add(new ChunkPosition(this.trackX, this.trackY, this.trackZ + 1));
            }
            else if (metadata == 1) {
                this.connectedTracks.add(new ChunkPosition(this.trackX - 1, this.trackY, this.trackZ));
                this.connectedTracks.add(new ChunkPosition(this.trackX + 1, this.trackY, this.trackZ));
            }
            else if (metadata == 2) {
                this.connectedTracks.add(new ChunkPosition(this.trackX - 1, this.trackY, this.trackZ));
                this.connectedTracks.add(new ChunkPosition(this.trackX + 1, this.trackY + 1, this.trackZ));
            }
            else if (metadata == 3) {
                this.connectedTracks.add(new ChunkPosition(this.trackX - 1, this.trackY + 1, this.trackZ));
                this.connectedTracks.add(new ChunkPosition(this.trackX + 1, this.trackY, this.trackZ));
            }
            else if (metadata == 4) {
                this.connectedTracks.add(new ChunkPosition(this.trackX, this.trackY + 1, this.trackZ - 1));
                this.connectedTracks.add(new ChunkPosition(this.trackX, this.trackY, this.trackZ + 1));
            }
            else if (metadata == 5) {
                this.connectedTracks.add(new ChunkPosition(this.trackX, this.trackY, this.trackZ - 1));
                this.connectedTracks.add(new ChunkPosition(this.trackX, this.trackY + 1, this.trackZ + 1));
            }
            else if (metadata == 6) {
                this.connectedTracks.add(new ChunkPosition(this.trackX + 1, this.trackY, this.trackZ));
                this.connectedTracks.add(new ChunkPosition(this.trackX, this.trackY, this.trackZ + 1));
            }
            else if (metadata == 7) {
                this.connectedTracks.add(new ChunkPosition(this.trackX - 1, this.trackY, this.trackZ));
                this.connectedTracks.add(new ChunkPosition(this.trackX, this.trackY, this.trackZ + 1));
            }
            else if (metadata == 8) {
                this.connectedTracks.add(new ChunkPosition(this.trackX - 1, this.trackY, this.trackZ));
                this.connectedTracks.add(new ChunkPosition(this.trackX, this.trackY, this.trackZ - 1));
            }
            else if (metadata == 9) {
                this.connectedTracks.add(new ChunkPosition(this.trackX + 1, this.trackY, this.trackZ));
                this.connectedTracks.add(new ChunkPosition(this.trackX, this.trackY, this.trackZ - 1));
            }
        }
        
        private void removeSoftConnections() {
            for (int i = 0; i < this.connectedTracks.size(); ++i) {
                final RailLogic minecartTrackLogic = this.getMinecartTrackLogic(this.connectedTracks.get(i));
                if (minecartTrackLogic == null || !minecartTrackLogic.isConnectedTo(this)) {
                    this.connectedTracks.remove(i--);
                }
                else {
                    this.connectedTracks.set(i, new ChunkPosition(minecartTrackLogic.trackX, minecartTrackLogic.trackY, minecartTrackLogic.trackZ));
                }
            }
        }
        
        private boolean isMinecartTrack(final int x, final int y, final int z) {
            return BlockRail.isRail(this.worldObj, x, y, z) || BlockRail.isRail(this.worldObj, x, y + 1, z) || BlockRail.isRail(this.worldObj, x, y - 1, z);
        }
        
        private RailLogic getMinecartTrackLogic(final ChunkPosition pos) {
            if (BlockRail.isRail(this.worldObj, pos.x, pos.y, pos.z)) {
                return new RailLogic(this.worldObj, pos.x, pos.y, pos.z);
            }
            if (BlockRail.isRail(this.worldObj, pos.x, pos.y + 1, pos.z)) {
                return new RailLogic(this.worldObj, pos.x, pos.y + 1, pos.z);
            }
            if (BlockRail.isRail(this.worldObj, pos.x, pos.y - 1, pos.z)) {
                return new RailLogic(this.worldObj, pos.x, pos.y - 1, pos.z);
            }
            return null;
        }
        
        private boolean isConnectedTo(final RailLogic rail) {
            for (int i = 0; i < this.connectedTracks.size(); ++i) {
                final ChunkPosition chunkPosition = this.connectedTracks.get(i);
                if (chunkPosition.x == rail.trackX && chunkPosition.z == rail.trackZ) {
                    return true;
                }
            }
            return false;
        }
        
        private boolean hasConnection(final int x, final int y, final int z) {
            for (int i = 0; i < this.connectedTracks.size(); ++i) {
                final ChunkPosition chunkPosition = this.connectedTracks.get(i);
                if (chunkPosition.x == x && chunkPosition.z == z) {
                    return true;
                }
            }
            return false;
        }
        
        private int getAdjacentTracks() {
            int n = 0;
            if (this.isMinecartTrack(this.trackX, this.trackY, this.trackZ - 1)) {
                ++n;
            }
            if (this.isMinecartTrack(this.trackX, this.trackY, this.trackZ + 1)) {
                ++n;
            }
            if (this.isMinecartTrack(this.trackX - 1, this.trackY, this.trackZ)) {
                ++n;
            }
            if (this.isMinecartTrack(this.trackX + 1, this.trackY, this.trackZ)) {
                ++n;
            }
            return n;
        }
        
        private boolean handleKeyPress(RailLogic rail) {
            if (this.isConnectedTo(rail)) {
                return true;
            }
            if (this.connectedTracks.size() == 2) {
                return false;
            }
            if (this.connectedTracks.size() == 0) {
                return true;
            }
            ChunkPosition chunkPosition = this.connectedTracks.get(0);
            if (rail.trackY == this.trackY && chunkPosition.y == this.trackY) {
                return true;
            }
            return true;
        }
        
        private void addConnection(final RailLogic rail) {
            this.connectedTracks.add(new ChunkPosition(rail.trackX, rail.trackY, rail.trackZ));
            final boolean hasConnection = this.hasConnection(this.trackX, this.trackY, this.trackZ - 1);
            final boolean hasConnection2 = this.hasConnection(this.trackX, this.trackY, this.trackZ + 1);
            final boolean hasConnection3 = this.hasConnection(this.trackX - 1, this.trackY, this.trackZ);
            final boolean hasConnection4 = this.hasConnection(this.trackX + 1, this.trackY, this.trackZ);
            int n = -1;
            if (hasConnection || hasConnection2) {
                n = 0;
            }
            if (hasConnection3 || hasConnection4) {
                n = 1;
            }
            if (!this.alwaysStraight) {
                if (hasConnection2 && hasConnection4 && !hasConnection && !hasConnection3) {
                    n = 6;
                }
                if (hasConnection2 && hasConnection3 && !hasConnection && !hasConnection4) {
                    n = 7;
                }
                if (hasConnection && hasConnection3 && !hasConnection2 && !hasConnection4) {
                    n = 8;
                }
                if (hasConnection && hasConnection4 && !hasConnection2 && !hasConnection3) {
                    n = 9;
                }
            }
            if (n == 0) {
                if (BlockRail.isRail(this.worldObj, this.trackX, this.trackY + 1, this.trackZ - 1)) {
                    n = 4;
                }
                if (BlockRail.isRail(this.worldObj, this.trackX, this.trackY + 1, this.trackZ + 1)) {
                    n = 5;
                }
            }
            if (n == 1) {
                if (BlockRail.isRail(this.worldObj, this.trackX + 1, this.trackY + 1, this.trackZ)) {
                    n = 2;
                }
                if (BlockRail.isRail(this.worldObj, this.trackX - 1, this.trackY + 1, this.trackZ)) {
                    n = 3;
                }
            }
            if (n < 0) {
                n = 0;
            }
            int metadata = n;
            if (this.alwaysStraight) {
                metadata = ((this.worldObj.getBlockMetadata(this.trackX, this.trackY, this.trackZ) & 0x8) | n);
            }
            this.worldObj.setBlockMetadataWithNotify(this.trackX, this.trackY, this.trackZ, metadata);
        }
        
        private boolean hasNeighborRail(final int x, final int y, final int z) {
            final RailLogic minecartTrackLogic = this.getMinecartTrackLogic(new ChunkPosition(x, y, z));
            if (minecartTrackLogic == null) {
                return false;
            }
            minecartTrackLogic.removeSoftConnections();
            return minecartTrackLogic.handleKeyPress(this);
        }
        
        public void updateState(final boolean powered, final boolean force) {
            final boolean hasNeighborRail = this.hasNeighborRail(this.trackX, this.trackY, this.trackZ - 1);
            final boolean hasNeighborRail2 = this.hasNeighborRail(this.trackX, this.trackY, this.trackZ + 1);
            final boolean hasNeighborRail3 = this.hasNeighborRail(this.trackX - 1, this.trackY, this.trackZ);
            final boolean hasNeighborRail4 = this.hasNeighborRail(this.trackX + 1, this.trackY, this.trackZ);
            int metadata = -1;
            if ((hasNeighborRail || hasNeighborRail2) && !hasNeighborRail3 && !hasNeighborRail4) {
                metadata = 0;
            }
            if ((hasNeighborRail3 || hasNeighborRail4) && !hasNeighborRail && !hasNeighborRail2) {
                metadata = 1;
            }
            if (!this.alwaysStraight) {
                if (hasNeighborRail2 && hasNeighborRail4 && !hasNeighborRail && !hasNeighborRail3) {
                    metadata = 6;
                }
                if (hasNeighborRail2 && hasNeighborRail3 && !hasNeighborRail && !hasNeighborRail4) {
                    metadata = 7;
                }
                if (hasNeighborRail && hasNeighborRail3 && !hasNeighborRail2 && !hasNeighborRail4) {
                    metadata = 8;
                }
                if (hasNeighborRail && hasNeighborRail4 && !hasNeighborRail2 && !hasNeighborRail3) {
                    metadata = 9;
                }
            }
            if (metadata == -1) {
                if (hasNeighborRail || hasNeighborRail2) {
                    metadata = 0;
                }
                if (hasNeighborRail3 || hasNeighborRail4) {
                    metadata = 1;
                }
                if (!this.alwaysStraight) {
                    if (powered) {
                        if (hasNeighborRail2 && hasNeighborRail4) {
                            metadata = 6;
                        }
                        if (hasNeighborRail3 && hasNeighborRail2) {
                            metadata = 7;
                        }
                        if (hasNeighborRail4 && hasNeighborRail) {
                            metadata = 9;
                        }
                        if (hasNeighborRail && hasNeighborRail3) {
                            metadata = 8;
                        }
                    }
                    else {
                        if (hasNeighborRail && hasNeighborRail3) {
                            metadata = 8;
                        }
                        if (hasNeighborRail4 && hasNeighborRail) {
                            metadata = 9;
                        }
                        if (hasNeighborRail3 && hasNeighborRail2) {
                            metadata = 7;
                        }
                        if (hasNeighborRail2 && hasNeighborRail4) {
                            metadata = 6;
                        }
                    }
                }
            }
            if (metadata == 0) {
                if (BlockRail.isRail(this.worldObj, this.trackX, this.trackY + 1, this.trackZ - 1)) {
                    metadata = 4;
                }
                if (BlockRail.isRail(this.worldObj, this.trackX, this.trackY + 1, this.trackZ + 1)) {
                    metadata = 5;
                }
            }
            if (metadata == 1) {
                if (BlockRail.isRail(this.worldObj, this.trackX + 1, this.trackY + 1, this.trackZ)) {
                    metadata = 2;
                }
                if (BlockRail.isRail(this.worldObj, this.trackX - 1, this.trackY + 1, this.trackZ)) {
                    metadata = 3;
                }
            }
            if (metadata < 0) {
                metadata = 0;
            }
            this.updateConnections(metadata);
            int metadata2 = metadata;
            if (this.alwaysStraight) {
                metadata2 = ((this.worldObj.getBlockMetadata(this.trackX, this.trackY, this.trackZ) & 0x8) | metadata);
            }
            if (force || this.worldObj.getBlockMetadata(this.trackX, this.trackY, this.trackZ) != metadata2) {
                this.worldObj.setBlockMetadataWithNotify(this.trackX, this.trackY, this.trackZ, metadata2);
                for (int i = 0; i < this.connectedTracks.size(); ++i) {
                    final RailLogic minecartTrackLogic = this.getMinecartTrackLogic(this.connectedTracks.get(i));
                    if (minecartTrackLogic != null) {
                        minecartTrackLogic.removeSoftConnections();
                        if (minecartTrackLogic.handleKeyPress(this)) {
                            minecartTrackLogic.addConnection(this);
                        }
                    }
                }
            }
        }
    }
}
