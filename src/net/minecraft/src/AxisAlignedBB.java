package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

public class AxisAlignedBB {
    private static List<AxisAlignedBB> boundingBoxes = new ArrayList<AxisAlignedBB>();
    private static int numBoundingBoxesInUse = 0;
    public double minX;
    public double minY;
    public double minZ;
    public double maxX;
    public double maxY;
    public double maxZ;

    public static AxisAlignedBB getBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static void clearBoundingBoxPool() {
        numBoundingBoxesInUse = 0;
    }

    public static AxisAlignedBB getBoundingBoxFromPool(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        if (numBoundingBoxesInUse >= boundingBoxes.size()) {
            boundingBoxes.add(AxisAlignedBB.getBoundingBox(0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
        }
        return boundingBoxes.get(numBoundingBoxesInUse++).setBounds(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private AxisAlignedBB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public AxisAlignedBB setBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        return this;
    }

    public AxisAlignedBB addCoord(double dx, double dy, double dz) {
        double d = this.minX;
        double d2 = this.minY;
        double d3 = this.minZ;
        double d4 = this.maxX;
        double d5 = this.maxY;
        double d6 = this.maxZ;
        if (dx < 0.0) {
            d += dx;
        }
        if (dx > 0.0) {
            d4 += dx;
        }
        if (dy < 0.0) {
            d2 += dy;
        }
        if (dy > 0.0) {
            d5 += dy;
        }
        if (dz < 0.0) {
            d3 += dz;
        }
        if (dz > 0.0) {
            d6 += dz;
        }
        return AxisAlignedBB.getBoundingBoxFromPool(d, d2, d3, d4, d5, d6);
    }

    public AxisAlignedBB expand(double dx, double dy, double dz) {
        double d = this.minX - dx;
        double d2 = this.minY - dy;
        double d3 = this.minZ - dz;
        double d4 = this.maxX + dx;
        double d5 = this.maxY + dy;
        double d6 = this.maxZ + dz;
        return AxisAlignedBB.getBoundingBoxFromPool(d, d2, d3, d4, d5, d6);
    }

    public AxisAlignedBB getOffsetBoundingBox(double dx, double dy, double dz) {
        return AxisAlignedBB.getBoundingBoxFromPool(this.minX + dx, this.minY + dy, this.minZ + dz, this.maxX + dx, this.maxY + dy, this.maxZ + dz);
    }

    public double calculateXOffset(final AxisAlignedBB box, double limit) {
        if (box.maxY <= this.minY || box.minY >= this.maxY) {
            return limit;
        }
        if (box.maxZ <= this.minZ || box.minZ >= this.maxZ) {
            return limit;
        }
        if (limit > 0.0 && box.maxX <= this.minX) {
            final double n = this.minX - box.maxX;
            if (n < limit) {
                limit = n;
            }
        }
        if (limit < 0.0 && box.minX >= this.maxX) {
            final double n2 = this.maxX - box.minX;
            if (n2 > limit) {
                limit = n2;
            }
        }
        return limit;
    }

    public double calculateYOffset(final AxisAlignedBB box, double limit) {
        if (box.maxX <= this.minX || box.minX >= this.maxX) {
            return limit;
        }
        if (box.maxZ <= this.minZ || box.minZ >= this.maxZ) {
            return limit;
        }
        if (limit > 0.0 && box.maxY <= this.minY) {
            final double n = this.minY - box.maxY;
            if (n < limit) {
                limit = n;
            }
        }
        if (limit < 0.0 && box.minY >= this.maxY) {
            final double n2 = this.maxY - box.minY;
            if (n2 > limit) {
                limit = n2;
            }
        }
        return limit;
    }

    public double calculateZOffset(final AxisAlignedBB box, double limit) {
        if (box.maxX <= this.minX || box.minX >= this.maxX) {
            return limit;
        }
        if (box.maxY <= this.minY || box.minY >= this.maxY) {
            return limit;
        }
        if (limit > 0.0 && box.maxZ <= this.minZ) {
            final double n = this.minZ - box.maxZ;
            if (n < limit) {
                limit = n;
            }
        }
        if (limit < 0.0 && box.minZ >= this.maxZ) {
            final double n2 = this.maxZ - box.minZ;
            if (n2 > limit) {
                limit = n2;
            }
        }
        return limit;
    }

    public boolean intersectsWith(AxisAlignedBB box) {
        if (box.maxX <= this.minX || box.minX >= this.maxX) {
            return false;
        }
        if (box.maxY <= this.minY || box.minY >= this.maxY) {
            return false;
        }
        return !(box.maxZ <= this.minZ) && !(box.minZ >= this.maxZ);
    }

    public AxisAlignedBB offset(double dx, double dy, double dz) {
        this.minX += dx;
        this.minY += dy;
        this.minZ += dz;
        this.maxX += dx;
        this.maxY += dy;
        this.maxZ += dz;
        return this;
    }

    public boolean isVecInXYZ(Vec3D vec) {
        if (vec.xCoord <= this.minX || vec.xCoord >= this.maxX) {
            return false;
        }
        if (vec.yCoord <= this.minY || vec.yCoord >= this.maxY) {
            return false;
        }
        return !(vec.zCoord <= this.minZ) && !(vec.zCoord >= this.maxZ);
    }

    public AxisAlignedBB getInsetBoundingBox(double dx, double dy, double dz) {
        double d = this.minX + dx;
        double d2 = this.minY + dy;
        double d3 = this.minZ + dz;
        double d4 = this.maxX - dx;
        double d5 = this.maxY - dy;
        double d6 = this.maxZ - dz;
        return AxisAlignedBB.getBoundingBoxFromPool(d, d2, d3, d4, d5, d6);
    }

    public AxisAlignedBB copy() {
        return AxisAlignedBB.getBoundingBoxFromPool(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }

    public MovingObjectPosition clip(Vec3D from, Vec3D to) {
        Vec3D vec3D = from.getIntermediateWithXValue(to, this.minX);
        Vec3D vec3D2 = from.getIntermediateWithXValue(to, this.maxX);
        Vec3D vec3D3 = from.getIntermediateWithYValue(to, this.minY);
        Vec3D vec3D4 = from.getIntermediateWithYValue(to, this.maxY);
        Vec3D vec3D5 = from.getIntermediateWithZValue(to, this.minZ);
        Vec3D vec3D6 = from.getIntermediateWithZValue(to, this.maxZ);
        if (!this.isVecInYZ(vec3D)) {
            vec3D = null;
        }
        if (!this.isVecInYZ(vec3D2)) {
            vec3D2 = null;
        }
        if (!this.isVecInXZ(vec3D3)) {
            vec3D3 = null;
        }
        if (!this.isVecInXZ(vec3D4)) {
            vec3D4 = null;
        }
        if (!this.isVecInXY(vec3D5)) {
            vec3D5 = null;
        }
        if (!this.isVecInXY(vec3D6)) {
            vec3D6 = null;
        }
        Vec3D vec3D7 = null;
        if (vec3D != null && (vec3D7 == null || from.squareDistanceTo(vec3D) < from.squareDistanceTo(vec3D7))) {
            vec3D7 = vec3D;
        }
        if (vec3D2 != null && (vec3D7 == null || from.squareDistanceTo(vec3D2) < from.squareDistanceTo(vec3D7))) {
            vec3D7 = vec3D2;
        }
        if (vec3D3 != null && (vec3D7 == null || from.squareDistanceTo(vec3D3) < from.squareDistanceTo(vec3D7))) {
            vec3D7 = vec3D3;
        }
        if (vec3D4 != null && (vec3D7 == null || from.squareDistanceTo(vec3D4) < from.squareDistanceTo(vec3D7))) {
            vec3D7 = vec3D4;
        }
        if (vec3D5 != null && (vec3D7 == null || from.squareDistanceTo(vec3D5) < from.squareDistanceTo(vec3D7))) {
            vec3D7 = vec3D5;
        }
        if (vec3D6 != null && (vec3D7 == null || from.squareDistanceTo(vec3D6) < from.squareDistanceTo(vec3D7))) {
            vec3D7 = vec3D6;
        }
        if (vec3D7 == null) {
            return null;
        }
        int n = -1;
        if (vec3D7 == vec3D) {
            n = 4;
        }
        if (vec3D7 == vec3D2) {
            n = 5;
        }
        if (vec3D7 == vec3D3) {
            n = 0;
        }
        if (vec3D7 == vec3D4) {
            n = 1;
        }
        if (vec3D7 == vec3D5) {
            n = 2;
        }
        if (vec3D7 == vec3D6) {
            n = 3;
        }
        return new MovingObjectPosition(0, 0, 0, n, vec3D7);
    }

    private boolean isVecInYZ(Vec3D vec) {
        if (vec == null) {
            return false;
        }
        return vec.yCoord >= this.minY && vec.yCoord <= this.maxY && vec.zCoord >= this.minZ && vec.zCoord <= this.maxZ;
    }

    private boolean isVecInXZ(Vec3D vec) {
        if (vec == null) {
            return false;
        }
        return vec.xCoord >= this.minX && vec.xCoord <= this.maxX && vec.zCoord >= this.minZ && vec.zCoord <= this.maxZ;
    }

    private boolean isVecInXY(Vec3D vec) {
        if (vec == null) {
            return false;
        }
        return vec.xCoord >= this.minX && vec.xCoord <= this.maxX && vec.yCoord >= this.minY && vec.yCoord <= this.maxY;
    }

    public void setBB(AxisAlignedBB box) {
        this.minX = box.minX;
        this.minY = box.minY;
        this.minZ = box.minZ;
        this.maxX = box.maxX;
        this.maxY = box.maxY;
        this.maxZ = box.maxZ;
    }

    public String toString() {
        return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
    }
}

