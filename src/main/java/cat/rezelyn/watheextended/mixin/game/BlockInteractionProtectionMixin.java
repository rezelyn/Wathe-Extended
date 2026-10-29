package cat.rezelyn.watheextended.mixin.game;

import cat.rezelyn.watheextended.api.MapVariables;
import cat.rezelyn.watheextended.block.ProtectedBlocks;
import cat.rezelyn.watheextended.block.ProtectedBlocks.Category;
import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class BlockInteractionProtectionMixin {

  @Shadow @Final private ServerPlayerEntity player;

  private static boolean watheExtended$isInsideProtectedArea(World world, BlockPos pos) {
    Vec3d center = Vec3d.ofCenter(pos);
    Box playArea = MapVariables.getPlayArea(world);
    Box readyArea = MapVariables.getReadyArea(world);
    Box lobbyArea = MapVariables.getLobbyArea(world);
    return (playArea != null && playArea.contains(center))
        || (readyArea != null && readyArea.contains(center))
        || (lobbyArea != null && lobbyArea.contains(center));
  }

  /**
   * Blocks protected interactions in configured areas, while allowing selected block categories in
   * the Play Area and Ready Area
   *
   * @param player the player interacting with the block
   * @param world the world containing the block
   * @param stack the item held during the interaction
   * @param hand the hand used for the interaction
   * @param hitResult the block hit by the interaction
   * @param cir the callback used to cancel the interaction
   */
  @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
  private void watheExtended$blockInteraction(
      ServerPlayerEntity player,
      World world,
      ItemStack stack,
      Hand hand,
      BlockHitResult hitResult,
      CallbackInfoReturnable<ActionResult> cir) {
    try {
      WatheExtendedWorldComponent component = WatheExtendedWorldComponent.KEY.get(world);
      if (!component.isBlockInteractionsProtected()) return;

      BlockPos pos = hitResult.getBlockPos();

      // BlockItem placement targets the clicked block when it is replaceable,
      // otherwise the adjacent block on the clicked face.
      if (stack.getItem() instanceof BlockItem) {
        BlockPos placementPos =
            world.getBlockState(pos).isReplaceable() ? pos : pos.offset(hitResult.getSide());
        if (watheExtended$isInsideProtectedArea(world, placementPos)) {
          cir.setReturnValue(ActionResult.FAIL);
          return;
        }
      }

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

      // playArea allowlist: allow lamps, doors, buttons and privacy glass
      if (inPlayArea) {
        if (ProtectedBlocks.isInAnyCategory(
            state,
            Category.LAMPS,
            Category.DOORS,
            Category.BUTTONS,
            Category.DECORATIONS,
            Category.PRIVACY_GLASS)) return;
      }

      // readyArea allowlist: allow doors only
      if (inReadyArea) {
        if (ProtectedBlocks.isInCategory(state, Category.DOORS)) return;
      }

      cir.setReturnValue(ActionResult.FAIL);
    } catch (Throwable ignored) {
    }
  }

  /**
   * Prevents breaking blocks inside configured protected areas while block protection is enabled
   *
   * @param pos the position of the block to break
   * @param cir the callback used to cancel the break
   */
  @Inject(method = "tryBreakBlock", at = @At("HEAD"), cancellable = true)
  private void watheExtended$blockBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
    try {
      World world = player.getWorld();
      WatheExtendedWorldComponent component = WatheExtendedWorldComponent.KEY.get(world);
      if (!component.isBlockInteractionsProtected()) return;

      if (watheExtended$isInsideProtectedArea(world, pos)) {
        cir.setReturnValue(false);
      }
    } catch (Throwable ignored) {
    }
  }
}
