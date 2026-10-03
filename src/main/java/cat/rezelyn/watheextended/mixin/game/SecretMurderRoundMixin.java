package cat.rezelyn.watheextended.mixin.game;

import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import dev.doctor4t.wathe.api.WatheGameModes;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.server.world.ServerWorld;
import org.agmas.harpymodloader.Harpymodloader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameFunctions.class)
public abstract class SecretMurderRoundMixin {

  @Inject(method = "finalizeGame", at = @At("TAIL"))
  private static void watheExtended$restoreDefaultAfterSecretRound(
      ServerWorld world, CallbackInfo ci) {
    GameWorldComponent game = GameWorldComponent.KEY.get(world);
    if (game.getGameMode() == WatheGameModes.SECRET_MURDER
        || game.getGameMode() == Harpymodloader.SECRET_MODDED_GAMEMODE) {
      WatheExtendedWorldComponent.KEY.get(world).restoreModdedMurderDefault();
    }
  }
}
