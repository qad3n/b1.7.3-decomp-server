package net.minecraft.src;

import java.util.Random;

public class BlockGrass
extends Block {
    protected BlockGrass(int id) {
        super(id, Material.grass);
        this.blockIndexInTexture = 3;
        this.setTickOnLoad(true);
    }

    public void updateTick(World world, int x, int y, int z, Random random) {
        if (world.singleplayerWorld) {
            return;
        }
        if (world.getBlockLightValue(x, y + 1, z) < 4 && Block.lightOpacity[world.getBlockId(x, y + 1, z)] > 2) {
            if (random.nextInt(4) != 0) {
                return;
            }
            world.setBlockWithNotify(x, y, z, Block.dirt.blockID);
        } else if (world.getBlockLightValue(x, y + 1, z) >= 9) {
            int n = x + random.nextInt(3) - 1;
            int n2 = y + random.nextInt(5) - 3;
            int n3 = z + random.nextInt(3) - 1;
            int n4 = world.getBlockId(n, n2 + 1, n3);
            if (world.getBlockId(n, n2, n3) == Block.dirt.blockID && world.getBlockLightValue(n, n2 + 1, n3) >= 4 && Block.lightOpacity[n4] <= 2) {
                world.setBlockWithNotify(n, n2, n3, Block.grass.blockID);
            }
        }
    }

    public int idDropped(int metadata, Random random) {
        return Block.dirt.idDropped(0, random);
    }
}

