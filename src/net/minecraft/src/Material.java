package net.minecraft.src;


public class Material {
    public static final Material air = new MaterialTransparent(MapColor.AIR);
    public static final Material grass = new Material(MapColor.GRASS);
    public static final Material ground = new Material(MapColor.DIRT);
    public static final Material wood = new Material(MapColor.WOOD).setBurning();
    public static final Material rock = new Material(MapColor.STONE).setRequiresTool();
    public static final Material iron = new Material(MapColor.IRON).setRequiresTool();
    public static final Material water = new MaterialLiquid(MapColor.WATER).setNoPushMobility();
    public static final Material lava = new MaterialLiquid(MapColor.LAVA).setNoPushMobility();
    public static final Material leaves = new Material(MapColor.FOLIAGE).setBurning().setTranslucent().setNoPushMobility();
    public static final Material plants = new MaterialLogic(MapColor.FOLIAGE).setNoPushMobility();
    public static final Material sponge = new Material(MapColor.WEB);
    public static final Material cloth = new Material(MapColor.WEB).setBurning();
    public static final Material fire = new MaterialTransparent(MapColor.AIR).setNoPushMobility();
    public static final Material sand = new Material(MapColor.SAND);
    public static final Material circuits = new MaterialLogic(MapColor.AIR).setNoPushMobility();
    public static final Material glass = new Material(MapColor.AIR).setTranslucent();
    public static final Material tnt = new Material(MapColor.LAVA).setBurning().setTranslucent();
    public static final Material wug = new Material(MapColor.FOLIAGE).setNoPushMobility();
    public static final Material ice = new Material(MapColor.ICE).setTranslucent();
    public static final Material snow = new MaterialLogic(MapColor.WHITE).setReplaceable().setTranslucent().setRequiresTool().setNoPushMobility();
    public static final Material builtSnow = new Material(MapColor.WHITE).setRequiresTool();
    public static final Material cactus = new Material(MapColor.FOLIAGE).setTranslucent().setNoPushMobility();
    public static final Material clay = new Material(MapColor.CLAY);
    public static final Material pumpkin = new Material(MapColor.FOLIAGE).setNoPushMobility();
    public static final Material portal = new MaterialPortal(MapColor.AIR).setImmovableMobility();
    public static final Material cakeMaterial = new Material(MapColor.AIR).setNoPushMobility();
    public static final Material web = new Material(MapColor.WEB).setRequiresTool().setNoPushMobility();
    public static final Material piston = new Material(MapColor.STONE).setImmovableMobility();
    private boolean canBurn;
    private boolean replaceable;
    private boolean isTranslucent;
    public final MapColor color;
    private boolean toolNotRequired = true;
    private int mobilityFlag;

    public Material(MapColor color) {
        this.color = color;
    }

    public boolean getIsLiquid() {
        return false;
    }

    public boolean isSolid() {
        return true;
    }

    public boolean getCanBlockGrass() {
        return true;
    }

    public boolean getIsSolid() {
        return true;
    }

    private Material setTranslucent() {
        this.isTranslucent = true;
        return this;
    }

    private Material setRequiresTool() {
        this.toolNotRequired = false;
        return this;
    }

    private Material setBurning() {
        this.canBurn = true;
        return this;
    }

    public boolean getBurning() {
        return this.canBurn;
    }

    public Material setReplaceable() {
        this.replaceable = true;
        return this;
    }

    public boolean isReplaceable() {
        return this.replaceable;
    }

    public boolean getIsOpaque() {
        if (this.isTranslucent) {
            return false;
        }
        return this.getIsSolid();
    }

    public boolean isToolNotRequired() {
        return this.toolNotRequired;
    }

    public int getMaterialMobility() {
        return this.mobilityFlag;
    }

    protected Material setNoPushMobility() {
        this.mobilityFlag = 1;
        return this;
    }

    protected Material setImmovableMobility() {
        this.mobilityFlag = 2;
        return this;
    }
}

