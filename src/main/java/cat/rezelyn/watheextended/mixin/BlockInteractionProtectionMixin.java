package cat.rezelyn.watheextended.mixin;

import cat.rezelyn.watheextended.api.MapVariables;
import cat.rezelyn.watheextended.block.ProtectedBlocks;
import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import dev.doctor4t.wathe.block.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
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
    private void watheExtended$blockInteraction(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        try {
            WatheExtendedWorldComponent component = WatheExtendedWorldComponent.KEY.get(world);
            if (!component.isBlockInteractionsProtected()) return;

            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);

            if (!ProtectedBlocks.isProtected(state)) return;

            Vec3d center = Vec3d.ofCenter(pos);

            Box playArea = MapVariables.getPlayArea(world);
            Box readyArea = MapVariables.getReadyArea(world);
            Box lobbyArea = MapVariables.getLobbyArea(world);

            boolean inPlayArea = playArea != null && playArea.contains(center);
            boolean inReadyArea = readyArea != null && readyArea.contains(center);
            boolean inLobbyArea = lobbyArea.contains(center);

            // scoped to lobbyArea, readyArea, and playArea only
            // stuff in the world that are outside these boxes are not protected
            if (!inLobbyArea && !inReadyArea && !inPlayArea) {
                return;
            }

            // playArea whitelist: allow lamps, doors, buttons
            if (inPlayArea) {
                Block block = state.getBlock();
                boolean isLamp = block instanceof ToggleableFacingLightBlock || block instanceof NeonTubeBlock || block instanceof NeonPillarBlock;
                boolean isDoor = block instanceof DoorBlock || block instanceof SmallDoorBlock;
                boolean isButton = block instanceof WatheButtonBlock;
                boolean isOrnament = block instanceof OrnamentBlock;
                if (isLamp || isDoor || isOrnament || isButton) return;
            }

            // readyArea whitelist: allow doors only
            if (inReadyArea) {
                Block block = state.getBlock();
                boolean isDoor = block instanceof DoorBlock || block instanceof SmallDoorBlock;
                if (isDoor) return;
            }

            cir.setReturnValue(ActionResult.FAIL);
        } catch (Throwable ignored) {
        }
    }
}
