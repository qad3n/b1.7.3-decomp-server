package net.minecraft.src;


public class BlockBreakable
extends Block {
    private boolean culling;

    protected BlockBreakable(int id, int sprite, Material material, boolean culling) {
        super(id, sprite, material);
        this.culling = culling;
    }

    public boolean isOpaqueCube() {
        return false;
    }
}

