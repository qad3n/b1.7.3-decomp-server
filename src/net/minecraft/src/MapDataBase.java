package net.minecraft.src;


public abstract class MapDataBase {
    public final String id;
    private boolean dirty;

    public MapDataBase(String id) {
        this.id = id;
    }

    public abstract void readNbt(NBTTagCompound var1);

    public abstract void writeNbt(NBTTagCompound var1);

    public void markDirty() {
        this.setDirty(true);
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isDirty() {
        return this.dirty;
    }
}

