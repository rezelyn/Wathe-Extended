package cat.rezelyn.watheextended.client.screen.guidebook;

import cat.rezelyn.watheextended.client.screen.ScreenUtils;
import cat.rezelyn.watheextended.network.ClientConfig;
import dev.doctor4t.wathe.util.ShopEntry;
import java.util.*;
import java.util.function.Supplier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class GuidebookEntryItems {

  private static final Map<String, Supplier<List<RoleItem>>> REGISTRY = new LinkedHashMap<>();
  private static final Map<String, Set<String>> EXCLUSIONS = new HashMap<>();

  static {
    register("wathe:vigilante", () -> List.of(starting("wathe:revolver")));
    register(
        "noellesroles:jester",
        () ->
            framingRoleItems(
                starting("noellesroles:fake_knife"),
                starting("noellesroles:fake_revolver")));
    register("noellesroles:awesome_binglus", () -> List.of(starting("wathe:note")));
    register(
        "noellesroles:better_vigilante",
        () ->
            List.of(
                starting("wathe:revolver"),
                starting("wathe:grenade")));
    register(
        "noellesroles:conductor",
        () -> List.of(starting("noellesroles:master_key")));
    register(
        "noellesroles:bartender",
        () ->
            List.of(
                shop(
                    "noellesroles:defense_vial",
                    ClientConfig.getInt("noellesroles.defenseVialPrice", 200))));
    register(
        "noellesroles:noisemaker",
        () -> List.of(shop("wathe:firecracker", 75)));
    register(
        "noellesroles:trapper",
        () ->
            List.of(
                shop(
                    "noellesroles:role_mine",
                    ClientConfig.getInt("noellesroles.roleMinePrice", 100))));
    register(
        "noellesroles:mimic",
        () -> framingRoleItems(starting("noellesroles:fake_knife")));
    register(
        "noellesroles:recon", () -> List.of(starting("noellesroles:role_mine")));
    register("noellesroles:executioner", GuidebookEntryItems::framingRoleItems);
    register("noellesroles:morphling", GuidebookEntryItems::framingShop);
    register("noellesroles:phantom", GuidebookEntryItems::framingShop);
    register("noellesroles:swapper", GuidebookEntryItems::framingShop);
    register(
        "kinswathe:hacker",
        () ->
            List.of(
                starting("kinswathe:phone"),
                shop(
                    "kinswathe:icon_weapon_cooldown_refresh",
                    ClientConfig.getInt("watheextended.refreshWeaponCooldown.price", 300)),
                shop(
                    "kinswathe:icon_ability_cooldown_refresh",
                    ClientConfig.getInt("watheextended.refreshAbilityCooldown.price", 400)),
                shop(
                    "kinswathe:icon_potion_effect_refresh",
                    ClientConfig.getInt("watheextended.refreshPotionEffect.price", 200))));
    register(
        "kinswathe:cleaner",
        () -> List.of(starting("kinswathe:sulfuric_acid_barrel")));
    register(
        "kinswathe:cook",
        () ->
            List.of(
                shop(
                    "kinswathe:pan",
                    ClientConfig.getInt("watheextended.pan.price", 250)),
                shop(Items.COOKED_BEEF.getDefaultStack(), 75),
                shop(Items.COOKED_CHICKEN.getDefaultStack(), 75),
                shop(Items.COOKED_PORKCHOP.getDefaultStack(), 75)));
    register(
        "kinswathe:dreamer",
        () -> List.of(starting("kinswathe:dream_imprint")));
    register(
        "kinswathe:drugmaker",
        () ->
            List.of(
                shop(
                    "kinswathe:poison_injector",
                    ClientConfig.getInt("watheextended.poisonInjector.price", 125)),
                shop(
                    "kinswathe:blowgun",
                    ClientConfig.getInt("watheextended.blowgun.price", 175))));
    register(
        "kinswathe:hunter",
        () ->
            List.of(
                shop(
                    "kinswathe:hunting_knife",
                    ClientConfig.getInt("watheextended.huntingKnife.price", 100))));
    EXCLUSIONS.put("kinswathe:hunter", Set.of("knife"));
    register(
        "kinswathe:kidnapper",
        () ->
            List.of(
                shop(
                    "kinswathe:knockout_drug",
                    ClientConfig.getInt("watheextended.knockoutDrug.price", 75))));
    register(
        "kinswathe:licensed_villain",
        () ->
            List.of(
                starting("wathe:lockpick"),
                shop(
                    "wathe:revolver",
                    ClientConfig.getInt("watheextended.revolver.price", 300))));
    register(
        "kinswathe:physician",
        () ->
            List.of(
                starting("kinswathe:medical_kit"),
                shop(
                    "kinswathe:pill",
                    ClientConfig.getInt("watheextended.pill.price", 300))));
    register(
        "kinswathe:technician",
        () ->
            List.of(
                shop(
                    "kinswathe:wrench",
                    ClientConfig.getInt("watheextended.wrench.price", 100)),
                shop(
                    "kinswathe:capture_device",
                    ClientConfig.getInt("watheextended.captureDevice.price", 100)),
                shop(
                    "kinswathe:icon_power_restoration",
                    ClientConfig.getInt("watheextended.powerRestoration.price", 300))));
    register(
        "stupid_express:arsonist",
        () ->
            List.of(
                starting("stupid_express:jerry_can"),
                starting("stupid_express:lighter")));
    register("stupid_express:initiate", () -> List.of(shop("wathe:knife", 200)));
    register(
        "starexpress:muzzler",
        () ->
            List.of(
                shop(
                    "starexpress:tape",
                    ClientConfig.getInt("starexpress.tape.price", 75))));
  }

  private GuidebookEntryItems() {}

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
        shop("wathe:knife", ClientConfig.getInt("watheextended.knife.price", 100)),
        shop(
            "wathe:revolver",
            ClientConfig.getInt("watheextended.revolver.price", 300)),
        shop(
            "wathe:grenade",
            ClientConfig.getInt("watheextended.grenade.price", 350)),
        shop(
            "wathe:psycho_mode",
            ClientConfig.getInt("watheextended.psychoMode.price", 300)),
        shop(
            "wathe:poison_vial",
            ClientConfig.getInt("watheextended.poisonVial.price", 100)),
        shop(
            "wathe:scorpion",
            ClientConfig.getInt("watheextended.scorpion.price", 50)),
        shop(
            "wathe:firecracker",
            ClientConfig.getInt("watheextended.firecracker.price", 10)),
        shop(
            "wathe:lockpick",
            ClientConfig.getInt("watheextended.lockpick.price", 50)),
        shop(
            "wathe:crowbar",
            ClientConfig.getInt("watheextended.crowbar.price", 25)),
        shop(
            "wathe:body_bag",
            ClientConfig.getInt("watheextended.bodyBag.price", 200)),
        shop(
            "wathe:blackout",
            ClientConfig.getInt("watheextended.blackout.price", 200)),
        shop("wathe:note", ClientConfig.getInt("watheextended.note.price", 10)));
  }

  private static void register(String id, Supplier<List<RoleItem>> supplier) {
    REGISTRY.put(id, supplier);
  }

  private static RoleItem shop(String itemId, int price) {
    Identifier id = Identifier.of(itemId);
    ItemStack stack = Registries.ITEM.getOrEmpty(id)
        .map(item -> item.getDefaultStack())
        .orElse(ItemStack.EMPTY);
    return new RoleItem(id.getPath(), id.toTranslationKey("item"), price, stack);
  }

  private static RoleItem shop(ItemStack stack, int price) {
    String icon = Registries.ITEM.getId(stack.getItem()).getPath();
    return new RoleItem(icon, stack.getTranslationKey(), price, stack);
  }

  private static RoleItem starting(String itemId) {
    return shop(itemId, 0);
  }

  private static List<RoleItem> framingRoleItems(RoleItem... startingItems) {
    List<RoleItem> items = new ArrayList<>(List.of(startingItems));
    for (ShopEntry entry : org.agmas.noellesroles.Noellesroles.FRAMING_ROLES_SHOP) {
      var stack = entry.stack();
      int price =
          stack.isOf(org.agmas.noellesroles.ModItems.DELUSION_VIAL)
              ? ClientConfig.getInt("noellesroles.delusionVialPrice", entry.price())
              : entry.price();
      items.add(shop(stack, price));
    }
    return items;
  }

  private static List<RoleItem> framingShop() {
    return List.of(
        shop(
            "wathe:lockpick",
            ClientConfig.getInt("watheextended.lockpick.price", 50)),
        shop(
            "noellesroles:delusion_vial",
            ClientConfig.getInt("noellesroles.delusionVialPrice", 30)));
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

  public record RoleItem(String icon, String name, int price, ItemStack stack) {


    public Text toText() {
      MutableText line = Text.literal("");

      if (!stack.isEmpty()) {
        line.append(GuidebookItemIcon.text(stack));
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
