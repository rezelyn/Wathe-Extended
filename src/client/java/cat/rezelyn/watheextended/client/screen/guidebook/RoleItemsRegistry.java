package cat.rezelyn.watheextended.client.screen.guidebook;

import cat.rezelyn.watheextended.api.kinswathe.ConfigHelper;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.*;
import java.util.function.Supplier;

public final class RoleItemsRegistry {

    public static final List<RoleItem> GLOBAL_KILLER_SHOP = List.of(
            new RoleItem("knife", "item.wathe.knife", 100),
            new RoleItem("revolver", "item.wathe.revolver", 300),
            new RoleItem("grenade", "item.wathe.grenade", 350),
            new RoleItem("psycho_mode", "item.wathe.psycho_mode", 300),
            new RoleItem("poison_vial", "item.wathe.poison_vial", 100),
            new RoleItem("scorpion", "item.wathe.scorpion", 50),
            new RoleItem("firecracker", "item.wathe.firecracker", 10),
            new RoleItem("lockpick", "item.wathe.lockpick", 50),
            new RoleItem("crowbar", "item.wathe.crowbar", 25),
            new RoleItem("body_bag", "item.wathe.body_bag", 200),
            new RoleItem("blackout", "item.wathe.blackout", 200),
            new RoleItem("note", "item.wathe.note", 10)
    );
    private static final String KEY_ITEM_DESC_PREFIX = "gui.watheextended.guidebook.item.";

    private static final String KEY_ITEM_DESC_SUFFIX = ".desc";
    private static final Map<String, Supplier<List<RoleItem>>> REGISTRY = new LinkedHashMap<>();

    static {
        register("wathe:vigilante", () -> List.of(
                starting("revolver", "item.wathe.revolver")
        ));
        register("noellesroles:jester", () -> List.of(
                starting("fake_knife", "item.noellesroles.fake_knife"),
                starting("fake_revolver", "item.noellesroles.fake_revolver")
        ));
        register("noellesroles:conductor", () -> List.of(
                starting("master_key", "item.noellesroles.master_key")
        ));
        register("noellesroles:bartender", () -> {
            int price = cat.rezelyn.watheextended.api.noellesroles.ConfigHelper.isLoaded()
                    ? readNoellesInt("defenseVialPrice", 100) : 100;
            return List.of(shop("defense_vial", "item.noellesroles.defense_vial", price));
        });
        register("noellesroles:noisemaker", () -> List.of(
                shop("firecracker", "item.wathe.firecracker", 75)
        ));
        register("noellesroles:trapper", () -> {
            int price = cat.rezelyn.watheextended.api.noellesroles.ConfigHelper.isLoaded()
                    ? readNoellesInt("roleMinePrice", 100) : 100;
            return List.of(shop("role_mine", "item.noellesroles.role_mine", price));
        });
        register("noellesroles:mimic", () -> List.of(
                starting("fake_knife", "item.noellesroles.fake_knife")
        ));
        register("noellesroles:executioner", () -> framingShop());
        register("noellesroles:morphling", () -> framingShop());
        register("noellesroles:phantom", () -> framingShop());
        register("noellesroles:swapper", () -> framingShop());
        register("noellesroles:voodoo", () -> framingShop());
        register("kinswathe:hacker", () -> List.of(
                starting("phone", "item.kinswathe.phone"),
                shop("icon_weapon_cooldown_refresh", "item.kinswathe.icon_weapon_cooldown_refresh", 300),
                shop("icon_ability_cooldown_refresh", "item.kinswathe.icon_ability_cooldown_refresh", 400),
                shop("icon_potion_effect_refresh", "item.kinswathe.icon_potion_effect_refresh", 200)
        ));
        register("kinswathe:cleaner", () -> List.of(
                starting("sulfuric_acid_barrel", "item.kinswathe.sulfuric_acid_barrel")
        ));
        register("kinswathe:cook", () -> {
            int price = ConfigHelper.isLoaded() ? ConfigHelper.getCookPanPrice(null) : 250;
            return List.of(shop("pan", "item.kinswathe.pan", price));
        });
        register("kinswathe:dreamer", () -> List.of(
                starting("dream_imprint", "item.kinswathe.dream_imprint")
        ));
        register("kinswathe:drugmaker", () -> {
            int piPrice = ConfigHelper.isLoaded() ? ConfigHelper.getDrugmakerPoisonInjectorPrice(null) : 100;
            int bgPrice = ConfigHelper.isLoaded() ? ConfigHelper.getDrugmakerBlowgunPrice(null) : 175;
            return List.of(
                    shop("poison_injector", "item.kinswathe.poison_injector", piPrice),
                    shop("blowgun", "item.kinswathe.blowgun", bgPrice)
            );
        });
        register("kinswathe:hunter", () -> {
            int price = ConfigHelper.isLoaded() ? ConfigHelper.getHunterAbilityPrice(null) : 125;
            return List.of(shop("hunting_knife", "item.kinswathe.hunting_knife", price));
        });
        register("kinswathe:kidnapper", () -> {
            int price = ConfigHelper.isLoaded() ? ConfigHelper.getKidnapperKnockoutDrugPrice(null) : 250;
            return List.of(shop("knockout_drug", "item.kinswathe.knockout_drug", price));
        });
        register("kinswathe:licensed_villain", () -> {
            int price = ConfigHelper.isLoaded() ? ConfigHelper.getLicensedVillainRevolverPrice(null) : 300;
            return List.of(
                    starting("lockpick", "item.wathe.lockpick"),
                    shop("revolver", "item.wathe.revolver", price)
            );
        });
        register("kinswathe:physician", () -> {
            int price = ConfigHelper.isLoaded() ? ConfigHelper.getPhysicianPillPrice(null) : 300;
            return List.of(
                    starting("medical_kit", "item.kinswathe.medical_kit"),
                    shop("pill", "item.kinswathe.pill", price)
            );
        });
        register("stupid_express:arsonist", () -> List.of(
                starting("jerry_can", "item.stupid_express.jerry_can"),
                starting("lighter", "item.stupid_express.lighter")
        ));
        register("stupid_express:initiate", () -> List.of(
                shop("knife", "item.wathe.knife", 200)
        ));
        register("starexpress:muzzler", () -> List.of(
                shop("tape", "item.starexpress.tape", 75)
        ));
    }

    private RoleItemsRegistry() {
    }

    public static List<RoleItem> getItemsForRole(String roleId, boolean isKillerSided) {
        Supplier<List<RoleItem>> supplier = REGISTRY.get(roleId);
        List<RoleItem> specific = supplier != null ? safeGet(supplier) : Collections.emptyList();

        if (isKillerSided) {
            List<RoleItem> merged = new ArrayList<>(specific);
            for (RoleItem global : GLOBAL_KILLER_SHOP) {
                boolean dup = specific.stream().anyMatch(ri -> ri.iconName().equals(global.iconName()));
                if (!dup) merged.add(global);
            }
            return Collections.unmodifiableList(merged);
        }
        return specific;
    }

    public static boolean hasExplicitRegistration(String roleId) {
        return REGISTRY.containsKey(roleId);
    }

    private static void register(String id, Supplier<List<RoleItem>> supplier) {
        REGISTRY.put(id, supplier);
    }

    private static RoleItem shop(String icon, String nameKey, int price) {
        return new RoleItem(icon, nameKey, price);
    }

    private static RoleItem starting(String icon, String nameKey) {
        return new RoleItem(icon, nameKey, 0);
    }

    private static List<RoleItem> framingShop() {
        return List.of(
                shop("lockpick", "item.wathe.lockpick", 50),
                shop("delusion_vial", "item.noellesroles.delusion_vial", 30)
        );
    }

    private static int readNoellesInt(String fieldName, int def) {
        try {
            Class<?> cfgCls = Class.forName("org.agmas.noellesroles.config.NoellesRolesConfig");
            Object handler = cfgCls.getField("HANDLER").get(null);
            Object instance = handler.getClass().getMethod("instance").invoke(handler);
            return (int) instance.getClass().getField(fieldName).get(instance);
        } catch (Throwable t) {
            return def;
        }
    }

    private static List<RoleItem> safeGet(Supplier<List<RoleItem>> s) {
        try {
            List<RoleItem> list = s.get();
            return list != null ? list : Collections.emptyList();
        } catch (Throwable t) {
            return Collections.emptyList();
        }
    }

    private static String prettyName(String raw) {
        String[] parts = raw.split("[_\\-]");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            if (!sb.isEmpty()) sb.append(' ');
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return sb.isEmpty() ? raw : sb.toString();
    }

    public record RoleItem(String iconName, String nameKey, int price) {

        public Text toText() {
            MutableText line = Text.literal("");

            if (GuidebookIcons.has(iconName)) {
                line.append(GuidebookIcons.icon(iconName));
                line.append(Text.literal(" ").styled(s -> s.withFont(null)));
            }

            String displayName = Text.translatable(nameKey).getString();
            if (displayName.equals(nameKey)) {
                int last = nameKey.lastIndexOf('.');
                displayName = last >= 0 ? prettyName(nameKey.substring(last + 1)) : nameKey;
            }
            line.append(Text.literal("§l" + displayName + "§r").styled(s -> s.withFont(null)));

            if (price > 0) {
                line.append(Text.literal(" §6(" + price + " ").styled(s -> s.withFont(null)));
                line.append(GuidebookIcons.icon("coin"));
                line.append(Text.literal("§6)§r").styled(s -> s.withFont(null)));
            }
            return line;
        }

        public Text descText() {
            String key = KEY_ITEM_DESC_PREFIX + iconName + KEY_ITEM_DESC_SUFFIX;
            String val = Text.translatable(key).getString();
            if (val.equals(key) || val.isBlank()) return null;
            return Text.literal("§7" + val + "§r").styled(s -> s.withFont(null));
        }
    }
}
