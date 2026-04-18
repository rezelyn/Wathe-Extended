package cat.rezelyn.watheextended.mixin.shop;

import cat.rezelyn.watheextended.modifiers.AdaptiveModifier;
import cat.rezelyn.watheextended.modifiers.TaxedModifier;
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

    @ModifyVariable(method = "addToBalance(I)V", at = @At("HEAD"), argsOnly = true, name = "amount")
    private int watheextended$applyTax(int amount) {
        try {
            if (amount <= 0) return amount;
            if (!(this.player instanceof ServerPlayerEntity p)) return amount;

            ServerWorld world = p.getServerWorld();
            GameWorldComponent game = GameWorldComponent.KEY.get(world);

            if (!game.isRunning()) return amount;
            if (!game.canUseKillerFeatures(p)) return amount;

            WorldModifierComponent modifier = WorldModifierComponent.KEY.get(world);

            if (!modifier.isModifier(p, WatheExtendedModifiers.TAXED)) return amount;

            AdaptiveModifier.KillContext kill = AdaptiveModifier.CURRENT_KILL.get();

            if (kill == null || !kill.killerUuid().equals(p.getUuid())) return amount;

            return TaxedModifier.applyTaxIfEligible(p.getUuid(), amount);
        } catch (Throwable t) {
            return amount;
        }
    }
}
