package cat.rezelyn.watheextended.game;

import cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper;
import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.game.modifiers.adaptive.AdaptiveModifier;
import cat.rezelyn.watheextended.game.modifiers.feather.SlowFallingFix;
import cat.rezelyn.watheextended.game.modifiers.lovers.ForbiddenLovers;
import cat.rezelyn.watheextended.game.modifiers.taxed.TaxedModifier;
import cat.rezelyn.watheextended.game.roles.awesomebinglus.AwesomeBinglusNote;
import cat.rezelyn.watheextended.mixin.game.PlayerSprintTimeAccessor;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.agmas.noellesroles.Noellesroles;

public final class GameEvents {

  private GameEvents() {}

  public static void register() {
    dev.doctor4t.wathe.api.event.GameEvents.ON_FINISH_INITIALIZE.register(
        (world, gameWorldComponent) -> {
          if (world instanceof ServerWorld server) {
            for (var player : server.getPlayers()) {
              Role role = gameWorldComponent.getRole(player);
              if (role != null && role.getMaxSprintTime() >= 0) {
                ((PlayerSprintTimeAccessor) player)
                    .watheextended$setSprintingTicks(role.getMaxSprintTime());
              }
            }
          }
          try {
            WatheExtendedWorldComponent game = WatheExtendedWorldComponent.KEY.get(world);
            game.clearKilledPlayers();
            game.clearRevolverPickupBlocks();
            game.setGameStartWorldTime(world.getTime());
          } catch (Throwable ignored) {
          }

          AdaptiveModifier.clearAll();
          TaxedModifier.clearAll();
          ItemPrices.applyWorldAll(world);

          if (world instanceof ServerWorld server) SlowFallingFix.applyOnGameStart(server);

          if (cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper.isLoaded()) {
            AwesomeBinglusNote.applyOnGameStart(world, gameWorldComponent);
            if (world instanceof ServerWorld server) {
              try {
                for (java.util.UUID uuid :
                    gameWorldComponent.getAllWithRole(Noellesroles.AWESOME_BINGLUS)) {
                  PlayerEntity player = server.getPlayerByUuid(uuid);
                  if (player != null) PlayerShopComponent.KEY.get(player).reset();
                }
              } catch (Throwable ignored) {
              }
            }
          }

          if (ConfigHelper.isLoaded()) {
            try {
              WatheExtendedWorldComponent wec = WatheExtendedWorldComponent.KEY.get(world);
              if (wec.isForbiddenLoversEnabled()) {
                ForbiddenLovers.apply(world, gameWorldComponent);
              }
            } catch (Throwable ignored) {
            }
          }
        });

    dev.doctor4t.wathe.api.event.GameEvents.ON_FINISH_FINALIZE.register(
        (world, gameWorldComponent) -> {
          try {
            WatheExtendedWorldComponent game = WatheExtendedWorldComponent.KEY.get(world);
            game.clearKilledPlayers();
            game.clearRevolverPickupBlocks();
          } catch (Throwable ignored) {
          }
          AdaptiveModifier.clearAll();
          TaxedModifier.clearAll();
          LastStand.clearAll();
        });
  }
}
