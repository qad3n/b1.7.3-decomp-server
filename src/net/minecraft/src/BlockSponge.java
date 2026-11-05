package net.minecraft.src;

public class BlockSponge extends Block {
    protected BlockSponge(int id) {
        super(id, Material.sponge);
        this.blockIndexInTexture = 48;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        byte b0 = 2;

        for (int i = x - b0; i <= x + b0; i++) {
            for (int j = y - b0; j <= y + b0; j++) {
                for (int k = z - b0; k <= z + b0; k++) {
                    if (world.getBlockMaterial(i, j, k) == Material.water) {
                    }
                }
            }
        }
    }

    @Override
    public void onBlockRemoval(World world, int x, int y, int z) {
        byte b0 = 2;

        for (int i = x - b0; i <= x + b0; i++) {
            for (int j = y - b0; j <= y + b0; j++) {
                for (int k = z - b0; k <= z + b0; k++) {
                    world.notifyBlocksOfNeighborChange(i, j, k, world.getBlockId(i, j, k));
                }
            }
        }
    }
}
