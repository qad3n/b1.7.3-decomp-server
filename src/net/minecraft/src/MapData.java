package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapData
extends MapDataBase {
    public int centerX;
    public int centerZ;
    public byte dimension;
    public byte scale;
    public byte[] colors = new byte[16384];
    public int ticks;
    public List<MapInfo> holders = new ArrayList<MapInfo>();
    private Map<EntityPlayer, MapInfo> holdersByPlayer = new HashMap<EntityPlayer, MapInfo>();
    public List<MapCoord> decorations = new ArrayList<MapCoord>();

    public MapData(String string) {
        super(string);
    }

    public void readNbt(NBTTagCompound nbt) {
        this.dimension = nbt.getByte("dimension");
        this.centerX = nbt.getInteger("xCenter");
        this.centerZ = nbt.getInteger("zCenter");
        this.scale = nbt.getByte("scale");
        if (this.scale < 0) {
            this.scale = 0;
        }
        if (this.scale > 4) {
            this.scale = (byte)4;
        }
        int n = nbt.getShort("width");
        int n2 = nbt.getShort("height");
        if (n == 128 && n2 == 128) {
            this.colors = nbt.getByteArray("colors");
        } else {
            byte[] byArray = nbt.getByteArray("colors");
            this.colors = new byte[16384];
            int n3 = (128 - n) / 2;
            int n4 = (128 - n2) / 2;
            for (int i = 0; i < n2; ++i) {
                int n5 = i + n4;
                if (n5 < 0 && n5 >= 128) continue;
                for (int j = 0; j < n; ++j) {
                    int n6 = j + n3;
                    if (n6 < 0 && n6 >= 128) continue;
                    this.colors[n6 + n5 * 128] = byArray[j + i * n];
                }
            }
        }
    }

    public void writeNbt(NBTTagCompound nbt) {
        nbt.setByte("dimension", this.dimension);
        nbt.setInteger("xCenter", this.centerX);
        nbt.setInteger("zCenter", this.centerZ);
        nbt.setByte("scale", this.scale);
        nbt.setShort("width", (short)128);
        nbt.setShort("height", (short)128);
        nbt.setByteArray("colors", this.colors);
    }

    public void tickHolder(EntityPlayer player, ItemStack item) {
        if (!this.holdersByPlayer.containsKey(player)) {
            MapInfo mapInfo = new MapInfo(player);
            this.holdersByPlayer.put(player, mapInfo);
            this.holders.add(mapInfo);
        }
        this.decorations.clear();
        for (int i = 0; i < this.holders.size(); ++i) {
            MapInfo mapInfo = this.holders.get(i);
            if (mapInfo.player.isDead || !mapInfo.player.inventory.contains(item)) {
                this.holdersByPlayer.remove(mapInfo.player);
                this.holders.remove(mapInfo);
                continue;
            }
            float f = (float)(mapInfo.player.posX - (double)this.centerX) / (float)(1 << this.scale);
            float f2 = (float)(mapInfo.player.posZ - (double)this.centerZ) / (float)(1 << this.scale);
            int n = 64;
            int n2 = 64;
            if (!(f >= (float)(-n)) || !(f2 >= (float)(-n2)) || !(f <= (float)n) || !(f2 <= (float)n2)) continue;
            byte by = 0;
            byte by2 = (byte)((double)(f * 2.0f) + 0.5);
            byte by3 = (byte)((double)(f2 * 2.0f) + 0.5);
            byte by4 = (byte)((double)(player.rotationYaw * 16.0f / 360.0f) + 0.5);
            if (this.dimension < 0) {
                int n3 = this.ticks / 10;
                by4 = (byte)(n3 * n3 * 34187121 + n3 * 121 >> 15 & 0xF);
            }
            if (mapInfo.player.dimension != this.dimension) continue;
            this.decorations.add(new MapCoord(by, by2, by3, by4));
        }
    }

    public byte[] getUpdateData(ItemStack item, World world, EntityPlayer player) {
        MapInfo mapInfo = this.holdersByPlayer.get(player);
        if (mapInfo == null) {
            return null;
        }
        byte[] byArray = mapInfo.getUpdateData(item);
        return byArray;
    }

    public void markDirty(int index, int minY, int maxY) {
        super.markDirty();
        for (int i = 0; i < this.holders.size(); i++) {
            MapData.MapInfo mapdata$mapinfo = this.holders.get(i);
            if (mapdata$mapinfo.dirtyMinY[index] < 0 || mapdata$mapinfo.dirtyMinY[index] > minY) {
                mapdata$mapinfo.dirtyMinY[index] = minY;
            }
            if (mapdata$mapinfo.dirtyMaxY[index] < 0 || mapdata$mapinfo.dirtyMaxY[index] < maxY) {
                mapdata$mapinfo.dirtyMaxY[index] = maxY;
            }
        }
    }

    public class MapCoord {
        public byte type;
        public byte x;
        public byte z;
        public byte rotation;

        public MapCoord(byte type, byte x, byte z, byte rotation) {
            this.type = type;
            this.x = x;
            this.z = z;
            this.rotation = rotation;
        }
    }

    public class MapInfo {
        public final EntityPlayer player;
        public int[] dirtyMinY = new int[128];
        public int[] dirtyMaxY = new int[128];
        private int nextDirtyPixel = 0;
        private int cooldown = 0;
        private byte[] decorations;

        public MapInfo(EntityPlayer player) {
            this.player = player;
            for (int i = 0; i < this.dirtyMinY.length; ++i) {
                this.dirtyMinY[i] = 0;
                this.dirtyMaxY[i] = 127;
            }
        }

        public byte[] getUpdateData(ItemStack item) {
            int n;
            if (--this.cooldown < 0) {
                this.cooldown = 4;
                byte[] byArray = new byte[MapData.this.decorations.size() * 3 + 1];
                byArray[0] = 1;
                for (n = 0; n < MapData.this.decorations.size(); ++n) {
                    MapCoord mapCoord = MapData.this.decorations.get(n);
                    byArray[n * 3 + 1] = (byte)(mapCoord.type + (mapCoord.rotation & 0xF) * 16);
                    byArray[n * 3 + 2] = mapCoord.x;
                    byArray[n * 3 + 3] = mapCoord.z;
                }
                n = 1;
                if (this.decorations == null || this.decorations.length != byArray.length) {
                    n = 0;
                } else {
                    for (int i = 0; i < byArray.length; ++i) {
                        if (byArray[i] == this.decorations[i]) continue;
                        n = 0;
                        break;
                    }
                }
                if (n == 0) {
                    this.decorations = byArray;
                    return byArray;
                }
            }
            for (int i = 0; i < 10; ++i) {
                n = this.nextDirtyPixel * 11 % 128;
                ++this.nextDirtyPixel;
                if (this.dirtyMinY[n] < 0) continue;
                int n2 = this.dirtyMaxY[n] - this.dirtyMinY[n] + 1;
                int n3 = this.dirtyMinY[n];
                byte[] byArray = new byte[n2 + 3];
                byArray[0] = 0;
                byArray[1] = (byte)n;
                byArray[2] = (byte)n3;
                for (int j = 0; j < byArray.length - 3; ++j) {
                    byArray[j + 3] = MapData.this.colors[(j + n3) * 128 + n];
                }
                this.dirtyMaxY[n] = -1;
                this.dirtyMinY[n] = -1;
                return byArray;
            }
            return null;
        }
    }
}

