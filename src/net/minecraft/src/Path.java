package net.minecraft.src;


public class Path {
    private PathPoint[] pathPoints = new PathPoint[1024];
    private int count = 0;

    public PathPoint addPoint(PathPoint node) {
        if (node.index >= 0) {
            throw new IllegalStateException("OW KNOWS!");
        }
        if (this.count == this.pathPoints.length) {
            PathPoint[] pathPointArray = new PathPoint[this.count << 1];
            System.arraycopy(this.pathPoints, 0, pathPointArray, 0, this.count);
            this.pathPoints = pathPointArray;
        }
        this.pathPoints[this.count] = node;
        node.index = this.count;
        this.sortBack(this.count++);
        return node;
    }

    public void clearPath() {
        this.count = 0;
    }

    public PathPoint dequeue() {
        PathPoint pathPoint = this.pathPoints[0];
        this.pathPoints[0] = this.pathPoints[--this.count];
        this.pathPoints[this.count] = null;
        if (this.count > 0) {
            this.sortForward(0);
        }
        pathPoint.index = -1;
        return pathPoint;
    }

    public void changeDistance(PathPoint node, float weight) {
        float f = node.distanceToTarget;
        node.distanceToTarget = weight;
        if (weight < f) {
            this.sortBack(node.index);
        } else {
            this.sortForward(node.index);
        }
    }

    private void sortBack(int index) {
        PathPoint pathPoint = this.pathPoints[index];
        float f = pathPoint.distanceToTarget;
        while (index > 0) {
            int n = index - 1 >> 1;
            PathPoint pathPoint2 = this.pathPoints[n];
            if (!(f < pathPoint2.distanceToTarget)) break;
            this.pathPoints[index] = pathPoint2;
            pathPoint2.index = index;
            index = n;
        }
        this.pathPoints[index] = pathPoint;
        pathPoint.index = index;
    }

    private void sortForward(int index) {
        PathPoint pathPoint = this.pathPoints[index];
        float f = pathPoint.distanceToTarget;
        while (true) {
            float f2;
            PathPoint pathPoint2;
            int n = 1 + (index << 1);
            int n2 = n + 1;
            if (n >= this.count) break;
            PathPoint pathPoint3 = this.pathPoints[n];
            float f3 = pathPoint3.distanceToTarget;
            if (n2 >= this.count) {
                pathPoint2 = null;
                f2 = Float.POSITIVE_INFINITY;
            } else {
                pathPoint2 = this.pathPoints[n2];
                f2 = pathPoint2.distanceToTarget;
            }
            if (f3 < f2) {
                if (!(f3 < f)) break;
                this.pathPoints[index] = pathPoint3;
                pathPoint3.index = index;
                index = n;
                continue;
            }
            if (!(f2 < f)) break;
            this.pathPoints[index] = pathPoint2;
            pathPoint2.index = index;
            index = n2;
        }
        this.pathPoints[index] = pathPoint;
        pathPoint.index = index;
    }

    public boolean isPathEmpty() {
        return this.count == 0;
    }
}

