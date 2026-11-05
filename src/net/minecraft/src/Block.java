package net.minecraft.src;

import java.util.ArrayList;
import java.util.Random;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Block {
    public static final StepSound soundPowderFootstep = new StepSound("stone", 1.0f, 1.0f);
    public static final StepSound soundWoodFootstep = new StepSound("wood", 1.0f, 1.0f);
    public static final StepSound soundGravelFootstep = new StepSound("gravel", 1.0f, 1.0f);
    public static final StepSound soundGrassFootstep = new StepSound("grass", 1.0f, 1.0f);
    public static final StepSound soundStoneFootstep = new StepSound("stone", 1.0f, 1.0f);
    public static final StepSound soundMetalFootstep = new StepSound("stone", 1.0f, 1.5f);
    public static final StepSound soundGlassFootstep = new StepSound("stone", 1.0f, 1.0f){};
    public static final StepSound soundClothFootstep = new StepSound("cloth", 1.0f, 1.0f);
    public static final StepSound soundSandFootstep = new StepSound("sand", 1.0f, 1.0f){};
    public static final Block[] blocksList = new Block[256];
    public static final boolean[] tickOnLoad = new boolean[256];
    public static final boolean[] opaqueCubeLookup = new boolean[256];
    public static final boolean[] isBlockContainer = new boolean[256];
    public static final int[] lightOpacity = new int[256];
    public static final boolean[] canBlockGrass = new boolean[256];
    public static final int[] lightValue = new int[256];
    public static final boolean[] requiresSelfNotify = new boolean[256];
    public static final Block stone = new BlockStone(1, 1).setHardness(1.5f).setResistance(10.0f).setStepSound(soundStoneFootstep).setBlockName("stone");
    public static final BlockGrass grass = (BlockGrass)new BlockGrass(2).setHardness(0.6f).setStepSound(soundGrassFootstep).setBlockName("grass");
    public static final Block dirt = new BlockDirt(3, 2).setHardness(0.5f).setStepSound(soundGravelFootstep).setBlockName("dirt");
    public static final Block cobblestone = new Block(4, 16, Material.rock).setHardness(2.0f).setResistance(10.0f).setStepSound(soundStoneFootstep).setBlockName("stonebrick");
    public static final Block planks = new Block(5, 4, Material.wood).setHardness(2.0f).setResistance(5.0f).setStepSound(soundWoodFootstep).setBlockName("wood").setRequiresSelfNotify();
    public static final Block sapling = new BlockSapling(6, 15).setHardness(0.0f).setStepSound(soundGrassFootstep).setBlockName("sapling").setRequiresSelfNotify();
    public static final Block bedrock = new Block(7, 17, Material.rock).setBlockUnbreakable().setResistance(6000000.0f).setStepSound(soundStoneFootstep).setBlockName("bedrock").disableStats();
    public static final Block waterMoving = new BlockFlowing(8, Material.water).setHardness(100.0f).setLightOpacity(3).setBlockName("water").disableStats().setRequiresSelfNotify();
    public static final Block waterStill = new BlockStationary(9, Material.water).setHardness(100.0f).setLightOpacity(3).setBlockName("water").disableStats().setRequiresSelfNotify();
    public static final Block lavaMoving = new BlockFlowing(10, Material.lava).setHardness(0.0f).setLightValue(1.0f).setLightOpacity(255).setBlockName("lava").disableStats().setRequiresSelfNotify();
    public static final Block lavaStill = new BlockStationary(11, Material.lava).setHardness(100.0f).setLightValue(1.0f).setLightOpacity(255).setBlockName("lava").disableStats().setRequiresSelfNotify();
    public static final Block sand = new BlockSand(12, 18).setHardness(0.5f).setStepSound(soundSandFootstep).setBlockName("sand");
    public static final Block gravel = new BlockGravel(13, 19).setHardness(0.6f).setStepSound(soundGravelFootstep).setBlockName("gravel");
    public static final Block oreGold = new BlockOre(14, 32).setHardness(3.0f).setResistance(5.0f).setStepSound(soundStoneFootstep).setBlockName("oreGold");
    public static final Block oreIron = new BlockOre(15, 33).setHardness(3.0f).setResistance(5.0f).setStepSound(soundStoneFootstep).setBlockName("oreIron");
    public static final Block oreCoal = new BlockOre(16, 34).setHardness(3.0f).setResistance(5.0f).setStepSound(soundStoneFootstep).setBlockName("oreCoal");
    public static final Block wood = new BlockLog(17).setHardness(2.0f).setStepSound(soundWoodFootstep).setBlockName("log").setRequiresSelfNotify();
    public static final BlockLeaves leaves = (BlockLeaves)new BlockLeaves(18, 52).setHardness(0.2f).setLightOpacity(1).setStepSound(soundGrassFootstep).setBlockName("leaves").disableStats().setRequiresSelfNotify();
    public static final Block sponge = new BlockSponge(19).setHardness(0.6f).setStepSound(soundGrassFootstep).setBlockName("sponge");
    public static final Block glass = new BlockGlass(20, 49, Material.glass, false).setHardness(0.3f).setStepSound(soundGlassFootstep).setBlockName("glass");
    public static final Block oreLapis = new BlockOre(21, 160).setHardness(3.0f).setResistance(5.0f).setStepSound(soundStoneFootstep).setBlockName("oreLapis");
    public static final Block blockLapis = new Block(22, 144, Material.rock).setHardness(3.0f).setResistance(5.0f).setStepSound(soundStoneFootstep).setBlockName("blockLapis");
    public static final Block dispenser = new BlockDispenser(23).setHardness(3.5f).setStepSound(soundStoneFootstep).setBlockName("dispenser").setRequiresSelfNotify();
    public static final Block sandStone = new BlockSandStone(24).setStepSound(soundStoneFootstep).setHardness(0.8f).setBlockName("sandStone");
    public static final Block musicBlock = new BlockNote(25).setHardness(0.8f).setBlockName("musicBlock").setRequiresSelfNotify();
    public static final Block bed = new BlockBed(26).setHardness(0.2f).setBlockName("bed").disableStats().setRequiresSelfNotify();
    public static final Block railPowered = new BlockRail(27, 179, true).setHardness(0.7f).setStepSound(soundMetalFootstep).setBlockName("goldenRail").setRequiresSelfNotify();
    public static final Block railDetector = new BlockDetectorRail(28, 195).setHardness(0.7f).setStepSound(soundMetalFootstep).setBlockName("detectorRail").setRequiresSelfNotify();
    public static final Block pistonStickyBase = new BlockPistonBase(29, 106, true).setBlockName("pistonStickyBase").setRequiresSelfNotify();
    public static final Block web = new BlockWeb(30, 11).setLightOpacity(1).setHardness(4.0f).setBlockName("web");
    public static final BlockTallGrass tallGrass = (BlockTallGrass)new BlockTallGrass(31, 39).setHardness(0.0f).setStepSound(soundGrassFootstep).setBlockName("tallgrass");
    public static final BlockDeadBush deadBush = (BlockDeadBush)new BlockDeadBush(32, 55).setHardness(0.0f).setStepSound(soundGrassFootstep).setBlockName("deadbush");
    public static final Block pistonBase = new BlockPistonBase(33, 107, false).setBlockName("pistonBase").setRequiresSelfNotify();
    public static final BlockPistonExtension pistonExtension = (BlockPistonExtension)new BlockPistonExtension(34, 107).setRequiresSelfNotify();
    public static final Block cloth = new BlockCloth().setHardness(0.8f).setStepSound(soundClothFootstep).setBlockName("cloth").setRequiresSelfNotify();
    public static final BlockPistonMoving pistonMoving = new BlockPistonMoving(36);
    public static final BlockFlower plantYellow = (BlockFlower)new BlockFlower(37, 13).setHardness(0.0f).setStepSound(soundGrassFootstep).setBlockName("flower");
    public static final BlockFlower plantRed = (BlockFlower)new BlockFlower(38, 12).setHardness(0.0f).setStepSound(soundGrassFootstep).setBlockName("rose");
    public static final BlockFlower mushroomBrown = (BlockFlower)new BlockMushroom(39, 29).setHardness(0.0f).setStepSound(soundGrassFootstep).setLightValue(0.125f).setBlockName("mushroom");
    public static final BlockFlower mushroomRed = (BlockFlower)new BlockMushroom(40, 28).setHardness(0.0f).setStepSound(soundGrassFootstep).setBlockName("mushroom");
    public static final Block blockGold = new BlockOreStorage(41, 23).setHardness(3.0f).setResistance(10.0f).setStepSound(soundMetalFootstep).setBlockName("blockGold");
    public static final Block blockSteel = new BlockOreStorage(42, 22).setHardness(5.0f).setResistance(10.0f).setStepSound(soundMetalFootstep).setBlockName("blockIron");
    public static final Block stairDouble = new BlockStep(43, true).setHardness(2.0f).setResistance(10.0f).setStepSound(soundStoneFootstep).setBlockName("stoneSlab");
    public static final Block stairSingle = new BlockStep(44, false).setHardness(2.0f).setResistance(10.0f).setStepSound(soundStoneFootstep).setBlockName("stoneSlab");
    public static final Block brick = new Block(45, 7, Material.rock).setHardness(2.0f).setResistance(10.0f).setStepSound(soundStoneFootstep).setBlockName("brick");
    public static final Block tnt = new BlockTNT(46, 8).setHardness(0.0f).setStepSound(soundGrassFootstep).setBlockName("tnt");
    public static final Block bookShelf = new BlockBookshelf(47, 35).setHardness(1.5f).setStepSound(soundWoodFootstep).setBlockName("bookshelf");
    public static final Block cobblestoneMossy = new Block(48, 36, Material.rock).setHardness(2.0f).setResistance(10.0f).setStepSound(soundStoneFootstep).setBlockName("stoneMoss");
    public static final Block obsidian = new BlockObsidian(49, 37).setHardness(10.0f).setResistance(2000.0f).setStepSound(soundStoneFootstep).setBlockName("obsidian");
    public static final Block torchWood = new BlockTorch(50, 80).setHardness(0.0f).setLightValue(0.9375f).setStepSound(soundWoodFootstep).setBlockName("torch").setRequiresSelfNotify();
    public static final BlockFire fire = (BlockFire)new BlockFire(51, 31).setHardness(0.0f).setLightValue(1.0f).setStepSound(soundWoodFootstep).setBlockName("fire").disableStats().setRequiresSelfNotify();
    public static final Block mobSpawner = new BlockMobSpawner(52, 65).setHardness(5.0f).setStepSound(soundMetalFootstep).setBlockName("mobSpawner").disableStats();
    public static final Block stairCompactPlanks = new BlockStairs(53, planks).setBlockName("stairsWood").setRequiresSelfNotify();
    public static final Block chest = new BlockChest(54).setHardness(2.5f).setStepSound(soundWoodFootstep).setBlockName("chest").setRequiresSelfNotify();
    public static final Block redstoneWire = new BlockRedstoneWire(55, 164).setHardness(0.0f).setStepSound(soundPowderFootstep).setBlockName("redstoneDust").disableStats().setRequiresSelfNotify();
    public static final Block oreDiamond = new BlockOre(56, 50).setHardness(3.0f).setResistance(5.0f).setStepSound(soundStoneFootstep).setBlockName("oreDiamond");
    public static final Block blockDiamond = new BlockOreStorage(57, 24).setHardness(5.0f).setResistance(10.0f).setStepSound(soundMetalFootstep).setBlockName("blockDiamond");
    public static final Block workbench = new BlockWorkbench(58).setHardness(2.5f).setStepSound(soundWoodFootstep).setBlockName("workbench");
    public static final Block crops = new BlockCrops(59, 88).setHardness(0.0f).setStepSound(soundGrassFootstep).setBlockName("crops").disableStats().setRequiresSelfNotify();
    public static final Block tilledField = new BlockFarmland(60).setHardness(0.6f).setStepSound(soundGravelFootstep).setBlockName("farmland");
    public static final Block stoneOvenIdle = new BlockFurnace(61, false).setHardness(3.5f).setStepSound(soundStoneFootstep).setBlockName("furnace").setRequiresSelfNotify();
    public static final Block stoneOvenActive = new BlockFurnace(62, true).setHardness(3.5f).setStepSound(soundStoneFootstep).setLightValue(0.875f).setBlockName("furnace").setRequiresSelfNotify();
    public static final Block signPost = new BlockSign(63, TileEntitySign.class, true).setHardness(1.0f).setStepSound(soundWoodFootstep).setBlockName("sign").disableStats().setRequiresSelfNotify();
    public static final Block doorWood = new BlockDoor(64, Material.wood).setHardness(3.0f).setStepSound(soundWoodFootstep).setBlockName("doorWood").disableStats().setRequiresSelfNotify();
    public static final Block ladder = new BlockLadder(65, 83).setHardness(0.4f).setStepSound(soundWoodFootstep).setBlockName("ladder").setRequiresSelfNotify();
    public static final Block minecartTrack = new BlockRail(66, 128, false).setHardness(0.7f).setStepSound(soundMetalFootstep).setBlockName("rail").setRequiresSelfNotify();
    public static final Block stairCompactCobblestone = new BlockStairs(67, cobblestone).setBlockName("stairsStone").setRequiresSelfNotify();
    public static final Block signWall = new BlockSign(68, TileEntitySign.class, false).setHardness(1.0f).setStepSound(soundWoodFootstep).setBlockName("sign").disableStats().setRequiresSelfNotify();
    public static final Block lever = new BlockLever(69, 96).setHardness(0.5f).setStepSound(soundWoodFootstep).setBlockName("lever").setRequiresSelfNotify();
    public static final Block pressurePlateStone = new BlockPressurePlate(70, Block.stone.blockIndexInTexture, BlockPressurePlate.EnumMobType.mobs, Material.rock).setHardness(0.5f).setStepSound(soundStoneFootstep).setBlockName("pressurePlate").setRequiresSelfNotify();
    public static final Block doorSteel = new BlockDoor(71, Material.iron).setHardness(5.0f).setStepSound(soundMetalFootstep).setBlockName("doorIron").disableStats().setRequiresSelfNotify();
    public static final Block pressurePlatePlanks = new BlockPressurePlate(72, Block.planks.blockIndexInTexture, BlockPressurePlate.EnumMobType.everything, Material.wood).setHardness(0.5f).setStepSound(soundWoodFootstep).setBlockName("pressurePlate").setRequiresSelfNotify();
    public static final Block oreRedstone = new BlockRedstoneOre(73, 51, false).setHardness(3.0f).setResistance(5.0f).setStepSound(soundStoneFootstep).setBlockName("oreRedstone").setRequiresSelfNotify();
    public static final Block oreRedstoneGlowing = new BlockRedstoneOre(74, 51, true).setLightValue(0.625f).setHardness(3.0f).setResistance(5.0f).setStepSound(soundStoneFootstep).setBlockName("oreRedstone").setRequiresSelfNotify();
    public static final Block torchRedstoneIdle = new BlockRedstoneTorch(75, 115, false).setHardness(0.0f).setStepSound(soundWoodFootstep).setBlockName("notGate").setRequiresSelfNotify();
    public static final Block torchRedstoneActive = new BlockRedstoneTorch(76, 99, true).setHardness(0.0f).setLightValue(0.5f).setStepSound(soundWoodFootstep).setBlockName("notGate").setRequiresSelfNotify();
    public static final Block button = new BlockButton(77, Block.stone.blockIndexInTexture).setHardness(0.5f).setStepSound(soundStoneFootstep).setBlockName("button").setRequiresSelfNotify();
    public static final Block snow = new BlockSnow(78, 66).setHardness(0.1f).setStepSound(soundClothFootstep).setBlockName("snow");
    public static final Block ice = new BlockIce(79, 67).setHardness(0.5f).setLightOpacity(3).setStepSound(soundGlassFootstep).setBlockName("ice");
    public static final Block blockSnow = new BlockSnowBlock(80, 66).setHardness(0.2f).setStepSound(soundClothFootstep).setBlockName("snow");
    public static final Block cactus = new BlockCactus(81, 70).setHardness(0.4f).setStepSound(soundClothFootstep).setBlockName("cactus");
    public static final Block blockClay = new BlockClay(82, 72).setHardness(0.6f).setStepSound(soundGravelFootstep).setBlockName("clay");
    public static final Block reed = new BlockReed(83, 73).setHardness(0.0f).setStepSound(soundGrassFootstep).setBlockName("reeds").disableStats();
    public static final Block jukebox = new BlockJukeBox(84, 74).setHardness(2.0f).setResistance(10.0f).setStepSound(soundStoneFootstep).setBlockName("jukebox").setRequiresSelfNotify();
    public static final Block fence = new BlockFence(85, 4).setHardness(2.0f).setResistance(5.0f).setStepSound(soundWoodFootstep).setBlockName("fence").setRequiresSelfNotify();
    public static final Block pumpkin = new BlockPumpkin(86, 102, false).setHardness(1.0f).setStepSound(soundWoodFootstep).setBlockName("pumpkin").setRequiresSelfNotify();
    public static final Block bloodStone = new BlockNetherrack(87, 103).setHardness(0.4f).setStepSound(soundStoneFootstep).setBlockName("hellrock");
    public static final Block slowSand = new BlockSoulSand(88, 104).setHardness(0.5f).setStepSound(soundSandFootstep).setBlockName("hellsand");
    public static final Block glowStone = new BlockGlowStone(89, 105, Material.rock).setHardness(0.3f).setStepSound(soundGlassFootstep).setLightValue(1.0f).setBlockName("lightgem");
    public static final BlockPortal portal = (BlockPortal)new BlockPortal(90, 14).setHardness(-1.0f).setStepSound(soundGlassFootstep).setLightValue(0.75f).setBlockName("portal");
    public static final Block pumpkinLantern = new BlockPumpkin(91, 102, true).setHardness(1.0f).setStepSound(soundWoodFootstep).setLightValue(1.0f).setBlockName("litpumpkin").setRequiresSelfNotify();
    public static final Block cake = new BlockCake(92, 121).setHardness(0.5f).setStepSound(soundClothFootstep).setBlockName("cake").disableStats().setRequiresSelfNotify();
    public static final Block redstoneRepeaterIdle = new BlockRedstoneRepeater(93, false).setHardness(0.0f).setStepSound(soundWoodFootstep).setBlockName("diode").disableStats().setRequiresSelfNotify();
    public static final Block redstoneRepeaterActive = new BlockRedstoneRepeater(94, true).setHardness(0.0f).setLightValue(0.625f).setStepSound(soundWoodFootstep).setBlockName("diode").disableStats().setRequiresSelfNotify();
    public static final Block lockedChest = new BlockLockedChest(95).setHardness(0.0f).setLightValue(1.0f).setStepSound(soundWoodFootstep).setBlockName("lockedchest").setTickOnLoad(true).setRequiresSelfNotify();
    public static final Block trapdoor = new BlockTrapDoor(96, Material.wood).setHardness(3.0f).setStepSound(soundWoodFootstep).setBlockName("trapdoor").disableStats().setRequiresSelfNotify();
    public int blockIndexInTexture;
    public final int blockID;
    protected float blockHardness;
    protected float blockResistance;
    protected boolean blockConstructorCalled = true;
    protected boolean enableStats = true;
    public double minX;
    public double minY;
    public double minZ;
    public double maxX;
    public double maxY;
    public double maxZ;
    public StepSound stepSound = soundPowderFootstep;
    public float blockParticleGravity = 1.0f;
    public final Material blockMaterial;
    public float slipperiness = 0.6f;
    private String blockName;

    protected Block(int id, Material material) {
        if (blocksList[id] != null) {
            throw new IllegalArgumentException("Slot " + id + " is already occupied by " + blocksList[id] + " when adding " + this);
        }
        this.blockMaterial = material;
        Block.blocksList[id] = this;
        this.blockID = id;
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        Block.opaqueCubeLookup[id] = this.isOpaqueCube();
        Block.lightOpacity[id] = this.isOpaqueCube() ? 255 : 0;
        Block.canBlockGrass[id] = !material.getCanBlockGrass();
        Block.isBlockContainer[id] = false;
    }

    protected Block setRequiresSelfNotify() {
        Block.requiresSelfNotify[this.blockID] = true;
        return this;
    }

    protected void setFireBurnRates() {
    }

    protected Block(int id, int sprite, Material material) {
        this(id, material);
        this.blockIndexInTexture = sprite;
    }

    protected Block setStepSound(StepSound sounds) {
        this.stepSound = sounds;
        return this;
    }

    protected Block setLightOpacity(int opacity) {
        Block.lightOpacity[this.blockID] = opacity;
        return this;
    }

    protected Block setLightValue(float light) {
        Block.lightValue[this.blockID] = (int)(15.0f * light);
        return this;
    }

    protected Block setResistance(float blastResistance) {
        this.blockResistance = blastResistance * 3.0f;
        return this;
    }

    public boolean isACube() {
        return true;
    }

    protected Block setHardness(float strength) {
        this.blockHardness = strength;
        if (this.blockResistance < strength * 5.0f) {
            this.blockResistance = strength * 5.0f;
        }
        return this;
    }

    protected Block setBlockUnbreakable() {
        this.setHardness(-1.0f);
        return this;
    }

    public float getHardness() {
        return this.blockHardness;
    }

    protected Block setTickOnLoad(boolean ticksRandomly) {
        Block.tickOnLoad[this.blockID] = ticksRandomly;
        return this;
    }

    public void setBlockBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int face) {
        return world.getBlockMaterial(x, y, z).isSolid();
    }

    public int getBlockTextureFromSideAndMetadata(int face, int metadata) {
        return this.getBlockTextureFromSide(face);
    }

    public int getBlockTextureFromSide(int face) {
        return this.blockIndexInTexture;
    }

    public void getCollidingBoundingBoxes(World world, int x, int y, int z, AxisAlignedBB shape, ArrayList<AxisAlignedBB> collisions) {
        AxisAlignedBB axisAlignedBB = this.getCollisionBoundingBoxFromPool(world, x, y, z);
        if (axisAlignedBB != null && shape.intersectsWith(axisAlignedBB)) {
            collisions.add(axisAlignedBB);
        }
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return AxisAlignedBB.getBoundingBoxFromPool((double)x + this.minX, (double)y + this.minY, (double)z + this.minZ, (double)x + this.maxX, (double)y + this.maxY, (double)z + this.maxZ);
    }

    public boolean isOpaqueCube() {
        return true;
    }

    public boolean canCollideCheck(int metadata, boolean allowLiquids) {
        return this.isCollidable();
    }

    public boolean isCollidable() {
        return true;
    }

    public void updateTick(World world, int x, int y, int z, Random random) {
    }

    public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int metadata) {
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlock) {
    }

    public int tickRate() {
        return 10;
    }

    public void onBlockAdded(World world, int x, int y, int z) {
    }

    public void onBlockRemoval(World world, int x, int y, int z) {
    }

    public int quantityDropped(Random random) {
        return 1;
    }

    public int idDropped(int metadata, Random random) {
        return this.blockID;
    }

    public float blockStrength(EntityPlayer player) {
        if (this.blockHardness < 0.0f) {
            return 0.0f;
        }
        if (!player.canHarvestBlock(this)) {
            return 1.0f / this.blockHardness / 100.0f;
        }
        return player.getCurrentPlayerStrVsBlock(this) / this.blockHardness / 30.0f;
    }

    public final void dropBlockAsItem(World world, int x, int y, int z, int metadata) {
        this.dropBlockAsItemWithChance(world, x, y, z, metadata, 1.0f);
    }

    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int metadata, float luck) {
        if (world.singleplayerWorld) {
            return;
        }
        int n = this.quantityDropped(world.rand);
        for (int i = 0; i < n; ++i) {
            int n2;
            if (world.rand.nextFloat() > luck || (n2 = this.idDropped(metadata, world.rand)) <= 0) continue;
            this.dropBlockAsItem_do(world, x, y, z, new ItemStack(n2, 1, this.damageDropped(metadata)));
        }
    }

    protected void dropBlockAsItem_do(World world, int x, int y, int z, ItemStack item) {
        if (world.singleplayerWorld) {
            return;
        }
        float f = 0.7f;
        double d = (double)(world.rand.nextFloat() * f) + (double)(1.0f - f) * 0.5;
        double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0f - f) * 0.5;
        double d3 = (double)(world.rand.nextFloat() * f) + (double)(1.0f - f) * 0.5;
        EntityItem entityItem = new EntityItem(world, (double)x + d, (double)y + d2, (double)z + d3, item);
        entityItem.delayBeforeCanPickup = 10;
        world.entityJoinedWorld(entityItem);
    }

    protected int damageDropped(int metadata) {
        return 0;
    }

    public float getExplosionResistance(Entity entity) {
        return this.blockResistance / 5.0f;
    }

    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3D from, Vec3D to) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        from = from.addVector(-x, -y, -z);
        to = to.addVector(-x, -y, -z);
        Vec3D vec3D = from.getIntermediateWithXValue(to, this.minX);
        Vec3D vec3D2 = from.getIntermediateWithXValue(to, this.maxX);
        Vec3D vec3D3 = from.getIntermediateWithYValue(to, this.minY);
        Vec3D vec3D4 = from.getIntermediateWithYValue(to, this.maxY);
        Vec3D vec3D5 = from.getIntermediateWithZValue(to, this.minZ);
        Vec3D vec3D6 = from.getIntermediateWithZValue(to, this.maxZ);
        if (!this.isVecInsideYZBounds(vec3D)) {
            vec3D = null;
        }
        if (!this.isVecInsideYZBounds(vec3D2)) {
            vec3D2 = null;
        }
        if (!this.isVecInsideXZBounds(vec3D3)) {
            vec3D3 = null;
        }
        if (!this.isVecInsideXZBounds(vec3D4)) {
            vec3D4 = null;
        }
        if (!this.isVecInsideXYBounds(vec3D5)) {
            vec3D5 = null;
        }
        if (!this.isVecInsideXYBounds(vec3D6)) {
            vec3D6 = null;
        }
        Vec3D vec3D7 = null;
        if (vec3D != null && (vec3D7 == null || from.distanceTo(vec3D) < from.distanceTo(vec3D7))) {
            vec3D7 = vec3D;
        }
        if (vec3D2 != null && (vec3D7 == null || from.distanceTo(vec3D2) < from.distanceTo(vec3D7))) {
            vec3D7 = vec3D2;
        }
        if (vec3D3 != null && (vec3D7 == null || from.distanceTo(vec3D3) < from.distanceTo(vec3D7))) {
            vec3D7 = vec3D3;
        }
        if (vec3D4 != null && (vec3D7 == null || from.distanceTo(vec3D4) < from.distanceTo(vec3D7))) {
            vec3D7 = vec3D4;
        }
        if (vec3D5 != null && (vec3D7 == null || from.distanceTo(vec3D5) < from.distanceTo(vec3D7))) {
            vec3D7 = vec3D5;
        }
        if (vec3D6 != null && (vec3D7 == null || from.distanceTo(vec3D6) < from.distanceTo(vec3D7))) {
            vec3D7 = vec3D6;
        }
        if (vec3D7 == null) {
            return null;
        }
        int n = -1;
        if (vec3D7 == vec3D) {
            n = 4;
        }
        if (vec3D7 == vec3D2) {
            n = 5;
        }
        if (vec3D7 == vec3D3) {
            n = 0;
        }
        if (vec3D7 == vec3D4) {
            n = 1;
        }
        if (vec3D7 == vec3D5) {
            n = 2;
        }
        if (vec3D7 == vec3D6) {
            n = 3;
        }
        return new MovingObjectPosition(x, y, z, n, vec3D7.addVector(x, y, z));
    }

    private boolean isVecInsideYZBounds(Vec3D pos) {
        if (pos == null) {
            return false;
        }
        return pos.yCoord >= this.minY && pos.yCoord <= this.maxY && pos.zCoord >= this.minZ && pos.zCoord <= this.maxZ;
    }

    private boolean isVecInsideXZBounds(Vec3D pos) {
        if (pos == null) {
            return false;
        }
        return pos.xCoord >= this.minX && pos.xCoord <= this.maxX && pos.zCoord >= this.minZ && pos.zCoord <= this.maxZ;
    }

    private boolean isVecInsideXYBounds(Vec3D pos) {
        if (pos == null) {
            return false;
        }
        return pos.xCoord >= this.minX && pos.xCoord <= this.maxX && pos.yCoord >= this.minY && pos.yCoord <= this.maxY;
    }

    public void onBlockDestroyedByExplosion(World world, int x, int y, int z) {
    }

    public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int face) {
        return this.canPlaceBlockAt(world, x, y, z);
    }

    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        int n = world.getBlockId(x, y, z);
        return n == 0 || Block.blocksList[n].blockMaterial.isReplaceable();
    }

    public boolean blockActivated(World world, int x, int y, int z, EntityPlayer player) {
        return false;
    }

    public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
    }

    public void onBlockPlaced(World world, int x, int y, int z, int face) {
    }

    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
    }

    public void velocityToAddToEntity(World world, int x, int y, int z, Entity entity, Vec3D velocity) {
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
    }

    public boolean isPoweringTo(IBlockAccess world, int x, int y, int z, int dir) {
        return false;
    }

    public boolean canProvidePower() {
        return false;
    }

    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
    }

    public boolean isIndirectlyPoweringTo(World world, int x, int y, int z, int dir) {
        return false;
    }

    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int metadata) {
        player.addStat(StatList.mineBlockStatArray[this.blockID], 1);
        this.dropBlockAsItem(world, x, y, z, metadata);
    }

    public boolean canBlockStay(World world, int x, int y, int z) {
        return true;
    }

    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entity) {
    }

    public Block setBlockName(String key) {
        this.blockName = "tile." + key;
        return this;
    }

    public String getNameLocalizedForStats() {
        return StatCollector.translateToLocal(this.getBlockName() + ".name");
    }

    public String getBlockName() {
        return this.blockName;
    }

    public void playBlock(World world, int x, int y, int z, int type, int data) {
    }

    public boolean getEnableStats() {
        return this.enableStats;
    }

    protected Block disableStats() {
        this.enableStats = false;
        return this;
    }

    public int getMobilityFlag() {
        return this.blockMaterial.getMaterialMobility();
    }

    static {
        Item.itemsList[Block.cloth.blockID] = new ItemCloth(Block.cloth.blockID - 256).setItemName("cloth");
        Item.itemsList[Block.wood.blockID] = new ItemLog(Block.wood.blockID - 256).setItemName("log");
        Item.itemsList[Block.stairSingle.blockID] = new ItemSlab(Block.stairSingle.blockID - 256).setItemName("stoneSlab");
        Item.itemsList[Block.sapling.blockID] = new ItemSapling(Block.sapling.blockID - 256).setItemName("sapling");
        Item.itemsList[Block.leaves.blockID] = new ItemLeaves(Block.leaves.blockID - 256).setItemName("leaves");
        Item.itemsList[Block.pistonBase.blockID] = new ItemPiston(Block.pistonBase.blockID - 256);
        Item.itemsList[Block.pistonStickyBase.blockID] = new ItemPiston(Block.pistonStickyBase.blockID - 256);
        for (int i = 0; i < 256; ++i) {
            if (blocksList[i] == null || Item.itemsList[i] != null) continue;
            Item.itemsList[i] = new ItemBlock(i - 256);
            blocksList[i].setFireBurnRates();
        }
        Block.canBlockGrass[0] = true;
        StatList.initBlockStats();
    }

    public static class StepSound {
        public final String key;
        public final float volume;
        public final float pitch;

        public StepSound(String key, float volume, float pitch) {
            this.key = key;
            this.volume = volume;
            this.pitch = pitch;
        }

        public float getVolume() {
            return this.volume;
        }

        public float getPitch() {
            return this.pitch;
        }

        public String getStepping() {
            return "step." + this.key;
        }
    }
}

