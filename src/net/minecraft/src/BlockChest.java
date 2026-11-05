package net.minecraft.src;

import java.util.Random;

public class BlockChest
extends BlockContainer {
    private Random random = new Random();

    protected BlockChest(int id) {
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
        int n = 0;
        if (world.getBlockId(x - 1, y, z) == this.blockID) {
            ++n;
        }
        if (world.getBlockId(x + 1, y, z) == this.blockID) {
            ++n;
        }
        if (world.getBlockId(x, y, z - 1) == this.blockID) {
            ++n;
        }
        if (world.getBlockId(x, y, z + 1) == this.blockID) {
            ++n;
        }
        if (n > 1) {
            return false;
        }
        if (this.isThereANeighborChest(world, x - 1, y, z)) {
            return false;
        }
        if (this.isThereANeighborChest(world, x + 1, y, z)) {
            return false;
        }
        if (this.isThereANeighborChest(world, x, y, z - 1)) {
            return false;
        }
        return !this.isThereANeighborChest(world, x, y, z + 1);
    }

    private boolean isThereANeighborChest(World world, int x, int y, int z) {
        if (world.getBlockId(x, y, z) != this.blockID) {
            return false;
        }
        if (world.getBlockId(x - 1, y, z) == this.blockID) {
            return true;
        }
        if (world.getBlockId(x + 1, y, z) == this.blockID) {
            return true;
        }
        if (world.getBlockId(x, y, z - 1) == this.blockID) {
            return true;
        }
        return world.getBlockId(x, y, z + 1) == this.blockID;
    }

    public void onBlockRemoval(World world, int x, int y, int z) {
        TileEntityChest tileEntityChest = (TileEntityChest)world.getBlockTileEntity(x, y, z);
        for (int i = 0; i < tileEntityChest.getSizeInventory(); ++i) {
            ItemStack itemStack = tileEntityChest.getStackInSlot(i);
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

    public boolean blockActivated(World world, int x, int y, int z, EntityPlayer player) {
        IInventory iInventory = (TileEntityChest)world.getBlockTileEntity(x, y, z);
        if (world.isBlockNormalCube(x, y + 1, z)) {
            return true;
        }
        if (world.getBlockId(x - 1, y, z) == this.blockID && world.isBlockNormalCube(x - 1, y + 1, z)) {
            return true;
        }
        if (world.getBlockId(x + 1, y, z) == this.blockID && world.isBlockNormalCube(x + 1, y + 1, z)) {
            return true;
        }
        if (world.getBlockId(x, y, z - 1) == this.blockID && world.isBlockNormalCube(x, y + 1, z - 1)) {
            return true;
        }
        if (world.getBlockId(x, y, z + 1) == this.blockID && world.isBlockNormalCube(x, y + 1, z + 1)) {
            return true;
        }
        if (world.getBlockId(x - 1, y, z) == this.blockID) {
            iInventory = new InventoryLargeChest("Large chest", (TileEntityChest)world.getBlockTileEntity(x - 1, y, z), iInventory);
        }
        if (world.getBlockId(x + 1, y, z) == this.blockID) {
            iInventory = new InventoryLargeChest("Large chest", iInventory, (TileEntityChest)world.getBlockTileEntity(x + 1, y, z));
        }
        if (world.getBlockId(x, y, z - 1) == this.blockID) {
            iInventory = new InventoryLargeChest("Large chest", (TileEntityChest)world.getBlockTileEntity(x, y, z - 1), iInventory);
        }
        if (world.getBlockId(x, y, z + 1) == this.blockID) {
            iInventory = new InventoryLargeChest("Large chest", iInventory, (TileEntityChest)world.getBlockTileEntity(x, y, z + 1));
        }
        if (world.singleplayerWorld) {
            return true;
        }
        player.displayGUIChest(iInventory);
        return true;
    }

    protected TileEntity getBlockEntity() {
        return new TileEntityChest();
    }
}

