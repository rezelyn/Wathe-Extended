package cat.rezelyn.watheextended.mixin;

import cat.rezelyn.watheextended.index.WatheExtendedItems;
import dev.doctor4t.wathe.game.mapeffect.HarpyExpressTrainMapEffect;
import dev.doctor4t.wathe.index.WatheItems;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(HarpyExpressTrainMapEffect.class)
public class HarpyExpressTrainMapEffectMixin {

    @Inject(method = "initializeMapEffects", at = @At("TAIL"))
    private void watheExtended$replaceLetterWithGuidebook(ServerWorld world, List<ServerPlayerEntity> players, CallbackInfo ci) {
        for (ServerPlayerEntity player : players) {
            for (int i = 0; i < player.getInventory().size(); i++) {
                ItemStack stack = player.getInventory().getStack(i);
                if (stack.isOf(WatheItems.LETTER)) {
                    player.getInventory().setStack(i, new ItemStack(WatheExtendedItems.GUIDEBOOK));
                }
            }
        }
    }
}
