package cat.rezelyn.watheextended.api.hml.config;

import blue.endless.jankson.*;
import net.fabricmc.loader.api.FabricLoader;
import org.agmas.harpymodloader.config.HarpyModLoaderConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DisabledModifiers {

    private static final File CONFIG_FILE = FabricLoader.getInstance()
            .getConfigDir().resolve("harpymodloader.json5").toFile();

    private DisabledModifiers() {
    }

    public static List<String> get() {
        try {
            List<String> live = HarpyModLoaderConfig.HANDLER.instance().disabledModifiers;
            if (live != null) return Collections.unmodifiableList(live);
        } catch (Throwable ignored) {
        }

        try {
            if (!CONFIG_FILE.exists()) return List.of();
            JsonObject obj = Jankson.builder().build().load(CONFIG_FILE);
            JsonElement el = obj.get("disabledModifiers");
            if (!(el instanceof JsonArray arr)) return List.of();
            List<String> result = new ArrayList<>();
            for (JsonElement item : arr)
                if (item instanceof JsonPrimitive prim) result.add(prim.asString());
            return Collections.unmodifiableList(result);
        } catch (Throwable ignored) {
            return List.of();
        }
    }
}
