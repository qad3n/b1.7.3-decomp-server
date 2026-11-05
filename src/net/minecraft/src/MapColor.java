package net.minecraft.src;

public class MapColor {
    public static final MapColor[] BY_ID = new MapColor[16];
    public static final MapColor AIR = new MapColor(0, 0);
    public static final MapColor GRASS = new MapColor(1, 8368696);
    public static final MapColor SAND = new MapColor(2, 16247203);
    public static final MapColor WEB = new MapColor(3, 0xA7A7A7);
    public static final MapColor LAVA = new MapColor(4, 0xFF0000);
    public static final MapColor ICE = new MapColor(5, 0xA0A0FF);
    public static final MapColor IRON = new MapColor(6, 0xA7A7A7);
    public static final MapColor FOLIAGE = new MapColor(7, 31744);
    public static final MapColor WHITE = new MapColor(8, 0xFFFFFF);
    public static final MapColor CLAY = new MapColor(9, 10791096);
    public static final MapColor DIRT = new MapColor(10, 12020271);
    public static final MapColor STONE = new MapColor(11, 0x707070);
    public static final MapColor WATER = new MapColor(12, 0x4040FF);
    public static final MapColor WOOD = new MapColor(13, 6837042);
    public final int color;
    public final int id;

    private MapColor(int id, int color) {
        this.id = id;
        this.color = color;
        MapColor.BY_ID[id] = this;
    }
}

