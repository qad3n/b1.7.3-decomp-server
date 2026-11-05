package net.minecraft.src;


public class ChunkBlockMap {
    private static byte[] BLOCKS = new byte[256];

    public static void fill(byte[] blocks) {
        for (int i = 0; i < blocks.length; ++i) {
            blocks[i] = BLOCKS[blocks[i] & 0xFF];
        }
    }

    static {
        try {
            for (int i = 0; i < 256; ++i) {
                byte by = (byte)i;
                if (by != 0 && Block.blocksList[by & 0xFF] == null) {
                    by = 0;
                }
                ChunkBlockMap.BLOCKS[i] = by;
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

