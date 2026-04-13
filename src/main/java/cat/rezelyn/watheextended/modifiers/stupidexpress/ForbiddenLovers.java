package cat.rezelyn.watheextended.modifiers.stupidexpress;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import org.agmas.harpymodloader.component.WorldModifierComponent;
import org.agmas.harpymodloader.modifiers.Modifier;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class ForbiddenLovers {

    private ForbiddenLovers() {
    }

    public static void apply(World world, GameWorldComponent component) {
        if (Math.random() >= WatheExtendedServerConfig.getForbiddenLoversChance()) return;

        try {
            WorldModifierComponent modifierComponent = WorldModifierComponent.KEY.get(world);

            // get stupid_express:lovers modifier
            Class<?> seModifiersClass = Class.forName("pro.fazeclan.river.stupid_express.constants.SEModifiers");
            Modifier loversModifier = (Modifier) seModifiersClass.getField("LOVERS").get(null);

            List<UUID> loverUuids = modifierComponent.getAllWithModifier(loversModifier);
            if (loverUuids.size() < 2) return; // need a pair of players

            // check for sides
            boolean hasKiller = false;
            boolean hasInnocent = false;
            for (UUID uuid : loverUuids) {
                PlayerEntity player = world.getPlayerByUuid(uuid);
                if (player == null) continue;
                if (component.isInnocent(player)) hasInnocent = true;
                else hasKiller = true;
            }
            if (hasKiller && hasInnocent) return; // valid pair

            UUID keepUuid = loverUuids.getFirst();
            for (int i = 1; i < loverUuids.size(); i++) {
                ArrayList<Modifier> modifier = modifierComponent.modifiers.get(loverUuids.get(i));
                if (modifier != null) modifier.remove(loversModifier);
            }

            PlayerEntity keepPlayer = world.getPlayerByUuid(keepUuid);
            if (keepPlayer == null) {
                ArrayList<Modifier> modifier = modifierComponent.modifiers.get(keepUuid);
                if (modifier != null) modifier.remove(loversModifier);
                modifierComponent.sync();
                return;
            }

            boolean keepIsKiller = !component.isInnocent(keepPlayer);
            boolean needKiller = !keepIsKiller;

            List<UUID> isLover = modifierComponent.getAllWithModifier(loversModifier);
            List<ServerPlayerEntity> candidates = new ArrayList<>();
            for (PlayerEntity player : world.getPlayers()) {
                if (!(player instanceof ServerPlayerEntity sp)) continue;
                if (component.getRole(sp) == null) continue; // no role
                boolean isKiller = !component.isInnocent(sp);
                if (isKiller != needKiller) continue; // wrong pair
                if (isLover.contains(sp.getUuid())) continue; // already a lover
                candidates.add(sp);
            }
            Collections.shuffle(candidates);

            if (!candidates.isEmpty()) {
                modifierComponent.addModifier(candidates.getFirst().getUuid(), loversModifier);
            } else {
                ArrayList<Modifier> modifier = modifierComponent.modifiers.get(keepUuid);
                if (modifier != null) modifier.remove(loversModifier);
            }

            modifierComponent.sync();
        } catch (Throwable ignored) {
        }
    }
}
