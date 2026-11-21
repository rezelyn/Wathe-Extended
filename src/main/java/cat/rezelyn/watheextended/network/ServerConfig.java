package cat.rezelyn.watheextended.network;

import cat.rezelyn.watheextended.WatheExtended;
import java.util.Collections;
import cat.rezelyn.orelib.api.network.ServerConfigs;
import net.minecraft.util.Identifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public final class ServerConfig {

  public static final Identifier CONFIG_ID = WatheExtended.id("config");
  private static final Map<String, Entry<?>> ENTRIES = new LinkedHashMap<>();

  private ServerConfig() {}

  public static <T> Entry<T> register(Entry<T> entry) {
    if (ENTRIES.containsKey(entry.key))
      throw new IllegalArgumentException("Config key already registered: " + entry.key);
    ENTRIES.put(entry.key, entry);
    return entry;
  }

  public static Map<String, Entry<?>> entries() {
    return Collections.unmodifiableMap(ENTRIES);
  }

  @SuppressWarnings("unchecked")
  public static NbtCompound buildSnapshot(World world) {
    NbtCompound nbt = new NbtCompound();
    for (Map.Entry<String, Entry<?>> e : ENTRIES.entrySet()) {
      Entry<Object> entry = (Entry<Object>) e.getValue();
      try {
        nbt.putString(e.getKey(), entry.serialize(entry.readServer(world)));
      } catch (Throwable ignored) {
      }
    }
    return nbt;
  }

  public static void sendToPlayer(ServerPlayerEntity player) {
    try {
      ServerWorld world =
          player.getServer() != null ? player.getServer().getOverworld() : player.getServerWorld();
      ServerPlayNetworking.send(player, new SyncPayload(buildSnapshot(world)));
    } catch (Throwable ignored) {
    }
  }

  public static void broadcastToAll(MinecraftServer server) {
    server.execute(
        () -> {
          ServerConfigs.invalidate(CONFIG_ID);
          for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList())
            sendToPlayer(player);
        });
  }

  public static final class Entry<T> {

    public final String key;
    public final T defaultValue;

    private final BiFunction<World, T, T> reader;
    private final BiConsumer<World, T> writer;
    private final Function<T, String> serializer;
    private final Function<String, T> deserializer;

    private Entry(
        String key,
        T defaultValue,
        BiFunction<World, T, T> reader,
        BiConsumer<World, T> writer,
        Function<T, String> serializer,
        Function<String, T> deserializer) {
      this.key = key;
      this.defaultValue = defaultValue;
      this.reader = reader;
      this.writer = writer;
      this.serializer = serializer;
      this.deserializer = deserializer;
    }

    private static <T> BiFunction<World, T, T> safeRead(Supplier<T> reader) {
      return (world, fallback) -> java.util.Objects.requireNonNull(reader.get());
    }

    private static <T> BiConsumer<World, T> safeWrite(Consumer<T> writer) {
      return (world, value) -> writer.accept(value);
    }

    private static <T> BiFunction<World, T, T> safeWorldRead(Function<World, T> reader) {
      return (world, fallback) -> java.util.Objects.requireNonNull(reader.apply(world));
    }

    private static <T> BiConsumer<World, T> safeWorldWrite(BiConsumer<World, T> writer) {
      return writer;
    }

    public static <T> Entry<T> global(
        String key,
        T def,
        Supplier<T> reader,
        Consumer<T> writer,
        Function<T, String> ser,
        Function<String, T> deser) {
      return new Entry<>(key, def, safeRead(reader), safeWrite(writer), ser, deser);
    }

    public static <T> Entry<T> worldScoped(
        String key,
        T def,
        Function<World, T> reader,
        BiConsumer<World, T> writer,
        Function<T, String> ser,
        Function<String, T> deser) {
      return new Entry<>(key, def, safeWorldRead(reader), safeWorldWrite(writer), ser, deser);
    }

    public static Entry<Boolean> globalBool(
        String key, boolean def, Supplier<Boolean> reader, Consumer<Boolean> world) {
      return global(key, def, reader, world, Object::toString, Boolean::parseBoolean);
    }

    public static Entry<Integer> globalInt(
        String key, int def, Supplier<Integer> reader, Consumer<Integer> world) {
      return global(
          key,
          def,
          reader,
          world,
          Object::toString,
          string -> {
            try {
              return Integer.parseInt(string);
            } catch (NumberFormatException exception) {
              return def;
            }
          });
    }

    public static Entry<Float> globalFloat(
        String key, float def, Supplier<Float> r, Consumer<Float> w) {
      return global(
          key,
          def,
          r,
          w,
          Object::toString,
          string -> {
            try {
              return Float.parseFloat(string);
            } catch (NumberFormatException exception) {
              return def;
            }
          });
    }

    public static Entry<String> globalString(
        String key, String def, Supplier<String> reader, Consumer<String> world) {
      return global(
          key,
          def,
          reader,
          world,
          value -> value != null ? value : "",
          value -> value != null ? value : def);
    }

    public static Entry<String> worldString(
        String key, String def, Function<World, String> reader, BiConsumer<World, String> writer) {
      return worldScoped(
          key,
          def,
          reader,
          writer,
          value -> value != null ? value : "",
          value -> value != null ? value : def);
    }

    public static Entry<Boolean> worldBool(
        String key,
        boolean def,
        Function<World, Boolean> reader,
        BiConsumer<World, Boolean> world) {
      return worldScoped(key, def, reader, world, Object::toString, Boolean::parseBoolean);
    }

    public static Entry<Integer> worldInt(
        String key, int def, Function<World, Integer> reader, BiConsumer<World, Integer> world) {
      return worldScoped(
          key,
          def,
          reader,
          world,
          Object::toString,
          string -> {
            try {
              return Integer.parseInt(string);
            } catch (NumberFormatException exception) {
              return def;
            }
          });
    }

    public static Entry<Float> worldFloat(
        String key, float def, Function<World, Float> reader, BiConsumer<World, Float> world) {
      return worldScoped(
          key,
          def,
          reader,
          world,
          Object::toString,
          string -> {
            try {
              return Float.parseFloat(string);
            } catch (NumberFormatException exception) {
              return def;
            }
          });
    }

    public T readServer(World world) {
      return reader.apply(world, defaultValue);
    }

    public void writeServer(World world, T value) {
      writer.accept(world, value);
    }

    public String serialize(T value) {
      return serializer.apply(value);
    }

    public T deserializeStrict(String raw) {
      if (defaultValue instanceof Boolean && !raw.equals("true") && !raw.equals("false"))
        throw new IllegalArgumentException("Invalid boolean");
      if (defaultValue instanceof Integer) Integer.parseInt(raw);
      if (defaultValue instanceof Float && !Float.isFinite(Float.parseFloat(raw)))
        throw new IllegalArgumentException("Invalid number");
      return deserializer.apply(raw);
    }

    public T deserialize(String raw) {
      return deserializer.apply(raw);
    }
  }

  public record SyncPayload(NbtCompound data) implements CustomPayload {
    public static final Id<SyncPayload> ID = new Id<>(WatheExtended.id("config_sync"));
    public static final PacketCodec<RegistryByteBuf, SyncPayload> CODEC =
        PacketCodec.of(
            (value, buffer) -> buffer.writeNbt(value.data()),
            buf -> new SyncPayload(buf.readNbt()));

    @Override
    public Id<? extends CustomPayload> getId() {
      return ID;
    }
  }
}
