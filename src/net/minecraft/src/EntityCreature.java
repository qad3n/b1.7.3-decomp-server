package net.minecraft.src;

public class EntityCreature extends EntityLiving {
    private PathEntity pathToEntity;
    protected Entity playerToAttack;
    protected boolean hasAttacked = false;

    public EntityCreature(World var1) {
        super(var1);
    }

    protected boolean hasStoppedMoving() {
        return false;
    }

    @Override
    protected void updatePlayerActionState() {
        this.hasAttacked = this.hasStoppedMoving();
        float f = 16.0F;
        if (this.playerToAttack == null) {
            this.playerToAttack = this.findPlayerToAttack();
            if (this.playerToAttack != null) {
                this.pathToEntity = this.worldObj.getPathToEntity(this, this.playerToAttack, f);
            }
        } else if (!this.playerToAttack.isEntityAlive()) {
            this.playerToAttack = null;
        } else {
            float f1 = this.playerToAttack.getDistanceToEntity(this);
            if (this.canEntityBeSeen(this.playerToAttack)) {
                this.attackEntity(this.playerToAttack, f1);
            } else {
                this.targetOutOfSight(this.playerToAttack, f1);
            }
        }

        if (this.hasAttacked || this.playerToAttack == null || this.pathToEntity != null && this.rand.nextInt(20) != 0) {
            if (!this.hasAttacked && (this.pathToEntity == null && this.rand.nextInt(80) == 0 || this.rand.nextInt(80) == 0)) {
                this.updateWanderingPath();
            }
        } else {
            this.pathToEntity = this.worldObj.getPathToEntity(this, this.playerToAttack, f);
        }

        int i = MathHelper.floor_double(this.boundingBox.minY + 0.5);
        boolean flag = this.isInWater();
        boolean flag1 = this.handleLavaMovement();
        this.rotationPitch = 0.0F;
        if (this.pathToEntity != null && this.rand.nextInt(100) != 0) {
            Vec3D vec3d = this.pathToEntity.getPosition(this);
            double d0 = this.width * 2.0F;

            while (vec3d != null && vec3d.squareDistanceTo(this.posX, vec3d.yCoord, this.posZ) < d0 * d0) {
                this.pathToEntity.incrementPathIndex();
                if (this.pathToEntity.isFinished()) {
                    vec3d = null;
                    this.pathToEntity = null;
                } else {
                    vec3d = this.pathToEntity.getPosition(this);
                }
            }

            this.isJumping = false;
            if (vec3d != null) {
                double d1 = vec3d.xCoord - this.posX;
                double d2 = vec3d.zCoord - this.posZ;
                double d3 = vec3d.yCoord - i;
                float f2 = (float)(Math.atan2(d2, d1) * 180.0 / (float) Math.PI) - 90.0F;
                float f3 = f2 - this.rotationYaw;
                this.moveForward = this.moveSpeed;

                while (f3 < -180.0F) {
                    f3 += 360.0F;
                }

                while (f3 >= 180.0F) {
                    f3 -= 360.0F;
                }

                if (f3 > 30.0F) {
                    f3 = 30.0F;
                }

                if (f3 < -30.0F) {
                    f3 = -30.0F;
                }

                this.rotationYaw += f3;
                if (this.hasAttacked && this.playerToAttack != null) {
                    double d4 = this.playerToAttack.posX - this.posX;
                    double d5 = this.playerToAttack.posZ - this.posZ;
                    float f4 = this.rotationYaw;
                    this.rotationYaw = (float)(Math.atan2(d5, d4) * 180.0 / (float) Math.PI) - 90.0F;
                    f3 = (f4 - this.rotationYaw + 90.0F) * (float) Math.PI / 180.0F;
                    this.moveStrafing = -MathHelper.sin(f3) * this.moveForward * 1.0F;
                    this.moveForward = MathHelper.cos(f3) * this.moveForward * 1.0F;
                }

                if (d3 > 0.0) {
                    this.isJumping = true;
                }
            }

            if (this.playerToAttack != null) {
                this.faceEntity(this.playerToAttack, 30.0F, 30.0F);
            }

            if (this.isCollidedHorizontally && !this.getGotPath()) {
                this.isJumping = true;
            }

            if (this.rand.nextFloat() < 0.8F && (flag || flag1)) {
                this.isJumping = true;
            }
        } else {
            super.updatePlayerActionState();
            this.pathToEntity = null;
        }
    }

    protected void updateWanderingPath() {
        boolean flag = false;
        int i = -1;
        int j = -1;
        int k = -1;
        float f = -99999.0F;

        for (int l = 0; l < 10; l++) {
            int i1 = MathHelper.floor_double(this.posX + this.rand.nextInt(13) - 6.0);
            int j1 = MathHelper.floor_double(this.posY + this.rand.nextInt(7) - 3.0);
            int k1 = MathHelper.floor_double(this.posZ + this.rand.nextInt(13) - 6.0);
            float f1 = this.getBlockPathWeight(i1, j1, k1);
            if (f1 > f) {
                f = f1;
                i = i1;
                j = j1;
                k = k1;
                flag = true;
            }
        }

        if (flag) {
            this.pathToEntity = this.worldObj.getEntityPathToXYZ(this, i, j, k, 10.0F);
        }
    }

    protected void attackEntity(Entity target, float distance) {
    }

    protected void targetOutOfSight(Entity target, float distance) {
    }

    protected float getBlockPathWeight(int x, int y, int z) {
        return 0.0F;
    }

    protected Entity findPlayerToAttack() {
        return null;
    }

    @Override
    public boolean getCanSpawnHere() {
        int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_double(this.boundingBox.minY);
        int k = MathHelper.floor_double(this.posZ);
        return super.getCanSpawnHere() && this.getBlockPathWeight(i, j, k) >= 0.0F;
    }

    public boolean getGotPath() {
        return this.pathToEntity != null;
    }

    public void setPathToEntity(PathEntity path) {
        this.pathToEntity = path;
    }

    public Entity getEntityToAttack() {
        return this.playerToAttack;
    }

    public void setEntityToAttack(Entity target) {
        this.playerToAttack = target;
    }
}
