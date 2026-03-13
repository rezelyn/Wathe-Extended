package cat.rezelyn.watheextended.api.config;

import cat.rezelyn.watheextended.WatheExtended;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.*;

public final class ServerConfig {

    private ServerConfig() {
    }

    public static final class Entry<T> {

        public final String key;
        public final T defaultValue;

        private final BiFunction<World, T, T> reader;
        private final BiConsumer<World, T> writer;
        private final Function<T, String> serializer;
        private final Function<String, T> deserializer;

        private Entry(String key, T defaultValue,
                      BiFunction<World, T, T> reader, BiConsumer<World, T> writer,
                      Function<T, String> serializer, Function<String, T> deserializer) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.reader = reader;
            this.writer = writer;
            this.serializer = serializer;
            this.deserializer = deserializer;
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

        public T deserialize(String raw) {
            return deserializer.apply(raw);
        }

        public static <T> Entry<T> global(String key, T def,
                                          Supplier<T> reader, Consumer<T> writer,
                                          Function<T, String> ser, Function<String, T> deser) {
            return new Entry<>(key, def,
                    (w, d) -> {
                        try {
                            return reader.get();
                        } catch (Throwable t) {
                            return d;
                        }
                    },
                    (w, v) -> {
                        try {
                            writer.accept(v);
                        } catch (Throwable ignored) {
                        }
                    },
                    ser, deser);
        }

        public static <T> Entry<T> worldScoped(String key, T def,
                                               Function<World, T> reader, BiConsumer<World, T> writer,
                                               Function<T, String> ser, Function<String, T> deser) {
            return new Entry<>(key, def,
                    (w, d) -> {
                        try {
                            T v = reader.apply(w);
                            return v != null ? v : d;
                        } catch (Throwable t) {
                            return d;
                        }
                    },
                    (w, v) -> {
                        try {
                            writer.accept(w, v);
                        } catch (Throwable ignored) {
                        }
                    },
                    ser, deser);
        }

        public static Entry<Boolean> globalBool(String key, boolean def, Supplier<Boolean> r, Consumer<Boolean> w) {
            return global(key, def, r, w, Object::toString, Boolean::parseBoolean);
        }

        public static Entry<Integer> globalInt(String key, int def, Supplier<Integer> r, Consumer<Integer> w) {
            return global(key, def, r, w, Object::toString, s -> {
                try {
                    return Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    return def;
                }
            });
        }

        public static Entry<Float> globalFloat(String key, float def, Supplier<Float> r, Consumer<Float> w) {
            return global(key, def, r, w, Object::toString, s -> {
                try {
                    return Float.parseFloat(s);
                } catch (NumberFormatException e) {
                    return def;
                }
            });
        }

        public static Entry<String> globalString(String key, String def, Supplier<String> r, Consumer<String> w) {
            return global(key, def, r, w, v -> v != null ? v : "", v -> v != null ? v : def);
        }

        public static Entry<Boolean> worldBool(String key, boolean def, Function<World, Boolean> r, BiConsumer<World, Boolean> w) {
            return worldScoped(key, def, r, w, Object::toString, Boolean::parseBoolean);
        }

        public static Entry<Integer> worldInt(String key, int def, Function<World, Integer> r, BiConsumer<World, Integer> w) {
            return worldScoped(key, def, r, w, Object::toString, s -> {
                try {
                    return Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    return def;
                }
            });
        }
    }

    private static final Map<String, Entry<?>> ENTRIES = new LinkedHashMap<>();

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

    @SuppressWarnings("unchecked")
    public static void applyChanges(Map<String, String> changes, World world) {
        for (Map.Entry<String, String> change : changes.entrySet()) {
            Entry<Object> entry = (Entry<Object>) ENTRIES.get(change.getKey());
            if (entry == null) continue;
            try {
                entry.writeServer(world, entry.deserialize(change.getValue()));
            } catch (Throwable ignored) {
            }
        }
    }

    public static void sendToPlayer(ServerPlayerEntity player) {
        try {
            ServerWorld overworld = player.getServer() != null ? player.getServer().getOverworld() : player.getServerWorld();
            ServerPlayNetworking.send(player, new SyncPayload(buildSnapshot(overworld)));
        } catch (Throwable ignored) {
        }
    }

    public static void broadcastToOps(MinecraftServer server) {
        server.execute(() -> {
            for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList())
                if (p.hasPermissionLevel(2)) sendToPlayer(p);
        });
    }

    public static void broadcastToAll(MinecraftServer server) {
        server.execute(() -> {
            for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList())
                sendToPlayer(p);
        });
    }

    public record SyncPayload(NbtCompound data) implements CustomPayload {
        public static final Id<SyncPayload> ID = new Id<>(WatheExtended.id("config_sync"));
        public static final PacketCodec<RegistryByteBuf, SyncPayload> CODEC = PacketCodec.of(
                (v, buf) -> buf.writeNbt(v.data()),
                buf -> new SyncPayload(buf.readNbt()));

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public record ChangePayload(Map<String, String> changes) implements CustomPayload {
        public static final Id<ChangePayload> ID = new Id<>(WatheExtended.id("config_change"));
        public static final PacketCodec<RegistryByteBuf, ChangePayload> CODEC = PacketCodec.of(
                (v, buf) -> {
                    NbtCompound nbt = new NbtCompound();
                    v.changes().forEach(nbt::putString);
                    buf.writeNbt(nbt);
                },
                buf -> {
                    NbtCompound nbt = buf.readNbt();
                    Map<String, String> map = new HashMap<>();
                    if (nbt != null) nbt.getKeys().forEach(k -> map.put(k, nbt.getString(k)));
                    return new ChangePayload(map);
                });

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
}
