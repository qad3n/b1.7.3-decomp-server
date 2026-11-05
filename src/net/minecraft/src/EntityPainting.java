// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.src;

import java.util.List;
import java.util.ArrayList;

public class EntityPainting extends Entity
{
    private int ticksSinceValidation;
    public int direction;
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public EnumArt art;
    
    public EntityPainting(final World world) {
        super(world);
        this.ticksSinceValidation = 0;
        this.direction = 0;
        this.yOffset = 0.0f;
        this.setSize(0.5f, 0.5f);
    }
    
    public EntityPainting(final World world, final int x, final int y, final int z, final int dir) {
        this(world);
        this.xPosition = x;
        this.yPosition = y;
        this.zPosition = z;
        final ArrayList list = new ArrayList();
        for (final EnumArt art : EnumArt.values()) {
            this.art = art;
            this.setDirection(dir);
            if (this.onValidSurface()) {
                list.add(art);
            }
        }
        if (list.size() > 0) {
            this.art = (EnumArt)list.get(this.rand.nextInt(list.size()));
        }
        this.setDirection(dir);
    }
    
    @Override
    protected void entityInit() {
    }
    
    public void setDirection(final int dir) {
        this.direction = dir;
        final float n = (float)(dir * 90);
        this.rotationYaw = n;
        this.prevRotationYaw = n;
        float n2 = (float)this.art.sizeX;
        final float n3 = (float)this.art.sizeY;
        float n4 = (float)this.art.sizeX;
        if (dir == 0 || dir == 2) {
            n4 = 0.5f;
        }
        else {
            n2 = 0.5f;
        }
        final float n5 = n2 / 32.0f;
        final float n6 = n3 / 32.0f;
        final float n7 = n4 / 32.0f;
        float n8 = this.xPosition + 0.5f;
        final float n9 = this.yPosition + 0.5f;
        float n10 = this.zPosition + 0.5f;
        final float n11 = 0.5625f;
        if (dir == 0) {
            n10 -= n11;
        }
        if (dir == 1) {
            n8 -= n11;
        }
        if (dir == 2) {
            n10 += n11;
        }
        if (dir == 3) {
            n8 += n11;
        }
        if (dir == 0) {
            n8 -= this.getPositionOffset(this.art.sizeX);
        }
        if (dir == 1) {
            n10 += this.getPositionOffset(this.art.sizeX);
        }
        if (dir == 2) {
            n8 += this.getPositionOffset(this.art.sizeX);
        }
        if (dir == 3) {
            n10 -= this.getPositionOffset(this.art.sizeX);
        }
        final float n12 = n9 + this.getPositionOffset(this.art.sizeY);
        this.setPosition(n8, n12, n10);
        final float n13 = -0.00625f;
        this.boundingBox.setBounds(n8 - n5 - n13, n12 - n6 - n13, n10 - n7 - n13, n8 + n5 + n13, n12 + n6 + n13, n10 + n7 + n13);
    }
    
    private float getPositionOffset(final int length) {
        if (length == 32) {
            return 0.5f;
        }
        if (length == 64) {
            return 0.5f;
        }
        return 0.0f;
    }
    
    @Override
    public void onUpdate() {
        if (this.ticksSinceValidation++ == 100 && !this.worldObj.singleplayerWorld) {
            this.ticksSinceValidation = 0;
            if (!this.onValidSurface()) {
                this.setEntityDead();
                this.worldObj.entityJoinedWorld(new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(Item.painting)));
            }
        }
    }
    
    public boolean onValidSurface() {
        if (this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).size() > 0) {
            return false;
        }
        final int n = this.art.sizeX / 16;
        final int n2 = this.art.sizeY / 16;
        int n3 = this.xPosition;
        final int yPosition = this.yPosition;
        int n4 = this.zPosition;
        if (this.direction == 0) {
            n3 = MathHelper.floor_double(this.posX - this.art.sizeX / 32.0f);
        }
        if (this.direction == 1) {
            n4 = MathHelper.floor_double(this.posZ - this.art.sizeX / 32.0f);
        }
        if (this.direction == 2) {
            n3 = MathHelper.floor_double(this.posX - this.art.sizeX / 32.0f);
        }
        if (this.direction == 3) {
            n4 = MathHelper.floor_double(this.posZ - this.art.sizeX / 32.0f);
        }
        final int floor_double = MathHelper.floor_double(this.posY - this.art.sizeY / 32.0f);
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n2; ++j) {
                Material material;
                if (this.direction == 0 || this.direction == 2) {
                    material = this.worldObj.getBlockMaterial(n3 + i, floor_double + j, this.zPosition);
                }
                else {
                    material = this.worldObj.getBlockMaterial(this.xPosition, floor_double + j, n4 + i);
                }
                if (!material.isSolid()) {
                    return false;
                }
            }
        }
        final List<Entity> entitiesWithinAABBExcludingEntity = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox);
        for (int k = 0; k < entitiesWithinAABBExcludingEntity.size(); ++k) {
            if (entitiesWithinAABBExcludingEntity.get(k) instanceof EntityPainting) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean canBeCollidedWith() {
        return true;
    }
    
    @Override
    public boolean attackEntityFrom(final Entity source, final int amount) {
        if (!this.isDead && !this.worldObj.singleplayerWorld) {
            this.setEntityDead();
            this.setBeenAttacked();
            this.worldObj.entityJoinedWorld(new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(Item.painting)));
        }
        return true;
    }
    
    public void writeEntityToNBT(final NBTTagCompound nbt) {
        nbt.setByte("Dir", (byte)this.direction);
        nbt.setString("Motive", this.art.title);
        nbt.setInteger("TileX", this.xPosition);
        nbt.setInteger("TileY", this.yPosition);
        nbt.setInteger("TileZ", this.zPosition);
    }
    
    public void readEntityFromNBT(final NBTTagCompound nbt) {
        this.direction = nbt.getByte("Dir");
        this.xPosition = nbt.getInteger("TileX");
        this.yPosition = nbt.getInteger("TileY");
        this.zPosition = nbt.getInteger("TileZ");
        final String string = nbt.getString("Motive");
        for (final EnumArt art : EnumArt.values()) {
            if (art.title.equals(string)) {
                this.art = art;
            }
        }
        if (this.art == null) {
            this.art = EnumArt.Kebab;
        }
        this.setDirection(this.direction);
    }
    
    @Override
    public void moveEntity(final double dx, final double dy, final double dz) {
        if (!this.worldObj.singleplayerWorld && dx * dx + dy * dy + dz * dz > 0.0) {
            this.setEntityDead();
            this.worldObj.entityJoinedWorld(new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(Item.painting)));
        }
    }
    
    @Override
    public void addVelocity(final double dx, final double dy, final double dz) {
        if (!this.worldObj.singleplayerWorld && dx * dx + dy * dy + dz * dz > 0.0) {
            this.setEntityDead();
            this.worldObj.entityJoinedWorld(new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(Item.painting)));
        }
    }
    
    public enum EnumArt
    {
        Kebab("Kebab", 16, 16, 0, 0), 
        Aztec("Aztec", 16, 16, 16, 0), 
        Alban("Alban", 16, 16, 32, 0), 
        Aztec2("Aztec2", 16, 16, 48, 0), 
        Bomb("Bomb", 16, 16, 64, 0), 
        Plant("Plant", 16, 16, 80, 0), 
        Wasteland("Wasteland", 16, 16, 96, 0), 
        Pool("Pool", 32, 16, 0, 32), 
        Courbet("Courbet", 32, 16, 32, 32), 
        Sea("Sea", 32, 16, 64, 32), 
        Sunset("Sunset", 32, 16, 96, 32), 
        Creebet("Creebet", 32, 16, 128, 32), 
        Wanderer("Wanderer", 16, 32, 0, 64), 
        Graham("Graham", 16, 32, 16, 64), 
        Match("Match", 32, 32, 0, 128), 
        Bust("Bust", 32, 32, 32, 128), 
        Stage("Stage", 32, 32, 64, 128), 
        Void("Void", 32, 32, 96, 128), 
        SkullAndRoses("SkullAndRoses", 32, 32, 128, 128), 
        Fighters("Fighters", 64, 32, 0, 96), 
        Pointer("Pointer", 64, 64, 0, 192), 
        Pigscene("Pigscene", 64, 64, 64, 192), 
        BurningSkull("BurningSkull", 64, 64, 128, 192), 
        Skeleton("Skeleton", 64, 48, 192, 64), 
        DonkeyKong("DonkeyKong", 64, 48, 192, 112);
        
        public static final int SKULL_AND_ROSES_LENGTH;
        public final String title;
        public final int sizeX;
        public final int sizeY;
        public final int offsetX;
        public final int offsetY;
        
        private EnumArt(final String name, final int width, final int height, final int u, final int v) {
            this.title = name;
            this.sizeX = width;
            this.sizeY = height;
            this.offsetX = u;
            this.offsetY = v;
        }
        
        static {
            SKULL_AND_ROSES_LENGTH = "SkullAndRoses".length();
        }
    }
}
