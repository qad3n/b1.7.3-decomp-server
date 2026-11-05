package net.minecraft.src;


public class BlockPumpkin
extends Block {
    private boolean blockType;

    protected BlockPumpkin(int id, int sprite, boolean lit) {
        super(id, Material.pumpkin);
        this.blockIndexInTexture = sprite;
        this.setTickOnLoad(true);
        this.blockType = lit;
    }

    public int getBlockTextureFromSideAndMetadata(int face, int metadata) {
        if (face == 1) {
            return this.blockIndexInTexture;
        }
        if (face == 0) {
            return this.blockIndexInTexture;
        }
        int n = this.blockIndexInTexture + 1 + 16;
        if (this.blockType) {
            ++n;
        }
        if (metadata == 2 && face == 2) {
            return n;
        }
        if (metadata == 3 && face == 5) {
            return n;
        }
        if (metadata == 0 && face == 3) {
            return n;
        }
        if (metadata == 1 && face == 4) {
            return n;
        }
        return this.blockIndexInTexture + 16;
    }

    public int getBlockTextureFromSide(int face) {
        if (face == 1) {
            return this.blockIndexInTexture;
        }
        if (face == 0) {
            return this.blockIndexInTexture;
        }
        if (face == 3) {
            return this.blockIndexInTexture + 1 + 16;
        }
        return this.blockIndexInTexture + 16;
    }

    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
    }

    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        int n = world.getBlockId(x, y, z);
        return (n == 0 || Block.blocksList[n].blockMaterial.isReplaceable()) && world.isBlockNormalCube(x, y - 1, z);
    }

    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entity) {
        int n = MathHelper.floor_double((double)(entity.rotationYaw * 4.0f / 360.0f) + 2.5) & 3;
        world.setBlockMetadataWithNotify(x, y, z, n);
    }
}

