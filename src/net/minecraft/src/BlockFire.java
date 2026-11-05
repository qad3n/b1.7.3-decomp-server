package net.minecraft.src;

import java.util.Random;

public class BlockFire
extends Block {
    private int[] chanceToEncourageFire = new int[256];
    private int[] abilityToCatchFire = new int[256];

    protected BlockFire(int id, int sprite) {
        super(id, sprite, Material.fire);
        this.setTickOnLoad(true);
    }

    public void setFireBurnRates() {
        this.setBurnRate(Block.planks.blockID, 5, 20);
        this.setBurnRate(Block.fence.blockID, 5, 20);
        this.setBurnRate(Block.stairCompactPlanks.blockID, 5, 20);
        this.setBurnRate(Block.wood.blockID, 5, 5);
        this.setBurnRate(Block.leaves.blockID, 30, 60);
        this.setBurnRate(Block.bookShelf.blockID, 30, 20);
        this.setBurnRate(Block.tnt.blockID, 15, 100);
        this.setBurnRate(Block.tallGrass.blockID, 60, 100);
        this.setBurnRate(Block.cloth.blockID, 30, 60);
    }

    private void setBurnRate(int block, int flammability, int burnChance) {
        this.chanceToEncourageFire[block] = flammability;
        this.abilityToCatchFire[block] = burnChance;
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isACube() {
        return false;
    }

    public int quantityDropped(Random random) {
        return 0;
    }

    public int tickRate() {
        return 40;
    }

    public void updateTick(World world, int x, int y, int z, Random random) {
        boolean bl;
        boolean bl2 = bl = world.getBlockId(x, y - 1, z) == Block.bloodStone.blockID;
        if (!this.canPlaceBlockAt(world, x, y, z)) {
            world.setBlockWithNotify(x, y, z, 0);
        }
        if (!bl && world.isRaining() && (world.canLightningStrikeAt(x, y, z) || world.canLightningStrikeAt(x - 1, y, z) || world.canLightningStrikeAt(x + 1, y, z) || world.canLightningStrikeAt(x, y, z - 1) || world.canLightningStrikeAt(x, y, z + 1))) {
            world.setBlockWithNotify(x, y, z, 0);
            return;
        }
        int n = world.getBlockMetadata(x, y, z);
        if (n < 15) {
            world.setBlockMetadata(x, y, z, n + random.nextInt(3) / 2);
        }
        world.scheduleUpdateTick(x, y, z, this.blockID, this.tickRate());
        if (!bl && !this.hasFlammableNeighbor(world, x, y, z)) {
            if (!world.isBlockNormalCube(x, y - 1, z) || n > 3) {
                world.setBlockWithNotify(x, y, z, 0);
            }
            return;
        }
        if (!bl && !this.canBlockCatchFire(world, x, y - 1, z) && n == 15 && random.nextInt(4) == 0) {
            world.setBlockWithNotify(x, y, z, 0);
            return;
        }
        this.tryToCatchBlockOnFire(world, x + 1, y, z, 300, random, n);
        this.tryToCatchBlockOnFire(world, x - 1, y, z, 300, random, n);
        this.tryToCatchBlockOnFire(world, x, y - 1, z, 250, random, n);
        this.tryToCatchBlockOnFire(world, x, y + 1, z, 250, random, n);
        this.tryToCatchBlockOnFire(world, x, y, z - 1, 300, random, n);
        this.tryToCatchBlockOnFire(world, x, y, z + 1, 300, random, n);
        for (int i = x - 1; i <= x + 1; ++i) {
            for (int j = z - 1; j <= z + 1; ++j) {
                for (int k = y - 1; k <= y + 4; ++k) {
                    int n2;
                    int n3;
                    if (i == x && k == y && j == z) continue;
                    int n4 = 100;
                    if (k > y + 1) {
                        n4 += (k - (y + 1)) * 100;
                    }
                    if ((n3 = this.getChanceOfNeighborsEncouragingFire(world, i, k, j)) <= 0 || (n2 = (n3 + 40) / (n + 30)) <= 0 || random.nextInt(n4) > n2 || world.isRaining() && world.canLightningStrikeAt(i, k, j) || world.canLightningStrikeAt(i - 1, k, z) || world.canLightningStrikeAt(i + 1, k, j) || world.canLightningStrikeAt(i, k, j - 1) || world.canLightningStrikeAt(i, k, j + 1)) continue;
                    int n5 = n + random.nextInt(5) / 4;
                    if (n5 > 15) {
                        n5 = 15;
                    }
                    world.setBlockAndMetadataWithNotify(i, k, j, this.blockID, n5);
                }
            }
        }
    }

    private void tryToCatchBlockOnFire(World world, int x, int y, int z, int chance, Random random, int metadata) {
        int i = this.abilityToCatchFire[world.getBlockId(x, y, z)];
        if (random.nextInt(chance) < i) {
            boolean flag = world.getBlockId(x, y, z) == Block.tnt.blockID;
            if (random.nextInt(metadata + 10) < 5 && !world.canLightningStrikeAt(x, y, z)) {
                int j = metadata + random.nextInt(5) / 4;
                if (j > 15) {
                    j = 15;
                }
                world.setBlockAndMetadataWithNotify(x, y, z, this.blockID, j);
            } else {
                world.setBlockWithNotify(x, y, z, 0);
            }
            if (flag) {
                Block.tnt.onBlockDestroyedByPlayer(world, x, y, z, 1);
            }
        }
    }

    private boolean hasFlammableNeighbor(World world, int x, int y, int z) {
        if (this.canBlockCatchFire(world, x + 1, y, z)) {
            return true;
        }
        if (this.canBlockCatchFire(world, x - 1, y, z)) {
            return true;
        }
        if (this.canBlockCatchFire(world, x, y - 1, z)) {
            return true;
        }
        if (this.canBlockCatchFire(world, x, y + 1, z)) {
            return true;
        }
        if (this.canBlockCatchFire(world, x, y, z - 1)) {
            return true;
        }
        return this.canBlockCatchFire(world, x, y, z + 1);
    }

    private int getChanceOfNeighborsEncouragingFire(World world, int x, int y, int z) {
        int n = 0;
        if (!world.isAirBlock(x, y, z)) {
            return 0;
        }
        n = this.getChanceToEncourageFire(world, x + 1, y, z, n);
        n = this.getChanceToEncourageFire(world, x - 1, y, z, n);
        n = this.getChanceToEncourageFire(world, x, y - 1, z, n);
        n = this.getChanceToEncourageFire(world, x, y + 1, z, n);
        n = this.getChanceToEncourageFire(world, x, y, z - 1, n);
        n = this.getChanceToEncourageFire(world, x, y, z + 1, n);
        return n;
    }

    public boolean isCollidable() {
        return false;
    }

    public boolean canBlockCatchFire(IBlockAccess world, int x, int y, int z) {
        return this.chanceToEncourageFire[world.getBlockId(x, y, z)] > 0;
    }

    public int getChanceToEncourageFire(World world, int x, int y, int z, int min) {
        int n = this.chanceToEncourageFire[world.getBlockId(x, y, z)];
        if (n > min) {
            return n;
        }
        return min;
    }

    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return world.isBlockNormalCube(x, y - 1, z) || this.hasFlammableNeighbor(world, x, y, z);
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlock) {
        if (!world.isBlockNormalCube(x, y - 1, z) && !this.hasFlammableNeighbor(world, x, y, z)) {
            world.setBlockWithNotify(x, y, z, 0);
            return;
        }
    }

    public void onBlockAdded(World world, int x, int y, int z) {
        if (world.getBlockId(x, y - 1, z) == Block.obsidian.blockID && Block.portal.tryToCreatePortal(world, x, y, z)) {
            return;
        }
        if (!world.isBlockNormalCube(x, y - 1, z) && !this.hasFlammableNeighbor(world, x, y, z)) {
            world.setBlockWithNotify(x, y, z, 0);
            return;
        }
        world.scheduleUpdateTick(x, y, z, this.blockID, this.tickRate());
    }
}

