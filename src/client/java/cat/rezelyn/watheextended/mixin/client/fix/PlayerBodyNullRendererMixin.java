package cat.rezelyn.watheextended.mixin.client.fix;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doctor4t.wathe.cca.PlayerMoodComponent;
import dev.doctor4t.wathe.client.render.entity.PlayerBodyEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerBodyEntityRenderer.class)
public class PlayerBodyNullRendererMixin {

    @WrapOperation(method = "render(Ldev/doctor4t/wathe/entity/PlayerBodyEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Ldev/doctor4t/wathe/cca/PlayerMoodComponent;isLowerThanDepressed()Z"))
    private boolean watheExtended$nullCheckMoodComponent(PlayerMoodComponent instance, Operation<Boolean> original) {
        if (instance == null) return false;
        return original.call(instance);
    }
}
