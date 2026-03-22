package cat.rezelyn.watheextended.api.hml;

import cat.rezelyn.watheextended.api.config.ClientConfig;
import cat.rezelyn.watheextended.api.config.ServerConfig;
import cat.rezelyn.watheextended.api.config.ServerConfig.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.agmas.harpymodloader.config.HarpyModLoaderConfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public final class ConfigHelper {

    private ConfigHelper() {}

    private static HarpyModLoaderConfig getInstance() {
        return HarpyModLoaderConfig.HANDLER.instance();
    }

    private static void save() {
        HarpyModLoaderConfig.HANDLER.save();
    }

    public static void registerEntries() {
        Function<List<String>, String> listSer = list -> list == null || list.isEmpty() ? "" : String.join(",", list);
        Function<String, List<String>> listDeser = s -> s == null || s.isEmpty() ? List.of() : Arrays.asList(s.split(",", -1));

        ServerConfig.register(Entry.global(
                "hml.disabled", List.<String>of(),
                () -> {
                    try {
                        List<String> v = getInstance().disabled;
                        return v != null ? Collections.unmodifiableList(v) : List.of();
                    } catch (Throwable t) {
                        return List.of();
                    }
                },
                v -> {
                    try {
                        getInstance().disabled = new java.util.ArrayList<>(v);
                        save();
                    } catch (Throwable ignored) {
                    }
                },
                listSer, listDeser));

        ServerConfig.register(Entry.global(
                "hml.disabledModifiers", List.<String>of(),
                () -> {
                    try {
                        List<String> v = getInstance().disabledModifiers;
                        return v != null ? Collections.unmodifiableList(v) : List.of();
                    } catch (Throwable t) {
                        return List.of();
                    }
                },
                v -> {
                    try {
                        getInstance().disabledModifiers = new java.util.ArrayList<>(v);
                        save();
                    } catch (Throwable ignored) {
                    }
                },
                listSer, listDeser));

        ServerConfig.register(Entry.globalInt("hml.modifierMaximum", 1,
                () -> {
                    try {
                        return getInstance().modifierMaximum;
                    } catch (Throwable t) {
                        return 1;
                    }
                },
                v -> {
                    try {
                        getInstance().modifierMaximum = v;
                        save();
                    } catch (Throwable ignored) {
                    }
                }));

        ServerConfig.register(Entry.globalInt("hml.modifierMultiplier", 1,
                () -> {
                    try {
                        return getInstance().modifierMultiplier;
                    } catch (Throwable t) {
                        return 1;
                    }
                },
                v -> {
                    try {
                        getInstance().modifierMultiplier = v;
                        save();
                    } catch (Throwable ignored) {
                    }
                }));
    }

    public static List<String> getDisabledRoles() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            return ClientConfig.getStringList("hml.disabled");
        try {
            List<String> v = getInstance().disabled;
            return v != null ? Collections.unmodifiableList(v) : List.of();
        } catch (Throwable t) {
            return List.of();
        }
    }

    public static List<String> getDisabledModifiers() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            return ClientConfig.getStringList("hml.disabledModifiers");
        try {
            List<String> v = getInstance().disabledModifiers;
            return v != null ? Collections.unmodifiableList(v) : List.of();
        } catch (Throwable t) {
            return List.of();
        }
    }

    public static int getModifierMaximum() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            return ClientConfig.getInt("hml.modifierMaximum", 1);
        try {
            return getInstance().modifierMaximum;
        } catch (Throwable t) {
            return 1;
        }
    }

    public static void setModifierMaximum(int value) {
        try {
            getInstance().modifierMaximum = value;
            save();
        } catch (Throwable ignored) {
        }
    }

    public static int getModifierMultiplier() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            return ClientConfig.getInt("hml.modifierMultiplier", 1);
        try {
            return getInstance().modifierMultiplier;
        } catch (Throwable t) {
            return 1;
        }
    }

    public static void setModifierMultiplier(int value) {
        try {
            getInstance().modifierMultiplier = value;
            save();
        } catch (Throwable ignored) {
        }
    }
}
