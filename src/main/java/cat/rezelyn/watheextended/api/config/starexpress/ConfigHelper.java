package cat.rezelyn.watheextended.api.config.starexpress;

import cat.rezelyn.watheextended.api.config.ConfigUtils;
import cat.rezelyn.watheextended.api.config.ServerConfig;
import cat.rezelyn.watheextended.api.config.ServerConfig.Entry;
import dev.doctor4t.wathe.api.Role;
import net.fabricmc.loader.api.FabricLoader;

public final class ConfigHelper {

    private ConfigHelper() {}

    public static boolean isLoaded() {
        return FabricLoader.getInstance().isModLoaded("starexpress");
    }

    private static final String CONFIG_CLASS = "org.aussiebox.starexpress.StarryExpress";

    private static Role cachedStarstruckRole;
    private static boolean starstruckRoleCached = false;

    private static Role getStarstruckRole() {
        if (!starstruckRoleCached) {
            starstruckRoleCached = true;
            if (!isLoaded()) return null;
            try {
                cachedStarstruckRole = (Role) ConfigUtils.getStaticField("org.aussiebox.starexpress.StarryExpressRoles", "STARSTRUCK");
            } catch (Throwable ignored) {
            }
        }
        return cachedStarstruckRole;
    }

    public static boolean isStarstruckRole(Role role) {
        if (role == null) return false;
        Role starstruck = getStarstruckRole();
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

    private static float readFloatServer(String string, String method, float def) {
        try {
            Object result = ConfigUtils.invoke(getSection(string), method);
            if (result instanceof Float f) return f;
            if (result instanceof Double d) return d.floatValue();
            return def;
        } catch (Throwable t) {
            return def;
        }
    }

    private static void writeFloatServer(String string, String method, float value) throws Exception {
        Object section = getSection(string);
        try {
            ConfigUtils.invokeWith(section, method, float.class, value);
        } catch (NoSuchMethodException e) {
            ConfigUtils.invokeWith(section, method, double.class, (double) value);
        }
    }

    public static void registerEntries() {
        if (!isLoaded()) return;

        // Starstruck
        ServerConfig.register(Entry.globalBool("starexpress.taskReducesCooldown", true, () -> readBoolServer("starstruckConfig", "taskReducesCooldown", true), value -> {
            try {
                writeBoolServer("starstruckConfig", "taskReducesCooldown", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("starexpress.taskCooldownReduction", 5, () -> readIntServer("starstruckConfig", "taskCooldownReduction", 5), value -> {
            try {
                writeIntServer("starstruckConfig", "taskCooldownReduction", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("starexpress.abilityCooldown", 90, () -> readIntServer("starstruckConfig", "abilityCooldown", 90), value -> {
            try {
                writeIntServer("starstruckConfig", "abilityCooldown", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("starexpress.abilityDuration", 15, () -> readIntServer("starstruckConfig", "abilityDuration", 15), value -> {
            try {
                writeIntServer("starstruckConfig", "abilityDuration", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalBool("starexpress.abilityAffectsMovementSpeed", true, () -> readBoolServer("starstruckConfig", "abilityAffectsMovementSpeed", true), value -> {
            try {
                writeBoolServer("starstruckConfig", "abilityAffectsMovementSpeed", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.global("starexpress.abilityWalkSpeed", 0.12f, () -> readFloatServer("starstruckConfig", "abilityWalkSpeed", 0.12f), value -> {
            try {
                writeFloatServer("starstruckConfig", "abilityWalkSpeed", value);
            } catch (Throwable ignored) {
            }
        }, Object::toString, s -> {
            try {
                return Float.parseFloat(s);
            } catch (NumberFormatException e) {
                return 0.12f;
            }
        }));
        ServerConfig.register(Entry.global("starexpress.abilitySprintSpeed", 0.15f, () -> readFloatServer("starstruckConfig", "abilitySprintSpeed", 0.15f), value -> {
            try {
                writeFloatServer("starstruckConfig", "abilitySprintSpeed", value);
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
        ServerConfig.register(Entry.globalInt("starexpress.suffocationTime", 60, () -> readIntServer("muzzlerConfig", "suffocationTime", 60), value -> {
            try {
                writeIntServer("muzzlerConfig", "suffocationTime", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("starexpress.tapeTearCheckCount", 5, () -> readIntServer("muzzlerConfig", "tapeTearCheckCount", 5), value -> {
            try {
                writeIntServer("muzzlerConfig", "tapeTearCheckCount", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.global("starexpress.tapeTearMoodChange", 0.1f, () -> readFloatServer("muzzlerConfig", "tapeTearMoodChange", 0.1f), value -> {
            try {
                writeFloatServer("muzzlerConfig", "tapeTearMoodChange", value);
            } catch (Throwable ignored) {
            }
        }, Object::toString, s -> {
            try {
                return Float.parseFloat(s);
            } catch (NumberFormatException e) {
                return 0.1f;
            }
        }));
        ServerConfig.register(Entry.globalBool("starexpress.killIfCheckedAtZero", true, () -> readBoolServer("muzzlerConfig", "killIfCheckedAtZero", true), value -> {
            try {
                writeBoolServer("muzzlerConfig", "killIfCheckedAtZero", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("starexpress.displaySilencedTipDelay", 120, () -> readIntServer("muzzlerConfig", "displaySilencedTipDelay", 120), value -> {
            try {
                writeIntServer("muzzlerConfig", "displaySilencedTipDelay", value);
            } catch (Throwable ignored) {
            }
        }));

        // Allergic
        ServerConfig.register(Entry.globalInt("starexpress.nothingChance", 3, () -> readIntServer("allergicConfig", "nothingChance", 3), value -> {
            try {
                writeIntServer("allergicConfig", "nothingChance", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("starexpress.instinctChance", 1, () -> readIntServer("allergicConfig", "instinctChance", 1), value -> {
            try {
                writeIntServer("allergicConfig", "instinctChance", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("starexpress.armorChance", 1, () -> readIntServer("allergicConfig", "armorChance", 1), value -> {
            try {
                writeIntServer("allergicConfig", "armorChance", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("starexpress.poisonChance", 1, () -> readIntServer("allergicConfig", "poisonChance", 1), value -> {
            try {
                writeIntServer("allergicConfig", "poisonChance", value);
            } catch (Throwable ignored) {
            }
        }));
        ServerConfig.register(Entry.globalInt("starexpress.instinctDuration", 3, () -> readIntServer("allergicConfig", "instinctDuration", 3), value -> {
            try {
                writeIntServer("allergicConfig", "instinctDuration", value);
            } catch (Throwable ignored) {
            }
        }));
    }

    public static boolean getStarstruckTaskReducesCooldown() {
        return ConfigUtils.clientBool("starexpress.taskReducesCooldown", true);
    }

    public static void setStarstruckTaskReducesCooldown(boolean value) throws Exception {
        ConfigUtils.apply("starexpress.taskReducesCooldown", value, null);
    }

    public static int getStarstruckTaskCooldownReduction() {
        return ConfigUtils.clientInt("starexpress.taskCooldownReduction", 5);
    }

    public static void setStarstruckTaskCooldownReduction(int value) throws Exception {
        ConfigUtils.apply("starexpress.taskCooldownReduction", value, null);
    }

    public static int getStarstruckAbilityCooldown() {
        return ConfigUtils.clientInt("starexpress.abilityCooldown", 90);
    }

    public static void setStarstruckAbilityCooldown(int value) throws Exception {
        ConfigUtils.apply("starexpress.abilityCooldown", value, null);
    }

    public static int getStarstruckAbilityDuration() {
        return ConfigUtils.clientInt("starexpress.abilityDuration", 15);
    }

    public static void setStarstruckAbilityDuration(int value) throws Exception {
        ConfigUtils.apply("starexpress.abilityDuration", value, null);
    }

    public static boolean getStarstruckAbilityAffectsMovementSpeed() {
        return ConfigUtils.clientBool("starexpress.abilityAffectsMovementSpeed", true);
    }

    public static void setStarstruckAbilityAffectsMovementSpeed(boolean value) throws Exception {
        ConfigUtils.apply("starexpress.abilityAffectsMovementSpeed", value, null);
    }

    public static float getStarstruckAbilityWalkSpeed() {
        return ConfigUtils.clientFloat("starexpress.abilityWalkSpeed", 0.12f);
    }

    public static void setStarstruckAbilityWalkSpeed(float value) throws Exception {
        ConfigUtils.apply("starexpress.abilityWalkSpeed", value, null);
    }

    public static float getStarstruckAbilitySprintSpeed() {
        return ConfigUtils.clientFloat("starexpress.abilitySprintSpeed", 0.15f);
    }

    public static void setStarstruckAbilitySprintSpeed(float value) throws Exception {
        ConfigUtils.apply("starexpress.abilitySprintSpeed", value, null);
    }

    public static int getMuzzlerTapeCooldown() {
        return ConfigUtils.clientInt("starexpress.tapeCooldown", 20);
    }

    public static void setMuzzlerTapeCooldown(int value) throws Exception {
        ConfigUtils.apply("starexpress.tapeCooldown", value, null);
    }

    public static void applyMuzzlerTapeCooldown(int value) {
        try { writeIntServer("muzzlerConfig", "tapeCooldown", value); } catch (Throwable ignored) {}
    }

    public static int getMuzzlerSuffocationTime() {
        return ConfigUtils.clientInt("starexpress.suffocationTime", 60);
    }

    public static void setMuzzlerSuffocationTime(int value) throws Exception {
        ConfigUtils.apply("starexpress.suffocationTime", value, null);
    }

    public static int getMuzzlerTapeTearCheckCount() {
        return ConfigUtils.clientInt("starexpress.tapeTearCheckCount", 5);
    }

    public static void setMuzzlerTapeTearCheckCount(int value) throws Exception {
        ConfigUtils.apply("starexpress.tapeTearCheckCount", value, null);
    }

    public static float getMuzzlerTapeTearMoodChange() {
        return ConfigUtils.clientFloat("starexpress.tapeTearMoodChange", 0.1f);
    }

    public static void setMuzzlerTapeTearMoodChange(float value) throws Exception {
        ConfigUtils.apply("starexpress.tapeTearMoodChange", value, null);
    }

    public static boolean getMuzzlerKillIfCheckedAtZero() {
        return ConfigUtils.clientBool("starexpress.killIfCheckedAtZero", true);
    }

    public static void setMuzzlerKillIfCheckedAtZero(boolean value) throws Exception {
        ConfigUtils.apply("starexpress.killIfCheckedAtZero", value, null);
    }

    public static int getMuzzlerDisplaySilencedTipDelay() {
        return ConfigUtils.clientInt("starexpress.displaySilencedTipDelay", 120);
    }

    public static void setMuzzlerDisplaySilencedTipDelay(int value) throws Exception {
        ConfigUtils.apply("starexpress.displaySilencedTipDelay", value, null);
    }

    public static int getAllergicNothingChance() {
        return ConfigUtils.clientInt("starexpress.nothingChance", 3);
    }

    public static void setAllergicNothingChance(int value) throws Exception {
        ConfigUtils.apply("starexpress.nothingChance", value, null);
    }

    public static int getAllergicInstinctChance() {
        return ConfigUtils.clientInt("starexpress.instinctChance", 1);
    }

    public static void setAllergicInstinctChance(int value) throws Exception {
        ConfigUtils.apply("starexpress.instinctChance", value, null);
    }

    public static int getAllergicArmorChance() {
        return ConfigUtils.clientInt("starexpress.armorChance", 1);
    }

    public static void setAllergicArmorChance(int value) throws Exception {
        ConfigUtils.apply("starexpress.armorChance", value, null);
    }

    public static int getAllergicPoisonChance() {
        return ConfigUtils.clientInt("starexpress.poisonChance", 1);
    }

    public static void setAllergicPoisonChance(int value) throws Exception {
        ConfigUtils.apply("starexpress.poisonChance", value, null);
    }

    public static int getAllergicInstinctDuration() {
        return ConfigUtils.clientInt("starexpress.instinctDuration", 3);
    }

    public static void setAllergicInstinctDuration(int value) throws Exception {
        ConfigUtils.apply("starexpress.instinctDuration", value, null);
    }
}
