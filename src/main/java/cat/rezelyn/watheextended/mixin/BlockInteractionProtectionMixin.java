package cat.rezelyn.watheextended.mixin;

import cat.rezelyn.watheextended.api.cca.MapVariables;
import cat.rezelyn.watheextended.cca.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.protection.ProtectedBlocks;
import dev.doctor4t.wathe.block.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class BlockInteractionProtectionMixin {

    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    private void watheExtended$blockInteraction(
            ServerPlayerEntity player,
            World world,
            net.minecraft.item.ItemStack stack,
            net.minecraft.util.Hand hand,
            BlockHitResult hitResult,
            CallbackInfoReturnable<ActionResult> cir) {
        try {
            WatheExtendedWorldComponent wec = WatheExtendedWorldComponent.KEY.get(world);
            if (!wec.isBlockInteractionsProtected()) return;

            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);

            if (!ProtectedBlocks.isProtected(state)) return;

            Box playArea = MapVariables.getPlayArea(world);
            if (playArea != null && playArea.contains(Vec3d.ofCenter(pos))) {
                Block block = state.getBlock();
                // allow lamps/doors/buttons interactions in the playArea
                boolean isLamp = block instanceof ToggleableFacingLightBlock || block instanceof NeonTubeBlock || block instanceof NeonPillarBlock;
                boolean isDoor = block instanceof DoorBlock || block instanceof SmallDoorBlock;
                boolean isButton = block instanceof WatheButtonBlock;
                boolean isOrnament = block instanceof OrnamentBlock;
                if (isLamp || isDoor || isOrnament || isButton) return;
            }

            Box readyArea = MapVariables.getReadyArea(world);
            if (readyArea != null && readyArea.contains(Vec3d.ofCenter(pos))) {
                Block block = state.getBlock();
                boolean isDoor = block instanceof DoorBlock || block instanceof SmallDoorBlock;
                if (isDoor) return;
            }

            cir.setReturnValue(ActionResult.FAIL);
        } catch (Throwable ignored) {
        }
    }
}


