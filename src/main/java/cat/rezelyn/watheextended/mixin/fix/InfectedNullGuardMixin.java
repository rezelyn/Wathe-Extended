package cat.rezelyn.watheextended.mixin.fix;

import org.agmas.noellesroles.infected.InfectedPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * <b>Fix:</b> Prevents a {@link NullPointerException} crash in Noelle's Roles' infection mechanic
 *
 * <p>If an {@link InfectedPlayerComponent} ticks with no infector <i>(e.g. the Infected left or
 * infection got cleared)</i>, the component is reset instead of crashing the server tick
 */
@Mixin(value = InfectedPlayerComponent.class, remap = false)
public class InfectedNullGuardMixin {

  @Inject(method = "serverTick", at = @At("HEAD"), cancellable = true, remap = false)
  private void watheextended$guardNullInfector(CallbackInfo ci) {
    if (((InfectedPlayerComponent) (Object) this).infector == null) {
      ((InfectedPlayerComponent) (Object) this).reset();
      ci.cancel();
    }
  }
}
