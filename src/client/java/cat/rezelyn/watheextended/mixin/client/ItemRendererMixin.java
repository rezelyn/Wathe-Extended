package cat.rezelyn.watheextended.mixin.client;

import cat.rezelyn.watheextended.WatheExtended;
import cat.rezelyn.watheextended.index.WatheExtendedItems;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @ModifyVariable(method = "renderItem", at = @At(value = "HEAD"), argsOnly = true)
    public BakedModel useRubyStaffModel(BakedModel value, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (stack.isOf(WatheExtendedItems.GUIDEBOOK) && renderMode != ModelTransformationMode.GUI) {
            return ((ItemRendererAccessor) this).watheextended$getModels().getModelManager().getModel(new ModelIdentifier(Identifier.of(WatheExtended.MOD_ID, "guidebook_3d"), "inventory"));
        }
        return value;
    }
}