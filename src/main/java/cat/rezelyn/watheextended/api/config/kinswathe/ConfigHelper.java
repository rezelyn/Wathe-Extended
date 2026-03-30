package cat.rezelyn.watheextended.api.config.kinswathe;

import cat.rezelyn.watheextended.api.config.ConfigUtils;
import cat.rezelyn.watheextended.api.config.ServerConfig;
import cat.rezelyn.watheextended.api.config.ServerConfig.Entry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.World;

public final class ConfigHelper {

    private ConfigHelper() {
    }

    public static boolean isLoaded() {
        return FabricLoader.getInstance().isModLoaded("kinswathe");
    }

    private static final String CONFIG_CLASS = "org.BsXinQin.kinswathe.KinsWatheConfig";
    private static final String WORLD_COMPONENT_CLASS = "org.BsXinQin.kinswathe.component.ConfigWorldComponent";

    private static boolean readWorldBoolServer(World world, String field, boolean def) {
        if (world != null) {
            try {
                Object comp = getWorldComponent(world);
                if (comp != null) return (boolean) ConfigUtils.getField(comp, field);
            } catch (Throwable ignored) {
            }
        }
        return ConfigUtils.readBool(CONFIG_CLASS, field, def);
    }

    private static int readWorldIntServer(World world, String field, int def) {
        if (world != null) {
            try {
                Object comp = getWorldComponent(world);
                if (comp != null) return (int) ConfigUtils.getField(comp, field);
            } catch (Throwable ignored) {
            }
        }
        return ConfigUtils.readInt(CONFIG_CLASS, field, def);
    }

    private static void writeWorldBoolServer(World world, String field, boolean value) throws Exception {
        Object comp = getWorldComponent(world);
        if (comp != null) ConfigUtils.setField(comp, field, value);
        ConfigUtils.writeBool(CONFIG_CLASS, field, value);
    }

    private static void writeWorldIntServer(World world, String field, int value) throws Exception {
        Object comp = getWorldComponent(world);
        if (comp != null) ConfigUtils.setField(comp, field, value);
        ConfigUtils.writeInt(CONFIG_CLASS, field, value);
    }

    private static Object getWorldComponent(World world) throws Exception {
        Object key = ConfigUtils.getStaticField(WORLD_COMPONENT_CLASS, "KEY");
        return key.getClass().getMethod("get", Object.class).invoke(key, world);
    }

    public static void registerEntries() {
        if (!isLoaded()) return;
        ServerConfig.register(Entry.worldBool("kinswathe.EnableJumpNotInGame", false, world -> readWorldBoolServer(world, "EnableJumpNotInGame", false), (world, value) -> {
            try {
                writeWorldBoolServer(world, "EnableJumpNotInGame", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.worldBool("kinswathe.EnableStartSafeTime", false, world -> readWorldBoolServer(world, "EnableStartSafeTime", false), (world, value) -> {
            try {
                writeWorldBoolServer(world, "EnableStartSafeTime", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.worldBool("kinswathe.EnableNoellesRolesModify", false, world -> readWorldBoolServer(world, "EnableNoellesRolesModify", false), (world, value) -> {
            try {
                writeWorldBoolServer(world, "EnableNoellesRolesModify", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.worldBool("kinswathe.BodymakerAbilityFakeRole", true, world -> readWorldBoolServer(world, "BodymakerAbilityFakeRole", true), (world, value) -> {
            try {
                writeWorldBoolServer(world, "BodymakerAbilityFakeRole", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.worldBool("kinswathe.ConductorInstinctModify", false, world -> readWorldBoolServer(world, "ConductorInstinctModify", false), (world, value) -> {
            try {
                writeWorldBoolServer(world, "ConductorInstinctModify", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.worldBool("kinswathe.CoronerInstinctModify", false, world -> readWorldBoolServer(world, "CoronerInstinctModify", false), (world, value) -> {
            try {
                writeWorldBoolServer(world, "CoronerInstinctModify", value);
            } catch (Throwable ignored) {
            }
        }));

        // config booleans
        ServerConfig.register(Entry.globalBool("kinswathe.EnableWatheModify", false, () -> ConfigUtils.readBool(CONFIG_CLASS, "EnableWatheModify", false), value -> {
            try {
                ConfigUtils.writeBool(CONFIG_CLASS, "EnableWatheModify", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalBool("kinswathe.PreventKillerDropRevolver", false, () -> ConfigUtils.readBool(CONFIG_CLASS, "PreventKillerDropRevolver", false), value -> {
            try {
                ConfigUtils.writeBool(CONFIG_CLASS, "PreventKillerDropRevolver", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalBool("kinswathe.HackerGenerateWithMimic", false, () -> ConfigUtils.readBool(CONFIG_CLASS, "HackerGenerateWithMimic", false), value -> {
            try {
                ConfigUtils.writeBool(CONFIG_CLASS, "HackerGenerateWithMimic", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalBool("kinswathe.HackerHasShop", true, () -> ConfigUtils.readBool(CONFIG_CLASS, "HackerHasShop", true), value -> {
            try {
                ConfigUtils.writeBool(CONFIG_CLASS, "HackerHasShop", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.worldInt("kinswathe.StartingCooldown", 30, world -> readWorldIntServer(world, "StartingCooldown", 30), (world, value) -> {
            try {
                writeWorldIntServer(world, "StartingCooldown", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.worldInt("kinswathe.BellringerAbilityPrice", 200, world -> readWorldIntServer(world, "BellringerAbilityPrice", 200), (world, value) -> {
            try {
                writeWorldIntServer(world, "BellringerAbilityPrice", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.worldInt("kinswathe.CleanerAbilityPrice", 200, world -> readWorldIntServer(world, "CleanerAbilityPrice", 200), (world, value) -> {
            try {
                writeWorldIntServer(world, "CleanerAbilityPrice", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.worldInt("kinswathe.CookPanPrice", 250, world -> readWorldIntServer(world, "CookPanPrice", 250), (world, value) -> {
            try {
                writeWorldIntServer(world, "CookPanPrice", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.worldInt("kinswathe.DetectiveAbilityPrice", 200, world -> readWorldIntServer(world, "DetectiveAbilityPrice", 200), (world, value) -> {
            try {
                writeWorldIntServer(world, "DetectiveAbilityPrice", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.worldInt("kinswathe.DrugmakerGetCoins", 50, world -> readWorldIntServer(world, "DrugmakerGetCoins", 50), (world, value) -> {
            try {
                writeWorldIntServer(world, "DrugmakerGetCoins", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.worldInt("kinswathe.DrugmakerPoisonInjectorPrice", 125, world -> readWorldIntServer(world, "DrugmakerPoisonInjectorPrice", 125), (world, value) -> {
            try {
                writeWorldIntServer(world, "DrugmakerPoisonInjectorPrice", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.worldInt("kinswathe.DrugmakerBlowgunPrice", 175, world -> readWorldIntServer(world, "DrugmakerBlowgunPrice", 175), (world, value) -> {
            try {
                writeWorldIntServer(world, "DrugmakerBlowgunPrice", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.worldInt("kinswathe.HunterAbilityPrice", 125, world -> readWorldIntServer(world, "HunterAbilityPrice", 125), (world, value) -> {
            try {
                writeWorldIntServer(world, "HunterAbilityPrice", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.worldInt("kinswathe.JudgeAbilityPrice", 300, world -> readWorldIntServer(world, "JudgeAbilityPrice", 300), (world, value) -> {
            try {
                writeWorldIntServer(world, "JudgeAbilityPrice", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.worldInt("kinswathe.KidnapperKnockoutDrugPrice", 75, world -> readWorldIntServer(world, "KidnapperKnockoutDrugPrice", 75), (world, value) -> {
            try {
                writeWorldIntServer(world, "KidnapperKnockoutDrugPrice", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.worldInt("kinswathe.LicensedVillainRevolverPrice", 300, world -> readWorldIntServer(world, "LicensedVillainRevolverPrice", 300), (world, value) -> {
            try {
                writeWorldIntServer(world, "LicensedVillainRevolverPrice", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.worldInt("kinswathe.PhysicianPillPrice", 300, world -> readWorldIntServer(world, "PhysicianPillPrice", 300), (world, value) -> {
            try {
                writeWorldIntServer(world, "PhysicianPillPrice", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("kinswathe.InitialCivilianIncome", 0, () -> ConfigUtils.readInt(CONFIG_CLASS, "InitialCivilianIncome", 0), value -> {
            try {
                ConfigUtils.writeInt(CONFIG_CLASS, "InitialCivilianIncome", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("kinswathe.InitialNeutralIncome", 0, () -> ConfigUtils.readInt(CONFIG_CLASS, "InitialNeutralIncome", 0), value -> {
            try {
                ConfigUtils.writeInt(CONFIG_CLASS, "InitialNeutralIncome", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("kinswathe.InitialKillerIncome", 100, () -> ConfigUtils.readInt(CONFIG_CLASS, "InitialKillerIncome", 100), value -> {
            try {
                ConfigUtils.writeInt(CONFIG_CLASS, "InitialKillerIncome", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("kinswathe.IncreaseMoneyWhenKill", 100, () -> ConfigUtils.readInt(CONFIG_CLASS, "IncreaseMoneyWhenKill", 100), value -> {
            try {
                ConfigUtils.writeInt(CONFIG_CLASS, "IncreaseMoneyWhenKill", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("kinswathe.BellringerAbilityCooldown", 120, () -> ConfigUtils.readInt(CONFIG_CLASS, "BellringerAbilityCooldown", 120), value -> {
            try {
                ConfigUtils.writeInt(CONFIG_CLASS, "BellringerAbilityCooldown", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("kinswathe.BodymakerAbilityCooldown", 90, () -> ConfigUtils.readInt(CONFIG_CLASS, "BodymakerAbilityCooldown", 90), value -> {
            try {
                ConfigUtils.writeInt(CONFIG_CLASS, "BodymakerAbilityCooldown", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("kinswathe.CleanerAbilityCooldown", 150, () -> ConfigUtils.readInt(CONFIG_CLASS, "CleanerAbilityCooldown", 150), value -> {
            try {
                ConfigUtils.writeInt(CONFIG_CLASS, "CleanerAbilityCooldown", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("kinswathe.DetectiveAbilityCooldown", 90, () -> ConfigUtils.readInt(CONFIG_CLASS, "DetectiveAbilityCooldown", 90), value -> {
            try {
                ConfigUtils.writeInt(CONFIG_CLASS, "DetectiveAbilityCooldown", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("kinswathe.DreamerInitialItemQuantity", 1, () -> ConfigUtils.readInt(CONFIG_CLASS, "DreamerInitialItemQuantity", 1), value -> {
            try {
                ConfigUtils.writeInt(CONFIG_CLASS, "DreamerInitialItemQuantity", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("kinswathe.DrugmakerPlayerLimit", 10, () -> ConfigUtils.readInt(CONFIG_CLASS, "DrugmakerPlayerLimit", 10), value -> {
            try {
                ConfigUtils.writeInt(CONFIG_CLASS, "DrugmakerPlayerLimit", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("kinswathe.HackerPlayerLimit", 10, () -> ConfigUtils.readInt(CONFIG_CLASS, "HackerPlayerLimit", 10), value -> {
            try {
                ConfigUtils.writeInt(CONFIG_CLASS, "HackerPlayerLimit", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("kinswathe.HackerHackingTime", 30, () -> ConfigUtils.readInt(CONFIG_CLASS, "HackerHackingTime", 30), value -> {
            try {
                ConfigUtils.writeInt(CONFIG_CLASS, "HackerHackingTime", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("kinswathe.HunterAbilityCooldown", 5, () -> ConfigUtils.readInt(CONFIG_CLASS, "HunterAbilityCooldown", 5), value -> {
            try {
                ConfigUtils.writeInt(CONFIG_CLASS, "HunterAbilityCooldown", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("kinswathe.JudgeAbilityGlowing", 90, () -> ConfigUtils.readInt(CONFIG_CLASS, "JudgeAbilityGlowing", 90), value -> {
            try {
                ConfigUtils.writeInt(CONFIG_CLASS, "JudgeAbilityGlowing", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("kinswathe.JudgeAbilityCooldown", 180, () -> ConfigUtils.readInt(CONFIG_CLASS, "JudgeAbilityCooldown", 180), value -> {
            try {
                ConfigUtils.writeInt(CONFIG_CLASS, "JudgeAbilityCooldown", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("kinswathe.LicensedVillainPlayerLimit", 10, () -> ConfigUtils.readInt(CONFIG_CLASS, "LicensedVillainPlayerLimit", 10), value -> {
            try {
                ConfigUtils.writeInt(CONFIG_CLASS, "LicensedVillainPlayerLimit", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("kinswathe.RobotAbilityDuration", 10, () -> ConfigUtils.readInt(CONFIG_CLASS, "RobotAbilityDuration", 10), value -> {
            try {
                ConfigUtils.writeInt(CONFIG_CLASS, "RobotAbilityDuration", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("kinswathe.RobotAbilityCooldown", 90, () -> ConfigUtils.readInt(CONFIG_CLASS, "RobotAbilityCooldown", 90), value -> {
            try {
                ConfigUtils.writeInt(CONFIG_CLASS, "RobotAbilityCooldown", value);
            } catch (Throwable ignored) {
            }
        }));
    }

    // getters

    public static boolean getEnableStaminaBar() {
        return ConfigUtils.readBool(CONFIG_CLASS, "EnableStaminaBar", false);
    }

    public static int getStartingCooldown(World world) {
        return ConfigUtils.clientInt("kinswathe.StartingCooldown", 30);
    }

    public static boolean getEnableStartSafeTime(World world) {
        return ConfigUtils.clientBool("kinswathe.EnableStartSafeTime", false);
    }

    public static boolean getEnableNoellesRolesModify(World world) {
        return ConfigUtils.clientBool("kinswathe.EnableNoellesRolesModify", false);
    }

    public static boolean getEnableWatheModify(World world) {
        return ConfigUtils.clientBool("kinswathe.EnableWatheModify", false);
    }

    public static int getInitialCivilianIncome(World world) {
        return ConfigUtils.clientInt("kinswathe.InitialCivilianIncome", 0);
    }

    public static int getInitialNeutralIncome(World world) {
        return ConfigUtils.clientInt("kinswathe.InitialNeutralIncome", 0);
    }

    public static int getInitialKillerIncome(World world) {
        return ConfigUtils.clientInt("kinswathe.InitialKillerIncome", 100);
    }

    public static int getIncreaseMoneyWhenKill(World world) {
        return ConfigUtils.clientInt("kinswathe.IncreaseMoneyWhenKill", 100);
    }

    public static boolean getPreventKillerDropRevolver(World world) {
        return ConfigUtils.clientBool("kinswathe.PreventKillerDropRevolver", false);
    }

    public static int getBellringerAbilityPrice(World world) {
        return ConfigUtils.clientInt("kinswathe.BellringerAbilityPrice", 200);
    }

    public static int getBellringerAbilityCooldown(World world) {
        return ConfigUtils.clientInt("kinswathe.BellringerAbilityCooldown", 120);
    }

    public static int getBodymakerAbilityCooldown(World world) {
        return ConfigUtils.clientInt("kinswathe.BodymakerAbilityCooldown", 90);
    }

    public static boolean getBodymakerAbilityFakeRole(World world) {
        return ConfigUtils.clientBool("kinswathe.BodymakerAbilityFakeRole", true);
    }

    public static int getCleanerAbilityPrice(World world) {
        return ConfigUtils.clientInt("kinswathe.CleanerAbilityPrice", 200);
    }

    public static int getCleanerAbilityCooldown(World world) {
        return ConfigUtils.clientInt("kinswathe.CleanerAbilityCooldown", 150);
    }

    public static int getCookPanPrice(World world) {
        return ConfigUtils.clientInt("kinswathe.CookPanPrice", 250);
    }

    public static int getDetectiveAbilityPrice(World world) {
        return ConfigUtils.clientInt("kinswathe.DetectiveAbilityPrice", 200);
    }

    public static int getDetectiveAbilityCooldown(World world) {
        return ConfigUtils.clientInt("kinswathe.DetectiveAbilityCooldown", 90);
    }

    public static int getDreamerInitialItemQuantity(World world) {
        return ConfigUtils.clientInt("kinswathe.DreamerInitialItemQuantity", 1);
    }

    public static int getDrugmakerPlayerLimit(World world) {
        return ConfigUtils.clientInt("kinswathe.DrugmakerPlayerLimit", 10);
    }

    public static int getDrugmakerGetCoins(World world) {
        return ConfigUtils.clientInt("kinswathe.DrugmakerGetCoins", 50);
    }

    public static int getDrugmakerPoisonInjectorPrice(World world) {
        return ConfigUtils.clientInt("kinswathe.DrugmakerPoisonInjectorPrice", 125);
    }

    public static int getDrugmakerBlowgunPrice(World world) {
        return ConfigUtils.clientInt("kinswathe.DrugmakerBlowgunPrice", 175);
    }

    public static int getHackerPlayerLimit(World world) {
        return ConfigUtils.clientInt("kinswathe.HackerPlayerLimit", 10);
    }

    public static boolean getHackerGenerateWithMimic(World world) {
        return ConfigUtils.clientBool("kinswathe.HackerGenerateWithMimic", false);
    }

    public static int getHackerHackingTime(World world) {
        return ConfigUtils.clientInt("kinswathe.HackerHackingTime", 30);
    }

    public static boolean getHackerHasShop(World world) {
        return ConfigUtils.clientBool("kinswathe.HackerHasShop", true);
    }

    public static int getHunterAbilityPrice(World world) {
        return ConfigUtils.clientInt("kinswathe.HunterAbilityPrice", 125);
    }

    public static int getHunterAbilityCooldown(World world) {
        return ConfigUtils.clientInt("kinswathe.HunterAbilityCooldown", 5);
    }

    public static int getJudgeAbilityPrice(World world) {
        return ConfigUtils.clientInt("kinswathe.JudgeAbilityPrice", 300);
    }

    public static int getJudgeAbilityGlowing(World world) {
        return ConfigUtils.clientInt("kinswathe.JudgeAbilityGlowing", 90);
    }

    public static int getJudgeAbilityCooldown(World world) {
        return ConfigUtils.clientInt("kinswathe.JudgeAbilityCooldown", 180);
    }

    public static int getKidnapperKnockoutDrugPrice(World world) {
        return ConfigUtils.clientInt("kinswathe.KidnapperKnockoutDrugPrice", 75);
    }

    public static int getLicensedVillainPlayerLimit(World world) {
        return ConfigUtils.clientInt("kinswathe.LicensedVillainPlayerLimit", 10);
    }

    public static int getLicensedVillainRevolverPrice(World world) {
        return ConfigUtils.clientInt("kinswathe.LicensedVillainRevolverPrice", 300);
    }

    public static int getPhysicianPillPrice(World world) {
        return ConfigUtils.clientInt("kinswathe.PhysicianPillPrice", 300);
    }

    public static int getRobotAbilityDuration(World world) {
        return ConfigUtils.clientInt("kinswathe.RobotAbilityDuration", 10);
    }

    public static int getRobotAbilityCooldown(World world) {
        return ConfigUtils.clientInt("kinswathe.RobotAbilityCooldown", 90);
    }

    public static boolean getConductorInstinctModify(World world) {
        return ConfigUtils.clientBool("kinswathe.ConductorInstinctModify", false);
    }

    public static boolean getCoronerInstinctModify(World world) {
        return ConfigUtils.clientBool("kinswathe.CoronerInstinctModify", false);
    }

    // setters

    public static void setEnableStaminaBar(boolean value) {
        try {
            ConfigUtils.writeBool(CONFIG_CLASS, "EnableStaminaBar", value);
        } catch (Throwable ignored) {
        }
    }

    public static void setStartingCooldown(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.StartingCooldown", value, world);
    }

    public static void setEnableJumpNotInGame(World world, boolean value) throws Exception {
        ConfigUtils.apply("kinswathe.EnableJumpNotInGame", value, world);
    }

    public static void setEnableStartSafeTime(World world, boolean value) throws Exception {
        ConfigUtils.apply("kinswathe.EnableStartSafeTime", value, world);
    }

    public static void setEnableNoellesRolesModify(World world, boolean value) throws Exception {
        ConfigUtils.apply("kinswathe.EnableNoellesRolesModify", value, world);
    }

    public static void setEnableWatheModify(World world, boolean value) throws Exception {
        ConfigUtils.apply("kinswathe.EnableWatheModify", value, world);
    }

    public static void setInitialCivilianIncome(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.InitialCivilianIncome", value, world);
    }

    public static void setInitialNeutralIncome(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.InitialNeutralIncome", value, world);
    }

    public static void setInitialKillerIncome(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.InitialKillerIncome", value, world);
    }

    public static void setIncreaseMoneyWhenKill(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.IncreaseMoneyWhenKill", value, world);
    }

    public static void setPreventKillerDropRevolver(World world, boolean value) throws Exception {
        ConfigUtils.apply("kinswathe.PreventKillerDropRevolver", value, world);
    }

    public static void setBellringerAbilityPrice(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.BellringerAbilityPrice", value, world);
    }

    public static void setBellringerAbilityCooldown(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.BellringerAbilityCooldown", value, world);
    }

    public static void setBodymakerAbilityCooldown(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.BodymakerAbilityCooldown", value, world);
    }

    public static void setBodymakerAbilityFakeRole(World world, boolean value) throws Exception {
        ConfigUtils.apply("kinswathe.BodymakerAbilityFakeRole", value, world);
    }

    public static void setCleanerAbilityPrice(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.CleanerAbilityPrice", value, world);
    }

    public static void setCleanerAbilityCooldown(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.CleanerAbilityCooldown", value, world);
    }

    public static void setCookPanPrice(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.CookPanPrice", value, world);
    }

    public static void setDetectiveAbilityPrice(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.DetectiveAbilityPrice", value, world);
    }

    public static void setDetectiveAbilityCooldown(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.DetectiveAbilityCooldown", value, world);
    }

    public static void setDreamerInitialItemQuantity(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.DreamerInitialItemQuantity", value, world);
    }

    public static void setDrugmakerPlayerLimit(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.DrugmakerPlayerLimit", value, world);
    }

    public static void setDrugmakerGetCoins(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.DrugmakerGetCoins", value, world);
    }

    public static void setDrugmakerPoisonInjectorPrice(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.DrugmakerPoisonInjectorPrice", value, world);
    }

    public static void setDrugmakerBlowgunPrice(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.DrugmakerBlowgunPrice", value, world);
    }

    public static void setHackerPlayerLimit(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.HackerPlayerLimit", value, world);
    }

    public static void setHackerGenerateWithMimic(World world, boolean value) throws Exception {
        ConfigUtils.apply("kinswathe.HackerGenerateWithMimic", value, world);
    }

    public static void setHackerHackingTime(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.HackerHackingTime", value, world);
    }

    public static void setHackerHasShop(World world, boolean value) throws Exception {
        ConfigUtils.apply("kinswathe.HackerHasShop", value, world);
    }

    public static void setHunterAbilityPrice(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.HunterAbilityPrice", value, world);
    }

    public static void setHunterAbilityCooldown(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.HunterAbilityCooldown", value, world);
    }

    public static void setJudgeAbilityPrice(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.JudgeAbilityPrice", value, world);
    }

    public static void setJudgeAbilityGlowing(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.JudgeAbilityGlowing", value, world);
    }

    public static void setJudgeAbilityCooldown(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.JudgeAbilityCooldown", value, world);
    }

    public static void setKidnapperKnockoutDrugPrice(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.KidnapperKnockoutDrugPrice", value, world);
    }

    public static void setLicensedVillainPlayerLimit(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.LicensedVillainPlayerLimit", value, world);
    }

    public static void setLicensedVillainRevolverPrice(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.LicensedVillainRevolverPrice", value, world);
    }

    public static void setPhysicianPillPrice(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.PhysicianPillPrice", value, world);
    }

    public static void setRobotAbilityDuration(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.RobotAbilityDuration", value, world);
    }

    public static void setRobotAbilityCooldown(World world, int value) throws Exception {
        ConfigUtils.apply("kinswathe.RobotAbilityCooldown", value, world);
    }

    public static void setConductorInstinctModify(World world, boolean value) throws Exception {
        ConfigUtils.apply("kinswathe.ConductorInstinctModify", value, world);
    }

    public static void setCoronerInstinctModify(World world, boolean value) throws Exception {
        ConfigUtils.apply("kinswathe.CoronerInstinctModify", value, world);
    }
}
