package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import net.minecraft.server.MinecraftServer;
import java.util.List;

public class NetServerHandler extends NetHandler implements ICommandListener {
    public static Logger logger = Logger.getLogger("Minecraft");
    public NetworkManager netManager;
    public boolean connectionClosed = false;
    private MinecraftServer mcServer;
    private EntityPlayerMP playerEntity;
    private int ticks;
    private int lastKeepAliveTime;
    private int playerInAirTime;
    private boolean moved;
    private double lastPosX;
    private double lastPosY;
    private double lastPosZ;
    private boolean hasMoved = true;
    private Map<Integer, Short> transactions = new HashMap<>();

    public NetServerHandler(MinecraftServer server, NetworkManager connection, EntityPlayerMP player) {
        this.mcServer = server;
        this.netManager = connection;
        connection.setNetHandler(this);
        this.playerEntity = player;
        player.playerNetServerHandler = this;
    }

    public void handlePackets() {
        this.moved = false;
        this.netManager.processReadPackets();
        if (this.ticks - this.lastKeepAliveTime > 20) {
            this.sendPacket(new Packet0KeepAlive());
        }
    }

    public void kickPlayer(String reason) {
        this.playerEntity.onDisconnect();
        this.sendPacket(new Packet255KickDisconnect(reason));
        this.netManager.serverShutdown();
        this.mcServer.configManager.sendPacketToAllPlayers(new Packet3Chat("§e" + this.playerEntity.username + " left the game."));
        this.mcServer.configManager.playerLoggedOut(this.playerEntity);
        this.connectionClosed = true;
    }

    @Override
    public void handleMovementTypePacket(Packet27Position packet) {
        this.playerEntity
            .setMovementType(packet.getSidewaysSpeed(), packet.getForwardSpeed(), packet.getJumping(), packet.getSneaking(), packet.getPitch(), packet.getYaw());
    }

    @Override
    public void handleFlying(Packet10Flying packet) {
        WorldServer worldserver = this.mcServer.getWorldManager(this.playerEntity.dimension);
        this.moved = true;
        if (!this.hasMoved) {
            double d0 = packet.yPosition - this.lastPosY;
            if (packet.xPosition == this.lastPosX && d0 * d0 < 0.01 && packet.zPosition == this.lastPosZ) {
                this.hasMoved = true;
            }
        }

        if (this.hasMoved) {
            if (this.playerEntity.ridingEntity != null) {
                float f4 = this.playerEntity.rotationYaw;
                float f = this.playerEntity.rotationPitch;
                this.playerEntity.ridingEntity.updateRiderPosition();
                double d9 = this.playerEntity.posX;
                double d10 = this.playerEntity.posY;
                double d11 = this.playerEntity.posZ;
                double d12 = 0.0;
                double d14 = 0.0;
                if (packet.rotating) {
                    f4 = packet.yaw;
                    f = packet.pitch;
                }

                if (packet.moving && packet.yPosition == -999.0 && packet.stance == -999.0) {
                    d12 = packet.xPosition;
                    d14 = packet.zPosition;
                }

                this.playerEntity.onGround = packet.onGround;
                this.playerEntity.onUpdateEntity(true);
                this.playerEntity.moveEntity(d12, 0.0, d14);
                this.playerEntity.setPositionAndRotation(d9, d10, d11, f4, f);
                this.playerEntity.motionX = d12;
                this.playerEntity.motionZ = d14;
                if (this.playerEntity.ridingEntity != null) {
                    worldserver.tickVehicle(this.playerEntity.ridingEntity, true);
                }

                if (this.playerEntity.ridingEntity != null) {
                    this.playerEntity.ridingEntity.updateRiderPosition();
                }

                this.mcServer.configManager.move(this.playerEntity);
                this.lastPosX = this.playerEntity.posX;
                this.lastPosY = this.playerEntity.posY;
                this.lastPosZ = this.playerEntity.posZ;
                worldserver.updateEntity(this.playerEntity);
                return;
            }

            if (this.playerEntity.isSleeping()) {
                this.playerEntity.onUpdateEntity(true);
                this.playerEntity
                    .setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
                worldserver.updateEntity(this.playerEntity);
                return;
            }

            double d8 = this.playerEntity.posY;
            this.lastPosX = this.playerEntity.posX;
            this.lastPosY = this.playerEntity.posY;
            this.lastPosZ = this.playerEntity.posZ;
            double d1 = this.playerEntity.posX;
            double d2 = this.playerEntity.posY;
            double d3 = this.playerEntity.posZ;
            float f1 = this.playerEntity.rotationYaw;
            float f2 = this.playerEntity.rotationPitch;
            if (packet.moving && packet.yPosition == -999.0 && packet.stance == -999.0) {
                packet.moving = false;
            }

            if (packet.moving) {
                d1 = packet.xPosition;
                d2 = packet.yPosition;
                d3 = packet.zPosition;
                double d4 = packet.stance - packet.yPosition;
                if (!this.playerEntity.isSleeping() && (d4 > 1.65 || d4 < 0.1)) {
                    this.kickPlayer("Illegal stance");
                    logger.warning(this.playerEntity.username + " had an illegal stance: " + d4);
                    return;
                }

                if (Math.abs(packet.xPosition) > 3.2E7 || Math.abs(packet.zPosition) > 3.2E7) {
                    this.kickPlayer("Illegal position");
                    return;
                }
            }

            if (packet.rotating) {
                f1 = packet.yaw;
                f2 = packet.pitch;
            }

            this.playerEntity.onUpdateEntity(true);
            this.playerEntity.ySize = 0.0F;
            this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, f1, f2);
            if (!this.hasMoved) {
                return;
            }

            double d13 = d1 - this.playerEntity.posX;
            double d5 = d2 - this.playerEntity.posY;
            double d6 = d3 - this.playerEntity.posZ;
            double d7 = d13 * d13 + d5 * d5 + d6 * d6;
            if (d7 > 100.0) {
                logger.warning(this.playerEntity.username + " moved too quickly!");
                this.kickPlayer("You moved too quickly :( (Hacking?)");
                return;
            }

            float f3 = 0.0625F;
            boolean flag = worldserver.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.boundingBox.copy().getInsetBoundingBox(f3, f3, f3))
                    .size()
                == 0;
            this.playerEntity.moveEntity(d13, d5, d6);
            d13 = d1 - this.playerEntity.posX;
            d5 = d2 - this.playerEntity.posY;
            if (d5 > -0.5 || d5 < 0.5) {
                d5 = 0.0;
            }

            d6 = d3 - this.playerEntity.posZ;
            d7 = d13 * d13 + d5 * d5 + d6 * d6;
            boolean flag1 = false;
            if (d7 > 0.0625 && !this.playerEntity.isSleeping()) {
                flag1 = true;
                logger.warning(this.playerEntity.username + " moved wrongly!");
                System.out.println("Got position " + d1 + ", " + d2 + ", " + d3);
                System.out.println("Expected " + this.playerEntity.posX + ", " + this.playerEntity.posY + ", " + this.playerEntity.posZ);
            }

            this.playerEntity.setPositionAndRotation(d1, d2, d3, f1, f2);
            boolean flag2 = worldserver.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.boundingBox.copy().getInsetBoundingBox(f3, f3, f3))
                    .size()
                == 0;
            if (flag && (flag1 || !flag2) && !this.playerEntity.isSleeping()) {
                this.teleportTo(this.lastPosX, this.lastPosY, this.lastPosZ, f1, f2);
                return;
            }

            AxisAlignedBB axisalignedbb = this.playerEntity.boundingBox.copy().expand(f3, f3, f3).addCoord(0.0, -0.55, 0.0);
            if (this.mcServer.allowFlight || worldserver.containsNonAir(axisalignedbb)) {
                this.playerInAirTime = 0;
            } else if (d5 >= -0.03125) {
                this.playerInAirTime++;
                if (this.playerInAirTime > 80) {
                    logger.warning(this.playerEntity.username + " was kicked for floating too long!");
                    this.kickPlayer("Flying is not enabled on this server");
                    return;
                }
            }

            this.playerEntity.onGround = packet.onGround;
            this.mcServer.configManager.move(this.playerEntity);
            this.playerEntity.handleFalling(this.playerEntity.posY - d8, packet.onGround);
        }
    }

    public void teleportTo(double x, double y, double z, float yaw, float pitch) {
        this.hasMoved = false;
        this.lastPosX = x;
        this.lastPosY = y;
        this.lastPosZ = z;
        this.playerEntity.setPositionAndRotation(x, y, z, yaw, pitch);
        this.playerEntity.playerNetServerHandler.sendPacket(new Packet10Flying.Packet13PlayerLookMove(x, y + 1.62F, y, z, yaw, pitch, false));
    }

    @Override
    public void handleBlockDig(Packet14BlockDig packet) {
        WorldServer worldserver = this.mcServer.getWorldManager(this.playerEntity.dimension);
        if (packet.status == 4) {
            this.playerEntity.dropCurrentItem();
        } else {
            boolean flag = worldserver.bypassSpawnProtection = worldserver.worldProvider.worldType != 0
                || this.mcServer.configManager.isOp(this.playerEntity.username);
            boolean flag1 = false;
            if (packet.status == 0) {
                flag1 = true;
            }

            if (packet.status == 2) {
                flag1 = true;
            }

            int i = packet.xPosition;
            int j = packet.yPosition;
            int k = packet.zPosition;
            if (flag1) {
                double d0 = this.playerEntity.posX - (i + 0.5);
                double d1 = this.playerEntity.posY - (j + 0.5);
                double d3 = this.playerEntity.posZ - (k + 0.5);
                double d5 = d0 * d0 + d1 * d1 + d3 * d3;
                if (d5 > 36.0) {
                    return;
                }
            }

            ChunkCoordinates chunkcoordinates = worldserver.getSpawnPoint();
            int l = (int)MathHelper.abs(i - chunkcoordinates.posX);
            int i1 = (int)MathHelper.abs(k - chunkcoordinates.posZ);
            if (l > i1) {
                i1 = l;
            }

            if (packet.status == 0) {
                if (i1 <= 16 && !flag) {
                    this.playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(i, j, k, worldserver));
                } else {
                    this.playerEntity.itemInWorldManager.startMiningBlock(i, j, k, packet.face);
                }
            } else if (packet.status == 2) {
                this.playerEntity.itemInWorldManager.finishMiningBlock(i, j, k);
                if (worldserver.getBlockId(i, j, k) != 0) {
                    this.playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(i, j, k, worldserver));
                }
            } else if (packet.status == 3) {
                double d2 = this.playerEntity.posX - (i + 0.5);
                double d4 = this.playerEntity.posY - (j + 0.5);
                double d6 = this.playerEntity.posZ - (k + 0.5);
                double d7 = d2 * d2 + d4 * d4 + d6 * d6;
                if (d7 < 256.0) {
                    this.playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(i, j, k, worldserver));
                }
            }

            worldserver.bypassSpawnProtection = false;
        }
    }

    @Override
    public void handlePlace(Packet15Place packet) {
        WorldServer worldserver = this.mcServer.getWorldManager(this.playerEntity.dimension);
        ItemStack itemstack = this.playerEntity.inventory.getCurrentItem();
        boolean flag = worldserver.bypassSpawnProtection = worldserver.worldProvider.worldType != 0
            || this.mcServer.configManager.isOp(this.playerEntity.username);
        if (packet.direction == 255) {
            if (itemstack == null) {
                return;
            }

            this.playerEntity.itemInWorldManager.useItem(this.playerEntity, worldserver, itemstack);
        } else {
            int i = packet.xPosition;
            int j = packet.yPosition;
            int k = packet.zPosition;
            int l = packet.direction;
            ChunkCoordinates chunkcoordinates = worldserver.getSpawnPoint();
            int i1 = (int)MathHelper.abs(i - chunkcoordinates.posX);
            int j1 = (int)MathHelper.abs(k - chunkcoordinates.posZ);
            if (i1 > j1) {
                j1 = i1;
            }

            if (this.hasMoved && this.playerEntity.getDistanceSq(i + 0.5, j + 0.5, k + 0.5) < 64.0 && (j1 > 16 || flag)) {
                this.playerEntity.itemInWorldManager.activeBlockOrUseItem(this.playerEntity, worldserver, itemstack, i, j, k, l);
            }

            this.playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(i, j, k, worldserver));
            if (l == 0) {
                j--;
            }

            if (l == 1) {
                j++;
            }

            if (l == 2) {
                k--;
            }

            if (l == 3) {
                k++;
            }

            if (l == 4) {
                i--;
            }

            if (l == 5) {
                i++;
            }

            this.playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(i, j, k, worldserver));
        }

        itemstack = this.playerEntity.inventory.getCurrentItem();
        if (itemstack != null && itemstack.stackSize == 0) {
            this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = null;
        }

        this.playerEntity.isChangingQuantityOnly = true;
        this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = ItemStack.copyOf(
            this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem]
        );
        Slot slot = this.playerEntity.currentCraftingInventory.getSlot(this.playerEntity.inventory, this.playerEntity.inventory.currentItem);
        this.playerEntity.currentCraftingInventory.updateCraftingMatrix();
        this.playerEntity.isChangingQuantityOnly = false;
        if (!ItemStack.areItemStacksEqual(this.playerEntity.inventory.getCurrentItem(), packet.itemStack)) {
            this.sendPacket(new Packet103SetSlot(this.playerEntity.currentCraftingInventory.windowId, slot.id, this.playerEntity.inventory.getCurrentItem()));
        }

        worldserver.bypassSpawnProtection = false;
    }

    @Override
    public void handleErrorMessage(String reason, Object[] args) {
        logger.info(this.playerEntity.username + " lost connection: " + reason);
        this.mcServer.configManager.sendPacketToAllPlayers(new Packet3Chat("§e" + this.playerEntity.username + " left the game."));
        this.mcServer.configManager.playerLoggedOut(this.playerEntity);
        this.connectionClosed = true;
    }

    @Override
    public void registerPacket(Packet packet) {
        logger.warning(this.getClass() + " wasn't prepared to deal with a " + packet.getClass());
        this.kickPlayer("Protocol error, unexpected packet");
    }

    public void sendPacket(Packet packet) {
        this.netManager.addToSendQueue(packet);
        this.lastKeepAliveTime = this.ticks;
    }

    public void handleBlockItemSwitch(Packet16BlockItemSwitch packet) {
        if (packet.id < 0 || packet.id > InventoryPlayer.getHotbarSize()) {
            logger.warning(this.playerEntity.username + " tried to set an invalid carried item");
            return;
        }
        this.playerEntity.inventory.currentItem = packet.id;
    }

    @Override
public void handleChat(final Packet3Chat packet) {
    final String message = packet.message;
    if (message.length() > 100) {
        this.kickPlayer("Chat message too long");
        return;
    }
    final String trim = message.trim();
    for (int i = 0; i < trim.length(); ++i) {
        if (ChatAllowedCharacters.allowedCharacters.indexOf(trim.charAt(i)) < 0) {
            this.kickPlayer("Illegal characters in chat");
            return;
        }
    }
    if (trim.startsWith("/")) {
        this.handleSlashCommand(trim);
    } else {
        final String string = "<" + this.playerEntity.username + "> " + trim;
        NetServerHandler.logger.info(string);
        this.mcServer.configManager.sendPacketToAllPlayers(new Packet3Chat(string));
    }
}

    private void handleSlashCommand(String command) {
        if (command.toLowerCase().startsWith("/me ")) {
            command = "* " + this.playerEntity.username + " " + command.substring(command.indexOf(" ")).trim();
            logger.info(command);
            this.mcServer.configManager.sendPacketToAllPlayers(new Packet3Chat(command));
        } else if (command.toLowerCase().startsWith("/kill")) {
            this.playerEntity.attackEntityFrom(null, 1000);
        } else if (command.toLowerCase().startsWith("/tell ")) {
            String[] astring = command.split(" ");
            if (astring.length >= 3) {
                command = command.substring(command.indexOf(" ")).trim();
                command = command.substring(command.indexOf(" ")).trim();
                command = "§7" + this.playerEntity.username + " whispers " + command;
                logger.info(command + " to " + astring[1]);
                if (!this.mcServer.configManager.sendPacketToPlayer(astring[1], new Packet3Chat(command))) {
                    this.sendPacket(new Packet3Chat("§cThere's no player by that name online."));
                }
            }
        } else if (this.mcServer.configManager.isOp(this.playerEntity.username)) {
            String s = command.substring(1);
            logger.info(this.playerEntity.username + " issued server command: " + s);
            this.mcServer.addCommand(s, this);
        } else {
            String s1 = command.substring(1);
            logger.info(this.playerEntity.username + " tried command: " + s1);
        }
    }

    @Override
    public void handleArmAnimation(Packet18Animation packet) {
        if (packet.animate == 1) {
            this.playerEntity.swingItem();
        }
    }

    @Override
    public void handlePlayerMovementAction(Packet19EntityAction packet) {
        if (packet.state == 1) {
            this.playerEntity.setSneaking(true);
        } else if (packet.state == 2) {
            this.playerEntity.setSneaking(false);
        } else if (packet.state == 3) {
            this.playerEntity.wakeUpPlayer(false, true, true);
            this.hasMoved = false;
        }
    }

    @Override
    public void handleKickDisconnect(Packet255KickDisconnect packet) {
        this.netManager.networkShutdown("disconnect.quitting");
    }

    public int getNumChunkDataPackets() {
        return this.netManager.getNumChunkDataPackets();
    }

    @Override
    public void log(String message) {
        this.sendPacket(new Packet3Chat("§7" + message));
    }

    @Override
    public String getUsername() {
        return this.playerEntity.username;
    }

    @Override
    public void handleInteractEntity(Packet7UseEntity packet) {
        WorldServer worldserver = this.mcServer.getWorldManager(this.playerEntity.dimension);
        Entity entity = worldserver.getEntity(packet.targetEntity);
        if (entity != null && this.playerEntity.canEntityBeSeen(entity) && this.playerEntity.getDistanceSqToEntity(entity) < 36.0) {
            if (packet.isLeftClick == 0) {
                this.playerEntity.useCurrentItemOnEntity(entity);
            } else if (packet.isLeftClick == 1) {
                this.playerEntity.attackTargetEntityWithCurrentItem(entity);
            }
        }
    }

    public void handleRespawnPacket(Packet9Respawn packet) {
        if (this.playerEntity.health > 0) {
            return;
        }
        this.playerEntity = this.mcServer.configManager.recreatePlayerEntity(this.playerEntity, 0);
    }

    @Override
    public void handleCraftingGuiClosedPacked(Packet101CloseWindow packet) {
        this.playerEntity.closeCraftingGui();
    }

    @Override
    public void handleInventoryMenuClickSlot(Packet102WindowClick packet) {
        if (this.playerEntity.currentCraftingInventory.windowId == packet.window_Id
            && this.playerEntity.currentCraftingInventory.getCanCraft(this.playerEntity)) {
            ItemStack itemstack = this.playerEntity
                .currentCraftingInventory
                .onClickSlot(packet.inventorySlot, packet.mouseClick, packet.quickMove, this.playerEntity);
            if (ItemStack.areItemStacksEqual(packet.itemStack, itemstack)) {
                this.playerEntity.playerNetServerHandler.sendPacket(new Packet106Transaction(packet.window_Id, packet.action, true));
                this.playerEntity.isChangingQuantityOnly = true;
                this.playerEntity.currentCraftingInventory.updateCraftingMatrix();
                this.playerEntity.updateHeldItem();
                this.playerEntity.isChangingQuantityOnly = false;
            } else {
                this.transactions.put(this.playerEntity.currentCraftingInventory.windowId, packet.action);
                this.playerEntity.playerNetServerHandler.sendPacket(new Packet106Transaction(packet.window_Id, packet.action, false));
                this.playerEntity.currentCraftingInventory.setCanCraft(this.playerEntity, false);
                ArrayList arraylist = new ArrayList();

                for (int i = 0; i < this.playerEntity.currentCraftingInventory.inventorySlots.size(); i++) {
                    arraylist.add(this.playerEntity.currentCraftingInventory.inventorySlots.get(i).getStack());
                }

                this.playerEntity.updateCraftingInventory(this.playerEntity.currentCraftingInventory, arraylist);
            }
        }
    }

    @Override
    public void handleInventoryMenuConfirm(Packet106Transaction packet) {
        Short oshort = this.transactions.get(this.playerEntity.currentCraftingInventory.windowId);
        if (oshort != null
            && packet.shortWindowId == oshort
            && this.playerEntity.currentCraftingInventory.windowId == packet.windowId
            && !this.playerEntity.currentCraftingInventory.getCanCraft(this.playerEntity)) {
            this.playerEntity.currentCraftingInventory.setCanCraft(this.playerEntity, true);
        }
    }

    @Override
    public void handleUpdateSign(Packet130UpdateSign packet) {
        WorldServer worldserver = this.mcServer.getWorldManager(this.playerEntity.dimension);
        if (worldserver.blockExists(packet.xPosition, packet.yPosition, packet.zPosition)) {
            TileEntity tileentity = worldserver.getBlockTileEntity(packet.xPosition, packet.yPosition, packet.zPosition);
            if (tileentity instanceof TileEntitySign) {
                TileEntitySign tileentitysign = (TileEntitySign)tileentity;
                if (!tileentitysign.getIsEditAble()) {
                    this.mcServer.logWarning("Player " + this.playerEntity.username + " just tried to change non-editable sign");
                    return;
                }
            }

            for (int k = 0; k < 4; k++) {
                boolean flag = true;
                if (packet.signLines[k].length() > 15) {
                    flag = false;
                } else {
                    for (int i = 0; i < packet.signLines[k].length(); i++) {
                        if (ChatAllowedCharacters.allowedCharacters.indexOf(packet.signLines[k].charAt(i)) < 0) {
                            flag = false;
                        }
                    }
                }

                if (!flag) {
                    packet.signLines[k] = "!?";
                }
            }

            if (tileentity instanceof TileEntitySign) {
                int l = packet.xPosition;
                int i1 = packet.yPosition;
                int j1 = packet.zPosition;
                TileEntitySign tileentitysign1 = (TileEntitySign)tileentity;

                for (int j = 0; j < 4; j++) {
                    tileentitysign1.signText[j] = packet.signLines[j];
                }

                tileentitysign1.setEditable(false);
                tileentitysign1.onInventoryChanged();
                worldserver.markBlockNeedsUpdate(l, i1, j1);
            }
        }
    }

    @Override
    public boolean isServerHandler() {
        return true;
    }
}
