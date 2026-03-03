package cat.rezelyn.watheextended.api.kinswathe;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.World;

public final class ConfigHelper {

    private ConfigHelper() {
    }

    public static boolean isLoaded() {
        return FabricLoader.getInstance().isModLoaded("kinswathe");
    }

    private static Object getConfigInstance() throws Exception {
        Class<?> configClass = Class.forName("org.BsXinQin.kinswathe.KinsWatheConfig");
        Object handler = configClass.getField("HANDLER").get(null);
        return handler.getClass().getMethod("instance").invoke(handler);
    }

    private static void saveConfig() throws Exception {
        Class<?> configClass = Class.forName("org.BsXinQin.kinswathe.KinsWatheConfig");
        Object handler = configClass.getField("HANDLER").get(null);
        handler.getClass().getMethod("save").invoke(handler);
    }

    private static Object getWorldComponent(World world) throws Exception {
        Class<?> compClass = Class.forName("org.BsXinQin.kinswathe.component.ConfigWorldComponent");
        Object key = compClass.getField("KEY").get(null);
        return key.getClass().getMethod("get", Object.class).invoke(key, world);
    }

    private static boolean readBool(String fieldName, boolean defaultValue) {
        try {
            Object cfg = getConfigInstance();
            return (boolean) cfg.getClass().getField(fieldName).get(cfg);
        } catch (Throwable t) {
            return defaultValue;
        }
    }

    private static boolean readWorldBool(World world, String fieldName, boolean defaultValue) {
        if (world == null) return readBool(fieldName, defaultValue);
        try {
            Object comp = getWorldComponent(world);
            if (comp == null) return readBool(fieldName, defaultValue);
            return (boolean) comp.getClass().getField(fieldName).get(comp);
        } catch (Throwable t) {
            return readBool(fieldName, defaultValue);
        }
    }

    private static int readInt(String fieldName, int defaultValue) {
        try {
            Object cfg = getConfigInstance();
            return (int) cfg.getClass().getField(fieldName).get(cfg);
        } catch (Throwable t) {
            return defaultValue;
        }
    }

    private static int readWorldInt(World world, String fieldName, int defaultValue) {
        if (world == null) return readInt(fieldName, defaultValue);
        try {
            Object comp = getWorldComponent(world);
            if (comp == null) return readInt(fieldName, defaultValue);
            return (int) comp.getClass().getField(fieldName).get(comp);
        } catch (Throwable t) {
            return readInt(fieldName, defaultValue);
        }
    }

    private static void setWorldBool(World world, String fieldName, boolean value) throws Exception {
        Object comp = getWorldComponent(world);
        if (comp != null) {
            comp.getClass().getField(fieldName).set(comp, value);
            comp.getClass().getMethod("sync").invoke(comp);
        }
        Object cfg = getConfigInstance();
        cfg.getClass().getField(fieldName).set(cfg, value);
        saveConfig();
    }

    private static void setWorldInt(World world, String fieldName, int value) throws Exception {
        Object comp = getWorldComponent(world);
        if (comp != null) {
            comp.getClass().getField(fieldName).set(comp, value);
            comp.getClass().getMethod("sync").invoke(comp);
        }
        Object cfg = getConfigInstance();
        cfg.getClass().getField(fieldName).set(cfg, value);
        saveConfig();
    }

    public static int getStartingCooldown(World world) {
        return readWorldInt(world, "StartingCooldown", 30);
    }

    public static void setStartingCooldown(World world, int seconds) throws Exception {
        setWorldInt(world, "StartingCooldown", seconds);
    }

    public static boolean getEnableStaminaBar() {
        return readBool("EnableStaminaBar", false);
    }

    public static void setEnableStaminaBar(boolean value) {
        try {
            Object cfg = getConfigInstance();
            cfg.getClass().getField("EnableStaminaBar").set(cfg, value);
            saveConfig();
        } catch (Throwable ignored) {
        }
    }

    public static boolean getEnableJumpNotInGame(World world) {
        return readWorldBool(world, "EnableJumpNotInGame", false);
    }

    public static void setEnableJumpNotInGame(World world, boolean value) throws Exception {
        setWorldBool(world, "EnableJumpNotInGame", value);
    }

    public static boolean getEnableStartSafeTime(World world) {
        return readWorldBool(world, "EnableStartSafeTime", false);
    }

    public static void setEnableStartSafeTime(World world, boolean value) throws Exception {
        setWorldBool(world, "EnableStartSafeTime", value);
    }

    public static boolean getEnableNoellesRolesModify(World world) {
        return readWorldBool(world, "EnableNoellesRolesModify", false);
    }

    public static void setEnableNoellesRolesModify(World world, boolean value) throws Exception {
        setWorldBool(world, "EnableNoellesRolesModify", value);
    }

    public static boolean getEnableWatheModify(World world) {
        return readBool("EnableWatheModify", false);
    }

    public static void setEnableWatheModify(World world, boolean value) throws Exception {
        Object cfg = getConfigInstance();
        cfg.getClass().getField("EnableWatheModify").set(cfg, value);
        saveConfig();
    }

    public static int getInitialCivilianIncome(World world) {
        return readInt("InitialCivilianIncome", 0);
    }

    public static void setInitialCivilianIncome(World world, int value) throws Exception {
        Object cfg = getConfigInstance();
        cfg.getClass().getField("InitialCivilianIncome").set(cfg, value);
        saveConfig();
    }

    public static int getInitialNeutralIncome(World world) {
        return readInt("InitialNeutralIncome", 0);
    }

    public static void setInitialNeutralIncome(World world, int value) throws Exception {
        Object cfg = getConfigInstance();
        cfg.getClass().getField("InitialNeutralIncome").set(cfg, value);
        saveConfig();
    }

    public static int getInitialKillerIncome(World world) {
        return readInt("InitialKillerIncome", 100);
    }

    public static void setInitialKillerIncome(World world, int value) throws Exception {
        Object cfg = getConfigInstance();
        cfg.getClass().getField("InitialKillerIncome").set(cfg, value);
        saveConfig();
    }

    public static int getIncreaseMoneyWhenKill(World world) {
        return readInt("IncreaseMoneyWhenKill", 100);
    }

    public static void setIncreaseMoneyWhenKill(World world, int value) throws Exception {
        Object cfg = getConfigInstance();
        cfg.getClass().getField("IncreaseMoneyWhenKill").set(cfg, value);
        saveConfig();
    }

    public static boolean getPreventKillerDropRevolver(World world) {
        return readBool("PreventKillerDropRevolver", false);
    }

    public static void setPreventKillerDropRevolver(World world, boolean value) throws Exception {
        Object cfg = getConfigInstance();
        cfg.getClass().getField("PreventKillerDropRevolver").set(cfg, value);
        saveConfig();
    }

    public static int getBellringerAbilityPrice(World world) {
        return readWorldInt(world, "BellringerAbilityPrice", 200);
    }

    public static void setBellringerAbilityPrice(World world, int value) throws Exception {
        setWorldInt(world, "BellringerAbilityPrice", value);
    }

    public static int getBellringerAbilityCooldown(World world) {
        return readInt("BellringerAbilityCooldown", 120);
    }

    public static void setBellringerAbilityCooldown(World world, int value) throws Exception {
        Object cfg = getConfigInstance();
        cfg.getClass().getField("BellringerAbilityCooldown").set(cfg, value);
        saveConfig();
    }

    public static int getBodymakerAbilityCooldown(World world) {
        return readInt("BodymakerAbilityCooldown", 90);
    }

    public static void setBodymakerAbilityCooldown(World world, int value) throws Exception {
        Object cfg = getConfigInstance();
        cfg.getClass().getField("BodymakerAbilityCooldown").set(cfg, value);
        saveConfig();
    }

    public static boolean getBodymakerAbilityFakeRole(World world) {
        return readWorldBool(world, "BodymakerAbilityFakeRole", true);
    }

    public static void setBodymakerAbilityFakeRole(World world, boolean value) throws Exception {
        setWorldBool(world, "BodymakerAbilityFakeRole", value);
    }

    public static int getCleanerAbilityPrice(World world) {
        return readWorldInt(world, "CleanerAbilityPrice", 200);
    }

    public static void setCleanerAbilityPrice(World world, int value) throws Exception {
        setWorldInt(world, "CleanerAbilityPrice", value);
    }

    public static int getCleanerAbilityCooldown(World world) {
        return readInt("CleanerAbilityCooldown", 150);
    }

    public static void setCleanerAbilityCooldown(World world, int value) throws Exception {
        Object cfg = getConfigInstance();
        cfg.getClass().getField("CleanerAbilityCooldown").set(cfg, value);
        saveConfig();
    }

    public static int getCookPanPrice(World world) {
        return readWorldInt(world, "CookPanPrice", 250);
    }

    public static void setCookPanPrice(World world, int value) throws Exception {
        setWorldInt(world, "CookPanPrice", value);
    }

    public static int getDetectiveAbilityPrice(World world) {
        return readWorldInt(world, "DetectiveAbilityPrice", 200);
    }

    public static void setDetectiveAbilityPrice(World world, int value) throws Exception {
        setWorldInt(world, "DetectiveAbilityPrice", value);
    }

    public static int getDetectiveAbilityCooldown(World world) {
        return readInt("DetectiveAbilityCooldown", 90);
    }

    public static void setDetectiveAbilityCooldown(World world, int value) throws Exception {
        Object cfg = getConfigInstance();
        cfg.getClass().getField("DetectiveAbilityCooldown").set(cfg, value);
        saveConfig();
    }

    public static int getDreamerInitialItemQuantity(World world) {
        return readInt("DreamerInitialItemQuantity", 1);
    }

    public static void setDreamerInitialItemQuantity(World world, int value) throws Exception {
        Object cfg = getConfigInstance();
        cfg.getClass().getField("DreamerInitialItemQuantity").set(cfg, value);
        saveConfig();
    }

    public static int getDrugmakerPlayerLimit(World world) {
        return readInt("DrugmakerPlayerLimit", 10);
    }

    public static void setDrugmakerPlayerLimit(World world, int value) throws Exception {
        Object cfg = getConfigInstance();
        cfg.getClass().getField("DrugmakerPlayerLimit").set(cfg, value);
        saveConfig();
    }

    public static int getDrugmakerGetCoins(World world) {
        return readWorldInt(world, "DrugmakerGetCoins", 50);
    }

    public static void setDrugmakerGetCoins(World world, int value) throws Exception {
        setWorldInt(world, "DrugmakerGetCoins", value);
    }

    public static int getDrugmakerPoisonInjectorPrice(World world) {
        return readWorldInt(world, "DrugmakerPoisonInjectorPrice", 125);
    }

    public static void setDrugmakerPoisonInjectorPrice(World world, int value) throws Exception {
        setWorldInt(world, "DrugmakerPoisonInjectorPrice", value);
    }

    public static int getDrugmakerBlowgunPrice(World world) {
        return readWorldInt(world, "DrugmakerBlowgunPrice", 175);
    }

    public static void setDrugmakerBlowgunPrice(World world, int value) throws Exception {
        setWorldInt(world, "DrugmakerBlowgunPrice", value);
    }

    public static int getHunterAbilityPrice(World world) {
        return readWorldInt(world, "HunterAbilityPrice", 125);
    }

    public static void setHunterAbilityPrice(World world, int value) throws Exception {
        setWorldInt(world, "HunterAbilityPrice", value);
    }

    public static int getHunterAbilityCooldown(World world) {
        return readInt("HunterAbilityCooldown", 5);
    }

    public static void setHunterAbilityCooldown(World world, int value) throws Exception {
        Object cfg = getConfigInstance();
        cfg.getClass().getField("HunterAbilityCooldown").set(cfg, value);
        saveConfig();
    }

    public static int getJudgeAbilityPrice(World world) {
        return readWorldInt(world, "JudgeAbilityPrice", 300);
    }

    public static void setJudgeAbilityPrice(World world, int value) throws Exception {
        setWorldInt(world, "JudgeAbilityPrice", value);
    }

    public static int getJudgeAbilityGlowing(World world) {
        return readInt("JudgeAbilityGlowing", 90);
    }

    public static void setJudgeAbilityGlowing(World world, int value) throws Exception {
        Object cfg = getConfigInstance();
        cfg.getClass().getField("JudgeAbilityGlowing").set(cfg, value);
        saveConfig();
    }

    public static int getJudgeAbilityCooldown(World world) {
        return readInt("JudgeAbilityCooldown", 180);
    }

    public static void setJudgeAbilityCooldown(World world, int value) throws Exception {
        Object cfg = getConfigInstance();
        cfg.getClass().getField("JudgeAbilityCooldown").set(cfg, value);
        saveConfig();
    }

    public static int getKidnapperKnockoutDrugPrice(World world) {
        return readWorldInt(world, "KidnapperKnockoutDrugPrice", 75);
    }

    public static void setKidnapperKnockoutDrugPrice(World world, int value) throws Exception {
        setWorldInt(world, "KidnapperKnockoutDrugPrice", value);
    }

    public static int getLicensedVillainPlayerLimit(World world) {
        return readInt("LicensedVillainPlayerLimit", 10);
    }

    public static void setLicensedVillainPlayerLimit(World world, int value) throws Exception {
        Object cfg = getConfigInstance();
        cfg.getClass().getField("LicensedVillainPlayerLimit").set(cfg, value);
        saveConfig();
    }

    public static int getLicensedVillainRevolverPrice(World world) {
        return readWorldInt(world, "LicensedVillainRevolverPrice", 300);
    }

    public static void setLicensedVillainRevolverPrice(World world, int value) throws Exception {
        setWorldInt(world, "LicensedVillainRevolverPrice", value);
    }

    public static int getPhysicianPillPrice(World world) {
        return readWorldInt(world, "PhysicianPillPrice", 300);
    }

    public static void setPhysicianPillPrice(World world, int value) throws Exception {
        setWorldInt(world, "PhysicianPillPrice", value);
    }

    public static int getRobotAbilityDuration(World world) {
        return readInt("RobotAbilityDuration", 10);
    }

    public static void setRobotAbilityDuration(World world, int value) throws Exception {
        Object cfg = getConfigInstance();
        cfg.getClass().getField("RobotAbilityDuration").set(cfg, value);
        saveConfig();
    }

    public static int getRobotAbilityCooldown(World world) {
        return readInt("RobotAbilityCooldown", 90);
    }

    public static void setRobotAbilityCooldown(World world, int value) throws Exception {
        Object cfg = getConfigInstance();
        cfg.getClass().getField("RobotAbilityCooldown").set(cfg, value);
        saveConfig();
    }

    public static boolean getConductorInstinctModify(World world) {
        return readWorldBool(world, "ConductorInstinctModify", false);
    }

    public static void setConductorInstinctModify(World world, boolean value) throws Exception {
        setWorldBool(world, "ConductorInstinctModify", value);
    }

    public static boolean getCoronerInstinctModify(World world) {
        return readWorldBool(world, "CoronerInstinctModify", false);
    }

    public static void setCoronerInstinctModify(World world, boolean value) throws Exception {
        setWorldBool(world, "CoronerInstinctModify", value);
    }
}
