package cat.rezelyn.watheextended.mixin.wathe;

import cat.rezelyn.watheextended.modifiers.adaptive.AdaptiveModifier;
import cat.rezelyn.watheextended.modifiers.taxed.TaxedModifier;
import cat.rezelyn.watheextended.modifiers.WatheExtendedModifiers;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.agmas.harpymodloader.component.WorldModifierComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerShopComponent.class)
public class PlayerShopComponentTaxedMixin {

    @Shadow @Final
    private PlayerEntity player;

    @ModifyVariable(method = "addToBalance(I)V", at = @At("HEAD"), argsOnly = true)
    private int watheextended$applyTax(int amount) {
        try {
            if (amount <= 0) return amount;
            if (!(player instanceof ServerPlayerEntity sp)) return amount;
            ServerWorld world = sp.getServerWorld();
            GameWorldComponent gwc = GameWorldComponent.KEY.get(world);
            if (!gwc.isRunning()) return amount;
            // only affect players that can use killer features
            if (!gwc.canUseKillerFeatures(sp)) return amount;
            WorldModifierComponent wmc = WorldModifierComponent.KEY.get(world);
            if (!wmc.isModifier(sp, WatheExtendedModifiers.TAXED)) return amount;
            AdaptiveModifier.KillContext ctx = AdaptiveModifier.CURRENT_KILL.get();
            if (ctx == null || !ctx.killerUuid().equals(sp.getUuid())) return amount;
            return TaxedModifier.applyTaxIfEligible(sp.getUuid(), amount);
        } catch (Throwable t) {
            return amount;
        }
    }
}
