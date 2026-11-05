package net.minecraft.src;

import java.util.Random;

public class BlockTNT
extends Block {
    public BlockTNT(int id, int sprite) {
        super(id, sprite, Material.tnt);
    }

    public int getBlockTextureFromSide(int face) {
        if (face == 0) {
            return this.blockIndexInTexture + 2;
        }
        if (face == 1) {
            return this.blockIndexInTexture + 1;
        }
        return this.blockIndexInTexture;
    }

    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        if (world.isBlockIndirectlyGettingPowered(x, y, z)) {
            this.onBlockDestroyedByPlayer(world, x, y, z, 1);
            world.setBlockWithNotify(x, y, z, 0);
        }
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlock) {
        if (neighborBlock > 0 && Block.blocksList[neighborBlock].canProvidePower() && world.isBlockIndirectlyGettingPowered(x, y, z)) {
            this.onBlockDestroyedByPlayer(world, x, y, z, 1);
            world.setBlockWithNotify(x, y, z, 0);
        }
    }

    public int quantityDropped(Random random) {
        return 0;
    }

    public void onBlockDestroyedByExplosion(World world, int x, int y, int z) {
        EntityTNTPrimed entityTNTPrimed = new EntityTNTPrimed(world, (float)x + 0.5f, (float)y + 0.5f, (float)z + 0.5f);
        entityTNTPrimed.fuse = world.rand.nextInt(entityTNTPrimed.fuse / 4) + entityTNTPrimed.fuse / 8;
        world.entityJoinedWorld(entityTNTPrimed);
    }

    public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int metadata) {
        if (world.singleplayerWorld) {
            return;
        }
        if ((metadata & 1) == 0) {
            this.dropBlockAsItem_do(world, x, y, z, new ItemStack(Block.tnt.blockID, 1, 0));
        } else {
            EntityTNTPrimed entityTNTPrimed = new EntityTNTPrimed(world, (float)x + 0.5f, (float)y + 0.5f, (float)z + 0.5f);
            world.entityJoinedWorld(entityTNTPrimed);
            world.playSoundAtEntity(entityTNTPrimed, "random.fuse", 1.0f, 1.0f);
        }
    }

    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().itemID == Item.flintAndSteel.shiftedIndex) {
            world.setBlockMetadata(x, y, z, 1);
        }
        super.onBlockClicked(world, x, y, z, player);
    }

    public boolean blockActivated(World world, int x, int y, int z, EntityPlayer player) {
        return super.blockActivated(world, x, y, z, player);
    }
}

