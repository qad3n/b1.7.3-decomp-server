package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

public class TileEntityPiston extends TileEntity {
    private int storedBlockID;
    private int storedMetadata;
    private int storedOrientation;
    private boolean isExtending;
    private boolean source;
    private float progress;
    private float lastProgress;
    private static List<Entity> movedEntities = new ArrayList<>();

    public TileEntityPiston() {
    }

    public TileEntityPiston(int movedBlock, int movedMetadata, int facing, boolean extending, boolean source) {
        this.storedBlockID = movedBlock;
        this.storedMetadata = movedMetadata;
        this.storedOrientation = facing;
        this.isExtending = extending;
        this.source = source;
    }

    public int getStoredBlockID() {
        return this.storedBlockID;
    }

    @Override
    public int getBlockMetadata() {
        return this.storedMetadata;
    }

    public boolean isExtending() {
        return this.isExtending;
    }

    public int getFacing() {
        return this.storedOrientation;
    }

    public float getProgress(float tickDelta) {
        if (tickDelta > 1.0F) {
            tickDelta = 1.0F;
        }

        return this.lastProgress + (this.progress - this.lastProgress) * tickDelta;
    }

    private void moveEntities(float progress, float amount) {
        if (!this.isExtending) {
            progress--;
        } else {
            progress = 1.0F - progress;
        }

        AxisAlignedBB axisalignedbb = Block.pistonMoving
            .getCollisionShape(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.storedBlockID, progress, this.storedOrientation);
        if (axisalignedbb != null) {
            List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(null, axisalignedbb);
            if (!list.isEmpty()) {
                movedEntities.addAll(list);

                for (Entity entity : movedEntities) {
                    entity.moveEntity(
                        amount * PistonBlockTextures.X_OFFSET[this.storedOrientation],
                        amount * PistonBlockTextures.Y_OFFSET[this.storedOrientation],
                        amount * PistonBlockTextures.Z_OFFSET[this.storedOrientation]
                    );
                }

                movedEntities.clear();
            }
        }
    }

    public void clearPistonTileEntity() {
        if (this.lastProgress < 1.0F) {
            this.lastProgress = this.progress = 1.0F;
            this.worldObj.removeBlockTileEntity(this.xCoord, this.yCoord, this.zCoord);
            this.invalidate();
            if (this.worldObj.getBlockId(this.xCoord, this.yCoord, this.zCoord) == Block.pistonMoving.blockID) {
                this.worldObj.setBlockAndMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, this.storedBlockID, this.storedMetadata);
            }
        }
    }

    public void updateEntity() {
        this.lastProgress = this.progress;
        if (this.lastProgress >= 1.0f) {
            this.moveEntities(1.0f, 0.25f);
            this.worldObj.removeBlockTileEntity(this.xCoord, this.yCoord, this.zCoord);
            this.invalidate();
            if (this.worldObj.getBlockId(this.xCoord, this.yCoord, this.zCoord) == Block.pistonMoving.blockID) {
                this.worldObj.setBlockAndMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, this.storedBlockID, this.storedMetadata);
            }
            return;
        }
        this.progress += 0.5f;
        if (this.progress >= 1.0f) {
            this.progress = 1.0f;
        }
        if (this.isExtending) {
            this.moveEntities(this.progress, this.progress - this.lastProgress + 0.0625f);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.storedBlockID = nbt.getInteger("blockId");
        this.storedMetadata = nbt.getInteger("blockData");
        this.storedOrientation = nbt.getInteger("facing");
        this.lastProgress = this.progress = nbt.getFloat("progress");
        this.isExtending = nbt.getBoolean("extending");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("blockId", this.storedBlockID);
        nbt.setInteger("blockData", this.storedMetadata);
        nbt.setInteger("facing", this.storedOrientation);
        nbt.setFloat("progress", this.lastProgress);
        nbt.setBoolean("extending", this.isExtending);
    }
}
