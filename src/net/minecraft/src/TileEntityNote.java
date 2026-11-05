package net.minecraft.src;


public class TileEntityNote
extends TileEntity {
    public byte note = 0;
    public boolean previousRedstoneState = false;

    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setByte("note", this.note);
    }

    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.note = nbt.getByte("note");
        if (this.note < 0) {
            this.note = 0;
        }
        if (this.note > 24) {
            this.note = (byte)24;
        }
    }

    public void changePitch() {
        this.note = (byte)((this.note + 1) % 25);
        this.onInventoryChanged();
    }

    public void triggerNote(World world, int x, int y, int z) {
        if (world.getBlockMaterial(x, y + 1, z) != Material.air) {
            return;
        }
        Material material = world.getBlockMaterial(x, y - 1, z);
        int n = 0;
        if (material == Material.rock) {
            n = 1;
        }
        if (material == Material.sand) {
            n = 2;
        }
        if (material == Material.glass) {
            n = 3;
        }
        if (material == Material.wood) {
            n = 4;
        }
        world.playNoteAt(x, y, z, n, this.note);
    }
}

