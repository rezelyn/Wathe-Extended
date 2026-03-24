package cat.rezelyn.watheextended.api.kinswathe;

import cat.rezelyn.watheextended.api.ClientConfig;
import cat.rezelyn.watheextended.api.ServerConfig;
import cat.rezelyn.watheextended.api.ServerConfig.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.World;

public final class ConfigHelper {

    private ConfigHelper() {}

    public static boolean isLoaded() {
        return FabricLoader.getInstance().isModLoaded("kinswathe");
    }

    private static Object getConfigInstance() throws Exception {
        Class<?> cls = Class.forName("org.BsXinQin.kinswathe.KinsWatheConfig");
        Object handler = cls.getField("HANDLER").get(null);
        return handler.getClass().getMethod("instance").invoke(handler);
    }

    private static void saveConfig() throws Exception {
        Class<?> cls = Class.forName("org.BsXinQin.kinswathe.KinsWatheConfig");
        Object handler = cls.getField("HANDLER").get(null);
        handler.getClass().getMethod("save").invoke(handler);
    }

    private static Object getWorldComponent(World world) throws Exception {
        Class<?> cls = Class.forName("org.BsXinQin.kinswathe.component.ConfigWorldComponent");
        Object key = cls.getField("KEY").get(null);
        return key.getClass().getMethod("get", Object.class).invoke(key, world);
    }

    private static boolean readBoolServer(String field, boolean def) {
        try {
            Object cfg = getConfigInstance();
            return (boolean) cfg.getClass().getField(field).get(cfg);
        } catch (Throwable t) {
            return def;
        }
    }

    private static boolean readWorldBoolServer(World world, String field, boolean def) {
        if (world != null) {
            try {
                Object comp = getWorldComponent(world);
                if (comp != null) return (boolean) comp.getClass().getField(field).get(comp);
            } catch (Throwable ignored) {
            }
        }
        return readBoolServer(field, def);
    }

    private static int readIntServer(String field, int def) {
        try {
            Object cfg = getConfigInstance();
            return (int) cfg.getClass().getField(field).get(cfg);
        } catch (Throwable t) {
            return def;
        }
    }

    private static int readWorldIntServer(World world, String field, int def) {
        if (world != null) {
            try {
                Object comp = getWorldComponent(world);
                if (comp != null) return (int) comp.getClass().getField(field).get(comp);
            } catch (Throwable ignored) {
            }
        }
        return readIntServer(field, def);
    }

    private static void setWorldBoolServer(World world, String field, boolean value) throws Exception {
        Object comp = getWorldComponent(world);
        if (comp != null) {
            comp.getClass().getField(field).set(comp, value);
        }
        Object cfg = getConfigInstance();
        cfg.getClass().getField(field).set(cfg, value);
        saveConfig();
    }

    private static void setWorldIntServer(World world, String field, int value) throws Exception {
        Object comp = getWorldComponent(world);
        if (comp != null) {
            comp.getClass().getField(field).set(comp, value);
        }
        Object cfg = getConfigInstance();
        cfg.getClass().getField(field).set(cfg, value);
        saveConfig();
    }

    private static void setConfigBoolServer(String field, boolean value) throws Exception {
        Object cfg = getConfigInstance();
        cfg.getClass().getField(field).set(cfg, value);
        saveConfig();
    }

    private static void setConfigIntServer(String field, int value) throws Exception {
        Object cfg = getConfigInstance();
        cfg.getClass().getField(field).set(cfg, value);
        saveConfig();
    }

    private static boolean clientBool(String cacheKey, boolean def) {
        return ClientConfig.getBool(cacheKey, def);
    }

    private static int clientInt(String cacheKey, int def) {
        return ClientConfig.getInt(cacheKey, def);
    }

    public static void registerEntries() {
        if (!isLoaded()) return;

        // world booleans
        reg(Entry.worldBool("kinswathe.EnableJumpNotInGame", false, w -> readWorldBoolServer(w, "EnableJumpNotInGame", false), (w, v) -> {
            try {
                setWorldBoolServer(w, "EnableJumpNotInGame", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.worldBool("kinswathe.EnableStartSafeTime", false, w -> readWorldBoolServer(w, "EnableStartSafeTime", false), (w, v) -> {
            try {
                setWorldBoolServer(w, "EnableStartSafeTime", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.worldBool("kinswathe.EnableNoellesRolesModify", false, w -> readWorldBoolServer(w, "EnableNoellesRolesModify", false), (w, v) -> {
            try {
                setWorldBoolServer(w, "EnableNoellesRolesModify", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.worldBool("kinswathe.BodymakerAbilityFakeRole", true, w -> readWorldBoolServer(w, "BodymakerAbilityFakeRole", true), (w, v) -> {
            try {
                setWorldBoolServer(w, "BodymakerAbilityFakeRole", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.worldBool("kinswathe.ConductorInstinctModify", false, w -> readWorldBoolServer(w, "ConductorInstinctModify", false), (w, v) -> {
            try {
                setWorldBoolServer(w, "ConductorInstinctModify", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.worldBool("kinswathe.CoronerInstinctModify", false, w -> readWorldBoolServer(w, "CoronerInstinctModify", false), (w, v) -> {
            try {
                setWorldBoolServer(w, "CoronerInstinctModify", v);
            } catch (Throwable ignored) {
            }
        }));

        // config booleans
        reg(Entry.globalBool("kinswathe.EnableWatheModify", false, () -> readBoolServer("EnableWatheModify", false), v -> {
            try {
                setConfigBoolServer("EnableWatheModify", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.globalBool("kinswathe.PreventKillerDropRevolver", false, () -> readBoolServer("PreventKillerDropRevolver", false), v -> {
            try {
                setConfigBoolServer("PreventKillerDropRevolver", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.globalBool("kinswathe.HackerGenerateWithMimic", false, () -> readBoolServer("HackerGenerateWithMimic", false), v -> {
            try {
                setConfigBoolServer("HackerGenerateWithMimic", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.globalBool("kinswathe.HackerHasShop", true, () -> readBoolServer("HackerHasShop", true), v -> {
            try {
                setConfigBoolServer("HackerHasShop", v);
            } catch (Throwable ignored) {
            }
        }));

        // world ints
        reg(Entry.worldInt("kinswathe.StartingCooldown", 30, w -> readWorldIntServer(w, "StartingCooldown", 30), (w, v) -> {
            try {
                setWorldIntServer(w, "StartingCooldown", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.worldInt("kinswathe.BellringerAbilityPrice", 200, w -> readWorldIntServer(w, "BellringerAbilityPrice", 200), (w, v) -> {
            try {
                setWorldIntServer(w, "BellringerAbilityPrice", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.worldInt("kinswathe.CleanerAbilityPrice", 200, w -> readWorldIntServer(w, "CleanerAbilityPrice", 200), (w, v) -> {
            try {
                setWorldIntServer(w, "CleanerAbilityPrice", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.worldInt("kinswathe.CookPanPrice", 250, w -> readWorldIntServer(w, "CookPanPrice", 250), (w, v) -> {
            try {
                setWorldIntServer(w, "CookPanPrice", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.worldInt("kinswathe.DetectiveAbilityPrice", 200, w -> readWorldIntServer(w, "DetectiveAbilityPrice", 200), (w, v) -> {
            try {
                setWorldIntServer(w, "DetectiveAbilityPrice", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.worldInt("kinswathe.DrugmakerGetCoins", 50, w -> readWorldIntServer(w, "DrugmakerGetCoins", 50), (w, v) -> {
            try {
                setWorldIntServer(w, "DrugmakerGetCoins", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.worldInt("kinswathe.DrugmakerPoisonInjectorPrice", 125, w -> readWorldIntServer(w, "DrugmakerPoisonInjectorPrice", 125), (w, v) -> {
            try {
                setWorldIntServer(w, "DrugmakerPoisonInjectorPrice", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.worldInt("kinswathe.DrugmakerBlowgunPrice", 175, w -> readWorldIntServer(w, "DrugmakerBlowgunPrice", 175), (w, v) -> {
            try {
                setWorldIntServer(w, "DrugmakerBlowgunPrice", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.worldInt("kinswathe.HunterAbilityPrice", 125, w -> readWorldIntServer(w, "HunterAbilityPrice", 125), (w, v) -> {
            try {
                setWorldIntServer(w, "HunterAbilityPrice", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.worldInt("kinswathe.JudgeAbilityPrice", 300, w -> readWorldIntServer(w, "JudgeAbilityPrice", 300), (w, v) -> {
            try {
                setWorldIntServer(w, "JudgeAbilityPrice", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.worldInt("kinswathe.KidnapperKnockoutDrugPrice", 75, w -> readWorldIntServer(w, "KidnapperKnockoutDrugPrice", 75), (w, v) -> {
            try {
                setWorldIntServer(w, "KidnapperKnockoutDrugPrice", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.worldInt("kinswathe.LicensedVillainRevolverPrice", 300, w -> readWorldIntServer(w, "LicensedVillainRevolverPrice", 300), (w, v) -> {
            try {
                setWorldIntServer(w, "LicensedVillainRevolverPrice", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.worldInt("kinswathe.PhysicianPillPrice", 300, w -> readWorldIntServer(w, "PhysicianPillPrice", 300), (w, v) -> {
            try {
                setWorldIntServer(w, "PhysicianPillPrice", v);
            } catch (Throwable ignored) {
            }
        }));

        // config ints
        reg(Entry.globalInt("kinswathe.InitialCivilianIncome", 0, () -> readIntServer("InitialCivilianIncome", 0), v -> {
            try {
                setConfigIntServer("InitialCivilianIncome", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.globalInt("kinswathe.InitialNeutralIncome", 0, () -> readIntServer("InitialNeutralIncome", 0), v -> {
            try {
                setConfigIntServer("InitialNeutralIncome", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.globalInt("kinswathe.InitialKillerIncome", 100, () -> readIntServer("InitialKillerIncome", 100), v -> {
            try {
                setConfigIntServer("InitialKillerIncome", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.globalInt("kinswathe.IncreaseMoneyWhenKill", 100, () -> readIntServer("IncreaseMoneyWhenKill", 100), v -> {
            try {
                setConfigIntServer("IncreaseMoneyWhenKill", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.globalInt("kinswathe.BellringerAbilityCooldown", 120, () -> readIntServer("BellringerAbilityCooldown", 120), v -> {
            try {
                setConfigIntServer("BellringerAbilityCooldown", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.globalInt("kinswathe.BodymakerAbilityCooldown", 90, () -> readIntServer("BodymakerAbilityCooldown", 90), v -> {
            try {
                setConfigIntServer("BodymakerAbilityCooldown", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.globalInt("kinswathe.CleanerAbilityCooldown", 150, () -> readIntServer("CleanerAbilityCooldown", 150), v -> {
            try {
                setConfigIntServer("CleanerAbilityCooldown", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.globalInt("kinswathe.DetectiveAbilityCooldown", 90, () -> readIntServer("DetectiveAbilityCooldown", 90), v -> {
            try {
                setConfigIntServer("DetectiveAbilityCooldown", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.globalInt("kinswathe.DreamerInitialItemQuantity", 1, () -> readIntServer("DreamerInitialItemQuantity", 1), v -> {
            try {
                setConfigIntServer("DreamerInitialItemQuantity", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.globalInt("kinswathe.DrugmakerPlayerLimit", 10, () -> readIntServer("DrugmakerPlayerLimit", 10), v -> {
            try {
                setConfigIntServer("DrugmakerPlayerLimit", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.globalInt("kinswathe.HackerPlayerLimit", 10, () -> readIntServer("HackerPlayerLimit", 10), v -> {
            try {
                setConfigIntServer("HackerPlayerLimit", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.globalInt("kinswathe.HackerHackingTime", 30, () -> readIntServer("HackerHackingTime", 30), v -> {
            try {
                setConfigIntServer("HackerHackingTime", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.globalInt("kinswathe.HunterAbilityCooldown", 5, () -> readIntServer("HunterAbilityCooldown", 5), v -> {
            try {
                setConfigIntServer("HunterAbilityCooldown", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.globalInt("kinswathe.JudgeAbilityGlowing", 90, () -> readIntServer("JudgeAbilityGlowing", 90), v -> {
            try {
                setConfigIntServer("JudgeAbilityGlowing", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.globalInt("kinswathe.JudgeAbilityCooldown", 180, () -> readIntServer("JudgeAbilityCooldown", 180), v -> {
            try {
                setConfigIntServer("JudgeAbilityCooldown", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.globalInt("kinswathe.LicensedVillainPlayerLimit", 10, () -> readIntServer("LicensedVillainPlayerLimit", 10), v -> {
            try {
                setConfigIntServer("LicensedVillainPlayerLimit", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.globalInt("kinswathe.RobotAbilityDuration", 10, () -> readIntServer("RobotAbilityDuration", 10), v -> {
            try {
                setConfigIntServer("RobotAbilityDuration", v);
            } catch (Throwable ignored) {
            }
        }));
        reg(Entry.globalInt("kinswathe.RobotAbilityCooldown", 90, () -> readIntServer("RobotAbilityCooldown", 90), v -> {
            try {
                setConfigIntServer("RobotAbilityCooldown", v);
            } catch (Throwable ignored) {
            }
        }));
    }

    private static <T> void reg(Entry<T> e) {
        ServerConfig.register(e);
    }

    private static boolean getBool(String key, boolean def) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) return ClientConfig.getBool(key, def);
        return def;
    }

    private static int getInt(String key, int def) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) return ClientConfig.getInt(key, def);
        return def;
    }

    public static boolean getEnableStaminaBar() {
        try {
            Object cfg = getConfigInstance();
            return (boolean) cfg.getClass().getField("EnableStaminaBar").get(cfg);
        } catch (Throwable t) {
            return false;
        }
    }

    public static void setEnableStaminaBar(boolean value) {
        try {
            Object cfg = getConfigInstance();
            cfg.getClass().getField("EnableStaminaBar").set(cfg, value);
            saveConfig();
        } catch (Throwable ignored) {
        }
    }

    public static int getStartingCooldown(World world) {
        return getInt("kinswathe.StartingCooldown", 30);
    }

    public static boolean getEnableJumpNotInGame(World world) {
        return getBool("kinswathe.EnableJumpNotInGame", false);
    }

    public static boolean getEnableStartSafeTime(World world) {
        return getBool("kinswathe.EnableStartSafeTime", false);
    }

    public static boolean getEnableNoellesRolesModify(World world) {
        return getBool("kinswathe.EnableNoellesRolesModify", false);
    }

    public static boolean getEnableWatheModify(World world) {
        return getBool("kinswathe.EnableWatheModify", false);
    }

    public static int getInitialCivilianIncome(World world) {
        return getInt("kinswathe.InitialCivilianIncome", 0);
    }

    public static int getInitialNeutralIncome(World world) {
        return getInt("kinswathe.InitialNeutralIncome", 0);
    }

    public static int getInitialKillerIncome(World world) {
        return getInt("kinswathe.InitialKillerIncome", 100);
    }

    public static int getIncreaseMoneyWhenKill(World world) {
        return getInt("kinswathe.IncreaseMoneyWhenKill", 100);
    }

    public static boolean getPreventKillerDropRevolver(World world) {
        return getBool("kinswathe.PreventKillerDropRevolver", false);
    }

    public static int getBellringerAbilityPrice(World world) {
        return getInt("kinswathe.BellringerAbilityPrice", 200);
    }

    public static int getBellringerAbilityCooldown(World world) {
        return getInt("kinswathe.BellringerAbilityCooldown", 120);
    }

    public static int getBodymakerAbilityCooldown(World world) {
        return getInt("kinswathe.BodymakerAbilityCooldown", 90);
    }

    public static boolean getBodymakerAbilityFakeRole(World world) {
        return getBool("kinswathe.BodymakerAbilityFakeRole", true);
    }

    public static int getCleanerAbilityPrice(World world) {
        return getInt("kinswathe.CleanerAbilityPrice", 200);
    }

    public static int getCleanerAbilityCooldown(World world) {
        return getInt("kinswathe.CleanerAbilityCooldown", 150);
    }

    public static int getCookPanPrice(World world) {
        return getInt("kinswathe.CookPanPrice", 250);
    }

    public static int getDetectiveAbilityPrice(World world) {
        return getInt("kinswathe.DetectiveAbilityPrice", 200);
    }

    public static int getDetectiveAbilityCooldown(World world) {
        return getInt("kinswathe.DetectiveAbilityCooldown", 90);
    }

    public static int getDreamerInitialItemQuantity(World world) {
        return getInt("kinswathe.DreamerInitialItemQuantity", 1);
    }

    public static int getDrugmakerPlayerLimit(World world) {
        return getInt("kinswathe.DrugmakerPlayerLimit", 10);
    }

    public static int getDrugmakerGetCoins(World world) {
        return getInt("kinswathe.DrugmakerGetCoins", 50);
    }

    public static int getDrugmakerPoisonInjectorPrice(World world) {
        return getInt("kinswathe.DrugmakerPoisonInjectorPrice", 125);
    }

    public static int getDrugmakerBlowgunPrice(World world) {
        return getInt("kinswathe.DrugmakerBlowgunPrice", 175);
    }

    public static int getHackerPlayerLimit(World world) {
        return getInt("kinswathe.HackerPlayerLimit", 10);
    }

    public static boolean getHackerGenerateWithMimic(World world) {
        return getBool("kinswathe.HackerGenerateWithMimic", false);
    }

    public static int getHackerHackingTime(World world) {
        return getInt("kinswathe.HackerHackingTime", 30);
    }

    public static boolean getHackerHasShop(World world) {
        return getBool("kinswathe.HackerHasShop", true);
    }

    public static int getHunterAbilityPrice(World world) {
        return getInt("kinswathe.HunterAbilityPrice", 125);
    }

    public static int getHunterAbilityCooldown(World world) {
        return getInt("kinswathe.HunterAbilityCooldown", 5);
    }

    public static int getJudgeAbilityPrice(World world) {
        return getInt("kinswathe.JudgeAbilityPrice", 300);
    }

    public static int getJudgeAbilityGlowing(World world) {
        return getInt("kinswathe.JudgeAbilityGlowing", 90);
    }

    public static int getJudgeAbilityCooldown(World world) {
        return getInt("kinswathe.JudgeAbilityCooldown", 180);
    }

    public static int getKidnapperKnockoutDrugPrice(World world) {
        return getInt("kinswathe.KidnapperKnockoutDrugPrice", 75);
    }

    public static int getLicensedVillainPlayerLimit(World world) {
        return getInt("kinswathe.LicensedVillainPlayerLimit", 10);
    }

    public static int getLicensedVillainRevolverPrice(World world) {
        return getInt("kinswathe.LicensedVillainRevolverPrice", 300);
    }

    public static int getPhysicianPillPrice(World world) {
        return getInt("kinswathe.PhysicianPillPrice", 300);
    }

    public static int getRobotAbilityDuration(World world) {
        return getInt("kinswathe.RobotAbilityDuration", 10);
    }

    public static int getRobotAbilityCooldown(World world) {
        return getInt("kinswathe.RobotAbilityCooldown", 90);
    }

    public static boolean getConductorInstinctModify(World world) {
        return getBool("kinswathe.ConductorInstinctModify", false);
    }

    public static boolean getCoronerInstinctModify(World world) {
        return getBool("kinswathe.CoronerInstinctModify", false);
    }

    public static void setStartingCooldown(World w, int v) throws Exception {
        apply("kinswathe.StartingCooldown", v, w);
    }

    public static void setEnableJumpNotInGame(World w, boolean v) throws Exception {
        apply("kinswathe.EnableJumpNotInGame", v, w);
    }

    public static void setEnableStartSafeTime(World w, boolean v) throws Exception {
        apply("kinswathe.EnableStartSafeTime", v, w);
    }

    public static void setEnableNoellesRolesModify(World w, boolean v) throws Exception {
        apply("kinswathe.EnableNoellesRolesModify", v, w);
    }

    public static void setEnableWatheModify(World w, boolean v) throws Exception {
        apply("kinswathe.EnableWatheModify", v, w);
    }

    public static void setInitialCivilianIncome(World w, int v) throws Exception {
        apply("kinswathe.InitialCivilianIncome", v, w);
    }

    public static void setInitialNeutralIncome(World w, int v) throws Exception {
        apply("kinswathe.InitialNeutralIncome", v, w);
    }

    public static void setInitialKillerIncome(World w, int v) throws Exception {
        apply("kinswathe.InitialKillerIncome", v, w);
    }

    public static void setIncreaseMoneyWhenKill(World w, int v) throws Exception {
        apply("kinswathe.IncreaseMoneyWhenKill", v, w);
    }

    public static void setPreventKillerDropRevolver(World w, boolean v) throws Exception {
        apply("kinswathe.PreventKillerDropRevolver", v, w);
    }

    public static void setBellringerAbilityPrice(World w, int v) throws Exception {
        apply("kinswathe.BellringerAbilityPrice", v, w);
    }

    public static void setBellringerAbilityCooldown(World w, int v) throws Exception {
        apply("kinswathe.BellringerAbilityCooldown", v, w);
    }

    public static void setBodymakerAbilityCooldown(World w, int v) throws Exception {
        apply("kinswathe.BodymakerAbilityCooldown", v, w);
    }

    public static void setBodymakerAbilityFakeRole(World w, boolean v) throws Exception {
        apply("kinswathe.BodymakerAbilityFakeRole", v, w);
    }

    public static void setCleanerAbilityPrice(World w, int v) throws Exception {
        apply("kinswathe.CleanerAbilityPrice", v, w);
    }

    public static void setCleanerAbilityCooldown(World w, int v) throws Exception {
        apply("kinswathe.CleanerAbilityCooldown", v, w);
    }

    public static void setCookPanPrice(World w, int v) throws Exception {
        apply("kinswathe.CookPanPrice", v, w);
    }

    public static void setDetectiveAbilityPrice(World w, int v) throws Exception {
        apply("kinswathe.DetectiveAbilityPrice", v, w);
    }

    public static void setDetectiveAbilityCooldown(World w, int v) throws Exception {
        apply("kinswathe.DetectiveAbilityCooldown", v, w);
    }

    public static void setDreamerInitialItemQuantity(World w, int v) throws Exception {
        apply("kinswathe.DreamerInitialItemQuantity", v, w);
    }

    public static void setDrugmakerPlayerLimit(World w, int v) throws Exception {
        apply("kinswathe.DrugmakerPlayerLimit", v, w);
    }

    public static void setDrugmakerGetCoins(World w, int v) throws Exception {
        apply("kinswathe.DrugmakerGetCoins", v, w);
    }

    public static void setDrugmakerPoisonInjectorPrice(World w, int v) throws Exception {
        apply("kinswathe.DrugmakerPoisonInjectorPrice", v, w);
    }

    public static void setDrugmakerBlowgunPrice(World w, int v) throws Exception {
        apply("kinswathe.DrugmakerBlowgunPrice", v, w);
    }

    public static void setHackerPlayerLimit(World w, int v) throws Exception {
        apply("kinswathe.HackerPlayerLimit", v, w);
    }

    public static void setHackerGenerateWithMimic(World w, boolean v) throws Exception {
        apply("kinswathe.HackerGenerateWithMimic", v, w);
    }

    public static void setHackerHackingTime(World w, int v) throws Exception {
        apply("kinswathe.HackerHackingTime", v, w);
    }

    public static void setHackerHasShop(World w, boolean v) throws Exception {
        apply("kinswathe.HackerHasShop", v, w);
    }

    public static void setHunterAbilityPrice(World w, int v) throws Exception {
        apply("kinswathe.HunterAbilityPrice", v, w);
    }

    public static void setHunterAbilityCooldown(World w, int v) throws Exception {
        apply("kinswathe.HunterAbilityCooldown", v, w);
    }

    public static void setJudgeAbilityPrice(World w, int v) throws Exception {
        apply("kinswathe.JudgeAbilityPrice", v, w);
    }

    public static void setJudgeAbilityGlowing(World w, int v) throws Exception {
        apply("kinswathe.JudgeAbilityGlowing", v, w);
    }

    public static void setJudgeAbilityCooldown(World w, int v) throws Exception {
        apply("kinswathe.JudgeAbilityCooldown", v, w);
    }

    public static void setKidnapperKnockoutDrugPrice(World w, int v) throws Exception {
        apply("kinswathe.KidnapperKnockoutDrugPrice", v, w);
    }

    public static void setLicensedVillainPlayerLimit(World w, int v) throws Exception {
        apply("kinswathe.LicensedVillainPlayerLimit", v, w);
    }

    public static void setLicensedVillainRevolverPrice(World w, int v) throws Exception {
        apply("kinswathe.LicensedVillainRevolverPrice", v, w);
    }

    public static void setPhysicianPillPrice(World w, int v) throws Exception {
        apply("kinswathe.PhysicianPillPrice", v, w);
    }

    public static void setRobotAbilityDuration(World w, int v) throws Exception {
        apply("kinswathe.RobotAbilityDuration", v, w);
    }

    public static void setRobotAbilityCooldown(World w, int v) throws Exception {
        apply("kinswathe.RobotAbilityCooldown", v, w);
    }

    public static void setConductorInstinctModify(World w, boolean v) throws Exception {
        apply("kinswathe.ConductorInstinctModify", v, w);
    }

    public static void setCoronerInstinctModify(World w, boolean v) throws Exception {
        apply("kinswathe.CoronerInstinctModify", v, w);
    }

    @SuppressWarnings("unchecked")
    private static <T> void apply(String key, T value, World world) {
        Entry<T> entry = (Entry<T>) ServerConfig.entries().get(key);
        if (entry != null) entry.writeServer(world, value);
    }
}
