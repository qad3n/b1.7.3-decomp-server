package net.minecraft.src;


public class MaterialLiquid
extends Material {
    public MaterialLiquid(MapColor mapColor) {
        super(mapColor);
        this.setReplaceable();
        this.setNoPushMobility();
    }

    public boolean getIsLiquid() {
        return true;
    }

    public boolean getIsSolid() {
        return false;
    }

    public boolean isSolid() {
        return false;
    }
}

