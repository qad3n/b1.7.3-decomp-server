package net.minecraft.src;


public class BlockWorkbench
extends Block {
    protected BlockWorkbench(int id) {
        super(id, Material.wood);
        this.blockIndexInTexture = 59;
    }

    public int getBlockTextureFromSide(int face) {
        if (face == 1) {
            return this.blockIndexInTexture - 16;
        }
        if (face == 0) {
            return Block.planks.getBlockTextureFromSide(0);
        }
        if (face == 2 || face == 4) {
            return this.blockIndexInTexture + 1;
        }
        return this.blockIndexInTexture;
    }

    public boolean blockActivated(World world, int x, int y, int z, EntityPlayer player) {
        if (world.singleplayerWorld) {
            return true;
        }
        player.displayWorkbenchGUI(x, y, z);
        return true;
    }
}

