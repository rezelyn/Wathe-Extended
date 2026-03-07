package cat.rezelyn.watheextended.api.starexpress;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;

public final class ConfigHelper {

    private static final File CONFIG_FILE = FabricLoader.getInstance()
            .getConfigDir().resolve("starexpress-server.json5").toFile();

    private ConfigHelper() {
    }

    public static boolean isLoaded() {
        return FabricLoader.getInstance().isModLoaded("starexpress");
    }

    private static Object getConfigInstance() throws Exception {
        Class<?> cls = Class.forName("org.aussiebox.starexpress.config.StarExpressServerConfig");
        Object handler = cls.getField("HANDLER").get(null);
        return handler.getClass().getMethod("instance").invoke(handler);
    }

    private static void saveConfig() throws Exception {
        Class<?> cls = Class.forName("org.aussiebox.starexpress.config.StarExpressServerConfig");
        Object handler = cls.getField("HANDLER").get(null);
        handler.getClass().getMethod("save").invoke(handler);
    }

    private static Object getSection(String sectionField) throws Exception {
        Object cfg = getConfigInstance();
        return cfg.getClass().getField(sectionField).get(cfg);
    }

    private static JsonObject readFile() {
        try {
            if (!CONFIG_FILE.exists()) return new JsonObject();
            return Jankson.builder().build().load(CONFIG_FILE);
        } catch (Throwable t) {
            return new JsonObject();
        }
    }

    private static boolean readFileBool(String section, String key, boolean def) {
        try {
            JsonObject root = readFile();
            JsonElement sec = root.get(section);
            if (!(sec instanceof JsonObject obj)) return def;
            JsonElement el = obj.get(key);
            if (el instanceof JsonPrimitive p) return Boolean.parseBoolean(p.asString());
        } catch (Throwable ignored) {
        }
        return def;
    }

    private static int readFileInt(String section, String key, int def) {
        try {
            JsonObject root = readFile();
            JsonElement sec = root.get(section);
            if (!(sec instanceof JsonObject obj)) return def;
            JsonElement el = obj.get(key);
            if (el instanceof JsonPrimitive p) return Integer.parseInt(p.asString());
        } catch (Throwable ignored) {
        }
        return def;
    }

    private static float readFileFloat(String section, String key, float def) {
        try {
            JsonObject root = readFile();
            JsonElement sec = root.get(section);
            if (!(sec instanceof JsonObject obj)) return def;
            JsonElement el = obj.get(key);
            if (el instanceof JsonPrimitive p) return Float.parseFloat(p.asString());
        } catch (Throwable ignored) {
        }
        return def;
    }

    private static boolean readLiveBool(String sectionField, String fieldName, boolean def) {
        try {
            Object section = getSection(sectionField);
            return (boolean) section.getClass().getField(fieldName).get(section);
        } catch (Throwable t) {
            return def;
        }
    }

    private static int readLiveInt(String sectionField, String fieldName, int def) {
        try {
            Object section = getSection(sectionField);
            return (int) section.getClass().getField(fieldName).get(section);
        } catch (Throwable t) {
            return def;
        }
    }

    private static float readLiveFloat(String sectionField, String fieldName, float def) {
        try {
            Object section = getSection(sectionField);
            Object val = section.getClass().getField(fieldName).get(section);
            if (val instanceof Float f) return f;
            if (val instanceof Double d) return d.floatValue();
        } catch (Throwable ignored) {
        }
        return def;
    }

    private static void setLiveBool(String sectionField, String fieldName, boolean value) throws Exception {
        Object section = getSection(sectionField);
        section.getClass().getField(fieldName).set(section, value);
        saveConfig();
    }

    private static void setLiveInt(String sectionField, String fieldName, int value) throws Exception {
        Object section = getSection(sectionField);
        section.getClass().getField(fieldName).set(section, value);
        saveConfig();
    }

    private static void setLiveFloat(String sectionField, String fieldName, float value) throws Exception {
        Object section = getSection(sectionField);
        try {
            section.getClass().getField(fieldName).set(section, value);
        } catch (IllegalArgumentException e) {
            section.getClass().getField(fieldName).set(section, (double) value);
        }
        saveConfig();
    }

    public static boolean getStarstruckTaskReducesCooldown() {
        boolean live = readLiveBool("starstruckConfig", "taskReducesCooldown", true);
        try { getSection("starstruckConfig"); return live; } catch (Throwable t) {}
        return readFileBool("starstruckConfig", "taskReducesCooldown", true);
    }

    public static void setStarstruckTaskReducesCooldown(boolean value) throws Exception {
        setLiveBool("starstruckConfig", "taskReducesCooldown", value);
    }

    public static int getStarstruckTaskCooldownReduction() {
        try { getSection("starstruckConfig"); return readLiveInt("starstruckConfig", "taskCooldownReduction", 5); } catch (Throwable t) {}
        return readFileInt("starstruckConfig", "taskCooldownReduction", 5);
    }

    public static void setStarstruckTaskCooldownReduction(int value) throws Exception {
        setLiveInt("starstruckConfig", "taskCooldownReduction", value);
    }

    public static int getStarstruckAbilityCooldown() {
        try { getSection("starstruckConfig"); return readLiveInt("starstruckConfig", "abilityCooldown", 90); } catch (Throwable t) {}
        return readFileInt("starstruckConfig", "abilityCooldown", 90);
    }

    public static void setStarstruckAbilityCooldown(int value) throws Exception {
        setLiveInt("starstruckConfig", "abilityCooldown", value);
    }

    public static int getStarstruckAbilityDuration() {
        try { getSection("starstruckConfig"); return readLiveInt("starstruckConfig", "abilityDuration", 15); } catch (Throwable t) {}
        return readFileInt("starstruckConfig", "abilityDuration", 15);
    }

    public static void setStarstruckAbilityDuration(int value) throws Exception {
        setLiveInt("starstruckConfig", "abilityDuration", value);
    }

    public static boolean getStarstruckAbilityAffectsMovementSpeed() {
        try { getSection("starstruckConfig"); return readLiveBool("starstruckConfig", "abilityAffectsMovementSpeed", true); } catch (Throwable t) {}
        return readFileBool("starstruckConfig", "abilityAffectsMovementSpeed", true);
    }

    public static void setStarstruckAbilityAffectsMovementSpeed(boolean value) throws Exception {
        setLiveBool("starstruckConfig", "abilityAffectsMovementSpeed", value);
    }

    public static float getStarstruckAbilityWalkSpeed() {
        try { getSection("starstruckConfig"); return readLiveFloat("starstruckConfig", "abilityWalkSpeed", 0.12f); } catch (Throwable t) {}
        return readFileFloat("starstruckConfig", "abilityWalkSpeed", 0.12f);
    }

    public static void setStarstruckAbilityWalkSpeed(float value) throws Exception {
        setLiveFloat("starstruckConfig", "abilityWalkSpeed", value);
    }

    public static float getStarstruckAbilitySprintSpeed() {
        try { getSection("starstruckConfig"); return readLiveFloat("starstruckConfig", "abilitySprintSpeed", 0.15f); } catch (Throwable t) {}
        return readFileFloat("starstruckConfig", "abilitySprintSpeed", 0.15f);
    }

    public static void setStarstruckAbilitySprintSpeed(float value) throws Exception {
        setLiveFloat("starstruckConfig", "abilitySprintSpeed", value);
    }

    public static int getMuzzlerTapeCooldown() {
        try { getSection("muzzlerConfig"); return readLiveInt("muzzlerConfig", "tapeCooldown", 20); } catch (Throwable t) {}
        return readFileInt("muzzlerConfig", "tapeCooldown", 20);
    }

    public static void setMuzzlerTapeCooldown(int value) throws Exception {
        setLiveInt("muzzlerConfig", "tapeCooldown", value);
    }

    public static int getMuzzlerSuffocationTime() {
        try { getSection("muzzlerConfig"); return readLiveInt("muzzlerConfig", "suffocationTime", 60); } catch (Throwable t) {}
        return readFileInt("muzzlerConfig", "suffocationTime", 60);
    }

    public static void setMuzzlerSuffocationTime(int value) throws Exception {
        setLiveInt("muzzlerConfig", "suffocationTime", value);
    }

    public static int getMuzzlerTapeTearCheckCount() {
        try { getSection("muzzlerConfig"); return readLiveInt("muzzlerConfig", "tapeTearCheckCount", 5); } catch (Throwable t) {}
        return readFileInt("muzzlerConfig", "tapeTearCheckCount", 5);
    }

    public static void setMuzzlerTapeTearCheckCount(int value) throws Exception {
        setLiveInt("muzzlerConfig", "tapeTearCheckCount", value);
    }

    public static float getMuzzlerTapeTearMoodChange() {
        try { getSection("muzzlerConfig"); return readLiveFloat("muzzlerConfig", "tapeTearMoodChange", 0.1f); } catch (Throwable t) {}
        return readFileFloat("muzzlerConfig", "tapeTearMoodChange", 0.1f);
    }

    public static void setMuzzlerTapeTearMoodChange(float value) throws Exception {
        setLiveFloat("muzzlerConfig", "tapeTearMoodChange", value);
    }

    public static boolean getMuzzlerKillIfCheckedAtZero() {
        try { getSection("muzzlerConfig"); return readLiveBool("muzzlerConfig", "killIfCheckedAtZero", true); } catch (Throwable t) {}
        return readFileBool("muzzlerConfig", "killIfCheckedAtZero", true);
    }

    public static void setMuzzlerKillIfCheckedAtZero(boolean value) throws Exception {
        setLiveBool("muzzlerConfig", "killIfCheckedAtZero", value);
    }

    public static int getMuzzlerDisplaySilencedTipDelay() {
        try { getSection("muzzlerConfig"); return readLiveInt("muzzlerConfig", "displaySilencedTipDelay", 120); } catch (Throwable t) {}
        return readFileInt("muzzlerConfig", "displaySilencedTipDelay", 120);
    }

    public static void setMuzzlerDisplaySilencedTipDelay(int value) throws Exception {
        setLiveInt("muzzlerConfig", "displaySilencedTipDelay", value);
    }

    public static int getAllergicNothingChance() {
        try { getSection("allergicConfig"); return readLiveInt("allergicConfig", "nothingChance", 3); } catch (Throwable t) {}
        return readFileInt("allergicConfig", "nothingChance", 3);
    }

    public static void setAllergicNothingChance(int value) throws Exception {
        setLiveInt("allergicConfig", "nothingChance", value);
    }

    public static int getAllergicInstinctChance() {
        try { getSection("allergicConfig"); return readLiveInt("allergicConfig", "instinctChance", 1); } catch (Throwable t) {}
        return readFileInt("allergicConfig", "instinctChance", 1);
    }

    public static void setAllergicInstinctChance(int value) throws Exception {
        setLiveInt("allergicConfig", "instinctChance", value);
    }

    public static int getAllergicArmorChance() {
        try { getSection("allergicConfig"); return readLiveInt("allergicConfig", "armorChance", 1); } catch (Throwable t) {}
        return readFileInt("allergicConfig", "armorChance", 1);
    }

    public static void setAllergicArmorChance(int value) throws Exception {
        setLiveInt("allergicConfig", "armorChance", value);
    }

    public static int getAllergicPoisonChance() {
        try { getSection("allergicConfig"); return readLiveInt("allergicConfig", "poisonChance", 1); } catch (Throwable t) {}
        return readFileInt("allergicConfig", "poisonChance", 1);
    }

    public static void setAllergicPoisonChance(int value) throws Exception {
        setLiveInt("allergicConfig", "poisonChance", value);
    }

    public static int getAllergicInstinctDuration() {
        try { getSection("allergicConfig"); return readLiveInt("allergicConfig", "instinctDuration", 3); } catch (Throwable t) {}
        return readFileInt("allergicConfig", "instinctDuration", 3);
    }

    public static void setAllergicInstinctDuration(int value) throws Exception {
        setLiveInt("allergicConfig", "instinctDuration", value);
    }
}

