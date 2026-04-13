package cat.rezelyn.watheextended.game;

import cat.rezelyn.watheextended.WatheExtended;
import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import cat.rezelyn.watheextended.api.GameStatus;
import dev.doctor4t.wathe.api.event.AllowPlayerDeath;
import dev.doctor4t.wathe.game.GameFunctions;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.agmas.harpymodloader.component.WorldModifierComponent;
import org.agmas.noellesroles.Noellesroles;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class LastStand {

    private static final Map<UUID, PendingDeath> pending = new LinkedHashMap<>();
    private static final Set<UUID> bypassed = new HashSet<>();

    private LastStand() {}

    private static class PendingDeath {
        final UUID victimId;
        @Nullable final UUID killerId;
        final Identifier deathReason;
        final String message;
        final int totalTicks;
        int remainingTicks;

        PendingDeath(UUID victimId, @Nullable UUID killerId, Identifier deathReason, String message, int totalTicks) {
            this.victimId = victimId;
            this.killerId = killerId;
            this.deathReason = deathReason;
            this.message = message;
            this.totalTicks = totalTicks;
            this.remainingTicks = totalTicks;
        }
    }

    public record LastStandPayload(int totalTicks) implements CustomPayload {
        public static final Id<LastStandPayload> ID = new Id<>(WatheExtended.id("last_stand"));
        public static final PacketCodec<RegistryByteBuf, LastStandPayload> CODEC =
                PacketCodec.of((v, buf) -> buf.writeInt(v.totalTicks()), buf -> new LastStandPayload(buf.readInt()));

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public static boolean tryActivate(PlayerEntity victim, @Nullable PlayerEntity killer, Identifier deathReason, String message) {
        if (!(victim instanceof ServerPlayerEntity serverVictim)) return false;
        UUID uuid = victim.getUuid();

        if (bypassed.contains(uuid)) return false;
        if (pending.containsKey(uuid)) return true;

        UUID killerId = killer != null ? killer.getUuid() : null;
        int totalTicks = WatheExtendedServerConfig.lastStandCooldown * 20;
        pending.put(uuid, new PendingDeath(uuid, killerId, deathReason, message, totalTicks));
        ServerPlayNetworking.send(serverVictim, new LastStandPayload(totalTicks));
        return true;
    }

    public static void tick(ServerWorld world) {
        if (pending.isEmpty()) return;

        List<UUID> toRemove = new ArrayList<>();

        for (Map.Entry<UUID, PendingDeath> entry : pending.entrySet()) {
            UUID uuid = entry.getKey();
            PendingDeath death = entry.getValue();

            ServerPlayerEntity victim = world.getServer().getPlayerManager().getPlayer(uuid);
            if (victim == null || victim.isSpectator() || victim.isCreative()) {
                toRemove.add(uuid);
                continue;
            }

            if (death.remainingTicks <= 0) {
                toRemove.add(uuid);
                bypassed.add(uuid);
                ServerPlayerEntity killer = death.killerId != null
                        ? world.getServer().getPlayerManager().getPlayer(death.killerId)
                        : null;
                try {
                    GameFunctions.killPlayer(victim, true, killer, death.deathReason);
                } catch (Throwable t) {
                } finally {
                    bypassed.remove(uuid);
                }
                continue;
            }

            if (death.remainingTicks % 20 == 0 && death.remainingTicks > death.totalTicks - 60) {
                victim.sendMessage(Text.literal(death.message), true);
            }

            death.remainingTicks--;
        }

        toRemove.forEach(pending::remove);
    }

    public static boolean isPending(UUID uuid) {
        return pending.containsKey(uuid);
    }

    public static void clearAll() {
        pending.clear();
        bypassed.clear();
    }

    public static void register() {
        AllowPlayerDeath.EVENT.register((victim, killer, deathReason) -> {
            if (!WatheExtendedServerConfig.lastStandEnabled) return true;
            if (deathReason == null) return true;
            if (!GameStatus.isActive(victim.getWorld())) return true;

            String id = deathReason.toString();
            String message = null;

            if ("stupid_express:broken_heart".equals(id)) {
                message = "You feel your heart begin to ache...";
            } else if ("noellesroles:voodoo".equals(id)) {
                if (killer != null) {
                    message = "Your identity has been compromised...";
                } else if (!isGuesser(victim)) {
                    message = "Strange visions curse your mind...";
                }
            }

            if (message == null) return true;

            return !tryActivate(victim, killer, deathReason, message);
        });
    }

    private static boolean isGuesser(PlayerEntity player) {
        try {
            WorldModifierComponent wmc = WorldModifierComponent.KEY.get(player.getWorld());
            return wmc.isModifier(player, Noellesroles.GUESSER);
        } catch (Throwable ignored) {}
        return false;
    }
}
