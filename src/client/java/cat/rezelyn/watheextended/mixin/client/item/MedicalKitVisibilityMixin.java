package cat.rezelyn.watheextended.mixin.client.item;

import cat.rezelyn.watheextended.network.ClientConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doctor4t.wathe.index.WatheItems;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.BsXinQin.kinswathe.KinsWatheItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HeldItemFeatureRenderer.class)
public abstract class MedicalKitVisibilityMixin {

  @WrapOperation(
      method = "render",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/entity/LivingEntity;getMainHandStack()Lnet/minecraft/item/ItemStack;"))
  private ItemStack watheextended$disguiseMedicalKit(
      LivingEntity entity,
      Operation<ItemStack> original,
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light,
      LivingEntity renderedEntity,
      float limbAngle,
      float limbDistance,
      float tickDelta) {
    ItemStack stack = original.call(entity);
    if (!stack.isOf(KinsWatheItems.MEDICAL_KIT)) return stack;

    long livingPlayers =
        entity.getWorld().getPlayers().stream()
            .filter(PlayerEntity::isAlive)
            .filter(player -> !player.isSpectator())
            .count();
    if (livingPlayers < ClientConfig.getInt("watheextended.medicalKit.visiblePlayerCount", 10)) {
      return WatheItems.LOCKPICK.getDefaultStack();
    }
    return stack;
  }
}
