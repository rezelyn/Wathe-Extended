package cat.rezelyn.watheextended.client.screen;

import cat.rezelyn.orelib.api.config.*;
import cat.rezelyn.orelib.api.model.CategoryDefinition;
import cat.rezelyn.orelib.api.model.GroupDefinition;
import cat.rezelyn.orelib.api.network.ClientConfigEditor;
import cat.rezelyn.orelib.api.network.ClientConfigNetwork;
import cat.rezelyn.orelib.api.setting.SettingKey;
import cat.rezelyn.watheextended.client.screen.config.*;
import cat.rezelyn.watheextended.network.ServerConfig;
import com.mojang.serialization.Codec;
import java.math.BigDecimal;
import java.util.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class ConfigScreen {
  private static final Set<String> BASE_ROLES =
      Set.of("civilian", "killer", "vigilante", "discovery_civilian", "loose_end", "secret_killer");
  private static final Set<String> INSTINCT_TIMERS =
      Set.of(
          "watheextended.instinct.capacity",
          "watheextended.instinct.drainRate",
          "watheextended.instinct.reloadRate");
  private static final Set<String> NON_PRESET_KEYS =
      Set.of("player.pronouns", "hml.disabled", "hml.disabledModifiers");

  private ConfigScreen() {}

  public static Screen create(Screen parent) {
    boolean op = isOp();

    Text title = Text.translatable("gui.watheextended.config.title");

    ConfigBuilder builder =
        OreConfig.builder(parent, title)
            .category("client", category -> ClientCategory.build(category, op));

    if (op) {
      builder
          .category("game", GameCategory::build)
          .category("map", MapCategory::build)
          .category("items", ItemsCategory::build)
          .category("roles", category -> RolesCategory.build(category, BASE_ROLES))
          .category("modifiers", ModifiersCategory::build);
    }

    var catalogue = builder.catalogue();
    var network = new ClientConfigNetwork(ServerConfig.CONFIG_ID);
    catalogue.settings().forEach(setting -> registerRemote(network, setting.key()));
    var editor = network.open(catalogue, capability -> capability.equals("op") && isOp());

    try {
      var screen =
          OreConfig.advanced(parent, title)
              .categories(catalogue.categories())
              .factoryDefaults(catalogue.factoryDefaults())
              .session(editor.session())
              .presets(editor.presetConfiguration(id -> !NON_PRESET_KEYS.contains(id)))
              .build();
      var presentation = new Presentation(catalogue, editor, screen);
      presentation.update();
      screen.setTickAction(presentation::update);
      screen.setCloseCallback(editor::close);
      return screen.asScreen();
    } catch (RuntimeException failure) {
      editor.close();
      throw failure;
    }
  }

  private static boolean isOp() {
    var player = MinecraftClient.getInstance().player;
    return player != null && player.hasPermissionLevel(2);
  }

  private static <T> void registerRemote(ClientConfigNetwork network, SettingKey<T> key) {
    String id = key.id();
    if (id.startsWith("client.")) return;
    Codec<?> codec;
    if (id.startsWith("role.enabled.") || id.startsWith("modifier.enabled.")) codec = Codec.BOOL;
    else if (INSTINCT_TIMERS.contains(id))
      codec =
          Codec.STRING.xmap(
              raw -> Float.parseFloat(raw) / 20f, seconds -> Float.toString(seconds * 20f));
    else if (key.type() == Float.class) codec = Codec.STRING.xmap(Float::valueOf, Object::toString);
    else if (key.type() == Integer.class)
      codec = Codec.STRING.xmap(raw -> new BigDecimal(raw).intValueExact(), Object::toString);
    else if (key.type() == Boolean.class)
      codec = Codec.STRING.xmap(ConfigScreen::parseBoolean, Object::toString);
    else codec = Codec.STRING;
    bind(network, key, codec);
  }

  private static boolean parseBoolean(String value) {
    if ("true".equalsIgnoreCase(value)) return true;
    if ("false".equalsIgnoreCase(value)) return false;
    throw new IllegalArgumentException("Invalid boolean " + value);
  }

  @SuppressWarnings("unchecked")
  private static <T> void bind(ClientConfigNetwork network, SettingKey<T> key, Codec<?> codec) {
    network.remote(key, (Codec<T>) codec);
  }

  private static final class Presentation {
    private final ConfigCatalogue catalogue;
    private final ClientConfigEditor editor;
    private final OreScreen screen;
    private final Map<String, Boolean> indicators = new HashMap<>();
    private long version = -1;
    private Boolean operator, supported, presets;

    Presentation(ConfigCatalogue catalogue, ClientConfigEditor editor, OreScreen screen) {
      this.catalogue = catalogue;
      this.editor = editor;
      this.screen = screen;
    }

    void update() {
      boolean op = isOp();
      boolean server = editor.supported();
      boolean hasPresets = editor.presetsSupported();
      if (!Objects.equals(operator, op) || !Objects.equals(presets, hasPresets)) {
        screen.setCategoryVisibility(
            id -> id.equals("client") || (op && (!id.equals("orelib:presets") || hasPresets)));
        if (operator != null && operator != op) editor.refresh();
        operator = op;
        presets = hasPresets;
      }
      if (!Objects.equals(supported, server)) {
        supported = server;
        screen.setHostNotice(
            server || MinecraftClient.getInstance().player == null
                ? Text.empty()
                : Text.literal("Server configuration unavailable."));
      }
      if (version == editor.session().version()) return;
      version = editor.session().version();
      var categories = new ArrayList<CategoryDefinition>();
      boolean changed = false;
      for (var category : catalogue.categories()) {
        String prefix =
            switch (category.id()) {
              case "roles" -> "role.enabled.";
              case "modifiers" -> "modifier.enabled.";
              default -> "";
            };
        if (prefix.isEmpty()) {
          categories.add(category);
          continue;
        }
        var items = new ArrayList<CategoryDefinition.PageItem>();
        for (var item : category.items()) {
          if (item instanceof CategoryDefinition.GroupItem(GroupDefinition group)) {
            String id = group.id().substring(category.id().length() + 1);
            var key = SettingKey.of(prefix + id, Boolean.class);
            boolean enabled = Boolean.TRUE.equals(editor.session().value(key).value());
            if (!Objects.equals(indicators.put(group.id(), enabled), enabled)) changed = true;
            var label =
                group
                    .title()
                    .copy()
                    .append("  ")
                    .append(ScreenUtils.icon(enabled ? "enabled" : "disabled"));
            item =
                new CategoryDefinition.GroupItem(
                    new GroupDefinition(
                        group.id(),
                        label,
                        group.description(),
                        group.initiallyCollapsed(),
                        group.entries()));
          }
          items.add(item);
        }
        categories.add(
            CategoryDefinition.ordered(
                category.id(), category.title(), category.description(), items));
      }
      if (changed) screen.replaceSettingCategories(categories, catalogue.factoryDefaults());
    }
  }
}
