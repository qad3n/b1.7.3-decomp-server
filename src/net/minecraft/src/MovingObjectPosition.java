package net.minecraft.src;


public class MovingObjectPosition {
    public EnumMovingObjectType typeOfHit;
    public int blockX;
    public int blockY;
    public int blockZ;
    public int sideHit;
    public Vec3D hitVec;
    public Entity entityHit;

    public MovingObjectPosition(int x, int y, int z, int face, Vec3D facePos) {
        this.typeOfHit = EnumMovingObjectType.TILE;
        this.blockX = x;
        this.blockY = y;
        this.blockZ = z;
        this.sideHit = face;
        this.hitVec = Vec3D.createVector(facePos.xCoord, facePos.yCoord, facePos.zCoord);
    }

    public MovingObjectPosition(Entity entity) {
        this.typeOfHit = EnumMovingObjectType.ENTITY;
        this.entityHit = entity;
        this.hitVec = Vec3D.createVector(entity.posX, entity.posY, entity.posZ);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum EnumMovingObjectType {
        TILE,
        ENTITY;

    }
}

