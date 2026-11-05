package net.minecraft.src;

import java.util.Random;

public class WorldGenHellLava
extends WorldGenerator {
    private int liquid;

    public WorldGenHellLava(int liquid) {
        this.liquid = liquid;
    }

    public boolean generate(World world, Random random, int x, int y, int z) {
        if (world.getBlockId(x, y + 1, z) != Block.bloodStone.blockID) {
            return false;
        }
        if (world.getBlockId(x, y, z) != 0 && world.getBlockId(x, y, z) != Block.bloodStone.blockID) {
            return false;
        }
        int n = 0;
        if (world.getBlockId(x - 1, y, z) == Block.bloodStone.blockID) {
            ++n;
        }
        if (world.getBlockId(x + 1, y, z) == Block.bloodStone.blockID) {
            ++n;
        }
        if (world.getBlockId(x, y, z - 1) == Block.bloodStone.blockID) {
            ++n;
        }
        if (world.getBlockId(x, y, z + 1) == Block.bloodStone.blockID) {
            ++n;
        }
        if (world.getBlockId(x, y - 1, z) == Block.bloodStone.blockID) {
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
        if (world.isAirBlock(x, y - 1, z)) {
            ++n2;
        }
        if (n == 4 && n2 == 1) {
            world.setBlockWithNotify(x, y, z, this.liquid);
            world.scheduledUpdatesAreImmediate = true;
            Block.blocksList[this.liquid].updateTick(world, x, y, z, random);
            world.scheduledUpdatesAreImmediate = false;
        }
        return true;
    }
}

