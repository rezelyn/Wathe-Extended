package cat.rezelyn.watheextended.client.render;

import cat.rezelyn.watheextended.block.IshPlushBlockEntity;
import dev.doctor4t.ratatouille.mixin.client.BlockRenderManagerAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class IshPlushBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {

    private final BlockRenderManager renderManager;

    public IshPlushBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.renderManager = context.getRenderManager();
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        double squash = 0.0;
        if (entity instanceof IshPlushBlockEntity plushBE) {
            squash = plushBE.squash;
        }

        double prevSquash = squash * 3.0;
        double lerpVal = MathHelper.lerp((double) tickDelta, prevSquash, squash);
        float squashF = (float) Math.pow(1.0 - 1.0 / (1.0 + lerpVal), 2.0);

        matrices.scale(1.0f, 1.0f - squashF, 1.0f);
        matrices.translate(0.5, 0.0, 0.5);
        matrices.scale(1.0f + squashF / 2.0f, 1.0f, 1.0f + squashF / 2.0f);
        matrices.translate(-0.5, 0.0, -0.5);

        BlockState state = entity.getCachedState();
        var model = renderManager.getModel(state);
        var modelRenderer = ((BlockRenderManagerAccessor) renderManager).getModelRenderer();
        var buffer = vertexConsumers.getBuffer(RenderLayers.getEntityBlockLayer(state, false));
        modelRenderer.render(matrices.peek(), buffer, state, model, 255f, 255f, 255f, light, overlay);

        matrices.pop();
    }
}
