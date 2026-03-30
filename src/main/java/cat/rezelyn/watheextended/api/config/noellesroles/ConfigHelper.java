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
        ServerConfig.register(Entry.globalInt("noellesroles.defenseVialPrice", 200, () -> ConfigUtils.readInt(CONFIG_CLASS, "defenseVialPrice", 200), v -> {
            try {
                ConfigUtils.writeInt(CONFIG_CLASS, "defenseVialPrice", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("noellesroles.roleMinePrice", 100, () -> ConfigUtils.readInt(CONFIG_CLASS, "roleMinePrice", 100), v -> {
            try {
                ConfigUtils.writeInt(CONFIG_CLASS, "roleMinePrice", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalString("noellesroles.guesserDiesAfterIncorrectGuess", "none", () -> ConfigUtils.readString(CONFIG_CLASS, "guesserDiesAfterIncorrectGuess", "none"), v -> {
            try {
                ConfigUtils.writeString(CONFIG_CLASS, "guesserDiesAfterIncorrectGuess", v);
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
}
