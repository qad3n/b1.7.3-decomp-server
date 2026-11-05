// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.src;

import java.util.Random;

public class BlockFarmland extends Block
{
    protected BlockFarmland(final int id) {
        super(id, Material.ground);
        this.blockIndexInTexture = 87;
        this.setTickOnLoad(true);
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.9375f, 1.0f);
        this.setLightOpacity(255);
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(final World world, final int x, final int y, final int z) {
        return AxisAlignedBB.getBoundingBoxFromPool(x + 0, y + 0, z + 0, x + 1, y + 1, z + 1);
    }
    
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    
    @Override
    public boolean isACube() {
        return false;
    }
    
    @Override
    public int getBlockTextureFromSideAndMetadata(final int face, final int metadata) {
        if (face == 1 && metadata > 0) {
            return this.blockIndexInTexture - 1;
        }
        if (face == 1) {
            return this.blockIndexInTexture;
        }
        return 2;
    }
    
    @Override
    public void updateTick(final World world, final int x, final int y, final int z, final Random random) {
        if (random.nextInt(5) == 0) {
            if (this.isWaterNearby(world, x, y, z) || world.canLightningStrikeAt(x, y + 1, z)) {
                world.setBlockMetadataWithNotify(x, y, z, 7);
            }
            else {
                final int blockMetadata = world.getBlockMetadata(x, y, z);
                if (blockMetadata > 0) {
                    world.setBlockMetadataWithNotify(x, y, z, blockMetadata - 1);
                }
                else if (!this.isCropsNearby(world, x, y, z)) {
                    world.setBlockWithNotify(x, y, z, Block.dirt.blockID);
                }
            }
        }
    }
    
    @Override
    public void onEntityWalking(final World world, final int x, final int y, final int z, final Entity entity) {
        if (world.rand.nextInt(4) == 0) {
            world.setBlockWithNotify(x, y, z, Block.dirt.blockID);
        }
    }
    
    private boolean isCropsNearby(final World world, final int x, final int y, final int z) {
        for (int n = 0, i = x - n; i <= x + n; ++i) {
            for (int j = z - n; j <= z + n; ++j) {
                if (world.getBlockId(i, y + 1, j) == Block.crops.blockID) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean isWaterNearby(final World world, final int x, final int y, final int z) {
        for (int i = x - 4; i <= x + 4; ++i) {
            for (int j = y; j <= y + 1; ++j) {
                for (int k = z - 4; k <= z + 4; ++k) {
                    if (world.getBlockMaterial(i, j, k) == Material.water) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlock) {
        super.onNeighborBlockChange(world, x, y, z, neighborBlock);
        Material material = world.getBlockMaterial(x, y + 1, z);
        if (material.isSolid()) {
            world.setBlockWithNotify(x, y, z, Block.dirt.blockID);
        }
    }
    
    @Override
    public int idDropped(final int metadata, final Random random) {
        return Block.dirt.idDropped(0, random);
    }
}
