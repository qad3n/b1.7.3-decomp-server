package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Explosion {
    public boolean isFlaming = false;
    private Random ExplosionRNG = new Random();
    private World worldObj;
    public double explosionX;
    public double explosionY;
    public double explosionZ;
    public Entity exploder;
    public float explosionSize;
    public Set<ChunkPosition> destroyedBlockPositions = new HashSet<>();

    public Explosion(World world, Entity source, double x, double y, double z, float power) {
        this.worldObj = world;
        this.exploder = source;
        this.explosionSize = power;
        this.explosionX = x;
        this.explosionY = y;
        this.explosionZ = z;
    }

    public void doExplosion() {
        float f = this.explosionSize;
        byte b0 = 16;

        for (int i = 0; i < b0; i++) {
            for (int j = 0; j < b0; j++) {
                for (int k = 0; k < b0; k++) {
                    if (i == 0 || i == b0 - 1 || j == 0 || j == b0 - 1 || k == 0 || k == b0 - 1) {
                        double d0 = i / (b0 - 1.0F) * 2.0F - 1.0F;
                        double d1 = j / (b0 - 1.0F) * 2.0F - 1.0F;
                        double d2 = k / (b0 - 1.0F) * 2.0F - 1.0F;
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 /= d3;
                        d1 /= d3;
                        d2 /= d3;
                        float f1 = this.explosionSize * (0.7F + this.worldObj.rand.nextFloat() * 0.6F);
                        double d5 = this.explosionX;
                        double d6 = this.explosionY;
                        double d7 = this.explosionZ;

                        for (float f2 = 0.3F; f1 > 0.0F; f1 -= f2 * 0.75F) {
                            int l1 = MathHelper.floor_double(d5);
                            int i2 = MathHelper.floor_double(d6);
                            int j2 = MathHelper.floor_double(d7);
                            int k2 = this.worldObj.getBlockId(l1, i2, j2);
                            if (k2 > 0) {
                                f1 -= (Block.blocksList[k2].getExplosionResistance(this.exploder) + 0.3F) * f2;
                            }

                            if (f1 > 0.0F) {
                                this.destroyedBlockPositions.add(new ChunkPosition(l1, i2, j2));
                            }

                            d5 += d0 * f2;
                            d6 += d1 * f2;
                            d7 += d2 * f2;
                        }
                    }
                }
            }
        }

        this.explosionSize *= 2.0F;
        int l2 = MathHelper.floor_double(this.explosionX - this.explosionSize - 1.0);
        int i3 = MathHelper.floor_double(this.explosionX + this.explosionSize + 1.0);
        int j3 = MathHelper.floor_double(this.explosionY - this.explosionSize - 1.0);
        int k3 = MathHelper.floor_double(this.explosionY + this.explosionSize + 1.0);
        int l = MathHelper.floor_double(this.explosionZ - this.explosionSize - 1.0);
        int l3 = MathHelper.floor_double(this.explosionZ + this.explosionSize + 1.0);
        List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.exploder, AxisAlignedBB.getBoundingBoxFromPool(l2, j3, l, i3, k3, l3));
        Vec3D vec3d = Vec3D.createVector(this.explosionX, this.explosionY, this.explosionZ);

        for (int i1 = 0; i1 < list.size(); i1++) {
            Entity entity = (Entity)list.get(i1);
            double d4 = entity.getDistance(this.explosionX, this.explosionY, this.explosionZ) / this.explosionSize;
            if (d4 <= 1.0) {
                double d9 = entity.posX - this.explosionX;
                double d10 = entity.posY - this.explosionY;
                double d11 = entity.posZ - this.explosionZ;
                double d12 = MathHelper.sqrt_double(d9 * d9 + d10 * d10 + d11 * d11);
                d9 /= d12;
                d10 /= d12;
                d11 /= d12;
                double d13 = this.worldObj.getBlockDensity(vec3d, entity.boundingBox);
                double d14 = (1.0 - d4) * d13;
                entity.attackEntityFrom(this.exploder, (int)((d14 * d14 + d14) / 2.0 * 8.0 * this.explosionSize + 1.0));
                double d8 = d14;
                entity.motionX += d9 * d8;
                entity.motionY += d10 * d8;
                entity.motionZ += d11 * d8;
            }
        }

        this.explosionSize = f;
        ArrayList arraylist = new ArrayList();
        arraylist.addAll(this.destroyedBlockPositions);
        if (this.isFlaming) {
            for (int i4 = arraylist.size() - 1; i4 >= 0; i4--) {
                ChunkPosition chunkposition = (ChunkPosition)arraylist.get(i4);
                int j4 = chunkposition.x;
                int k4 = chunkposition.y;
                int j1 = chunkposition.z;
                int l4 = this.worldObj.getBlockId(j4, k4, j1);
                int k1 = this.worldObj.getBlockId(j4, k4 - 1, j1);
                if (l4 == 0 && Block.opaqueCubeLookup[k1] && this.ExplosionRNG.nextInt(3) == 0) {
                    this.worldObj.setBlockWithNotify(j4, k4, j1, Block.fire.blockID);
                }
            }
        }
    }

    public void doEffects(boolean createFire) {
        this.worldObj
            .playSoundEffect(
                this.explosionX,
                this.explosionY,
                this.explosionZ,
                "random.explode",
                4.0F,
                (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F
            );
        ArrayList arraylist = new ArrayList();
        arraylist.addAll(this.destroyedBlockPositions);

        for (int i = arraylist.size() - 1; i >= 0; i--) {
            ChunkPosition chunkposition = (ChunkPosition)arraylist.get(i);
            int j = chunkposition.x;
            int k = chunkposition.y;
            int l = chunkposition.z;
            int i1 = this.worldObj.getBlockId(j, k, l);
            if (createFire) {
                double d0 = j + this.worldObj.rand.nextFloat();
                double d1 = k + this.worldObj.rand.nextFloat();
                double d2 = l + this.worldObj.rand.nextFloat();
                double d3 = d0 - this.explosionX;
                double d4 = d1 - this.explosionY;
                double d5 = d2 - this.explosionZ;
                double d6 = MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
                d3 /= d6;
                d4 /= d6;
                d5 /= d6;
                double d7 = 0.5 / (d6 / this.explosionSize + 0.1);
                d7 *= this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F;
                d3 *= d7;
                d4 *= d7;
                d5 *= d7;
                this.worldObj
                    .spawnParticle(
                        "explode", (d0 + this.explosionX * 1.0) / 2.0, (d1 + this.explosionY * 1.0) / 2.0, (d2 + this.explosionZ * 1.0) / 2.0, d3, d4, d5
                    );
                this.worldObj.spawnParticle("smoke", d0, d1, d2, d3, d4, d5);
            }

            if (i1 > 0) {
                Block.blocksList[i1].dropBlockAsItemWithChance(this.worldObj, j, k, l, this.worldObj.getBlockMetadata(j, k, l), 0.3F);
                this.worldObj.setBlockWithNotify(j, k, l, 0);
                Block.blocksList[i1].onBlockDestroyedByExplosion(this.worldObj, j, k, l);
            }
        }
    }
}
