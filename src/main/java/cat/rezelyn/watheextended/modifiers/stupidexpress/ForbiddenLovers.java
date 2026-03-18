package cat.rezelyn.watheextended.modifiers.stupidexpress;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
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

    public static void apply(World world, GameWorldComponent gwc) {
        if (Math.random() >= WatheExtendedServerConfig.getForbiddenLoversChance()) return;

        try {
            org.agmas.harpymodloader.component.WorldModifierComponent wmc =
                    org.agmas.harpymodloader.component.WorldModifierComponent.KEY.get(world);

            // get stupid_express:lovers modifier
            Class<?> seModifiersClass = Class.forName("pro.fazeclan.river.stupid_express.constants.SEModifiers");
            org.agmas.harpymodloader.modifiers.Modifier loversModifier =
                    (org.agmas.harpymodloader.modifiers.Modifier) seModifiersClass.getField("LOVERS").get(null);

            List<UUID> loverUuids = wmc.getAllWithModifier(loversModifier);
            if (loverUuids.size() < 2) return; // need a pair of players

            // check for sides
            boolean hasKiller = false;
            boolean hasInnocent = false;
            for (UUID uuid : loverUuids) {
                PlayerEntity p = world.getPlayerByUuid(uuid);
                if (p == null) continue;
                if (gwc.isInnocent(p)) hasInnocent = true;
                else hasKiller = true;
            }
            if (hasKiller && hasInnocent) return; // valid pair

            UUID keepUuid = loverUuids.getFirst();
            for (int i = 1; i < loverUuids.size(); i++) {
                ArrayList<org.agmas.harpymodloader.modifiers.Modifier> modList =
                        wmc.modifiers.get(loverUuids.get(i));
                if (modList != null) modList.remove(loversModifier);
            }

            PlayerEntity keepPlayer = world.getPlayerByUuid(keepUuid);
            if (keepPlayer == null) {
                ArrayList<org.agmas.harpymodloader.modifiers.Modifier> keepList =
                        wmc.modifiers.get(keepUuid);
                if (keepList != null) keepList.remove(loversModifier);
                wmc.sync();
                return;
            }

            boolean keepIsKiller = !gwc.isInnocent(keepPlayer);
            boolean needKiller = !keepIsKiller;

            List<UUID> isLover = wmc.getAllWithModifier(loversModifier);
            List<ServerPlayerEntity> candidates = new ArrayList<>();
            for (PlayerEntity player : world.getPlayers()) {
                if (!(player instanceof ServerPlayerEntity sp)) continue;
                if (gwc.getRole(sp) == null) continue; // no role
                boolean isKiller = !gwc.isInnocent(sp);
                if (isKiller != needKiller) continue; // wrong pair
                if (isLover.contains(sp.getUuid())) continue; // already a lover
                candidates.add(sp);
            }
            Collections.shuffle(candidates);

            if (!candidates.isEmpty()) {
                wmc.addModifier(candidates.getFirst().getUuid(), loversModifier);
            } else {
                ArrayList<org.agmas.harpymodloader.modifiers.Modifier> keepList =
                        wmc.modifiers.get(keepUuid);
                if (keepList != null) keepList.remove(loversModifier);
            }

            wmc.sync();
        } catch (Throwable ignored) {
        }
    }
}
