package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

public class Vec3D {
    private static List<Vec3D> vectorList = new ArrayList<Vec3D>();
    private static int nextVector = 0;
    public double xCoord;
    public double yCoord;
    public double zCoord;

    public static Vec3D createVectorHelper(double x, double y, double z) {
        return new Vec3D(x, y, z);
    }

    public static void initialize() {
        nextVector = 0;
    }

    public static Vec3D createVector(double x, double y, double z) {
        if (nextVector >= vectorList.size()) {
            vectorList.add(Vec3D.createVectorHelper(0.0, 0.0, 0.0));
        }
        return vectorList.get(nextVector++).setComponents(x, y, z);
    }

    private Vec3D(double x, double y, double z) {
        if (x == -0.0) {
            x = 0.0;
        }
        if (y == -0.0) {
            y = 0.0;
        }
        if (z == -0.0) {
            z = 0.0;
        }
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
    }

    private Vec3D setComponents(double x, double y, double z) {
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
        return this;
    }

    public Vec3D normalize() {
        double d = MathHelper.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
        if (d < 1.0E-4) {
            return Vec3D.createVector(0.0, 0.0, 0.0);
        }
        return Vec3D.createVector(this.xCoord / d, this.yCoord / d, this.zCoord / d);
    }

    public Vec3D addVector(double x, double y, double z) {
        return Vec3D.createVector(this.xCoord + x, this.yCoord + y, this.zCoord + z);
    }

    public double distanceTo(Vec3D vec) {
        double d = vec.xCoord - this.xCoord;
        double d2 = vec.yCoord - this.yCoord;
        double d3 = vec.zCoord - this.zCoord;
        return MathHelper.sqrt_double(d * d + d2 * d2 + d3 * d3);
    }

    public double squareDistanceTo(Vec3D vec) {
        double d = vec.xCoord - this.xCoord;
        double d2 = vec.yCoord - this.yCoord;
        double d3 = vec.zCoord - this.zCoord;
        return d * d + d2 * d2 + d3 * d3;
    }

    public double squareDistanceTo(double x, double y, double z) {
        double d = x - this.xCoord;
        double d2 = y - this.yCoord;
        double d3 = z - this.zCoord;
        return d * d + d2 * d2 + d3 * d3;
    }

    public double lengthVector() {
        return MathHelper.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
    }

    public Vec3D getIntermediateWithXValue(Vec3D vec, double x) {
        double d = vec.xCoord - this.xCoord;
        double d2 = vec.yCoord - this.yCoord;
        double d3 = vec.zCoord - this.zCoord;
        if (d * d < (double)1.0E-7f) {
            return null;
        }
        double d4 = (x - this.xCoord) / d;
        if (d4 < 0.0 || d4 > 1.0) {
            return null;
        }
        return Vec3D.createVector(this.xCoord + d * d4, this.yCoord + d2 * d4, this.zCoord + d3 * d4);
    }

    public Vec3D getIntermediateWithYValue(Vec3D vec, double y) {
        double d = vec.xCoord - this.xCoord;
        double d2 = vec.yCoord - this.yCoord;
        double d3 = vec.zCoord - this.zCoord;
        if (d2 * d2 < (double)1.0E-7f) {
            return null;
        }
        double d4 = (y - this.yCoord) / d2;
        if (d4 < 0.0 || d4 > 1.0) {
            return null;
        }
        return Vec3D.createVector(this.xCoord + d * d4, this.yCoord + d2 * d4, this.zCoord + d3 * d4);
    }

    public Vec3D getIntermediateWithZValue(Vec3D vec, double z) {
        double d = vec.xCoord - this.xCoord;
        double d2 = vec.yCoord - this.yCoord;
        double d3 = vec.zCoord - this.zCoord;
        if (d3 * d3 < (double)1.0E-7f) {
            return null;
        }
        double d4 = (z - this.zCoord) / d3;
        if (d4 < 0.0 || d4 > 1.0) {
            return null;
        }
        return Vec3D.createVector(this.xCoord + d * d4, this.yCoord + d2 * d4, this.zCoord + d3 * d4);
    }

    public String toString() {
        return "(" + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + ")";
    }
}

