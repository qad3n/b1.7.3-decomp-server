package net.minecraft.src;


public class BlockSandStone
extends Block {
    public BlockSandStone(int id) {
        super(id, 192, Material.rock);
    }

    public int getBlockTextureFromSide(int face) {
        if (face == 1) {
            return this.blockIndexInTexture - 16;
        }
        if (face == 0) {
            return this.blockIndexInTexture + 16;
        }
        return this.blockIndexInTexture;
    }
}

