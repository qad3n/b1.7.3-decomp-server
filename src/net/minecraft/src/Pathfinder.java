package net.minecraft.src;

public class Pathfinder {
    private IBlockAccess worldMap;
    private Path path = new Path();
    private MCHash<PathPoint> pointMap = new MCHash<>();
    private PathPoint[] pathOptions = new PathPoint[32];

    public Pathfinder(IBlockAccess world) {
        this.worldMap = world;
    }

    public PathEntity createEntityPathTo(Entity entity, Entity target, float range) {
        return this.createEntityPathTo(entity, target.posX, target.boundingBox.minY, target.posZ, range);
    }

    public PathEntity createEntityPathTo(Entity entity, int x, int y, int z, float range) {
        return this.createEntityPathTo(entity, x + 0.5F, y + 0.5F, z + 0.5F, range);
    }

    private PathEntity createEntityPathTo(Entity entity, double x, double y, double z, float range) {
        this.path.clearPath();
        this.pointMap.clearMap();
        PathPoint pathPoint = this.openPoint(MathHelper.floor_double(entity.boundingBox.minX), MathHelper.floor_double(entity.boundingBox.minY), MathHelper.floor_double(entity.boundingBox.minZ));
        PathPoint pathPoint2 = this.openPoint(MathHelper.floor_double(x - (double) (entity.width / 2.0f)), MathHelper.floor_double(y), MathHelper.floor_double(z - (double) (entity.width / 2.0f)));
        PathPoint pathPoint3 = new PathPoint(MathHelper.floor_float(entity.width + 1.0f), MathHelper.floor_float(entity.height + 1.0f), MathHelper.floor_float(entity.width + 1.0f));
        PathEntity pathEntity = this.addToPath(entity, pathPoint, pathPoint2, pathPoint3, range);
        return pathEntity;
    }

    private PathEntity addToPath(final Entity entity, final PathPoint start, final PathPoint target, final PathPoint bounds, final float range) {
        start.totalPathDistance = 0.0f;
        start.distanceToNext = start.distanceTo(target);
        start.distanceToTarget = start.distanceToNext;
        this.path.clearPath();
        this.path.addPoint(start);
        PathPoint target2 = start;
        while (!this.path.isPathEmpty()) {
            final PathPoint dequeue = this.path.dequeue();
            if (dequeue.equals(target)) {
                return this.createEntityPath(start, target);
            }
            if (dequeue.distanceTo(target) < target2.distanceTo(target)) {
                target2 = dequeue;
            }
            dequeue.isFirst = true;
            for (int pathOptions = this.findPathOptions(entity, dequeue, bounds, target, range), i = 0; i < pathOptions; ++i) {
                final PathPoint node = this.pathOptions[i];
                final float totalPathDistance = dequeue.totalPathDistance + dequeue.distanceTo(node);
                if (!node.isAssigned() || totalPathDistance < node.totalPathDistance) {
                    node.previous = dequeue;
                    node.totalPathDistance = totalPathDistance;
                    node.distanceToNext = node.distanceTo(target);
                    if (node.isAssigned()) {
                        this.path.changeDistance(node, node.totalPathDistance + node.distanceToNext);
                    } else {
                        node.distanceToTarget = node.totalPathDistance + node.distanceToNext;
                        this.path.addPoint(node);
                    }
                }
            }
        }
        if (target2 == start) {
            return null;
        }
        return this.createEntityPath(start, target2);
    }

    private int findPathOptions(Entity entity, PathPoint node, PathPoint bounds, PathPoint target, float range) {
        int i = 0;
        byte b0 = 0;
        if (this.getVerticalOffset(entity, node.xCoord, node.yCoord + 1, node.zCoord, bounds) == 1) {
            b0 = 1;
        }

        PathPoint pathpoint = this.getSafePoint(entity, node.xCoord, node.yCoord, node.zCoord + 1, bounds, b0);
        PathPoint pathpoint1 = this.getSafePoint(entity, node.xCoord - 1, node.yCoord, node.zCoord, bounds, b0);
        PathPoint pathpoint2 = this.getSafePoint(entity, node.xCoord + 1, node.yCoord, node.zCoord, bounds, b0);
        PathPoint pathpoint3 = this.getSafePoint(entity, node.xCoord, node.yCoord, node.zCoord - 1, bounds, b0);
        if (pathpoint != null && !pathpoint.isFirst && pathpoint.distanceTo(target) < range) {
            this.pathOptions[i++] = pathpoint;
        }

        if (pathpoint1 != null && !pathpoint1.isFirst && pathpoint1.distanceTo(target) < range) {
            this.pathOptions[i++] = pathpoint1;
        }

        if (pathpoint2 != null && !pathpoint2.isFirst && pathpoint2.distanceTo(target) < range) {
            this.pathOptions[i++] = pathpoint2;
        }

        if (pathpoint3 != null && !pathpoint3.isFirst && pathpoint3.distanceTo(target) < range) {
            this.pathOptions[i++] = pathpoint3;
        }

        return i;
    }

    private PathPoint getSafePoint(Entity entity, int x, int y, int z, PathPoint bounds, int neighborBlockingType) {
        PathPoint pathpoint = null;
        if (this.getVerticalOffset(entity, x, y, z, bounds) == 1) {
            pathpoint = this.openPoint(x, y, z);
        }

        if (pathpoint == null && neighborBlockingType > 0 && this.getVerticalOffset(entity, x, y + neighborBlockingType, z, bounds) == 1) {
            pathpoint = this.openPoint(x, y + neighborBlockingType, z);
            y += neighborBlockingType;
        }

        if (pathpoint != null) {
            int i = 0;
            int j = 0;

            while (y > 0 && (j = this.getVerticalOffset(entity, x, y - 1, z, bounds)) == 1) {
                if (++i >= 4) {
                    return null;
                }

                if (--y > 0) {
                    pathpoint = this.openPoint(x, y, z);
                }
            }

            if (j == -2) {
                return null;
            }
        }

        return pathpoint;
    }

    private final PathPoint openPoint(int x, int y, int z) {
        int i = PathPoint.hash(x, y, z);
        PathPoint pathpoint = this.pointMap.lookup(i);
        if (pathpoint == null) {
            pathpoint = new PathPoint(x, y, z);
            this.pointMap.addKey(i, pathpoint);
        }

        return pathpoint;
    }

    private int getVerticalOffset(Entity entity, int x, int y, int z, PathPoint bounds) {
        for (int i = x; i < x + bounds.xCoord; i++) {
            for (int j = y; j < y + bounds.yCoord; j++) {
                for (int k = z; k < z + bounds.zCoord; k++) {
                    int l = this.worldMap.getBlockId(i, j, k);
                    if (l > 0) {
                        if (l != Block.doorSteel.blockID && l != Block.doorWood.blockID) {
                            Material material = Block.blocksList[l].blockMaterial;
                            if (material.getIsSolid()) {
                                return 0;
                            }

                            if (material == Material.water) {
                                return -1;
                            }

                            if (material == Material.lava) {
                                return -2;
                            }
                        } else {
                            int i1 = this.worldMap.getBlockMetadata(i, j, k);
                            if (!BlockDoor.getOpen(i1)) {
                                return 0;
                            }
                        }
                    }
                }
            }
        }

        return 1;
    }

    private PathEntity createEntityPath(PathPoint start, PathPoint target) {
        int i = 1;

        for (PathPoint pathpoint = target; pathpoint.previous != null; pathpoint = pathpoint.previous) {
            i++;
        }

        PathPoint[] apathpoint = new PathPoint[i];
        PathPoint pathpoint1 = target;

        for (apathpoint[--i] = pathpoint1; pathpoint1.previous != null; apathpoint[--i] = pathpoint1) {
            pathpoint1 = pathpoint1.previous;
        }

        return new PathEntity(apathpoint);
    }
}
