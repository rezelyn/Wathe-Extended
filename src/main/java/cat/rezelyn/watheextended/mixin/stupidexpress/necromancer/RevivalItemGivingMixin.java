package cat.rezelyn.watheextended.mixin.stupidexpress.necromancer;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.entity.PlayerBodyEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.agmas.harpymodloader.events.ModdedRoleAssigned;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pro.fazeclan.river.stupid_express.role.necromancer.RevivalSelectionHandler;

// fix: players revived by the Necromancer not receiving specific-role items
@Mixin(value = RevivalSelectionHandler.class, remap = false)
public class RevivalItemGivingMixin {

    @Inject(method = "lambda$init$1", at = @At("RETURN"), remap = false)
    private static void watheextended$giveRevivalRoleItems(PlayerEntity interactingPlayer, World world, Hand hand, Entity entity, EntityHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (cir.getReturnValue() != ActionResult.CONSUME) return;
        if (!(entity instanceof PlayerBodyEntity body)) return;
        if (!(world instanceof ServerWorld serverWorld)) return;

        PlayerEntity revivedPlayer = serverWorld.getPlayerByUuid(body.getPlayerUuid());
        if (revivedPlayer == null) return;

        GameWorldComponent gwc = GameWorldComponent.KEY.get(world);
        Role assignedRole = gwc.getRole(revivedPlayer);
        if (assignedRole == null) return;

        ModdedRoleAssigned.EVENT.invoker().assignModdedRole(revivedPlayer, assignedRole);
    }
}
