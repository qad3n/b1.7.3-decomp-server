package net.minecraft.src;


public class BlockOreStorage
extends Block {
    public BlockOreStorage(int id, int sprite) {
        super(id, Material.iron);
        this.blockIndexInTexture = sprite;
    }

    public int getBlockTextureFromSide(int face) {
        return this.blockIndexInTexture;
    }
}

