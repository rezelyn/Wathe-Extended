package cat.rezelyn.watheextended.api.config.starexpress;

import cat.rezelyn.watheextended.api.config.ConfigUtils;
import cat.rezelyn.watheextended.api.config.ServerConfig;
import cat.rezelyn.watheextended.api.config.ServerConfig.Entry;
import net.fabricmc.loader.api.FabricLoader;

public final class ConfigHelper {

    private ConfigHelper() {}

    public static boolean isLoaded() {
        return FabricLoader.getInstance().isModLoaded("starexpress");
    }

    private static final String CONFIG_CLASS = "org.aussiebox.starexpress.StarryExpress";

    private static dev.doctor4t.wathe.api.Role cachedStarstruckRole;
    private static boolean starstruckRoleCached = false;

    private static dev.doctor4t.wathe.api.Role getStarstruckRole() {
        if (!starstruckRoleCached) {
            starstruckRoleCached = true;
            if (!isLoaded()) return null;
            try {
                cachedStarstruckRole = (dev.doctor4t.wathe.api.Role) ConfigUtils.getStaticField("org.aussiebox.starexpress.StarryExpressRoles", "STARSTRUCK");
            } catch (Throwable ignored) {
            }
        }
        return cachedStarstruckRole;
    }

    public static boolean isStarstruckRole(dev.doctor4t.wathe.api.Role role) {
        if (role == null) return false;
        dev.doctor4t.wathe.api.Role starstruck = getStarstruckRole();
        return starstruck != null && role == starstruck;
    }

    private static Object getSection(String sectionField) throws Exception {
        return ConfigUtils.getField(ConfigUtils.getStaticField(CONFIG_CLASS, "CONFIG"), sectionField);
    }

    private static boolean readBoolServer(String section, String method, boolean def) {
        try {
            return (boolean) ConfigUtils.invoke(getSection(section), method);
        } catch (Throwable t) {
            return def;
        }
    }

    private static void writeBoolServer(String section, String method, boolean value) throws Exception {
        ConfigUtils.invokeWith(getSection(section), method, boolean.class, value);
    }

    private static int readIntServer(String section, String method, int def) {
        try {
            return (int) ConfigUtils.invoke(getSection(section), method);
        } catch (Throwable t) {
            return def;
        }
    }

    private static void writeIntServer(String section, String method, int value) throws Exception {
        ConfigUtils.invokeWith(getSection(section), method, int.class, value);
    }

    private static float readFloatServer(String section, String method, float def) {
        try {
            Object result = ConfigUtils.invoke(getSection(section), method);
            if (result instanceof Float f) return f;
            if (result instanceof Double d) return d.floatValue();
            return def;
        } catch (Throwable t) {
            return def;
        }
    }

    private static void writeFloatServer(String section, String method, float value) throws Exception {
        Object sec = getSection(section);
        try {
            ConfigUtils.invokeWith(sec, method, float.class, value);
        } catch (NoSuchMethodException e) {
            ConfigUtils.invokeWith(sec, method, double.class, (double) value);
        }
    }

    public static void registerEntries() {
        if (!isLoaded()) return;

        // Starstruck
        ServerConfig.register(Entry.globalBool("starexpress.taskReducesCooldown", true, () -> readBoolServer("starstruckConfig", "taskReducesCooldown", true), v -> {
            try {
                writeBoolServer("starstruckConfig", "taskReducesCooldown", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("starexpress.taskCooldownReduction", 5, () -> readIntServer("starstruckConfig", "taskCooldownReduction", 5), v -> {
            try {
                writeIntServer("starstruckConfig", "taskCooldownReduction", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("starexpress.abilityCooldown", 90, () -> readIntServer("starstruckConfig", "abilityCooldown", 90), v -> {
            try {
                writeIntServer("starstruckConfig", "abilityCooldown", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("starexpress.abilityDuration", 15, () -> readIntServer("starstruckConfig", "abilityDuration", 15), v -> {
            try {
                writeIntServer("starstruckConfig", "abilityDuration", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalBool("starexpress.abilityAffectsMovementSpeed", true, () -> readBoolServer("starstruckConfig", "abilityAffectsMovementSpeed", true), v -> {
            try {
                writeBoolServer("starstruckConfig", "abilityAffectsMovementSpeed", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.global("starexpress.abilityWalkSpeed", 0.12f, () -> readFloatServer("starstruckConfig", "abilityWalkSpeed", 0.12f), v -> {
            try {
                writeFloatServer("starstruckConfig", "abilityWalkSpeed", v);
            } catch (Throwable ignored) {
            }
        }, Object::toString, s -> {
            try {
                return Float.parseFloat(s);
            } catch (NumberFormatException e) {
                return 0.12f;
            }
        }));
        ServerConfig.register(Entry.global("starexpress.abilitySprintSpeed", 0.15f, () -> readFloatServer("starstruckConfig", "abilitySprintSpeed", 0.15f), v -> {
            try {
                writeFloatServer("starstruckConfig", "abilitySprintSpeed", v);
            } catch (Throwable ignored) {
            }
        }, Object::toString, s -> {
            try {
                return Float.parseFloat(s);
            } catch (NumberFormatException e) {
                return 0.15f;
            }
        }));

        // Muzzler
        ServerConfig.register(Entry.globalInt("starexpress.tapeCooldown", 20, () -> readIntServer("muzzlerConfig", "tapeCooldown", 20), v -> {
            try {
                writeIntServer("muzzlerConfig", "tapeCooldown", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("starexpress.suffocationTime", 60, () -> readIntServer("muzzlerConfig", "suffocationTime", 60), v -> {
            try {
                writeIntServer("muzzlerConfig", "suffocationTime", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("starexpress.tapeTearCheckCount", 5, () -> readIntServer("muzzlerConfig", "tapeTearCheckCount", 5), v -> {
            try {
                writeIntServer("muzzlerConfig", "tapeTearCheckCount", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.global("starexpress.tapeTearMoodChange", 0.1f, () -> readFloatServer("muzzlerConfig", "tapeTearMoodChange", 0.1f), v -> {
            try {
                writeFloatServer("muzzlerConfig", "tapeTearMoodChange", v);
            } catch (Throwable ignored) {
            }
        }, Object::toString, s -> {
            try {
                return Float.parseFloat(s);
            } catch (NumberFormatException e) {
                return 0.1f;
            }
        }));
        ServerConfig.register(Entry.globalBool("starexpress.killIfCheckedAtZero", true, () -> readBoolServer("muzzlerConfig", "killIfCheckedAtZero", true), v -> {
            try {
                writeBoolServer("muzzlerConfig", "killIfCheckedAtZero", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("starexpress.displaySilencedTipDelay", 120, () -> readIntServer("muzzlerConfig", "displaySilencedTipDelay", 120), v -> {
            try {
                writeIntServer("muzzlerConfig", "displaySilencedTipDelay", v);
            } catch (Throwable ignored) {
            }
        }));

        // Allergic
        ServerConfig.register(Entry.globalInt("starexpress.nothingChance", 3, () -> readIntServer("allergicConfig", "nothingChance", 3), v -> {
            try {
                writeIntServer("allergicConfig", "nothingChance", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("starexpress.instinctChance", 1, () -> readIntServer("allergicConfig", "instinctChance", 1), v -> {
            try {
                writeIntServer("allergicConfig", "instinctChance", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("starexpress.armorChance", 1, () -> readIntServer("allergicConfig", "armorChance", 1), v -> {
            try {
                writeIntServer("allergicConfig", "armorChance", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("starexpress.poisonChance", 1, () -> readIntServer("allergicConfig", "poisonChance", 1), v -> {
            try {
                writeIntServer("allergicConfig", "poisonChance", v);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("starexpress.instinctDuration", 3, () -> readIntServer("allergicConfig", "instinctDuration", 3), v -> {
            try {
                writeIntServer("allergicConfig", "instinctDuration", v);
            } catch (Throwable ignored) {
            }
        }));
    }

    public static boolean getStarstruckTaskReducesCooldown() {
        return ConfigUtils.clientBool("starexpress.taskReducesCooldown", true);
    }

    public static void setStarstruckTaskReducesCooldown(boolean v) throws Exception {
        ConfigUtils.apply("starexpress.taskReducesCooldown", v, null);
    }

    public static int getStarstruckTaskCooldownReduction() {
        return ConfigUtils.clientInt("starexpress.taskCooldownReduction", 5);
    }

    public static void setStarstruckTaskCooldownReduction(int v) throws Exception {
        ConfigUtils.apply("starexpress.taskCooldownReduction", v, null);
    }

    public static int getStarstruckAbilityCooldown() {
        return ConfigUtils.clientInt("starexpress.abilityCooldown", 90);
    }

    public static void setStarstruckAbilityCooldown(int v) throws Exception {
        ConfigUtils.apply("starexpress.abilityCooldown", v, null);
    }

    public static int getStarstruckAbilityDuration() {
        return ConfigUtils.clientInt("starexpress.abilityDuration", 15);
    }

    public static void setStarstruckAbilityDuration(int v) throws Exception {
        ConfigUtils.apply("starexpress.abilityDuration", v, null);
    }

    public static boolean getStarstruckAbilityAffectsMovementSpeed() {
        return ConfigUtils.clientBool("starexpress.abilityAffectsMovementSpeed", true);
    }

    public static void setStarstruckAbilityAffectsMovementSpeed(boolean v) throws Exception {
        ConfigUtils.apply("starexpress.abilityAffectsMovementSpeed", v, null);
    }

    public static float getStarstruckAbilityWalkSpeed() {
        return ConfigUtils.clientFloat("starexpress.abilityWalkSpeed", 0.12f);
    }

    public static void setStarstruckAbilityWalkSpeed(float v) throws Exception {
        ConfigUtils.apply("starexpress.abilityWalkSpeed", v, null);
    }

    public static float getStarstruckAbilitySprintSpeed() {
        return ConfigUtils.clientFloat("starexpress.abilitySprintSpeed", 0.15f);
    }

    public static void setStarstruckAbilitySprintSpeed(float v) throws Exception {
        ConfigUtils.apply("starexpress.abilitySprintSpeed", v, null);
    }

    public static int getMuzzlerTapeCooldown() {
        return ConfigUtils.clientInt("starexpress.tapeCooldown", 20);
    }

    public static void setMuzzlerTapeCooldown(int v) throws Exception {
        ConfigUtils.apply("starexpress.tapeCooldown", v, null);
    }

    public static int getMuzzlerSuffocationTime() {
        return ConfigUtils.clientInt("starexpress.suffocationTime", 60);
    }

    public static void setMuzzlerSuffocationTime(int v) throws Exception {
        ConfigUtils.apply("starexpress.suffocationTime", v, null);
    }

    public static int getMuzzlerTapeTearCheckCount() {
        return ConfigUtils.clientInt("starexpress.tapeTearCheckCount", 5);
    }

    public static void setMuzzlerTapeTearCheckCount(int v) throws Exception {
        ConfigUtils.apply("starexpress.tapeTearCheckCount", v, null);
    }

    public static float getMuzzlerTapeTearMoodChange() {
        return ConfigUtils.clientFloat("starexpress.tapeTearMoodChange", 0.1f);
    }

    public static void setMuzzlerTapeTearMoodChange(float v) throws Exception {
        ConfigUtils.apply("starexpress.tapeTearMoodChange", v, null);
    }

    public static boolean getMuzzlerKillIfCheckedAtZero() {
        return ConfigUtils.clientBool("starexpress.killIfCheckedAtZero", true);
    }

    public static void setMuzzlerKillIfCheckedAtZero(boolean v) throws Exception {
        ConfigUtils.apply("starexpress.killIfCheckedAtZero", v, null);
    }

    public static int getMuzzlerDisplaySilencedTipDelay() {
        return ConfigUtils.clientInt("starexpress.displaySilencedTipDelay", 120);
    }

    public static void setMuzzlerDisplaySilencedTipDelay(int v) throws Exception {
        ConfigUtils.apply("starexpress.displaySilencedTipDelay", v, null);
    }

    public static int getAllergicNothingChance() {
        return ConfigUtils.clientInt("starexpress.nothingChance", 3);
    }

    public static void setAllergicNothingChance(int v) throws Exception {
        ConfigUtils.apply("starexpress.nothingChance", v, null);
    }

    public static int getAllergicInstinctChance() {
        return ConfigUtils.clientInt("starexpress.instinctChance", 1);
    }

    public static void setAllergicInstinctChance(int v) throws Exception {
        ConfigUtils.apply("starexpress.instinctChance", v, null);
    }

    public static int getAllergicArmorChance() {
        return ConfigUtils.clientInt("starexpress.armorChance", 1);
    }

    public static void setAllergicArmorChance(int v) throws Exception {
        ConfigUtils.apply("starexpress.armorChance", v, null);
    }

    public static int getAllergicPoisonChance() {
        return ConfigUtils.clientInt("starexpress.poisonChance", 1);
    }

    public static void setAllergicPoisonChance(int v) throws Exception {
        ConfigUtils.apply("starexpress.poisonChance", v, null);
    }

    public static int getAllergicInstinctDuration() {
        return ConfigUtils.clientInt("starexpress.instinctDuration", 3);
    }

    public static void setAllergicInstinctDuration(int v) throws Exception {
        ConfigUtils.apply("starexpress.instinctDuration", v, null);
    }
}
