//  package cat.rezelyn.watheextended.mixin.fix;
//
//  import dev.doctor4t.wathe.cca.PlayerPoisonComponent;
//  import dev.doctor4t.wathe.util.PoisonUtils;
//  import org.spongepowered.asm.mixin.Mixin;
//  import org.spongepowered.asm.mixin.injection.At;
//  import org.spongepowered.asm.mixin.injection.Redirect;
//
//  import java.util.UUID;
//
//  // fix: catches npe from Noelle's Roles poisonOverride when poisoner uuid is null for bedPoison
//  @Mixin(value = PoisonUtils.class, remap = false)
//  public abstract class PoisonNullGuardMixin {
//
//      @Redirect(method = "bedPoison", at = @At(value = "INVOKE", target = "Ldev/doctor4t/wathe/cca/PlayerPoisonComponent;setPoisonTicks(ILjava/util/UUID;)V"), remap = false)
//      private static void watheextended$guardNullPoisoner(PlayerPoisonComponent component, int ticks, UUID poisoner) {
//          if (component == null || poisoner == null) return;
//          component.setPoisonTicks(ticks, poisoner);
//      }
//  }
