package cat.rezelyn.watheextended.api.starexpress;

import net.fabricmc.loader.api.FabricLoader;

public final class ConfigHelper {

    private ConfigHelper() {
    }

    public static boolean isLoaded() {
        return FabricLoader.getInstance().isModLoaded("starexpress");
    }

    private static Object getConfig() throws Exception {
        Class<?> mainClass = Class.forName("org.aussiebox.starexpress.StarryExpress");
        return mainClass.getField("CONFIG").get(null);
    }

    private static Object getSection(String sectionField) throws Exception {
        Object cfg = getConfig();
        return cfg.getClass().getField(sectionField).get(cfg);
    }

    private static boolean readBool(String section, String method, boolean def) {
        try {
            Object sec = getSection(section);
            return (boolean) sec.getClass().getMethod(method).invoke(sec);
        } catch (Throwable t) {
            return def;
        }
    }

    private static void writeBool(String section, String method, boolean value) throws Exception {
        Object sec = getSection(section);
        sec.getClass().getMethod(method, boolean.class).invoke(sec, value);
    }

    private static int readInt(String section, String method, int def) {
        try {
            Object sec = getSection(section);
            return (int) sec.getClass().getMethod(method).invoke(sec);
        } catch (Throwable t) {
            return def;
        }
    }

    private static void writeInt(String section, String method, int value) throws Exception {
        Object sec = getSection(section);
        sec.getClass().getMethod(method, int.class).invoke(sec, value);
    }

    private static float readFloat(String section, String method, float def) {
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

    private static void writeFloat(String section, String method, float value) throws Exception {
        Object sec = getSection(section);
        try {
            sec.getClass().getMethod(method, float.class).invoke(sec, value);
        } catch (NoSuchMethodException e) {
            sec.getClass().getMethod(method, double.class).invoke(sec, (double) value);
        }
    }

    public static boolean getStarstruckTaskReducesCooldown() {
        return readBool("starstruckConfig", "taskReducesCooldown", true);
    }

    public static void setStarstruckTaskReducesCooldown(boolean value) throws Exception {
        writeBool("starstruckConfig", "taskReducesCooldown", value);
    }

    public static int getStarstruckTaskCooldownReduction() {
        return readInt("starstruckConfig", "taskCooldownReduction", 5);
    }

    public static void setStarstruckTaskCooldownReduction(int value) throws Exception {
        writeInt("starstruckConfig", "taskCooldownReduction", value);
    }

    public static int getStarstruckAbilityCooldown() {
        return readInt("starstruckConfig", "abilityCooldown", 90);
    }

    public static void setStarstruckAbilityCooldown(int value) throws Exception {
        writeInt("starstruckConfig", "abilityCooldown", value);
    }

    public static int getStarstruckAbilityDuration() {
        return readInt("starstruckConfig", "abilityDuration", 15);
    }

    public static void setStarstruckAbilityDuration(int value) throws Exception {
        writeInt("starstruckConfig", "abilityDuration", value);
    }

    public static boolean getStarstruckAbilityAffectsMovementSpeed() {
        return readBool("starstruckConfig", "abilityAffectsMovementSpeed", true);
    }

    public static void setStarstruckAbilityAffectsMovementSpeed(boolean value) throws Exception {
        writeBool("starstruckConfig", "abilityAffectsMovementSpeed", value);
    }

    public static float getStarstruckAbilityWalkSpeed() {
        return readFloat("starstruckConfig", "abilityWalkSpeed", 0.12f);
    }

    public static void setStarstruckAbilityWalkSpeed(float value) throws Exception {
        writeFloat("starstruckConfig", "abilityWalkSpeed", value);
    }

    public static float getStarstruckAbilitySprintSpeed() {
        return readFloat("starstruckConfig", "abilitySprintSpeed", 0.15f);
    }

    public static void setStarstruckAbilitySprintSpeed(float value) throws Exception {
        writeFloat("starstruckConfig", "abilitySprintSpeed", value);
    }

    public static int getMuzzlerTapeCooldown() {
        return readInt("muzzlerConfig", "tapeCooldown", 20);
    }

    public static void setMuzzlerTapeCooldown(int value) throws Exception {
        writeInt("muzzlerConfig", "tapeCooldown", value);
    }

    public static int getMuzzlerSuffocationTime() {
        return readInt("muzzlerConfig", "suffocationTime", 60);
    }

    public static void setMuzzlerSuffocationTime(int value) throws Exception {
        writeInt("muzzlerConfig", "suffocationTime", value);
    }

    public static int getMuzzlerTapeTearCheckCount() {
        return readInt("muzzlerConfig", "tapeTearCheckCount", 5);
    }

    public static void setMuzzlerTapeTearCheckCount(int value) throws Exception {
        writeInt("muzzlerConfig", "tapeTearCheckCount", value);
    }

    public static float getMuzzlerTapeTearMoodChange() {
        return readFloat("muzzlerConfig", "tapeTearMoodChange", 0.1f);
    }

    public static void setMuzzlerTapeTearMoodChange(float value) throws Exception {
        writeFloat("muzzlerConfig", "tapeTearMoodChange", value);
    }

    public static boolean getMuzzlerKillIfCheckedAtZero() {
        return readBool("muzzlerConfig", "killIfCheckedAtZero", true);
    }

    public static void setMuzzlerKillIfCheckedAtZero(boolean value) throws Exception {
        writeBool("muzzlerConfig", "killIfCheckedAtZero", value);
    }

    public static int getMuzzlerDisplaySilencedTipDelay() {
        return readInt("muzzlerConfig", "displaySilencedTipDelay", 120);
    }

    public static void setMuzzlerDisplaySilencedTipDelay(int value) throws Exception {
        writeInt("muzzlerConfig", "displaySilencedTipDelay", value);
    }

    public static int getAllergicNothingChance() {
        return readInt("allergicConfig", "nothingChance", 3);
    }

    public static void setAllergicNothingChance(int value) throws Exception {
        writeInt("allergicConfig", "nothingChance", value);
    }

    public static int getAllergicInstinctChance() {
        return readInt("allergicConfig", "instinctChance", 1);
    }

    public static void setAllergicInstinctChance(int value) throws Exception {
        writeInt("allergicConfig", "instinctChance", value);
    }

    public static int getAllergicArmorChance() {
        return readInt("allergicConfig", "armorChance", 1);
    }

    public static void setAllergicArmorChance(int value) throws Exception {
        writeInt("allergicConfig", "armorChance", value);
    }

    public static int getAllergicPoisonChance() {
        return readInt("allergicConfig", "poisonChance", 1);
    }

    public static void setAllergicPoisonChance(int value) throws Exception {
        writeInt("allergicConfig", "poisonChance", value);
    }

    public static int getAllergicInstinctDuration() {
        return readInt("allergicConfig", "instinctDuration", 3);
    }

    public static void setAllergicInstinctDuration(int value) throws Exception {
        writeInt("allergicConfig", "instinctDuration", value);
    }
}

