package net.minecraft.src;

import java.util.Random;

public class WorldGenLiquids
extends WorldGenerator {
    private int liquidBlockId;

    public WorldGenLiquids(int liquid) {
        this.liquidBlockId = liquid;
    }

    public boolean generate(World world, Random random, int x, int y, int z) {
        if (world.getBlockId(x, y + 1, z) != Block.stone.blockID) {
            return false;
        }
        if (world.getBlockId(x, y - 1, z) != Block.stone.blockID) {
            return false;
        }
        if (world.getBlockId(x, y, z) != 0 && world.getBlockId(x, y, z) != Block.stone.blockID) {
            return false;
        }
        int n = 0;
        if (world.getBlockId(x - 1, y, z) == Block.stone.blockID) {
            ++n;
        }
        if (world.getBlockId(x + 1, y, z) == Block.stone.blockID) {
            ++n;
        }
        if (world.getBlockId(x, y, z - 1) == Block.stone.blockID) {
            ++n;
        }
        if (world.getBlockId(x, y, z + 1) == Block.stone.blockID) {
            ++n;
        }
        int n2 = 0;
        if (world.isAirBlock(x - 1, y, z)) {
            ++n2;
        }
        if (world.isAirBlock(x + 1, y, z)) {
            ++n2;
        }
        if (world.isAirBlock(x, y, z - 1)) {
            ++n2;
        }
        if (world.isAirBlock(x, y, z + 1)) {
            ++n2;
        }
        if (n == 3 && n2 == 1) {
            world.setBlockWithNotify(x, y, z, this.liquidBlockId);
            world.scheduledUpdatesAreImmediate = true;
            Block.blocksList[this.liquidBlockId].updateTick(world, x, y, z, random);
            world.scheduledUpdatesAreImmediate = false;
        }
        return true;
    }
}

