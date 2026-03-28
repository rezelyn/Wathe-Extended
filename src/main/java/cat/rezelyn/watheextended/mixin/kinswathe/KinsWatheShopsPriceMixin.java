package cat.rezelyn.watheextended.mixin.kinswathe;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "org.BsXinQin.kinswathe.KinsWatheShops", remap = false)
public class KinsWatheShopsPriceMixin {

    @Inject(method = "getItemPrice(Ljava/lang/String;I)I", at = @At("HEAD"), cancellable = true, require = 0)
    private static void watheextended$overrideItemPrice(String key, int defaultValue, CallbackInfoReturnable<Integer> cir) {
        Integer override = switch (key) {
            case "KNIFE" -> WatheExtendedServerConfig.huntingKnifePrice;
            case "REVOLVER" -> WatheExtendedServerConfig.revolverPrice;
            case "GRENADE" -> WatheExtendedServerConfig.grenadePrice;
            case "PSYCHO_MODE" -> WatheExtendedServerConfig.psychoModePrice;
            case "POISON_VIAL" -> WatheExtendedServerConfig.poisonVialPrice;
            case "SCORPION" -> WatheExtendedServerConfig.scorpionPrice;
            case "FIRECRACKER" -> WatheExtendedServerConfig.firecrackerPrice;
            case "LOCKPICK" -> WatheExtendedServerConfig.lockpickPrice;
            case "CROWBAR" -> WatheExtendedServerConfig.crowbarPrice;
            case "BODY_BAG" -> WatheExtendedServerConfig.bodyBagPrice;
            case "BLACKOUT" -> WatheExtendedServerConfig.blackoutPrice;
            default -> null;
        };
        if (override != null) cir.setReturnValue(override);
    }
}
