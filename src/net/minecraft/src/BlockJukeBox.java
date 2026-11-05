package net.minecraft.src;


public class BlockJukeBox
extends BlockContainer {
    protected BlockJukeBox(int id, int sprite) {
        super(id, sprite, Material.wood);
    }

    public int getBlockTextureFromSide(int face) {
        return this.blockIndexInTexture + (face == 1 ? 1 : 0);
    }

    public boolean blockActivated(World world, int x, int y, int z, EntityPlayer player) {
        if (world.getBlockMetadata(x, y, z) == 0) {
            return false;
        }
        this.removeRecord(world, x, y, z);
        return true;
    }

    public void ejectRecord(World world, int x, int y, int z, int record) {
        if (world.singleplayerWorld) {
            return;
        }
        TileEntityRecordPlayer tileEntityRecordPlayer = (TileEntityRecordPlayer)world.getBlockTileEntity(x, y, z);
        tileEntityRecordPlayer.record = record;
        tileEntityRecordPlayer.onInventoryChanged();
        world.setBlockMetadataWithNotify(x, y, z, 1);
    }

    public void removeRecord(World world, int x, int y, int z) {
        if (world.singleplayerWorld) {
            return;
        }
        TileEntityRecordPlayer tileEntityRecordPlayer = (TileEntityRecordPlayer)world.getBlockTileEntity(x, y, z);
        int n = tileEntityRecordPlayer.record;
        if (n == 0) {
            return;
        }
        world.doEvent(1005, x, y, z, 0);
        world.playRecord(null, x, y, z);
        tileEntityRecordPlayer.record = 0;
        tileEntityRecordPlayer.onInventoryChanged();
        world.setBlockMetadataWithNotify(x, y, z, 0);
        int n2 = n;
        float f = 0.7f;
        double d = (double)(world.rand.nextFloat() * f) + (double)(1.0f - f) * 0.5;
        double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0f - f) * 0.2 + 0.6;
        double d3 = (double)(world.rand.nextFloat() * f) + (double)(1.0f - f) * 0.5;
        EntityItem entityItem = new EntityItem(world, (double)x + d, (double)y + d2, (double)z + d3, new ItemStack(n2, 1, 0));
        entityItem.delayBeforeCanPickup = 10;
        world.entityJoinedWorld(entityItem);
    }

    public void onBlockRemoval(World world, int x, int y, int z) {
        this.removeRecord(world, x, y, z);
        super.onBlockRemoval(world, x, y, z);
    }

    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int metadata, float luck) {
        if (world.singleplayerWorld) {
            return;
        }
        super.dropBlockAsItemWithChance(world, x, y, z, metadata, luck);
    }

    protected TileEntity getBlockEntity() {
        return new TileEntityRecordPlayer();
    }

    public static class TileEntityRecordPlayer
    extends TileEntity {
        public int record;

        public void readFromNBT(NBTTagCompound nbt) {
            super.readFromNBT(nbt);
            this.record = nbt.getInteger("Record");
        }

        public void writeToNBT(NBTTagCompound nbt) {
            super.writeToNBT(nbt);
            if (this.record > 0) {
                nbt.setInteger("Record", this.record);
            }
        }
    }
}

