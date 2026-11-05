package net.minecraft.src;


public class BlockNote
extends BlockContainer {
    public BlockNote(int id) {
        super(id, 74, Material.wood);
    }

    public int getBlockTextureFromSide(int face) {
        return this.blockIndexInTexture;
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlock) {
        if (neighborBlock > 0 && Block.blocksList[neighborBlock].canProvidePower()) {
            boolean bl = world.isBlockGettingPowered(x, y, z);
            TileEntityNote tileEntityNote = (TileEntityNote)world.getBlockTileEntity(x, y, z);
            if (tileEntityNote.previousRedstoneState != bl) {
                if (bl) {
                    tileEntityNote.triggerNote(world, x, y, z);
                }
                tileEntityNote.previousRedstoneState = bl;
            }
        }
    }

    public boolean blockActivated(World world, int x, int y, int z, EntityPlayer player) {
        if (world.singleplayerWorld) {
            return true;
        }
        TileEntityNote tileEntityNote = (TileEntityNote)world.getBlockTileEntity(x, y, z);
        tileEntityNote.changePitch();
        tileEntityNote.triggerNote(world, x, y, z);
        return true;
    }

    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        if (world.singleplayerWorld) {
            return;
        }
        TileEntityNote tileEntityNote = (TileEntityNote)world.getBlockTileEntity(x, y, z);
        tileEntityNote.triggerNote(world, x, y, z);
    }

    protected TileEntity getBlockEntity() {
        return new TileEntityNote();
    }

    public void playBlock(World world, int x, int y, int z, int type, int data) {
        float f = (float)Math.pow(2.0, (double)(data - 12) / 12.0);
        String string = "harp";
        if (type == 1) {
            string = "bd";
        }
        if (type == 2) {
            string = "snare";
        }
        if (type == 3) {
            string = "hat";
        }
        if (type == 4) {
            string = "bassattack";
        }
        world.playSoundEffect((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "note." + string, 3.0f, f);
        world.spawnParticle("note", (double)x + 0.5, (double)y + 1.2, (double)z + 0.5, (double)data / 24.0, 0.0, 0.0);
    }
}

