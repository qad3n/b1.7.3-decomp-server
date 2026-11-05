package net.minecraft.src;

public class MetadataChunkBlock {
    public final EnumSkyBlock type;
    public int minX;
    public int minY;
    public int minZ;
    public int maxX;
    public int maxY;
    public int maxZ;

    public MetadataChunkBlock(EnumSkyBlock type, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.type = type;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public void run(World world) {
        int i = this.maxX - this.minX + 1;
        int j = this.maxY - this.minY + 1;
        int k = this.maxZ - this.minZ + 1;
        int l = i * j * k;
        if (l > 32768) {
            System.out.println("Light too large, skipping!");
        } else {
            int i1 = 0;
            int j1 = 0;
            boolean flag = false;
            boolean flag1 = false;

            for (int k1 = this.minX; k1 <= this.maxX; k1++) {
                for (int l1 = this.minZ; l1 <= this.maxZ; l1++) {
                    int i2 = k1 >> 4;
                    int j2 = l1 >> 4;
                    boolean flag2 = false;
                    if (flag && i2 == i1 && j2 == j1) {
                        flag2 = flag1;
                    } else {
                        flag2 = world.doChunksNearChunkExist(k1, 0, l1, 1);
                        if (flag2) {
                            Chunk chunk = world.getChunkFromChunkCoords(k1 >> 4, l1 >> 4);
                            if (chunk.isEmpty()) {
                                flag2 = false;
                            }
                        }

                        flag1 = flag2;
                        i1 = i2;
                        j1 = j2;
                    }

                    if (flag2) {
                        if (this.minY < 0) {
                            this.minY = 0;
                        }

                        if (this.maxY >= 128) {
                            this.maxY = 127;
                        }

                        for (int j5 = this.minY; j5 <= this.maxY; j5++) {
                            int k2 = world.getSavedLightValue(this.type, k1, j5, l1);
                            int l2 = 0;
                            int i3 = world.getBlockId(k1, j5, l1);
                            int j3 = Block.lightOpacity[i3];
                            if (j3 == 0) {
                                j3 = 1;
                            }

                            int k3 = 0;
                            if (this.type == EnumSkyBlock.Sky) {
                                if (world.canExistingBlockSeeTheSky(k1, j5, l1)) {
                                    k3 = 15;
                                }
                            } else if (this.type == EnumSkyBlock.Block) {
                                k3 = Block.lightValue[i3];
                            }

                            if (j3 >= 15 && k3 == 0) {
                                l2 = 0;
                            } else {
                                int l3 = world.getSavedLightValue(this.type, k1 - 1, j5, l1);
                                int i4 = world.getSavedLightValue(this.type, k1 + 1, j5, l1);
                                int j4 = world.getSavedLightValue(this.type, k1, j5 - 1, l1);
                                int k4 = world.getSavedLightValue(this.type, k1, j5 + 1, l1);
                                int l4 = world.getSavedLightValue(this.type, k1, j5, l1 - 1);
                                int i5 = world.getSavedLightValue(this.type, k1, j5, l1 + 1);
                                l2 = l3;
                                if (i4 > l2) {
                                    l2 = i4;
                                }

                                if (j4 > l2) {
                                    l2 = j4;
                                }

                                if (k4 > l2) {
                                    l2 = k4;
                                }

                                if (l4 > l2) {
                                    l2 = l4;
                                }

                                if (i5 > l2) {
                                    l2 = i5;
                                }

                                l2 -= j3;
                                if (l2 < 0) {
                                    l2 = 0;
                                }

                                if (k3 > l2) {
                                    l2 = k3;
                                }
                            }

                            if (k2 != l2) {
                                world.setLightValue(this.type, k1, j5, l1, l2);
                                int k5 = l2 - 1;
                                if (k5 < 0) {
                                    k5 = 0;
                                }

                                world.neighborLightPropagationChanged(this.type, k1 - 1, j5, l1, k5);
                                world.neighborLightPropagationChanged(this.type, k1, j5 - 1, l1, k5);
                                world.neighborLightPropagationChanged(this.type, k1, j5, l1 - 1, k5);
                                if (k1 + 1 >= this.maxX) {
                                    world.neighborLightPropagationChanged(this.type, k1 + 1, j5, l1, k5);
                                }

                                if (j5 + 1 >= this.maxY) {
                                    world.neighborLightPropagationChanged(this.type, k1, j5 + 1, l1, k5);
                                }

                                if (l1 + 1 >= this.maxZ) {
                                    world.neighborLightPropagationChanged(this.type, k1, j5, l1 + 1, k5);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean expand(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        if (minX >= this.minX && minY >= this.minY && minZ >= this.minZ && maxX <= this.maxX && maxY <= this.maxY && maxZ <= this.maxZ) {
            return true;
        }

        byte b0 = 1;
        if (minX >= this.minX - b0
            && minY >= this.minY - b0
            && minZ >= this.minZ - b0
            && maxX <= this.maxX + b0
            && maxY <= this.maxY + b0
            && maxZ <= this.maxZ + b0) {
            int i = this.maxX - this.minX;
            int j = this.maxY - this.minY;
            int k = this.maxZ - this.minZ;
            if (minX > this.minX) {
                minX = this.minX;
            }

            if (minY > this.minY) {
                minY = this.minY;
            }

            if (minZ > this.minZ) {
                minZ = this.minZ;
            }

            if (maxX < this.maxX) {
                maxX = this.maxX;
            }

            if (maxY < this.maxY) {
                maxY = this.maxY;
            }

            if (maxZ < this.maxZ) {
                maxZ = this.maxZ;
            }

            int l = maxX - minX;
            int i1 = maxY - minY;
            int j1 = maxZ - minZ;
            int k1 = i * j * k;
            int l1 = l * i1 * j1;
            if (l1 - k1 <= 2) {
                this.minX = minX;
                this.minY = minY;
                this.minZ = minZ;
                this.maxX = maxX;
                this.maxY = maxY;
                this.maxZ = maxZ;
                return true;
            }
        }

        return false;
    }
}
