package net.minecraft.src;

import java.util.ArrayList;
import java.util.Random;

public class BlockPistonExtension extends Block {
    private int forcedSprite = -1;

    public BlockPistonExtension(int id, int sprite) {
        super(id, sprite, Material.piston);
        this.setStepSound(soundStoneFootstep);
        this.setHardness(0.5F);
    }

    @Override
    public void onBlockRemoval(World world, int x, int y, int z) {
        super.onBlockRemoval(world, x, y, z);
        int i = world.getBlockMetadata(x, y, z);
        int j = PistonBlockTextures.OPPOSITE[getFacing(i)];
        x += PistonBlockTextures.X_OFFSET[j];
        y += PistonBlockTextures.Y_OFFSET[j];
        z += PistonBlockTextures.Z_OFFSET[j];
        int k = world.getBlockId(x, y, z);
        if (k == Block.pistonBase.blockID || k == Block.pistonStickyBase.blockID) {
            i = world.getBlockMetadata(x, y, z);
            if (BlockPistonBase.isExtended(i)) {
                Block.blocksList[k].dropBlockAsItem(world, x, y, z, i);
                world.setBlockWithNotify(x, y, z, 0);
            }
        }
    }

    @Override
public int getBlockTextureFromSideAndMetadata(int face, int metadata) {
    int n = BlockPistonExtension.getFacing(metadata);
    if (face == n) {
        if (this.forcedSprite >= 0) {
            return this.forcedSprite;
        }
        if ((metadata & 8) != 0) {
            return this.blockIndexInTexture - 1;
        }
        return this.blockIndexInTexture;
    }
    if (face == PistonBlockTextures.OPPOSITE[n]) {
        return 107;
    }
    return 108;
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
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int face) {
        return false;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public void getCollidingBoundingBoxes(World world, int x, int y, int z, AxisAlignedBB shape, ArrayList<AxisAlignedBB> collisions) {
        int i = world.getBlockMetadata(x, y, z);
        switch (getFacing(i)) {
            case 0:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
                super.getCollidingBoundingBoxes(world, x, y, z, shape, collisions);
                this.setBlockBounds(0.375F, 0.25F, 0.375F, 0.625F, 1.0F, 0.625F);
                super.getCollidingBoundingBoxes(world, x, y, z, shape, collisions);
                break;
            case 1:
                this.setBlockBounds(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
                super.getCollidingBoundingBoxes(world, x, y, z, shape, collisions);
                this.setBlockBounds(0.375F, 0.0F, 0.375F, 0.625F, 0.75F, 0.625F);
                super.getCollidingBoundingBoxes(world, x, y, z, shape, collisions);
                break;
            case 2:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
                super.getCollidingBoundingBoxes(world, x, y, z, shape, collisions);
                this.setBlockBounds(0.25F, 0.375F, 0.25F, 0.75F, 0.625F, 1.0F);
                super.getCollidingBoundingBoxes(world, x, y, z, shape, collisions);
                break;
            case 3:
                this.setBlockBounds(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
                super.getCollidingBoundingBoxes(world, x, y, z, shape, collisions);
                this.setBlockBounds(0.25F, 0.375F, 0.0F, 0.75F, 0.625F, 0.75F);
                super.getCollidingBoundingBoxes(world, x, y, z, shape, collisions);
                break;
            case 4:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
                super.getCollidingBoundingBoxes(world, x, y, z, shape, collisions);
                this.setBlockBounds(0.375F, 0.25F, 0.25F, 0.625F, 0.75F, 1.0F);
                super.getCollidingBoundingBoxes(world, x, y, z, shape, collisions);
                break;
            case 5:
                this.setBlockBounds(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                super.getCollidingBoundingBoxes(world, x, y, z, shape, collisions);
                this.setBlockBounds(0.0F, 0.375F, 0.25F, 0.75F, 0.625F, 0.75F);
                super.getCollidingBoundingBoxes(world, x, y, z, shape, collisions);
        }

        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int i = world.getBlockMetadata(x, y, z);
        switch (getFacing(i)) {
            case 0:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
                break;
            case 1:
                this.setBlockBounds(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
                break;
            case 2:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
                break;
            case 3:
                this.setBlockBounds(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
                break;
            case 4:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
                break;
            case 5:
                this.setBlockBounds(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlock) {
        int i = getFacing(world.getBlockMetadata(x, y, z));
        int j = world.getBlockId(x - PistonBlockTextures.X_OFFSET[i], y - PistonBlockTextures.Y_OFFSET[i], z - PistonBlockTextures.Z_OFFSET[i]);
        if (j != Block.pistonBase.blockID && j != Block.pistonStickyBase.blockID) {
            world.setBlockWithNotify(x, y, z, 0);
        } else {
            Block.blocksList[j]
                .onNeighborBlockChange(
                    world, x - PistonBlockTextures.X_OFFSET[i], y - PistonBlockTextures.Y_OFFSET[i], z - PistonBlockTextures.Z_OFFSET[i], neighborBlock
                );
        }
    }

    public static int getFacing(int metadata) {
        return metadata & 7;
    }
}
