package net.minecraft.src;


public class PathEntity {
    private final PathPoint[] points;
    public final int pathLength;
    private int pathIndex;

    public PathEntity(PathPoint[] nodes) {
        this.points = nodes;
        this.pathLength = nodes.length;
    }

    public void incrementPathIndex() {
        ++this.pathIndex;
    }

    public boolean isFinished() {
        return this.pathIndex >= this.points.length;
    }

    public PathPoint getLastNode() {
        if (this.pathLength > 0) {
            return this.points[this.pathLength - 1];
        }
        return null;
    }

    public Vec3D getPosition(Entity target) {
        double d = (double)this.points[this.pathIndex].xCoord + (double)((int)(target.width + 1.0f)) * 0.5;
        double d2 = this.points[this.pathIndex].yCoord;
        double d3 = (double)this.points[this.pathIndex].zCoord + (double)((int)(target.width + 1.0f)) * 0.5;
        return Vec3D.createVector(d, d2, d3);
    }
}

