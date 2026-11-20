package cat.rezelyn.watheextended.mixin.game.roles.physician;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import java.awt.Color;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.agmas.noellesroles.infected.InfectedPlayerComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InfectedPlayerComponent.class, remap = false)
public abstract class PhysicianInfectionAlertMixin {
  @Shadow @Final private PlayerEntity player;
  @Shadow public int infectedTicks;

  @Inject(method = "infect", at = @At("HEAD"))
  private void watheextended$alertPhysicians(PlayerEntity infector, CallbackInfo ci) {
    if (infectedTicks > 0
        || !(player instanceof ServerPlayerEntity target)
        || !GameFunctions.isPlayerAliveAndSurvival(target)) return;

    var world = target.getServerWorld();
    var game = GameWorldComponent.KEY.get(world);
    for (var physician : world.getPlayers()) {
      if (game.isRole(physician, KinsWatheRoles.PHYSICIAN)
          && GameFunctions.isPlayerAliveAndSurvival(physician)) {
        physician.sendMessage(
            Text.translatable("tip.kinswathe.physician.poisoned").withColor(Color.RED.getRGB()),
            true);
      }
    }
  }
}
