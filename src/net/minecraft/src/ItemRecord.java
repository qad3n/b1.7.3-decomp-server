package net.minecraft.src;


public class ItemRecord
extends Item {
    public final String recordName;

    protected ItemRecord(int id, String record) {
        super(id);
        this.recordName = record;
        this.maxStackSize = 1;
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int face) {
        if (world.getBlockId(x, y, z) == Block.jukebox.blockID && world.getBlockMetadata(x, y, z) == 0) {
            if (world.singleplayerWorld) {
                return true;
            }
            ((BlockJukeBox)Block.jukebox).ejectRecord(world, x, y, z, this.shiftedIndex);
            world.doEvent(null, 1005, x, y, z, this.shiftedIndex);
            --stack.stackSize;
            return true;
        }
        return false;
    }
}

