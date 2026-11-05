package net.minecraft.src;

import java.util.Random;

public class BlockDispenser
extends BlockContainer {
    private Random random = new Random();

    protected BlockDispenser(int id) {
        super(id, Material.rock);
        this.blockIndexInTexture = 45;
    }

    public int tickRate() {
        return 4;
    }

    public int idDropped(int metadata, Random random) {
        return Block.dispenser.blockID;
    }

    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        this.setDispenserDefaultDirection(world, x, y, z);
    }

    private void setDispenserDefaultDirection(World world, int x, int y, int z) {
        if (world.singleplayerWorld) {
            return;
        }
        int n = world.getBlockId(x, y, z - 1);
        int n2 = world.getBlockId(x, y, z + 1);
        int n3 = world.getBlockId(x - 1, y, z);
        int n4 = world.getBlockId(x + 1, y, z);
        int n5 = 3;
        if (Block.opaqueCubeLookup[n] && !Block.opaqueCubeLookup[n2]) {
            n5 = 3;
        }
        if (Block.opaqueCubeLookup[n2] && !Block.opaqueCubeLookup[n]) {
            n5 = 2;
        }
        if (Block.opaqueCubeLookup[n3] && !Block.opaqueCubeLookup[n4]) {
            n5 = 5;
        }
        if (Block.opaqueCubeLookup[n4] && !Block.opaqueCubeLookup[n3]) {
            n5 = 4;
        }
        world.setBlockMetadataWithNotify(x, y, z, n5);
    }

    public int getBlockTextureFromSide(int face) {
        if (face == 1) {
            return this.blockIndexInTexture + 17;
        }
        if (face == 0) {
            return this.blockIndexInTexture + 17;
        }
        if (face == 3) {
            return this.blockIndexInTexture + 1;
        }
        return this.blockIndexInTexture;
    }

    public boolean blockActivated(World world, int x, int y, int z, EntityPlayer player) {
        if (world.singleplayerWorld) {
            return true;
        }
        TileEntityDispenser tileEntityDispenser = (TileEntityDispenser)world.getBlockTileEntity(x, y, z);
        player.displayGUIDispenser(tileEntityDispenser);
        return true;
    }

    private void dispenseItem(World world, int x, int y, int z, Random random) {
        int n = world.getBlockMetadata(x, y, z);
        int n2 = 0;
        int n3 = 0;
        if (n == 3) {
            n3 = 1;
        } else if (n == 2) {
            n3 = -1;
        } else {
            n2 = n == 5 ? 1 : -1;
        }
        TileEntityDispenser tileEntityDispenser = (TileEntityDispenser)world.getBlockTileEntity(x, y, z);
        ItemStack itemStack = tileEntityDispenser.getRandomStackFromInventory();
        double d = (double)x + (double)n2 * 0.6 + 0.5;
        double d2 = (double)y + 0.5;
        double d3 = (double)z + (double)n3 * 0.6 + 0.5;
        if (itemStack == null) {
            world.doEvent(1001, x, y, z, 0);
        } else {
            if (itemStack.itemID == Item.arrow.shiftedIndex) {
                EntityArrow entityArrow = new EntityArrow(world, d, d2, d3);
                entityArrow.setArrowHeading(n2, 0.1f, n3, 1.1f, 6.0f);
                entityArrow.player = true;
                world.entityJoinedWorld(entityArrow);
                world.doEvent(1002, x, y, z, 0);
            } else if (itemStack.itemID == Item.egg.shiftedIndex) {
                EntityEgg entityEgg = new EntityEgg(world, d, d2, d3);
                entityEgg.thrown(n2, 0.1f, n3, 1.1f, 6.0f);
                world.entityJoinedWorld(entityEgg);
                world.doEvent(1002, x, y, z, 0);
            } else if (itemStack.itemID == Item.snowball.shiftedIndex) {
                EntitySnowball entitySnowball = new EntitySnowball(world, d, d2, d3);
                entitySnowball.thrown(n2, 0.1f, n3, 1.1f, 6.0f);
                world.entityJoinedWorld(entitySnowball);
                world.doEvent(1002, x, y, z, 0);
            } else {
                EntityItem entityItem = new EntityItem(world, d, d2 - 0.3, d3, itemStack);
                double d4 = random.nextDouble() * 0.1 + 0.2;
                entityItem.motionX = (double)n2 * d4;
                entityItem.motionY = 0.2f;
                entityItem.motionZ = (double)n3 * d4;
                entityItem.motionX += random.nextGaussian() * (double)0.0075f * 6.0;
                entityItem.motionY += random.nextGaussian() * (double)0.0075f * 6.0;
                entityItem.motionZ += random.nextGaussian() * (double)0.0075f * 6.0;
                world.entityJoinedWorld(entityItem);
                world.doEvent(1000, x, y, z, 0);
            }
            world.doEvent(2000, x, y, z, n2 + 1 + (n3 + 1) * 3);
        }
    }

    @Override
public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlock) {
    if (neighborBlock > 0 && Block.blocksList[neighborBlock].canProvidePower()) {
        boolean flag = world.isBlockIndirectlyGettingPowered(x, y, z) || world.isBlockIndirectlyGettingPowered(x, y + 1, z);
        if (flag) {
            world.scheduleUpdateTick(x, y, z, this.blockID, this.tickRate());
        }
    }
}

    public void updateTick(World world, int x, int y, int z, Random random) {
        if (world.isBlockIndirectlyGettingPowered(x, y, z) || world.isBlockIndirectlyGettingPowered(x, y + 1, z)) {
            this.dispenseItem(world, x, y, z, random);
        }
    }

    protected TileEntity getBlockEntity() {
        return new TileEntityDispenser();
    }

    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entity) {
        int n = MathHelper.floor_double((double)(entity.rotationYaw * 4.0f / 360.0f) + 0.5) & 3;
        if (n == 0) {
            world.setBlockMetadataWithNotify(x, y, z, 2);
        }
        if (n == 1) {
            world.setBlockMetadataWithNotify(x, y, z, 5);
        }
        if (n == 2) {
            world.setBlockMetadataWithNotify(x, y, z, 3);
        }
        if (n == 3) {
            world.setBlockMetadataWithNotify(x, y, z, 4);
        }
    }

    public void onBlockRemoval(World world, int x, int y, int z) {
        TileEntityDispenser tileEntityDispenser = (TileEntityDispenser)world.getBlockTileEntity(x, y, z);
        for (int i = 0; i < tileEntityDispenser.getSizeInventory(); ++i) {
            ItemStack itemStack = tileEntityDispenser.getStackInSlot(i);
            if (itemStack == null) continue;
            float f = this.random.nextFloat() * 0.8f + 0.1f;
            float f2 = this.random.nextFloat() * 0.8f + 0.1f;
            float f3 = this.random.nextFloat() * 0.8f + 0.1f;
            while (itemStack.stackSize > 0) {
                int n = this.random.nextInt(21) + 10;
                if (n > itemStack.stackSize) {
                    n = itemStack.stackSize;
                }
                itemStack.stackSize -= n;
                EntityItem entityItem = new EntityItem(world, (float)x + f, (float)y + f2, (float)z + f3, new ItemStack(itemStack.itemID, n, itemStack.getItemDamage()));
                float f4 = 0.05f;
                entityItem.motionX = (float)this.random.nextGaussian() * f4;
                entityItem.motionY = (float)this.random.nextGaussian() * f4 + 0.2f;
                entityItem.motionZ = (float)this.random.nextGaussian() * f4;
                world.entityJoinedWorld(entityItem);
            }
        }
        super.onBlockRemoval(world, x, y, z);
    }
}

