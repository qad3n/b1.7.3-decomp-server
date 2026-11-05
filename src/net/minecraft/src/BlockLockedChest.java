package net.minecraft.src;

import java.util.Random;

public class BlockLockedChest
extends Block {
    protected BlockLockedChest(int id) {
        super(id, Material.wood);
        this.blockIndexInTexture = 26;
    }

    public int getBlockTextureFromSide(int face) {
        if (face == 1) {
            return this.blockIndexInTexture - 1;
        }
        if (face == 0) {
            return this.blockIndexInTexture - 1;
        }
        if (face == 3) {
            return this.blockIndexInTexture + 1;
        }
        return this.blockIndexInTexture;
    }

    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return true;
    }

    public void updateTick(World world, int x, int y, int z, Random random) {
        world.setBlockWithNotify(x, y, z, 0);
    }
}

