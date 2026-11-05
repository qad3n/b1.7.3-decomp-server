package net.minecraft.src;


public class BlockCloth
extends Block {
    public BlockCloth() {
        super(35, 64, Material.cloth);
    }

    public int getBlockTextureFromSideAndMetadata(int face, int metadata) {
        if (metadata == 0) {
            return this.blockIndexInTexture;
        }
        metadata = ~(metadata & 0xF);
        return 113 + ((metadata & 8) >> 3) + (metadata & 7) * 16;
    }

    protected int damageDropped(int metadata) {
        return metadata;
    }

    public static int getBlockMetadataForItemMetadata(int metadata) {
        return ~metadata & 0xF;
    }

    public static int getItemMetadataForBlockMetadata(int metadata) {
        return ~metadata & 0xF;
    }
}

