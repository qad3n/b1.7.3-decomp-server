package net.minecraft.src;


public abstract class NetHandler {
    public abstract boolean isServerHandler();

    public void handleMapChunk(Packet51MapChunk packet) {
    }

    public void registerPacket(Packet packet) {
    }

    public void handleErrorMessage(String reason, Object[] args) {
    }

    public void handleKickDisconnect(Packet255KickDisconnect packet) {
        this.registerPacket(packet);
    }

    public void handleLogin(Packet1Login packet) {
        this.registerPacket(packet);
    }

    public void handleFlying(Packet10Flying packet) {
        this.registerPacket(packet);
    }

    public void handleMultiBlockChange(Packet52MultiBlockChange packet) {
        this.registerPacket(packet);
    }

    public void handleBlockDig(Packet14BlockDig packet) {
        this.registerPacket(packet);
    }

    public void handleBlockChange(Packet53BlockChange packet) {
        this.registerPacket(packet);
    }

    public void handlePreChunk(Packet50PreChunk packet) {
        this.registerPacket(packet);
    }

    public void handleNamedEntitySpawn(Packet20NamedEntitySpawn packet) {
        this.registerPacket(packet);
    }

    public void handleEntity(Packet30Entity packet) {
        this.registerPacket(packet);
    }

    public void handleEntityTeleport(Packet34EntityTeleport packet) {
        this.registerPacket(packet);
    }

    public void handlePlace(Packet15Place packet) {
        this.registerPacket(packet);
    }

    public void handleBlockItemSwitch(Packet16BlockItemSwitch packet) {
        this.registerPacket(packet);
    }

    public void handleDestroyEntity(Packet29DestroyEntity packet) {
        this.registerPacket(packet);
    }

    public void handlePickupSpawn(Packet21PickupSpawn packet) {
        this.registerPacket(packet);
    }

    public void handleCollect(Packet22Collect packet) {
        this.registerPacket(packet);
    }

    public void handleChat(Packet3Chat packet) {
        this.registerPacket(packet);
    }

    public void handleVehicleSpawn(Packet23VehicleSpawn packet) {
        this.registerPacket(packet);
    }

    public void handleArmAnimation(Packet18Animation packet) {
        this.registerPacket(packet);
    }

    public void handlePlayerMovementAction(Packet19EntityAction packet) {
        this.registerPacket(packet);
    }

    public void handleHandshake(Packet2Handshake packet) {
        this.registerPacket(packet);
    }

    public void handleMobSpawn(Packet24MobSpawn packet) {
        this.registerPacket(packet);
    }

    public void handleUpdateTime(Packet4UpdateTime packet) {
        this.registerPacket(packet);
    }

    public void handleSpawnPosition(Packet6SpawnPosition packet) {
        this.registerPacket(packet);
    }

    public void handleEntityVelocity(Packet28EntityVelocity packet) {
        this.registerPacket(packet);
    }

    public void handleEntityData(Packet40EntityMetadata packet) {
        this.registerPacket(packet);
    }

    public void handleRideEntity(Packet39AttachEntity packet) {
        this.registerPacket(packet);
    }

    public void handleInteractEntity(Packet7UseEntity packet) {
        this.registerPacket(packet);
    }

    public void handleEntityEvent(Packet38EntityStatus packet) {
        this.registerPacket(packet);
    }

    public void handleHealth(Packet8UpdateHealth packet) {
        this.registerPacket(packet);
    }

    public void handleRespawnPacket(Packet9Respawn packet) {
        this.registerPacket(packet);
    }

    public void handleExplosion(Packet60Explosion packet) {
        this.registerPacket(packet);
    }

    public void handleOpenInventoryMenu(Packet100OpenWindow packet) {
        this.registerPacket(packet);
    }

    public void handleCraftingGuiClosedPacked(Packet101CloseWindow packet) {
        this.registerPacket(packet);
    }

    public void handleInventoryMenuClickSlot(Packet102WindowClick packet) {
        this.registerPacket(packet);
    }

    public void handleInventoryMenuSlotContent(Packet103SetSlot packet) {
        this.registerPacket(packet);
    }

    public void handleInventoryMenuContent(Packet104WindowItems packet) {
        this.registerPacket(packet);
    }

    public void handleUpdateSign(Packet130UpdateSign packet) {
        this.registerPacket(packet);
    }

    public void handleInventoryMenuData(Packet105UpdateProgressbar packet) {
        this.registerPacket(packet);
    }

    public void handlePlayerInventory(Packet5PlayerInventory packet) {
        this.registerPacket(packet);
    }

    public void handleInventoryMenuConfirm(Packet106Transaction packet) {
        this.registerPacket(packet);
    }

    public void handleAddPainting(Packet25EntityPainting packet) {
        this.registerPacket(packet);
    }

    public void handleBlockEvent(Packet54PlayNoteBlock packet) {
        this.registerPacket(packet);
    }

    public void handleStat(Packet200Statistic packet) {
        this.registerPacket(packet);
    }

    public void handlePlayerSleep(Packet17Sleep packet) {
        this.registerPacket(packet);
    }

    public void handleMovementTypePacket(Packet27Position packet) {
        this.registerPacket(packet);
    }

    public void handleGameEvent(Packet70Bed packet) {
        this.registerPacket(packet);
    }

    public void handleAddGlobalEntity(Packet71Weather packet) {
        this.registerPacket(packet);
    }

    public void handleMapData(Packet131MapData packet) {
        this.registerPacket(packet);
    }

    public void handleWorldEvent(Packet61DoorChange packet) {
        this.registerPacket(packet);
    }
}

