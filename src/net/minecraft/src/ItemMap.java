package net.minecraft.src;


public class ItemMap
extends ItemMapBase {
    protected ItemMap(int n) {
        super(n);
        this.setMaxStackSize(1);
    }

    public MapData getSavedMapData(ItemStack item, World world) {
        String string = "map_" + item.getItemDamage();
        MapData mapData = world.loadSavedData(MapData.class, "map_" + item.getItemDamage());
        if (mapData == null) {
            item.setItemDamage(world.getSavedDataCount("map"));
            string = "map_" + item.getItemDamage();
            mapData = new MapData(string);
            mapData.centerX = world.getWorldInfo().getSpawnX();
            mapData.centerZ = world.getWorldInfo().getSpawnZ();
            mapData.scale = (byte)3;
            mapData.dimension = (byte)world.worldProvider.worldType;
            mapData.markDirty();
            world.setSavedData(string, mapData);
        }
        return mapData;
    }

    public void update(World world, Entity entity, MapData data) {
        if (world.worldProvider.worldType != data.dimension) {
            return;
        }
        int n = 128;
        int n2 = 128;
        int n3 = 1 << data.scale;
        int n4 = data.centerX;
        int n5 = data.centerZ;
        int n6 = MathHelper.floor_double(entity.posX - (double)n4) / n3 + n / 2;
        int n7 = MathHelper.floor_double(entity.posZ - (double)n5) / n3 + n2 / 2;
        int n8 = 128 / n3;
        if (world.worldProvider.noSky) {
            n8 /= 2;
        }
        ++data.ticks;
        for (int i = n6 - n8 + 1; i < n6 + n8; ++i) {
            if ((i & 0xF) != (data.ticks & 0xF)) continue;
            int n9 = 255;
            int n10 = 0;
            double d = 0.0;
            for (int j = n7 - n8 - 1; j < n7 + n8; ++j) {
                byte by;
                byte by2;
                int n11;
                int n12;
                int n13;
                int n14;
                int n15;
                if (i < 0 || j < -1 || i >= n || j >= n2) continue;
                int n16 = i - n6;
                int n17 = j - n7;
                boolean bl = n16 * n16 + n17 * n17 > (n8 - 2) * (n8 - 2);
                int n18 = (n4 / n3 + i - n / 2) * n3;
                int n19 = (n5 / n3 + j - n2 / 2) * n3;
                int n20 = 0;
                int n21 = 0;
                int n22 = 0;
                int[] nArray = new int[256];
                Chunk chunk = world.getChunkFromBlockCoords(n18, n19);
                int n23 = n18 & 0xF;
                int n24 = n19 & 0xF;
                int n25 = 0;
                double d2 = 0.0;
                if (world.worldProvider.noSky) {
                    n15 = n18 + n19 * 231871;
                    n15 = n15 * n15 * 31287121 + n15 * 11;
                    if ((n15 >> 20 & 1) == 0) {
                        nArray[Block.dirt.blockID] += 10;
                    } else {
                        nArray[Block.stone.blockID] += 10;
                    }
                    d2 = 100.0;
                } else {
                    for (n15 = 0; n15 < n3; ++n15) {
                        for (n14 = 0; n14 < n3; ++n14) {
                            n13 = chunk.getHeightValue(n15 + n23, n14 + n24) + 1;
                            int n28 = 0;
                            if (n13 > 1) {
                                n12 = 0;
                                do {
                                    n12 = 1;
                                    n28 = chunk.getBlockID(n15 + n23, n13 - 1, n14 + n24);
                                    if (n28 == 0) {
                                        n12 = 0;
                                    } else if (n13 > 0 && n28 > 0 && Block.blocksList[n28].blockMaterial.color == MapColor.AIR) {
                                        n12 = 0;
                                    }
                                    if (n12 != 0) continue;
                                    --n13;
                                    n28 = chunk.getBlockID(n15 + n23, n13 - 1, n14 + n24);
                                } while (n12 == 0);
                                if (n28 != 0 && Block.blocksList[n28].blockMaterial.getIsLiquid()) {
                                    n11 = n13 - 1;
                                    int n29 = 0;
                                    do {
                                        n29 = chunk.getBlockID(n15 + n23, n11--, n14 + n24);
                                        ++n25;
                                    } while (n11 > 0 && n29 != 0 && Block.blocksList[n29].blockMaterial.getIsLiquid());
                                }
                            }
                            d2 += (double)n13 / (double)(n3 * n3);
                            ++nArray[n28];
                        }
                    }
                }
                n25 /= n3 * n3;
                n20 /= n3 * n3;
                n21 /= n3 * n3;
                n22 /= n3 * n3;
                n15 = 0;
                n14 = 0;
                for (n13 = 0; n13 < 256; ++n13) {
                    if (nArray[n13] <= n15) continue;
                    n14 = n13;
                    n15 = nArray[n13];
                }
                double d3 = (d2 - d) * 4.0 / (double)(n3 + 4) + ((double)(i + j & 1) - 0.5) * 0.4;
                n12 = 1;
                if (d3 > 0.6) {
                    n12 = 2;
                }
                if (d3 < -0.6) {
                    n12 = 0;
                }
                n11 = 0;
                if (n14 > 0) {
                    MapColor mapColor = Block.blocksList[n14].blockMaterial.color;
                    if (mapColor == MapColor.WATER) {
                        d3 = (double)n25 * 0.1 + (double)(i + j & 1) * 0.2;
                        n12 = 1;
                        if (d3 < 0.5) {
                            n12 = 2;
                        }
                        if (d3 > 0.9) {
                            n12 = 0;
                        }
                    }
                    n11 = mapColor.id;
                }
                d = d2;
                if (j < 0 || n16 * n16 + n17 * n17 >= n8 * n8 || bl && (i + j & 1) == 0) continue;
                by2 = data.colors[i + j * n];
                by = (byte)(n11 * 4 + n12);
                if (by2 == by) continue;
                if (n9 > j) {
                    n9 = j;
                }
                if (n10 < j) {
                    n10 = j;
                }
                data.colors[i + j * n] = by;
            }
            if (n9 > n10) continue;
            data.markDirty(i, n9, n10);
        }
    }

    public void tick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.singleplayerWorld) {
            return;
        }
        MapData mapData = this.getSavedMapData(stack, world);
        if (entity instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer)entity;
            mapData.tickHolder(entityPlayer, stack);
        }
        if (selected) {
            this.update(world, entity, mapData);
        }
    }

    public void onResult(ItemStack stack, World world, EntityPlayer player) {
        stack.setItemDamage(world.getSavedDataCount("map"));
        String string = "map_" + stack.getItemDamage();
        MapData mapData = new MapData(string);
        world.setSavedData(string, mapData);
        mapData.centerX = MathHelper.floor_double(player.posX);
        mapData.centerZ = MathHelper.floor_double(player.posZ);
        mapData.scale = (byte)3;
        mapData.dimension = (byte)world.worldProvider.worldType;
        mapData.markDirty();
    }

    public Packet getUpdatePacket(ItemStack stack, World world, EntityPlayer player) {
        byte[] byArray = this.getSavedMapData(stack, world).getUpdateData(stack, world, player);
        if (byArray == null) {
            return null;
        }
        return new Packet131MapData((short)Item.MAP.shiftedIndex, (short)stack.getItemDamage(), byArray);
    }
}

