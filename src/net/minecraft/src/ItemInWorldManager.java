package net.minecraft.src;


public class ItemInWorldManager {
    private WorldServer thisWorld;
    public EntityPlayer thisPlayer;
    private float field_672_d = 0.0f;
    private int miningStartTime;
    private int targetX;
    private int targetY;
    private int targetZ;
    private int ticks;
    private boolean wasMiningBlock;
    private int prevTargetX;
    private int prevTargetY;
    private int prevTargetZ;
    private int prevMiningStartTime;

    public ItemInWorldManager(WorldServer world) {
        this.thisWorld = world;
    }

    public void tick() {
        ++this.ticks;
        if (this.wasMiningBlock) {
            int n = this.ticks - this.prevMiningStartTime;
            int n2 = this.thisWorld.getBlockId(this.prevTargetX, this.prevTargetY, this.prevTargetZ);
            if (n2 != 0) {
                Block block = Block.blocksList[n2];
                float f = block.blockStrength(this.thisPlayer) * (float)(n + 1);
                if (f >= 1.0f) {
                    this.wasMiningBlock = false;
                    this.tryMineBlock(this.prevTargetX, this.prevTargetY, this.prevTargetZ);
                }
            } else {
                this.wasMiningBlock = false;
            }
        }
    }

    public void startMiningBlock(int x, int y, int z, int face) {
        this.thisWorld.extinguishFire(null, x, y, z, face);
        this.miningStartTime = this.ticks;
        int n = this.thisWorld.getBlockId(x, y, z);
        if (n > 0) {
            Block.blocksList[n].onBlockClicked(this.thisWorld, x, y, z, this.thisPlayer);
        }
        if (n > 0 && Block.blocksList[n].blockStrength(this.thisPlayer) >= 1.0f) {
            this.tryMineBlock(x, y, z);
        } else {
            this.targetX = x;
            this.targetY = y;
            this.targetZ = z;
        }
    }

    public void finishMiningBlock(int x, int y, int z) {
        if (x == this.targetX && y == this.targetY && z == this.targetZ) {
            int n = this.ticks - this.miningStartTime;
            int n2 = this.thisWorld.getBlockId(x, y, z);
            if (n2 != 0) {
                Block block = Block.blocksList[n2];
                float f = block.blockStrength(this.thisPlayer) * (float)(n + 1);
                if (f >= 0.7f) {
                    this.tryMineBlock(x, y, z);
                } else if (!this.wasMiningBlock) {
                    this.wasMiningBlock = true;
                    this.prevTargetX = x;
                    this.prevTargetY = y;
                    this.prevTargetZ = z;
                    this.prevMiningStartTime = this.miningStartTime;
                }
            }
        }
        this.field_672_d = 0.0f;
    }

    public boolean removeBlock(int x, int y, int z) {
        Block block = Block.blocksList[this.thisWorld.getBlockId(x, y, z)];
        int n = this.thisWorld.getBlockMetadata(x, y, z);
        boolean bl = this.thisWorld.setBlockWithNotify(x, y, z, 0);
        if (block != null && bl) {
            block.onBlockDestroyedByPlayer(this.thisWorld, x, y, z, n);
        }
        return bl;
    }

    public boolean tryMineBlock(int x, int y, int z) {
        int n = this.thisWorld.getBlockId(x, y, z);
        int n2 = this.thisWorld.getBlockMetadata(x, y, z);
        this.thisWorld.doEvent(this.thisPlayer, 2001, x, y, z, n + this.thisWorld.getBlockMetadata(x, y, z) * 256);
        boolean bl = this.removeBlock(x, y, z);
        ItemStack itemStack = this.thisPlayer.getCurrentEquippedItem();
        if (itemStack != null) {
            itemStack.mineBlock(n, x, y, z, this.thisPlayer);
            if (itemStack.stackSize == 0) {
                itemStack.onRemoved(this.thisPlayer);
                this.thisPlayer.destroyCurrentEquippedItem();
            }
        }
        if (bl && this.thisPlayer.canHarvestBlock(Block.blocksList[n])) {
            Block.blocksList[n].harvestBlock(this.thisWorld, this.thisPlayer, x, y, z, n2);
            ((EntityPlayerMP)this.thisPlayer).playerNetServerHandler.sendPacket(new Packet53BlockChange(x, y, z, this.thisWorld));
        }
        return bl;
    }

    public boolean useItem(EntityPlayer player, World world, ItemStack itemInHand) {
        int n = itemInHand.stackSize;
        ItemStack itemStack = itemInHand.useItemRightClick(world, player);
        if (itemStack != itemInHand || itemStack != null && itemStack.stackSize != n) {
            player.inventory.mainInventory[player.inventory.currentItem] = itemStack;
            if (itemStack.stackSize == 0) {
                player.inventory.mainInventory[player.inventory.currentItem] = null;
            }
            return true;
        }
        return false;
    }

    public boolean activeBlockOrUseItem(EntityPlayer player, World world, ItemStack itemInHand, int x, int y, int z, int face) {
        int n = world.getBlockId(x, y, z);
        if (n > 0 && Block.blocksList[n].blockActivated(world, x, y, z, player)) {
            return true;
        }
        if (itemInHand == null) {
            return false;
        }
        return itemInHand.useItem(player, world, x, y, z, face);
    }
}

