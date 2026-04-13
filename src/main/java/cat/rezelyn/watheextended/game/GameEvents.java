package cat.rezelyn.watheextended.game;

import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.modifiers.AdaptiveModifier;
import cat.rezelyn.watheextended.roles.noellesroles.AwesomeBinglusNote;
import cat.rezelyn.watheextended.modifiers.noellesroles.FeatherModifierFix;
import cat.rezelyn.watheextended.modifiers.stupidexpress.ForbiddenLovers;
import cat.rezelyn.watheextended.modifiers.TaxedModifier;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.agmas.noellesroles.Noellesroles;

public final class GameEvents {

    private GameEvents() {
    }

    public static void register() {
        dev.doctor4t.wathe.api.event.GameEvents.ON_FINISH_INITIALIZE.register((world, gameWorldComponent) -> {
            try {
                WatheExtendedWorldComponent game = WatheExtendedWorldComponent.KEY.get(world);
                game.clearKilledPlayers();
                game.setGameStartWorldTime(world.getTime());
            } catch (Throwable ignored) {
            }

            AdaptiveModifier.clearAll();
            TaxedModifier.clearAll();
            ItemPrices.applyWorldAll(world);

            if (world instanceof ServerWorld server) FeatherModifierFix.applyOnGameStart(server);

            if (cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.isLoaded()) {
                AwesomeBinglusNote.applyOnGameStart(world, gameWorldComponent);
                if (world instanceof ServerWorld server) {
                    try {
                        for (java.util.UUID uuid : gameWorldComponent.getAllWithRole(Noellesroles.AWESOME_BINGLUS)) {
                            PlayerEntity player = server.getPlayerByUuid(uuid);
                            if (player != null) PlayerShopComponent.KEY.get(player).reset();
                        }
                    } catch (Throwable ignored) {
                    }
                }
            }

            if (cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper.isLoaded()) {
                try {
                    WatheExtendedWorldComponent wec = WatheExtendedWorldComponent.KEY.get(world);
                    if (wec.isForbiddenLoversEnabled()) {
                        ForbiddenLovers.apply(world, gameWorldComponent);
                    }
                } catch (Throwable ignored) {
                }
            }
        });

        dev.doctor4t.wathe.api.event.GameEvents.ON_FINISH_FINALIZE.register((world, gameWorldComponent) -> {
            try {
                WatheExtendedWorldComponent.KEY.get(world).clearKilledPlayers();
            } catch (Throwable ignored) {
            }
            AdaptiveModifier.clearAll();
            TaxedModifier.clearAll();
            LastStand.clearAll();
        });
    }
}
