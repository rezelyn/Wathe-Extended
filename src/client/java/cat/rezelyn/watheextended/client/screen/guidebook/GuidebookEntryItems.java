package cat.rezelyn.watheextended.client.screen.guidebook;

import cat.rezelyn.watheextended.api.config.ClientConfig;
import cat.rezelyn.watheextended.client.screen.ScreenUtils;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.*;
import java.util.function.Supplier;

public final class GuidebookEntryItems {

    private static final Map<String, Supplier<List<RoleItem>>> REGISTRY = new LinkedHashMap<>();
    private static final Map<String, Set<String>> EXCLUSIONS = new HashMap<>();

    static {
        register("wathe:vigilante", () -> List.of(
                starting("revolver", "item.wathe.revolver")
        ));
        register("noellesroles:jester", () -> List.of(
                starting("fake_knife", "item.noellesroles.fake_knife"),
                starting("fake_revolver", "item.noellesroles.fake_revolver")
        ));
        register("noellesroles:awesome_binglus", () -> List.of(
                starting("note", "item.wathe.note")
        ));
        register("noellesroles:better_vigilante", () -> List.of(
                starting("revolver", "item.wathe.revolver"),
                starting("grenade", "item.wathe.grenade")
        ));
        register("noellesroles:conductor", () -> List.of(
                starting("master_key", "item.noellesroles.master_key")));
        register("noellesroles:bartender", () -> List.of(
                shop("defense_vial", "item.noellesroles.defense_vial", ClientConfig.getInt("noellesroles.defenseVialPrice", 200))
        ));
        register("noellesroles:noisemaker", () -> List.of(
                shop("firecracker", "item.wathe.firecracker", 75)
        ));
        register("noellesroles:trapper", () -> List.of(
                shop("role_mine", "item.noellesroles.role_mine", ClientConfig.getInt("noellesroles.roleMinePrice", 100))
        ));
        register("noellesroles:mimic", () -> List.of(
                starting("fake_knife", "item.noellesroles.fake_knife")
        ));
        register("noellesroles:executioner", GuidebookEntryItems::framingShop);
        register("noellesroles:morphling", GuidebookEntryItems::framingShop);
        register("noellesroles:phantom", GuidebookEntryItems::framingShop);
        register("noellesroles:swapper", GuidebookEntryItems::framingShop);
        register("noellesroles:voodoo", GuidebookEntryItems::framingShop);
        register("kinswathe:hacker", () -> List.of(
                starting("phone", "item.kinswathe.phone"),
                shop("icon_weapon_cooldown_refresh", "item.kinswathe.icon_weapon_cooldown_refresh", ClientConfig.getInt("watheextended.refreshWeaponCooldown.price", 300)),
                shop("icon_ability_cooldown_refresh", "item.kinswathe.icon_ability_cooldown_refresh", ClientConfig.getInt("watheextended.refreshAbilityCooldown.price", 400)),
                shop("icon_potion_effect_refresh", "item.kinswathe.icon_potion_effect_refresh", ClientConfig.getInt("watheextended.refreshPotionEffect.price", 200))
        ));
        register("kinswathe:cleaner", () -> List.of(
                starting("sulfuric_acid_barrel", "item.kinswathe.sulfuric_acid_barrel")
        ));
        register("kinswathe:cook", () -> List.of(
                shop("pan", "item.kinswathe.pan", ClientConfig.getInt("watheextended.pan.price", 250))
        ));
        register("kinswathe:dreamer", () -> List.of(
                starting("dream_imprint", "item.kinswathe.dream_imprint")
        ));
        register("kinswathe:drugmaker", () -> List.of(
                shop("poison_injector", "item.kinswathe.poison_injector", ClientConfig.getInt("watheextended.poisonInjector.price", 125)),
                shop("blowgun", "item.kinswathe.blowgun", ClientConfig.getInt("watheextended.blowgun.price", 175))
        ));
        register("kinswathe:hunter", () -> List.of(
                shop("hunting_knife", "item.kinswathe.hunting_knife", ClientConfig.getInt("watheextended.huntingKnife.price", 100))
        ));
        EXCLUSIONS.put("kinswathe:hunter", Set.of("knife"));
        register("kinswathe:kidnapper", () -> List.of(
                shop("knockout_drug", "item.kinswathe.knockout_drug", ClientConfig.getInt("watheextended.knockoutDrug.price", 75))
        ));
        register("kinswathe:licensed_villain", () -> List.of(
                starting("lockpick", "item.wathe.lockpick"),
                shop("revolver", "item.wathe.revolver", ClientConfig.getInt("watheextended.revolver.price", 300))
        ));
        register("kinswathe:physician", () -> List.of(
                starting("medical_kit", "item.kinswathe.medical_kit"),
                shop("pill", "item.kinswathe.pill", ClientConfig.getInt("watheextended.pill.price", 300))
        ));
        register("kinswathe:technician", () -> List.of(
                shop("wrench", "item.kinswathe.wrench", ClientConfig.getInt("watheextended.wrench.price", 100)),
                shop("capture_device", "item.kinswathe.capture_device", ClientConfig.getInt("watheextended.captureDevice.price", 100)),
                shop("icon_power_restoration", "item.kinswathe.icon_power_restoration", ClientConfig.getInt("watheextended.powerRestoration.price", 300))
        ));
        register("stupid_express:arsonist", () -> List.of(
                starting("jerry_can", "item.stupid_express.jerry_can"),
                starting("lighter", "item.stupid_express.lighter")
        ));
        register("stupid_express:initiate", () -> List.of(
                shop("knife", "item.wathe.knife", 200)
        ));
        register("starexpress:muzzler", () -> List.of(
                shop("tape", "item.starexpress.tape", ClientConfig.getInt("starexpress.tape.price", 75))
        ));
    }

    private GuidebookEntryItems() {
    }

    public static List<RoleItem> getItemsForRole(String roleId, boolean isKillerSided) {
        Supplier<List<RoleItem>> supplier = REGISTRY.get(roleId);
        List<RoleItem> specific = supplier != null ? safeGet(supplier) : Collections.emptyList();

        if (isKillerSided) {
            List<RoleItem> global = buildGlobalKillerShop();
            Set<String> excluded = EXCLUSIONS.getOrDefault(roleId, Set.of());
            List<RoleItem> merged = new ArrayList<>(specific);
            for (RoleItem item : global) {
                boolean duplicate = specific.stream().anyMatch(s -> s.icon().equals(item.icon()));
                if (!duplicate && !excluded.contains(item.icon())) merged.add(item);
            }
            return Collections.unmodifiableList(merged);
        }
        return specific;
    }

    public static boolean hasExplicitRegistration(String roleId) {
        return REGISTRY.containsKey(roleId);
    }

    private static List<RoleItem> buildGlobalKillerShop() {
        return List.of(
                shop("knife", "item.wathe.knife", ClientConfig.getInt("watheextended.knife.price", 100)),
                shop("revolver", "item.wathe.revolver", ClientConfig.getInt("watheextended.revolver.price", 300)),
                shop("grenade", "item.wathe.grenade", ClientConfig.getInt("watheextended.grenade.price", 350)),
                shop("psycho_mode", "item.wathe.psycho_mode", ClientConfig.getInt("watheextended.psychoMode.price", 300)),
                shop("poison_vial", "item.wathe.poison_vial", ClientConfig.getInt("watheextended.poisonVial.price", 100)),
                shop("scorpion", "item.wathe.scorpion", ClientConfig.getInt("watheextended.scorpion.price", 50)),
                shop("firecracker", "item.wathe.firecracker", ClientConfig.getInt("watheextended.firecracker.price", 10)),
                shop("lockpick", "item.wathe.lockpick", ClientConfig.getInt("watheextended.lockpick.price", 50)),
                shop("crowbar", "item.wathe.crowbar", ClientConfig.getInt("watheextended.crowbar.price", 25)),
                shop("body_bag", "item.wathe.body_bag", ClientConfig.getInt("watheextended.bodyBag.price", 200)),
                shop("blackout", "item.wathe.blackout", ClientConfig.getInt("watheextended.blackout.price", 200)),
                shop("note", "item.wathe.note", ClientConfig.getInt("watheextended.note.price", 10))
        );
    }

    private static void register(String id, Supplier<List<RoleItem>> supplier) {
        REGISTRY.put(id, supplier);
    }

    private static RoleItem shop(String icon, String name, int price) {
        return new RoleItem(icon, name, price);
    }

    private static RoleItem starting(String icon, String name) {
        return new RoleItem(icon, name, 0);
    }

    private static List<RoleItem> framingShop() {
        return List.of(
                shop("lockpick", "item.wathe.lockpick", ClientConfig.getInt("watheextended.lockpick.price", 50)),
                shop("delusion_vial", "item.noellesroles.delusion_vial", ClientConfig.getInt("noellesroles.delusionVialPrice", 30))
        );
    }

    private static List<RoleItem> safeGet(Supplier<List<RoleItem>> supplier) {
        try {
            List<RoleItem> list = supplier.get();
            return list != null ? list : Collections.emptyList();
        } catch (Throwable ignored) {
            return Collections.emptyList();
        }
    }

    private static String prettyName(String raw) {
        String[] parts = raw.split("[_\\-]");
        StringBuilder string = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            if (!string.isEmpty()) string.append(' ');
            string.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return string.isEmpty() ? raw : string.toString();
    }

    public record RoleItem(String icon, String name, int price) {

        public Text toText() {
            MutableText line = Text.literal("");

            if (ScreenUtils.hasIcon(icon)) {
                line.append(ScreenUtils.icon(icon));
                line.append(Text.literal(" ").styled(style -> style.withFont(null)));
            }

            String text = Text.translatable(name).getString();
            if (text.equals(name)) {
                int last = name.lastIndexOf('.');
                text = last >= 0 ? prettyName(name.substring(last + 1)) : name;
            }
            line.append(Text.literal("§l" + text + "§r").styled(style -> style.withFont(null)));

            if (price > 0) {
                line.append(Text.literal(" §6(" + price + " ").styled(style -> style.withFont(null)));
                line.append(ScreenUtils.icon("coin"));
                line.append(Text.literal("§6)§r").styled(style -> style.withFont(null)));
            }
            return line;
        }

        public String descString() {
            String key = "gui.watheextended.guidebook.item." + icon + ".desc";
            String value = Text.translatable(key).getString();
            if (value.equals(key) || value.isBlank()) return null;
            return value;
        }
    }
}
