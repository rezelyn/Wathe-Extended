package cat.rezelyn.watheextended.mixin.stupidexpress;

import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pro.fazeclan.river.stupid_express.role.thief.ThiefItemRules;

import java.util.List;

@Mixin(value = ThiefItemRules.class, remap = false)
public class ThiefItemRulesMixin {

    @Final
    @Shadow
    private static List<Identifier> CAN_TAKE;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void watheExtended$addCanTakeItems(CallbackInfo ci) {
        CAN_TAKE.add(Identifier.of("kinswathe", "pan"));
        CAN_TAKE.add(Identifier.of("kinswathe", "poison_injector"));
        CAN_TAKE.add(Identifier.of("kinswathe", "blowgun"));
        CAN_TAKE.add(Identifier.of("kinswathe", "pill"));
        CAN_TAKE.add(Identifier.of("noellesroles", "delusion_vial"));
        CAN_TAKE.add(Identifier.of("noellesroles", "defense_vial"));
        CAN_TAKE.add(Identifier.of("starexpress", "tape"));
    }
}


