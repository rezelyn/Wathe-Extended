package cat.rezelyn.watheextended.api.starexpress;

import cat.rezelyn.watheextended.api.config.ClientConfig;
import cat.rezelyn.watheextended.api.config.ServerConfig;
import cat.rezelyn.watheextended.api.config.ServerConfig.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public final class ConfigHelper {

    private ConfigHelper() {
    }

    public static boolean isLoaded() {
        return FabricLoader.getInstance().isModLoaded("starexpress");
    }
    private static dev.doctor4t.wathe.api.Role cachedStarstruckRole;
    private static boolean starstruckRoleCached = false;

    private static dev.doctor4t.wathe.api.Role getStarstruckRole() {
        if (!starstruckRoleCached) {
            starstruckRoleCached = true;
            if (!isLoaded()) return null;
            try {
                cachedStarstruckRole = (dev.doctor4t.wathe.api.Role)
                        Class.forName("org.aussiebox.starexpress.StarryExpressRoles")
                                .getField("STARSTRUCK").get(null);
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

    private static Object getConfig() throws Exception {
        Class<?> cls = Class.forName("org.aussiebox.starexpress.StarryExpress");
        return cls.getField("CONFIG").get(null);
    }

    private static Object getSection(String sectionField) throws Exception {
        Object cfg = getConfig();
        return cfg.getClass().getField(sectionField).get(cfg);
    }

    private static boolean readBoolServer(String section, String method, boolean def) {
        try {
            return (boolean) getSection(section).getClass().getMethod(method).invoke(getSection(section));
        } catch (Throwable t) {
            return def;
        }
    }

    private static void writeBoolServer(String section, String method, boolean value) throws Exception {
        getSection(section).getClass().getMethod(method, boolean.class).invoke(getSection(section), value);
    }

    private static int readIntServer(String section, String method, int def) {
        try {
            Object sec = getSection(section);
            return (int) sec.getClass().getMethod(method).invoke(sec);
        } catch (Throwable t) {
            return def;
        }
    }

    private static void writeIntServer(String section, String method, int value) throws Exception {
        getSection(section).getClass().getMethod(method, int.class).invoke(getSection(section), value);
    }

    private static float readFloatServer(String section, String method, float def) {
        try {
            Object sec = getSection(section);
            Object result = sec.getClass().getMethod(method).invoke(sec);
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
            sec.getClass().getMethod(method, float.class).invoke(sec, value);
        } catch (NoSuchMethodException e) {
            sec.getClass().getMethod(method, double.class).invoke(sec, (double) value);
        }
    }

    public static void registerEntries() {
        if (!isLoaded()) return;

        // Starstruck
        reg(Entry.globalBool("starexpress.taskReducesCooldown", true,
                () -> readBoolServer("starstruckConfig", "taskReducesCooldown", true),
                v -> {
                    try {
                        writeBoolServer("starstruckConfig", "taskReducesCooldown", v);
                    } catch (Throwable ignored) {
                    }
                }));
        reg(Entry.globalInt("starexpress.taskCooldownReduction", 5,
                () -> readIntServer("starstruckConfig", "taskCooldownReduction", 5),
                v -> {
                    try {
                        writeIntServer("starstruckConfig", "taskCooldownReduction", v);
                    } catch (Throwable ignored) {
                    }
                }));
        reg(Entry.globalInt("starexpress.abilityCooldown", 90,
                () -> readIntServer("starstruckConfig", "abilityCooldown", 90),
                v -> {
                    try {
                        writeIntServer("starstruckConfig", "abilityCooldown", v);
                    } catch (Throwable ignored) {
                    }
                }));
        reg(Entry.globalInt("starexpress.abilityDuration", 15,
                () -> readIntServer("starstruckConfig", "abilityDuration", 15),
                v -> {
                    try {
                        writeIntServer("starstruckConfig", "abilityDuration", v);
                    } catch (Throwable ignored) {
                    }
                }));
        reg(Entry.globalBool("starexpress.abilityAffectsMovementSpeed", true,
                () -> readBoolServer("starstruckConfig", "abilityAffectsMovementSpeed", true),
                v -> {
                    try {
                        writeBoolServer("starstruckConfig", "abilityAffectsMovementSpeed", v);
                    } catch (Throwable ignored) {
                    }
                }));
        reg(Entry.global("starexpress.abilityWalkSpeed", 0.12f,
                () -> readFloatServer("starstruckConfig", "abilityWalkSpeed", 0.12f),
                v -> {
                    try {
                        writeFloatServer("starstruckConfig", "abilityWalkSpeed", v);
                    } catch (Throwable ignored) {
                    }
                },
                Object::toString, s -> {
                    try {
                        return Float.parseFloat(s);
                    } catch (NumberFormatException e) {
                        return 0.12f;
                    }
                }));
        reg(Entry.global("starexpress.abilitySprintSpeed", 0.15f,
                () -> readFloatServer("starstruckConfig", "abilitySprintSpeed", 0.15f),
                v -> {
                    try {
                        writeFloatServer("starstruckConfig", "abilitySprintSpeed", v);
                    } catch (Throwable ignored) {
                    }
                },
                Object::toString, s -> {
                    try {
                        return Float.parseFloat(s);
                    } catch (NumberFormatException e) {
                        return 0.15f;
                    }
                }));

        // Muzzler
        reg(Entry.globalInt("starexpress.tapeCooldown", 20,
                () -> readIntServer("muzzlerConfig", "tapeCooldown", 20),
                v -> {
                    try {
                        writeIntServer("muzzlerConfig", "tapeCooldown", v);
                    } catch (Throwable ignored) {
                    }
                }));
        reg(Entry.globalInt("starexpress.suffocationTime", 60,
                () -> readIntServer("muzzlerConfig", "suffocationTime", 60),
                v -> {
                    try {
                        writeIntServer("muzzlerConfig", "suffocationTime", v);
                    } catch (Throwable ignored) {
                    }
                }));
        reg(Entry.globalInt("starexpress.tapeTearCheckCount", 5,
                () -> readIntServer("muzzlerConfig", "tapeTearCheckCount", 5),
                v -> {
                    try {
                        writeIntServer("muzzlerConfig", "tapeTearCheckCount", v);
                    } catch (Throwable ignored) {
                    }
                }));
        reg(Entry.global("starexpress.tapeTearMoodChange", 0.1f,
                () -> readFloatServer("muzzlerConfig", "tapeTearMoodChange", 0.1f),
                v -> {
                    try {
                        writeFloatServer("muzzlerConfig", "tapeTearMoodChange", v);
                    } catch (Throwable ignored) {
                    }
                },
                Object::toString, s -> {
                    try {
                        return Float.parseFloat(s);
                    } catch (NumberFormatException e) {
                        return 0.1f;
                    }
                }));
        reg(Entry.globalBool("starexpress.killIfCheckedAtZero", true,
                () -> readBoolServer("muzzlerConfig", "killIfCheckedAtZero", true),
                v -> {
                    try {
                        writeBoolServer("muzzlerConfig", "killIfCheckedAtZero", v);
                    } catch (Throwable ignored) {
                    }
                }));
        reg(Entry.globalInt("starexpress.displaySilencedTipDelay", 120,
                () -> readIntServer("muzzlerConfig", "displaySilencedTipDelay", 120),
                v -> {
                    try {
                        writeIntServer("muzzlerConfig", "displaySilencedTipDelay", v);
                    } catch (Throwable ignored) {
                    }
                }));

        // Allergic
        reg(Entry.globalInt("starexpress.nothingChance", 3,
                () -> readIntServer("allergicConfig", "nothingChance", 3),
                v -> {
                    try {
                        writeIntServer("allergicConfig", "nothingChance", v);
                    } catch (Throwable ignored) {
                    }
                }));
        reg(Entry.globalInt("starexpress.instinctChance", 1,
                () -> readIntServer("allergicConfig", "instinctChance", 1),
                v -> {
                    try {
                        writeIntServer("allergicConfig", "instinctChance", v);
                    } catch (Throwable ignored) {
                    }
                }));
        reg(Entry.globalInt("starexpress.armorChance", 1,
                () -> readIntServer("allergicConfig", "armorChance", 1),
                v -> {
                    try {
                        writeIntServer("allergicConfig", "armorChance", v);
                    } catch (Throwable ignored) {
                    }
                }));
        reg(Entry.globalInt("starexpress.poisonChance", 1,
                () -> readIntServer("allergicConfig", "poisonChance", 1),
                v -> {
                    try {
                        writeIntServer("allergicConfig", "poisonChance", v);
                    } catch (Throwable ignored) {
                    }
                }));
        reg(Entry.globalInt("starexpress.instinctDuration", 3,
                () -> readIntServer("allergicConfig", "instinctDuration", 3),
                v -> {
                    try {
                        writeIntServer("allergicConfig", "instinctDuration", v);
                    } catch (Throwable ignored) {
                    }
                }));
    }

    private static <T> void reg(Entry<T> e) {
        ServerConfig.register(e);
    }

    private static boolean cb(String key, boolean def) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            return ClientConfig.getBool(key, def);
        return def;
    }

    private static int ci(String key, int def) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            return ClientConfig.getInt(key, def);
        return def;
    }

    private static float cf(String key, float def) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            return ClientConfig.getFloat(key, def);
        return def;
    }

    public static boolean getStarstruckTaskReducesCooldown() {
        return cb("starexpress.taskReducesCooldown", true);
    }

    public static void setStarstruckTaskReducesCooldown(boolean v) throws Exception {
        applyV("starexpress.taskReducesCooldown", v);
    }

    public static int getStarstruckTaskCooldownReduction() {
        return ci("starexpress.taskCooldownReduction", 5);
    }

    public static void setStarstruckTaskCooldownReduction(int v) throws Exception {
        applyV("starexpress.taskCooldownReduction", v);
    }

    public static int getStarstruckAbilityCooldown() {
        return ci("starexpress.abilityCooldown", 90);
    }

    public static void setStarstruckAbilityCooldown(int v) throws Exception {
        applyV("starexpress.abilityCooldown", v);
    }

    public static int getStarstruckAbilityDuration() {
        return ci("starexpress.abilityDuration", 15);
    }

    public static void setStarstruckAbilityDuration(int v) throws Exception {
        applyV("starexpress.abilityDuration", v);
    }

    public static boolean getStarstruckAbilityAffectsMovementSpeed() {
        return cb("starexpress.abilityAffectsMovementSpeed", true);
    }

    public static void setStarstruckAbilityAffectsMovementSpeed(boolean v) throws Exception {
        applyV("starexpress.abilityAffectsMovementSpeed", v);
    }

    public static float getStarstruckAbilityWalkSpeed() {
        return cf("starexpress.abilityWalkSpeed", 0.12f);
    }

    public static void setStarstruckAbilityWalkSpeed(float v) throws Exception {
        applyV("starexpress.abilityWalkSpeed", v);
    }

    public static float getStarstruckAbilitySprintSpeed() {
        return cf("starexpress.abilitySprintSpeed", 0.15f);
    }

    public static void setStarstruckAbilitySprintSpeed(float v) throws Exception {
        applyV("starexpress.abilitySprintSpeed", v);
    }

    public static int getMuzzlerTapeCooldown() {
        return ci("starexpress.tapeCooldown", 20);
    }

    public static void setMuzzlerTapeCooldown(int v) throws Exception {
        applyV("starexpress.tapeCooldown", v);
    }

    public static int getMuzzlerSuffocationTime() {
        return ci("starexpress.suffocationTime", 60);
    }

    public static void setMuzzlerSuffocationTime(int v) throws Exception {
        applyV("starexpress.suffocationTime", v);
    }

    public static int getMuzzlerTapeTearCheckCount() {
        return ci("starexpress.tapeTearCheckCount", 5);
    }

    public static void setMuzzlerTapeTearCheckCount(int v) throws Exception {
        applyV("starexpress.tapeTearCheckCount", v);
    }

    public static float getMuzzlerTapeTearMoodChange() {
        return cf("starexpress.tapeTearMoodChange", 0.1f);
    }

    public static void setMuzzlerTapeTearMoodChange(float v) throws Exception {
        applyV("starexpress.tapeTearMoodChange", v);
    }

    public static boolean getMuzzlerKillIfCheckedAtZero() {
        return cb("starexpress.killIfCheckedAtZero", true);
    }

    public static void setMuzzlerKillIfCheckedAtZero(boolean v) throws Exception {
        applyV("starexpress.killIfCheckedAtZero", v);
    }

    public static int getMuzzlerDisplaySilencedTipDelay() {
        return ci("starexpress.displaySilencedTipDelay", 120);
    }

    public static void setMuzzlerDisplaySilencedTipDelay(int v) throws Exception {
        applyV("starexpress.displaySilencedTipDelay", v);
    }

    public static int getAllergicNothingChance() {
        return ci("starexpress.nothingChance", 3);
    }

    public static void setAllergicNothingChance(int v) throws Exception {
        applyV("starexpress.nothingChance", v);
    }

    public static int getAllergicInstinctChance() {
        return ci("starexpress.instinctChance", 1);
    }

    public static void setAllergicInstinctChance(int v) throws Exception {
        applyV("starexpress.instinctChance", v);
    }

    public static int getAllergicArmorChance() {
        return ci("starexpress.armorChance", 1);
    }

    public static void setAllergicArmorChance(int v) throws Exception {
        applyV("starexpress.armorChance", v);
    }

    public static int getAllergicPoisonChance() {
        return ci("starexpress.poisonChance", 1);
    }

    public static void setAllergicPoisonChance(int v) throws Exception {
        applyV("starexpress.poisonChance", v);
    }

    public static int getAllergicInstinctDuration() {
        return ci("starexpress.instinctDuration", 3);
    }

    public static void setAllergicInstinctDuration(int v) throws Exception {
        applyV("starexpress.instinctDuration", v);
    }

    @SuppressWarnings("unchecked")
    private static <T> void applyV(String key, T value) {
        Entry<T> entry = (Entry<T>) ServerConfig.entries().get(key);
        if (entry != null) entry.writeServer(null, value);
    }
}
