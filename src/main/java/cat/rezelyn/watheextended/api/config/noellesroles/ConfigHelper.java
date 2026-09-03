package cat.rezelyn.watheextended.api.config.noellesroles;

import cat.rezelyn.watheextended.api.config.ConfigUtils;
import cat.rezelyn.watheextended.api.config.ServerConfig;
import cat.rezelyn.watheextended.api.config.ServerConfig.Entry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.World;

public final class ConfigHelper {

    private ConfigHelper() {}

    public static boolean isLoaded() {
        return FabricLoader.getInstance().isModLoaded("noellesroles");
    }

    private static final String CONFIG_CLASS = "org.agmas.noellesroles.config.NoellesRolesConfig";

    public static void registerEntries() {
        if (!isLoaded()) return;

        ServerConfig.register(Entry.globalBool("noellesroles.insanePlayersSeeMorphs", false, () -> ConfigUtils.readBool(CONFIG_CLASS, "insanePlayersSeeMorphs", false), v -> {
            try {
                ConfigUtils.writeBool(CONFIG_CLASS, "insanePlayersSeeMorphs", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalBool("noellesroles.voodooNonKillerDeaths", false, () -> ConfigUtils.readBool(CONFIG_CLASS, "voodooNonKillerDeaths", false), v -> {
            try {
                ConfigUtils.writeBool(CONFIG_CLASS, "voodooNonKillerDeaths", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalBool("noellesroles.voodooShotLikeEvil", true, () -> ConfigUtils.readBool(CONFIG_CLASS, "voodooShotLikeEvil", true), v -> {
            try {
                ConfigUtils.writeBool(CONFIG_CLASS, "voodooShotLikeEvil", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalBool("noellesroles.allowCivillianGuessers", false, () -> ConfigUtils.readBool(CONFIG_CLASS, "allowCivillianGuessers", false), v -> {
            try {
                ConfigUtils.writeBool(CONFIG_CLASS, "allowCivillianGuessers", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("noellesroles.playerCountToMakeConducterKeyVisible", 10, () -> ConfigUtils.readInt(CONFIG_CLASS, "playerCountToMakeConducterKeyVisible", 10), v -> {
            try {
                ConfigUtils.writeInt(CONFIG_CLASS, "playerCountToMakeConducterKeyVisible", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("noellesroles.maximumDefenseVials", 1, () -> ConfigUtils.readInt(CONFIG_CLASS, "maximumDefenseVials", 1), v -> {
            try {
                ConfigUtils.writeInt(CONFIG_CLASS, "maximumDefenseVials", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalString("noellesroles.guesserDiesAfterIncorrectGuess", "none", () -> ConfigUtils.readString(CONFIG_CLASS, "guesserDiesAfterIncorrectGuess", "none"), v -> {
            try {
                ConfigUtils.writeString(CONFIG_CLASS, "guesserDiesAfterIncorrectGuess", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalBool("noellesroles.shitpostRoles", false, () -> ConfigUtils.readBool(CONFIG_CLASS, "shitpostRoles", false), v -> {
            try {
                ConfigUtils.writeBool(CONFIG_CLASS, "shitpostRoles", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("noellesroles.generalCooldownTicks", 600, () -> ConfigUtils.readInt(CONFIG_CLASS, "generalCooldownTicks", 600), v -> {
            try {
                ConfigUtils.writeInt(CONFIG_CLASS, "generalCooldownTicks", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("noellesroles.defenseMaximumTime", -1, () -> ConfigUtils.readInt(CONFIG_CLASS, "defenseMaximumTime", -1), v -> {
            try {
                ConfigUtils.writeInt(CONFIG_CLASS, "defenseMaximumTime", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("noellesroles.introvertDisableRange", 20, () -> ConfigUtils.readInt(CONFIG_CLASS, "introvertDisableRange", 20), v -> {
            try {
                ConfigUtils.writeInt(CONFIG_CLASS, "introvertDisableRange", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("noellesroles.infectedKillTime", 1100, () -> ConfigUtils.readInt(CONFIG_CLASS, "infectedKillTime", 1100), v -> {
            try {
                ConfigUtils.writeInt(CONFIG_CLASS, "infectedKillTime", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("noellesroles.infectedCoughChance", 5, () -> ConfigUtils.readInt(CONFIG_CLASS, "infectedCoughChance", 5), v -> {
            try {
                ConfigUtils.writeInt(CONFIG_CLASS, "infectedCoughChance", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalBool("noellesroles.guesserCanUseInstinct", true, () -> ConfigUtils.readBool(CONFIG_CLASS, "guesserCanUseInstinct", true), v -> {
            try {
                ConfigUtils.writeBool(CONFIG_CLASS, "guesserCanUseInstinct", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalBool("noellesroles.trapperSeesNames", false, () -> ConfigUtils.readBool(CONFIG_CLASS, "trapperSeesNames", false), v -> {
            try {
                ConfigUtils.writeBool(CONFIG_CLASS, "trapperSeesNames", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalBool("noellesroles.reconsSeeNames", false, () -> ConfigUtils.readBool(CONFIG_CLASS, "reconsSeeNames", false), v -> {
            try {
                ConfigUtils.writeBool(CONFIG_CLASS, "reconsSeeNames", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalBool("noellesroles.executionCanPickUpGun", true, () -> ConfigUtils.readBool(CONFIG_CLASS, "executionCanPickUpGun", true), v -> {
            try {
                ConfigUtils.writeBool(CONFIG_CLASS, "executionCanPickUpGun", v);
            } catch (Throwable ignored) {
            }
        }));
    }

    public static boolean getInsanePlayersSeeMorphs(World world) {
        return ConfigUtils.clientBool("noellesroles.insanePlayersSeeMorphs", false);
    }

    public static void setInsanePlayersSeeMorphs(World world, boolean value) throws Exception {
        ConfigUtils.apply("noellesroles.insanePlayersSeeMorphs", value, world);
    }

    public static boolean getVoodooNonKillerDeaths() {
        return ConfigUtils.clientBool("noellesroles.voodooNonKillerDeaths", false);
    }

    public static void setVoodooNonKillerDeaths(boolean value) throws Exception {
        ConfigUtils.apply("noellesroles.voodooNonKillerDeaths", value, null);
    }

    public static boolean getVoodooShotLikeEvil() {
        return ConfigUtils.clientBool("noellesroles.voodooShotLikeEvil", true);
    }

    public static void setVoodooShotLikeEvil(boolean value) throws Exception {
        ConfigUtils.apply("noellesroles.voodooShotLikeEvil", value, null);
    }

    public static int getPlayerCountToMakeConducterKeyVisible() {
        return ConfigUtils.clientInt("noellesroles.playerCountToMakeConducterKeyVisible", 10);
    }

    public static void setPlayerCountToMakeConducterKeyVisible(int value) throws Exception {
        ConfigUtils.apply("noellesroles.playerCountToMakeConducterKeyVisible", value, null);
    }

    public static int getMaximumDefenseVials() {
        return ConfigUtils.clientInt("noellesroles.maximumDefenseVials", 1);
    }

    public static void setMaximumDefenseVials(int v) throws Exception {
        ConfigUtils.apply("noellesroles.maximumDefenseVials", v, null);
    }

    public static int getDefenseVialPrice() {
        return ConfigUtils.clientInt("noellesroles.defenseVialPrice", 200);
    }

    public static void setDefenseVialPrice(int v) throws Exception {
        ConfigUtils.apply("noellesroles.defenseVialPrice", v, null);
    }

    public static int getRoleMinePrice() {
        return ConfigUtils.clientInt("noellesroles.roleMinePrice", 100);
    }

    public static void setRoleMinePrice(int v) throws Exception {
        ConfigUtils.apply("noellesroles.roleMinePrice", v, null);
    }

    public static boolean getAllowCivillianGuessers() {
        return ConfigUtils.clientBool("noellesroles.allowCivillianGuessers", false);
    }

    public static void setAllowCivillianGuessers(boolean v) throws Exception {
        ConfigUtils.apply("noellesroles.allowCivillianGuessers", v, null);
    }

    public static String getGuesserDiesAfterIncorrectGuess() {
        return ConfigUtils.clientString("noellesroles.guesserDiesAfterIncorrectGuess", "death");
    }

    public static void setGuesserDiesAfterIncorrectGuess(String v) throws Exception {
        ConfigUtils.apply("noellesroles.guesserDiesAfterIncorrectGuess", v, null);
    }

    public static int getDelusionVialPrice() {
        return ConfigUtils.clientInt("noellesroles.delusionVialPrice", 30);
    }

    public static void setDelusionVialPrice(int v) throws Exception {
        ConfigUtils.apply("noellesroles.delusionVialPrice", v, null);
    }

    public static boolean getShitpostRoles() {
        return ConfigUtils.clientBool("noellesroles.shitpostRoles", false);
    }

    public static void setShitpostRoles(boolean v) throws Exception {
        ConfigUtils.apply("noellesroles.shitpostRoles", v, null);
    }

    public static int getGeneralCooldownTicks() {
        return ConfigUtils.clientInt("noellesroles.generalCooldownTicks", 600);
    }

    public static void setGeneralCooldownTicks(int v) throws Exception {
        ConfigUtils.apply("noellesroles.generalCooldownTicks", v, null);
    }

    public static int getDefenseMaximumTime() {
        return ConfigUtils.clientInt("noellesroles.defenseMaximumTime", -1);
    }

    public static void setDefenseMaximumTime(int v) throws Exception {
        ConfigUtils.apply("noellesroles.defenseMaximumTime", v, null);
    }

    public static int getIntrovertDisableRange() {
        return ConfigUtils.clientInt("noellesroles.introvertDisableRange", 20);
    }

    public static void setIntrovertDisableRange(int v) throws Exception {
        ConfigUtils.apply("noellesroles.introvertDisableRange", v, null);
    }

    public static int getInfectedKillTime() {
        return ConfigUtils.clientInt("noellesroles.infectedKillTime", 1100);
    }

    public static void setInfectedKillTime(int v) throws Exception {
        ConfigUtils.apply("noellesroles.infectedKillTime", v, null);
    }

    public static int getInfectedCoughChance() {
        return ConfigUtils.clientInt("noellesroles.infectedCoughChance", 5);
    }

    public static void setInfectedCoughChance(int v) throws Exception {
        ConfigUtils.apply("noellesroles.infectedCoughChance", v, null);
    }

    public static boolean getGuesserCanUseInstinct() {
        return ConfigUtils.clientBool("noellesroles.guesserCanUseInstinct", true);
    }

    public static void setGuesserCanUseInstinct(boolean v) throws Exception {
        ConfigUtils.apply("noellesroles.guesserCanUseInstinct", v, null);
    }

    public static boolean getTrapperSeesNames() {
        return ConfigUtils.clientBool("noellesroles.trapperSeesNames", false);
    }

    public static void setTrapperSeesNames(boolean v) throws Exception {
        ConfigUtils.apply("noellesroles.trapperSeesNames", v, null);
    }

    public static boolean getReconsSeeNames() {
        return ConfigUtils.clientBool("noellesroles.reconsSeeNames", false);
    }

    public static void setReconsSeeNames(boolean v) throws Exception {
        ConfigUtils.apply("noellesroles.reconsSeeNames", v, null);
    }

    public static boolean getExecutionCanPickUpGun() {
        return ConfigUtils.clientBool("noellesroles.executionCanPickUpGun", true);
    }

    public static void setExecutionCanPickUpGun(boolean v) throws Exception {
        ConfigUtils.apply("noellesroles.executionCanPickUpGun", v, null);
    }
}
