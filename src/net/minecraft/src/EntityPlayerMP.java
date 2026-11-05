package net.minecraft.src;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import net.minecraft.server.MinecraftServer;

public class EntityPlayerMP extends EntityPlayer implements ICrafting {
    public NetServerHandler playerNetServerHandler;
    public MinecraftServer mcServer;
    public ItemInWorldManager itemInWorldManager;
    public double trackedX;
    public double trackedZ;
    public List<ChunkCoordIntPair> loadedChunks = new LinkedList<>();
    public Set<ChunkCoordIntPair> allLoadedChunks = new HashSet<>();
    private int lastHealth = -99999999;
    private int ticksOfInvuln = 60;
    private ItemStack[] playerInventory = new ItemStack[]{null, null, null, null, null};
    private int currentWindowId = 0;
    public boolean isChangingQuantityOnly;

    public EntityPlayerMP(MinecraftServer server, World world, String name, ItemInWorldManager interactionManager) {
        super(world);
        interactionManager.thisPlayer = this;
        this.itemInWorldManager = interactionManager;
        ChunkCoordinates chunkcoordinates = world.getSpawnPoint();
        int i = chunkcoordinates.posX;
        int j = chunkcoordinates.posZ;
        int k = chunkcoordinates.posY;
        if (!world.worldProvider.noSky) {
            i += this.rand.nextInt(20) - 10;
            k = world.findTopSolidBlock(i, j);
            j += this.rand.nextInt(20) - 10;
        }

        this.setLocationAndAngles(i + 0.5, k, j + 0.5, 0.0F, 0.0F);
        this.mcServer = server;
        this.stepHeight = 0.0F;
        this.username = name;
        this.yOffset = 0.0F;
    }

    @Override
    public void setWorldHandler(World world) {
        super.setWorldHandler(world);
        this.itemInWorldManager = new ItemInWorldManager((WorldServer)world);
        this.itemInWorldManager.thisPlayer = this;
    }

    public void initMenu() {
        this.currentCraftingInventory.onCraftGuiOpened(this);
    }

    @Override
    public ItemStack[] getInventory() {
        return this.playerInventory;
    }

    @Override
    protected void resetHeight() {
        this.yOffset = 0.0F;
    }

    @Override
    public float getEyeHeight() {
        return 1.62F;
    }

    @Override
    public void onUpdate() {
        this.itemInWorldManager.tick();
        this.ticksOfInvuln--;
        this.currentCraftingInventory.updateCraftingMatrix();

        for (int i = 0; i < 5; i++) {
            ItemStack itemstack = this.getEquipmentInSlot(i);
            if (itemstack != this.playerInventory[i]) {
                this.mcServer.getEntityTracker(this.dimension).sendPacketToTrackedPlayers(this, new Packet5PlayerInventory(this.entityId, i, itemstack));
                this.playerInventory[i] = itemstack;
            }
        }
    }

    public ItemStack getEquipmentInSlot(int slot) {
        if (slot == 0) {
            return this.inventory.getCurrentItem();
        }
        return this.inventory.armorInventory[slot - 1];
    }

    @Override
    public void onDeath(Entity killer) {
        this.inventory.dropAllItems();
    }

    @Override
    public boolean attackEntityFrom(Entity source, int amount) {
        if (this.ticksOfInvuln > 0) {
            return false;
        }

        if (!this.mcServer.pvpOn) {
            if (source instanceof EntityPlayer) {
                return false;
            }

            if (source instanceof EntityArrow) {
                EntityArrow entityarrow = (EntityArrow)source;
                if (entityarrow.owner instanceof EntityPlayer) {
                    return false;
                }
            }
        }

        return super.attackEntityFrom(source, amount);
    }

    @Override
    protected boolean isPVPEnabled() {
        return this.mcServer.pvpOn;
    }

    @Override
    public void heal(int amount) {
        super.heal(amount);
    }

    public void onUpdateEntity(boolean syncChunks) {
        super.onUpdate();

        for (int i = 0; i < this.inventory.getSizeInventory(); i++) {
            ItemStack itemstack = this.inventory.getStackInSlot(i);
            if (itemstack != null && Item.itemsList[itemstack.itemID].isNetworkSynced() && this.playerNetServerHandler.getNumChunkDataPackets() <= 2) {
                Packet packet = ((ItemMapBase)Item.itemsList[itemstack.itemID]).getUpdatePacket(itemstack, this.worldObj, this);
                if (packet != null) {
                    this.playerNetServerHandler.sendPacket(packet);
                }
            }
        }

        if (syncChunks && !this.loadedChunks.isEmpty()) {
            ChunkCoordIntPair chunkcoordintpair = this.loadedChunks.get(0);
            if (chunkcoordintpair != null) {
                boolean flag = false;
                if (this.playerNetServerHandler.getNumChunkDataPackets() < 4) {
                    flag = true;
                }

                if (flag) {
                    WorldServer worldserver = this.mcServer.getWorldManager(this.dimension);
                    this.loadedChunks.remove(chunkcoordintpair);
                    this.playerNetServerHandler
                        .sendPacket(new Packet51MapChunk(chunkcoordintpair.chunkXPos * 16, 0, chunkcoordintpair.chunkZPos * 16, 16, 128, 16, worldserver));
                    List list = worldserver.getTileEntityList(
                        chunkcoordintpair.chunkXPos * 16,
                        0,
                        chunkcoordintpair.chunkZPos * 16,
                        chunkcoordintpair.chunkXPos * 16 + 16,
                        128,
                        chunkcoordintpair.chunkZPos * 16 + 16
                    );

                    for (int j = 0; j < list.size(); j++) {
                        this.getTileEntityInfo((TileEntity)list.get(j));
                    }
                }
            }
        }

        if (this.inPortal) {
            if (this.mcServer.propertyManagerObj.getBooleanProperty("allow-nether", true)) {
                if (this.currentCraftingInventory != this.personalCraftingInventory) {
                    this.usePersonalCraftingInventory();
                }

                if (this.ridingEntity != null) {
                    this.mountEntity(this.ridingEntity);
                } else {
                    this.timeInPortal += 0.0125F;
                    if (this.timeInPortal >= 1.0F) {
                        this.timeInPortal = 1.0F;
                        this.timeUntilPortal = 10;
                        this.mcServer.configManager.sendPlayerToOtherDimension(this);
                    }
                }

                this.inPortal = false;
            }
        } else {
            if (this.timeInPortal > 0.0F) {
                this.timeInPortal -= 0.05F;
            }

            if (this.timeInPortal < 0.0F) {
                this.timeInPortal = 0.0F;
            }
        }

        if (this.timeUntilPortal > 0) {
            this.timeUntilPortal--;
        }

        if (this.health != this.lastHealth) {
            this.playerNetServerHandler.sendPacket(new Packet8UpdateHealth(this.health));
            this.lastHealth = this.health;
        }
    }

    private void getTileEntityInfo(TileEntity blockEntity) {
        if (blockEntity != null) {
            Packet packet = blockEntity.getDescriptionPacket();
            if (packet != null) {
                this.playerNetServerHandler.sendPacket(packet);
            }
        }
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
    }

    @Override
    public void onItemPickup(Entity item, int count) {
        if (!item.isDead) {
            EntityTracker entitytracker = this.mcServer.getEntityTracker(this.dimension);
            if (item instanceof EntityItem) {
                entitytracker.sendPacketToTrackedPlayers(item, new Packet22Collect(item.entityId, this.entityId));
            }

            if (item instanceof EntityArrow) {
                entitytracker.sendPacketToTrackedPlayers(item, new Packet22Collect(item.entityId, this.entityId));
            }
        }

        super.onItemPickup(item, count);
        this.currentCraftingInventory.updateCraftingMatrix();
    }

    @Override
    public void swingItem() {
        if (!this.isSwinging) {
            this.swingProgressInt = -1;
            this.isSwinging = true;
            EntityTracker entitytracker = this.mcServer.getEntityTracker(this.dimension);
            entitytracker.sendPacketToTrackedPlayers(this, new Packet18Animation(this, 1));
        }
    }

    public void afterRespawn() {
    }

    @Override
    public EntityPlayer.EnumStatus goToSleep(int x, int y, int z) {
        EntityPlayer.EnumStatus entityplayer$enumstatus = super.goToSleep(x, y, z);
        if (entityplayer$enumstatus == EntityPlayer.EnumStatus.OK) {
            EntityTracker entitytracker = this.mcServer.getEntityTracker(this.dimension);
            Packet17Sleep packet17sleep = new Packet17Sleep(this, 0, x, y, z);
            entitytracker.sendPacketToTrackedPlayers(this, packet17sleep);
            this.playerNetServerHandler.teleportTo(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            this.playerNetServerHandler.sendPacket(packet17sleep);
        }

        return entityplayer$enumstatus;
    }

    @Override
    public void wakeUpPlayer(boolean resetSleepTimer, boolean updateAllPlayersSleeping, boolean setSpawnPoint) {
        if (this.isSleeping()) {
            EntityTracker entitytracker = this.mcServer.getEntityTracker(this.dimension);
            entitytracker.sendPacketToTrackedPlayersAndTrackedEntity(this, new Packet18Animation(this, 3));
        }

        super.wakeUpPlayer(resetSleepTimer, updateAllPlayersSleeping, setSpawnPoint);
        if (this.playerNetServerHandler != null) {
            this.playerNetServerHandler.teleportTo(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        }
    }

    @Override
    public void mountEntity(Entity entity) {
        super.mountEntity(entity);
        this.playerNetServerHandler.sendPacket(new Packet39AttachEntity(this, this.ridingEntity));
        this.playerNetServerHandler.teleportTo(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
    }

    @Override
    protected void updateFallState(double dy, boolean onGround) {
    }

    public void handleFalling(double distance, boolean onGround) {
        super.updateFallState(distance, onGround);
    }

    private void getNextWidowId() {
        this.currentWindowId = this.currentWindowId % 100 + 1;
    }

    @Override
    public void displayWorkbenchGUI(int x, int y, int z) {
        this.getNextWidowId();
        this.playerNetServerHandler.sendPacket(new Packet100OpenWindow(this.currentWindowId, 1, "Crafting", 9));
        this.currentCraftingInventory = new ContainerWorkbench(this.inventory, this.worldObj, x, y, z);
        this.currentCraftingInventory.windowId = this.currentWindowId;
        this.currentCraftingInventory.onCraftGuiOpened(this);
    }

    @Override
    public void displayGUIChest(IInventory inventory) {
        this.getNextWidowId();
        this.playerNetServerHandler.sendPacket(new Packet100OpenWindow(this.currentWindowId, 0, inventory.getInvName(), inventory.getSizeInventory()));
        this.currentCraftingInventory = new ContainerChest(this.inventory, inventory);
        this.currentCraftingInventory.windowId = this.currentWindowId;
        this.currentCraftingInventory.onCraftGuiOpened(this);
    }

    @Override
    public void displayGUIFurnace(TileEntityFurnace furnace) {
        this.getNextWidowId();
        this.playerNetServerHandler.sendPacket(new Packet100OpenWindow(this.currentWindowId, 2, furnace.getInvName(), furnace.getSizeInventory()));
        this.currentCraftingInventory = new ContainerFurnace(this.inventory, furnace);
        this.currentCraftingInventory.windowId = this.currentWindowId;
        this.currentCraftingInventory.onCraftGuiOpened(this);
    }

    @Override
    public void displayGUIDispenser(TileEntityDispenser dispenser) {
        this.getNextWidowId();
        this.playerNetServerHandler.sendPacket(new Packet100OpenWindow(this.currentWindowId, 3, dispenser.getInvName(), dispenser.getSizeInventory()));
        this.currentCraftingInventory = new ContainerDispenser(this.inventory, dispenser);
        this.currentCraftingInventory.windowId = this.currentWindowId;
        this.currentCraftingInventory.onCraftGuiOpened(this);
    }

    @Override
public void updateCraftingInventorySlot(Container menu, int slot, ItemStack item) {
    if (menu.getSlot(slot) instanceof SlotCrafting) {
        return;
    }
    if (this.isChangingQuantityOnly) {
        return;
    }
    this.playerNetServerHandler.sendPacket(new Packet103SetSlot(menu.windowId, slot, item));
}

    public void setMenu(Container menu) {
        this.updateCraftingInventory(menu, menu.getItems());
    }

    @Override
    public void updateCraftingInventory(Container menu, List<ItemStack> items) {
        this.playerNetServerHandler.sendPacket(new Packet104WindowItems(menu.windowId, items));
        this.playerNetServerHandler.sendPacket(new Packet103SetSlot(-1, -1, this.inventory.getItemStack()));
    }

    @Override
    public void updateCraftingInventoryInfo(Container menu, int id, int value) {
        this.playerNetServerHandler.sendPacket(new Packet105UpdateProgressbar(menu.windowId, id, value));
    }

    @Override
    public void onItemStackChanged(ItemStack item) {
    }

    @Override
    public void usePersonalCraftingInventory() {
        this.playerNetServerHandler.sendPacket(new Packet101CloseWindow(this.currentCraftingInventory.windowId));
        this.closeCraftingGui();
    }

    public void updateHeldItem() {
        if (this.isChangingQuantityOnly) {
            return;
        }
        this.playerNetServerHandler.sendPacket(new Packet103SetSlot(-1, -1, this.inventory.getItemStack()));
    }

    public void closeCraftingGui() {
        this.currentCraftingInventory.onCraftGuiClosed(this);
        this.currentCraftingInventory = this.personalCraftingInventory;
    }

    public void setMovementType(float sidewaysSpeed, float forwardSpeed, boolean jumping, boolean sneaking, float pitch, float yaw) {
        this.moveStrafing = sidewaysSpeed;
        this.moveForward = forwardSpeed;
        this.isJumping = jumping;
        this.setSneaking(sneaking);
        this.rotationPitch = pitch;
        this.rotationYaw = yaw;
    }

    @Override
public void addStat(StatBase stat, int amount) {
    if (stat == null) {
        return;
    }
    if (!stat.local) {
        while (amount > 100) {
            this.playerNetServerHandler.sendPacket(new Packet200Statistic(stat.statId, 100));
            amount -= 100;
        }
        this.playerNetServerHandler.sendPacket(new Packet200Statistic(stat.statId, amount));
    }
}

    public void onDisconnect() {
        if (this.ridingEntity != null) {
            this.mountEntity(this.ridingEntity);
        }

        if (this.riddenByEntity != null) {
            this.riddenByEntity.mountEntity(this);
        }

        if (this.sleeping) {
            this.wakeUpPlayer(true, false, false);
        }
    }

    public void markHealthDirty() {
        this.lastHealth = -99999999;
    }

    @Override
    public void addMessage(String message) {
        StringTranslate stringtranslate = StringTranslate.getInstance();
        String s = stringtranslate.translateKey(message);
        this.playerNetServerHandler.sendPacket(new Packet3Chat(s));
    }
}
