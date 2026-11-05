// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.src;

import java.util.Random;

public class WorldGenForest extends WorldGenerator
{
    @Override
    public boolean generate(final World world, final Random random, final int x, final int y, final int z) {
        final int n = random.nextInt(3) + 5;
        int n2 = 1;
        if (y < 1 || y + n + 1 > 128) {
            return false;
        }
        for (int i = y; i <= y + 1 + n; ++i) {
            int n3 = 1;
            if (i == y) {
                n3 = 0;
            }
            if (i >= y + 1 + n - 2) {
                n3 = 2;
            }
            for (int x2 = x - n3; x2 <= x + n3 && n2 != 0; ++x2) {
                for (int z2 = z - n3; z2 <= z + n3 && n2 != 0; ++z2) {
                    if (i >= 0 && i < 128) {
                        final int blockId = world.getBlockId(x2, i, z2);
                        if (blockId != 0 && blockId != Block.leaves.blockID) {
                            n2 = 0;
                        }
                    }
                    else {
                        n2 = 0;
                    }
                }
            }
        }
        if (n2 == 0) {
            return false;
        }
        final int blockId2 = world.getBlockId(x, y - 1, z);
        if ((blockId2 != Block.grass.blockID && blockId2 != Block.dirt.blockID) || y >= 128 - n - 1) {
            return false;
        }
        world.setBlock(x, y - 1, z, Block.dirt.blockID);
        for (int j = y - 3 + n; j <= y + n; ++j) {
            final int n4 = j - (y + n);
            for (int n5 = 1 - n4 / 2, k = x - n5; k <= x + n5; ++k) {
                final int a = k - x;
                for (int l = z - n5; l <= z + n5; ++l) {
                    final int a2 = l - z;
                    if (Math.abs(a) == n5 && Math.abs(a2) == n5) {
                        if (random.nextInt(2) == 0) {
                            continue;
                        }
                        if (n4 == 0) {
                            continue;
                        }
                    }
                    if (!Block.opaqueCubeLookup[world.getBlockId(k, j, l)]) {
                        world.setBlockAndMetadata(k, j, l, Block.leaves.blockID, 2);
                    }
                }
            }
        }
        for (int n6 = 0; n6 < n; ++n6) {
            final int blockId3 = world.getBlockId(x, y + n6, z);
            if (blockId3 == 0 || blockId3 == Block.leaves.blockID) {
                world.setBlockAndMetadata(x, y + n6, z, Block.wood.blockID, 2);
            }
        }
        return true;
    }
}
