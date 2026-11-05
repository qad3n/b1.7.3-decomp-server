package net.minecraft.src;

import java.util.ArrayList;
import java.util.Random;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class BlockStairs
extends Block {
    private Block modelBlock;

    protected BlockStairs(int id, Block baseBlock) {
        super(id, baseBlock.blockIndexInTexture, baseBlock.blockMaterial);
        this.modelBlock = baseBlock;
        this.setHardness(baseBlock.blockHardness);
        this.setResistance(baseBlock.blockResistance / 3.0f);
        this.setStepSound(baseBlock.stepSound);
        this.setLightOpacity(255);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
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
    public void getCollidingBoundingBoxes(World world, int x, int y, int z, AxisAlignedBB shape, ArrayList<AxisAlignedBB> collisions) {
        int n = world.getBlockMetadata(x, y, z);
        if (n == 0) {
            this.setBlockBounds(0.0f, 0.0f, 0.0f, 0.5f, 0.5f, 1.0f);
            super.getCollidingBoundingBoxes(world, x, y, z, shape, collisions);
            this.setBlockBounds(0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
            super.getCollidingBoundingBoxes(world, x, y, z, shape, collisions);
        } else if (n == 1) {
            this.setBlockBounds(0.0f, 0.0f, 0.0f, 0.5f, 1.0f, 1.0f);
            super.getCollidingBoundingBoxes(world, x, y, z, shape, collisions);
            this.setBlockBounds(0.5f, 0.0f, 0.0f, 1.0f, 0.5f, 1.0f);
            super.getCollidingBoundingBoxes(world, x, y, z, shape, collisions);
        } else if (n == 2) {
            this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.5f, 0.5f);
            super.getCollidingBoundingBoxes(world, x, y, z, shape, collisions);
            this.setBlockBounds(0.0f, 0.0f, 0.5f, 1.0f, 1.0f, 1.0f);
            super.getCollidingBoundingBoxes(world, x, y, z, shape, collisions);
        } else if (n == 3) {
            this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.5f);
            super.getCollidingBoundingBoxes(world, x, y, z, shape, collisions);
            this.setBlockBounds(0.0f, 0.0f, 0.5f, 1.0f, 0.5f, 1.0f);
            super.getCollidingBoundingBoxes(world, x, y, z, shape, collisions);
        }
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        this.modelBlock.onBlockClicked(world, x, y, z, player);
    }

    @Override
    public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int metadata) {
        this.modelBlock.onBlockDestroyedByPlayer(world, x, y, z, metadata);
    }

    @Override
    public float getExplosionResistance(Entity entity) {
        return this.modelBlock.getExplosionResistance(entity);
    }

    @Override
    public int idDropped(int metadata, Random random) {
        return this.modelBlock.idDropped(metadata, random);
    }

    @Override
    public int quantityDropped(Random random) {
        return this.modelBlock.quantityDropped(random);
    }

    @Override
    public int getBlockTextureFromSideAndMetadata(int face, int metadata) {
        return this.modelBlock.getBlockTextureFromSideAndMetadata(face, metadata);
    }

    @Override
    public int getBlockTextureFromSide(int face) {
        return this.modelBlock.getBlockTextureFromSide(face);
    }

    @Override
    public int tickRate() {
        return this.modelBlock.tickRate();
    }

    @Override
    public void velocityToAddToEntity(World world, int x, int y, int z, Entity entity, Vec3D velocity) {
        this.modelBlock.velocityToAddToEntity(world, x, y, z, entity, velocity);
    }

    @Override
    public boolean isCollidable() {
        return this.modelBlock.isCollidable();
    }

    @Override
    public boolean canCollideCheck(int metadata, boolean allowLiquids) {
        return this.modelBlock.canCollideCheck(metadata, allowLiquids);
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return this.modelBlock.canPlaceBlockAt(world, x, y, z);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        this.onNeighborBlockChange(world, x, y, z, 0);
        this.modelBlock.onBlockAdded(world, x, y, z);
    }

    @Override
    public void onBlockRemoval(World world, int x, int y, int z) {
        this.modelBlock.onBlockRemoval(world, x, y, z);
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int metadata, float luck) {
        this.modelBlock.dropBlockAsItemWithChance(world, x, y, z, metadata, luck);
    }

    @Override
    public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
        this.modelBlock.onEntityWalking(world, x, y, z, entity);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        this.modelBlock.updateTick(world, x, y, z, random);
    }

    @Override
    public boolean blockActivated(World world, int x, int y, int z, EntityPlayer player) {
        return this.modelBlock.blockActivated(world, x, y, z, player);
    }

    @Override
    public void onBlockDestroyedByExplosion(World world, int x, int y, int z) {
        this.modelBlock.onBlockDestroyedByExplosion(world, x, y, z);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entity) {
        int n = MathHelper.floor_double((double)(entity.rotationYaw * 4.0f / 360.0f) + 0.5) & 3;
        if (n == 0) {
            world.setBlockMetadataWithNotify(x, y, z, 2);
        }
        if (n == 1) {
            world.setBlockMetadataWithNotify(x, y, z, 1);
        }
        if (n == 2) {
            world.setBlockMetadataWithNotify(x, y, z, 3);
        }
        if (n == 3) {
            world.setBlockMetadataWithNotify(x, y, z, 0);
        }
    }
}

