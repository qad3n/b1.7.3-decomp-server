package net.minecraft.src;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum EnumSkyBlock {
    Sky(15),
    Block(0);

    public final int defaultValue;

    private EnumSkyBlock(int defaultValue) {
        this.defaultValue = defaultValue;
    }
}

