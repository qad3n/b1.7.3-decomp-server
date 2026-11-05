package net.minecraft.src;

import java.util.Random;

public class BlockRedstoneOre extends Block {
    private boolean lit;

    public BlockRedstoneOre(int id, int sprite, boolean lit) {
        super(id, sprite, Material.rock);
        if (lit) {
            this.setTickOnLoad(true);
        }

        this.lit = lit;
    }

    @Override
    public int tickRate() {
        return 30;
    }

    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        this.interact(world, x, y, z);
        super.onBlockClicked(world, x, y, z, player);
    }

    @Override
    public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
        this.interact(world, x, y, z);
        super.onEntityWalking(world, x, y, z, entity);
    }

    @Override
    public boolean blockActivated(World world, int x, int y, int z, EntityPlayer player) {
        this.interact(world, x, y, z);
        return super.blockActivated(world, x, y, z, player);
    }

    private void interact(World world, int x, int y, int z) {
        this.addParticles(world, x, y, z);
        if (this.blockID == Block.oreRedstone.blockID) {
            world.setBlockWithNotify(x, y, z, Block.oreRedstoneGlowing.blockID);
        }
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (this.blockID == Block.oreRedstoneGlowing.blockID) {
            world.setBlockWithNotify(x, y, z, Block.oreRedstone.blockID);
        }
    }

    @Override
    public int idDropped(int metadata, Random random) {
        return Item.redstone.shiftedIndex;
    }

    @Override
    public int quantityDropped(Random random) {
        return 4 + random.nextInt(2);
    }

    private void addParticles(World world, int x, int y, int z) {
        Random random = world.rand;
        double d0 = 0.0625;

        for (int i = 0; i < 6; i++) {
            double d1 = x + random.nextFloat();
            double d2 = y + random.nextFloat();
            double d3 = z + random.nextFloat();
            if (i == 0 && !world.isBlockOpaqueCube(x, y + 1, z)) {
                d2 = y + 1 + d0;
            }

            if (i == 1 && !world.isBlockOpaqueCube(x, y - 1, z)) {
                d2 = y + 0 - d0;
            }

            if (i == 2 && !world.isBlockOpaqueCube(x, y, z + 1)) {
                d3 = z + 1 + d0;
            }

            if (i == 3 && !world.isBlockOpaqueCube(x, y, z - 1)) {
                d3 = z + 0 - d0;
            }

            if (i == 4 && !world.isBlockOpaqueCube(x + 1, y, z)) {
                d1 = x + 1 + d0;
            }

            if (i == 5 && !world.isBlockOpaqueCube(x - 1, y, z)) {
                d1 = x + 0 - d0;
            }

            if (d1 < x || d1 > x + 1 || d2 < 0.0 || d2 > y + 1 || d3 < z || d3 > z + 1) {
                world.spawnParticle("reddust", d1, d2, d3, 0.0, 0.0, 0.0);
            }
        }
    }
}
