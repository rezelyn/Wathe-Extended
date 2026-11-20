package cat.rezelyn.watheextended.client.mixin.game.roles.physician;

import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.game.GameFunctions;
import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.BsXinQin.kinswathe.KinsWatheRoles;
import org.agmas.noellesroles.infected.InfectedPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WatheClient.class)
public abstract class PhysicianInfectionHighlightMixin {
  @Inject(method = "getInstinctHighlight", at = @At("HEAD"), cancellable = true)
  private static void watheextended$highlightInfection(
      Entity entity, CallbackInfoReturnable<Integer> cir) {
    var viewer = MinecraftClient.getInstance().player;
    var game = WatheClient.gameComponent;
    if (entity instanceof PlayerEntity target
        && viewer != null
        && game != null
        && game.isRole(viewer, KinsWatheRoles.PHYSICIAN)
        && WatheClient.isPlayerAliveAndInSurvival()
        && GameFunctions.isPlayerAliveAndSurvival(target)
        && InfectedPlayerComponent.KEY.get(target).infectedTicks > 0) {
      cir.setReturnValue(Color.RED.getRGB());
    }
  }
}
