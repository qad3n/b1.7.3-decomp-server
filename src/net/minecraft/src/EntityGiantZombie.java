package net.minecraft.src;


public class EntityGiantZombie
extends EntityMob {
    public EntityGiantZombie(World world) {
        super(world);
        this.texture = "/mob/zombie.png";
        this.moveSpeed = 0.5f;
        this.attackStrength = 50;
        this.health *= 10;
        this.yOffset *= 6.0f;
        this.setSize(this.width * 6.0f, this.height * 6.0f);
    }

    protected float getBlockPathWeight(int x, int y, int z) {
        return this.worldObj.getLightBrightness(x, y, z) - 0.5f;
    }
}

