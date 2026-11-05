package net.minecraft.src;

import java.util.Random;

public class BlockStationary extends BlockFluid {
    protected BlockStationary(int var1, Material var2) {
        super(var1, var2);
        this.setTickOnLoad(false);
        if (var2 == Material.lava) {
            this.setTickOnLoad(true);
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlock) {
        super.onNeighborBlockChange(world, x, y, z, neighborBlock);
        if (world.getBlockId(x, y, z) == this.blockID) {
            this.convertToFlowing(world, x, y, z);
        }
    }

    private void convertToFlowing(World world, int x, int y, int z) {
        int i = world.getBlockMetadata(x, y, z);
        world.editingBlocks = true;
        world.setBlockAndMetadata(x, y, z, this.blockID - 1, i);
        world.markBlocksDirty(x, y, z, x, y, z);
        world.scheduleUpdateTick(x, y, z, this.blockID - 1, this.tickRate());
        world.editingBlocks = false;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (this.blockMaterial == Material.lava) {
            int i = random.nextInt(3);

            for (int j = 0; j < i; j++) {
                x += random.nextInt(3) - 1;
                y++;
                z += random.nextInt(3) - 1;
                int k = world.getBlockId(x, y, z);
                if (k == 0) {
                    if (this.isFlammable(world, x - 1, y, z)
                        || this.isFlammable(world, x + 1, y, z)
                        || this.isFlammable(world, x, y, z - 1)
                        || this.isFlammable(world, x, y, z + 1)
                        || this.isFlammable(world, x, y - 1, z)
                        || this.isFlammable(world, x, y + 1, z)) {
                        world.setBlockWithNotify(x, y, z, Block.fire.blockID);
                        return;
                    }
                } else if (Block.blocksList[k].blockMaterial.getIsSolid()) {
                    return;
                }
            }
        }
    }

    private boolean isFlammable(World world, int x, int y, int z) {
        return world.getBlockMaterial(x, y, z).getBurning();
    }
}
