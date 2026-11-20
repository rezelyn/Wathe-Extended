package cat.rezelyn.watheextended.network;

import cat.rezelyn.orelib.api.network.ServerConfigSchema;
import cat.rezelyn.orelib.api.network.ServerConfigs;
import cat.rezelyn.orelib.api.network.ServerPresetProvider;
import cat.rezelyn.orelib.api.setting.SettingKey;
import cat.rezelyn.orelib.api.setting.Validation;
import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import cat.rezelyn.watheextended.api.ModifiersId;
import cat.rezelyn.watheextended.api.RolesId;
import cat.rezelyn.watheextended.game.ItemCooldowns;
import cat.rezelyn.watheextended.game.ItemPrices;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

final class ConfigSchema {
  private static final String ROLES = "hml.disabled";
  private static final String MODIFIERS = "hml.disabledModifiers";
  private static final String PRONOUNS = "player.pronouns";
  private static final ServerConfigSchema<ServerPlayerEntity> SCHEMA =
      new ServerConfigSchema<>(ServerConfig.CONFIG_ID);
  private static final Set<String> members = new HashSet<>();
  private static final Set<String> commands = new HashSet<>();

  private ConfigSchema() {}

  static void register() {
    ServerConfig.entries()
        .forEach((id, entry) -> {
          if (!id.equals(ROLES) && !id.equals(MODIFIERS)) registerEntry(entry);
        });
    SCHEMA.register(
        SettingKey.of(PRONOUNS, String.class),
        Codec.STRING,
        p -> true,
        p -> true,
        p -> PronounsManager.get(p.getUuid()),
        (p, value) -> {
          String clean = value.strip();
          return clean.length() <= PronounsManager.MAX_LENGTH
              ? Validation.accepted(clean)
              : Validation.rejected(Text.literal("Pronouns are too long."));
        },
        ServerNetworking::setPronouns);
    ServerConfigs.register(SCHEMA);
    var directory =
        FabricLoader.getInstance().getConfigDir().resolve("watheextended").resolve("presets");
    ServerConfigs.registerPresets(
        new ServerPresetProvider<ServerPlayerEntity>(
            SCHEMA.id(),
            p ->
                new PresetManager(
                    directory,
                    p.getGameProfile().getName(),
                    p.getUuid(),
                    Set.copyOf(RolesId.get()),
                    Set.copyOf(ModifiersId.get())),
            ConfigSchema::canManage,
            p -> p.getGameProfile().getName(),
            p -> {
              WatheExtendedServerConfig.save();
              ItemPrices.applyAll();
              ItemCooldowns.applyAll();
              ConfigSync.gameplayChanged();
            },
            (p, key) -> !key.equals(PRONOUNS)));

    ServerLifecycleEvents.SERVER_STARTED.register(server -> {
      ConfigEntries.registerChances(entry -> {
        if (!ServerConfig.entries().containsKey(entry.key)) {
          registerEntry(ServerConfig.register(entry));
        }
      });
      registerMembers(RolesId.get(), "role.enabled.", ROLES);
      registerMembers(ModifiersId.get(), "modifier.enabled.", MODIFIERS);
      refreshCommands(CommandSettings.snapshot(server.getOverworld()).keySet());
    });
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
      members.forEach(SCHEMA::unregister);
      members.clear();
      refreshCommands(Set.of());
    });
  }

  private static ServerWorld world(ServerPlayerEntity player) {
    return player.getServerWorld().getServer().getOverworld();
  }

  private static boolean canManage(ServerPlayerEntity player) {
    return player.hasPermissionLevel(2);
  }

  private static <T> void registerEntry(ServerConfig.Entry<T> entry) {
    SCHEMA.register(
        SettingKey.of(entry.key, String.class),
        Codec.STRING,
        p -> true,
        ConfigSchema::canManage,
        p -> entry.serialize(entry.readServer(world(p))),
        (p, raw) -> Validation.accepted(entry.serialize(entry.deserializeStrict(raw))),
        (p, raw) -> {
          entry.writeServer(world(p), entry.deserializeStrict(raw));
          ConfigSync.gameplayChanged();
        });
  }

  @SuppressWarnings("unchecked") // HML registers both disabled lists as Entry<List<String>
  private static ServerConfig.Entry<List<String>> membership(String key) {
    return (ServerConfig.Entry<List<String>>) ServerConfig.entries().get(key);
  }

  static List<String> disabledRoles(ServerWorld world) {
    return membership(ROLES).readServer(world);
  }

  static List<String> disabledModifiers(ServerWorld world) {
    return membership(MODIFIERS).readServer(world);
  }

  private static void registerMembers(List<String> ids, String prefix, String key) {
    var entry = membership(key);
    for (String id : ids) {
      if (!members.add(prefix + id)) continue;
      SCHEMA.register(
          SettingKey.of(prefix + id, Boolean.class),
          Codec.BOOL,
          p -> true,
          ConfigSchema::canManage,
          p -> !entry.readServer(world(p)).contains(id),
          (p, enabled) -> Validation.accepted(enabled),
          (p, enabled) -> {
            var disabled = new ArrayList<>(entry.readServer(world(p)));
            if (enabled) disabled.removeIf(id::equals);
            else if (!disabled.contains(id)) disabled.add(id);
            entry.writeServer(world(p), disabled);
            ConfigSync.gameplayChanged();
          });
    }
  }

  static void refreshCommands(Set<String> keys) {
    commands.removeIf(key -> !keys.contains(key) && SCHEMA.unregister(key));
    for (String key : keys) {
      if (!commands.add(key)) continue;
      SCHEMA.register(
          SettingKey.of(key, String.class),
          Codec.STRING,
          p -> true,
          ConfigSchema::canManage,
          p -> CommandSettings.read(world(p), key),
          (p, raw) -> Validation.accepted(CommandSettings.normalize(key, raw)),
          (p, normalized) -> {
            var level = world(p);
            var source = p.getCommandSource().withWorld(level).withLevel(4).withSilent();
            try {
              if (level
                      .getServer()
                      .getCommandManager()
                      .getDispatcher()
                      .execute(CommandSettings.command(key, normalized), source)
                  <= 0)
                throw new IllegalArgumentException("The command did not apply this setting.");
            } catch (CommandSyntaxException exception) {
              throw new IllegalArgumentException(exception.getMessage(), exception);
            }
          });
    }
  }
}
