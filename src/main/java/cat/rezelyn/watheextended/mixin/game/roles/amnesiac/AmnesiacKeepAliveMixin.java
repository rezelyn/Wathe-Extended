package cat.rezelyn.watheextended.mixin.game.roles.amnesiac;

import cat.rezelyn.watheextended.api.config.stupidexpress.ConfigHelper;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.game.GameFunctions.WinStatus;
import java.util.List;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import pro.fazeclan.river.stupid_express.constants.SERoles;

@Mixin(targets = "dev.doctor4t.wathe.game.gamemode.MurderGameMode")
public abstract class AmnesiacKeepAliveMixin {

  @Inject(
      method = "tickServerGameLoop",
      at =
          @At(
              value = "FIELD",
              target =
                  "Ldev/doctor4t/wathe/game/GameFunctions$WinStatus;NONE:Ldev/doctor4t/wathe/game/GameFunctions$WinStatus;",
              ordinal = 3),
      cancellable = true,
      locals = LocalCapture.CAPTURE_FAILSOFT)
  private void watheExtended$keepAliveForAmnesiac(
      ServerWorld world, GameWorldComponent game, CallbackInfo ci, WinStatus winStatus) {
    if (!ConfigHelper.getAmnesiacKeepsGameGoing() || winStatus != WinStatus.PASSENGERS) return;

    List<ServerPlayerEntity> players = world.getPlayers(GameFunctions::isPlayerAliveAndSurvival);
    boolean amnesiacAlive =
        players.stream().anyMatch(player -> game.isRole(player, SERoles.AMNESIAC));
    if (amnesiacAlive) ci.cancel();
  }
}
