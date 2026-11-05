package net.minecraft.src;


public class BlockLeavesBase
extends Block {
    protected boolean graphicsLevel;

    protected BlockLeavesBase(int id, int sprite, Material material, boolean culling) {
        super(id, sprite, material);
        this.graphicsLevel = culling;
    }

    public boolean isOpaqueCube() {
        return false;
    }
}

