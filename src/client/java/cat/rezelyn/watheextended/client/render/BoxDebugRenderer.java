package cat.rezelyn.watheextended.client.render;

import cat.rezelyn.watheextended.api.MapVariables;
import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.game.TeleportationSlot;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.doctor4t.wathe.block_entity.DoorBlockEntity;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.*;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;

import java.util.List;

public final class BoxDebugRenderer {

    private static final float BEAM = 0.04f;
    private static final double HIT_W = 0.3;
    private static final double HIT_H = 1.8;
    private static final float[] PLAY_AREA_COLOR = {1.0f, 0.22f, 0.22f, 0.9f};
    private static final float[] READY_AREA_COLOR = {0.22f, 1.0f, 0.22f, 0.9f};
    private static final float[] LOBBY_AREA_COLOR = {0.22f, 0.55f, 1.0f, 0.9f};
    private static final float[] CUBE_COLOR = {1.0f, 1.0f, 1.0f, 0.95f};
    private static final float[] VIEW_COLOR = {0.0f, 0.9f, 1.0f, 0.95f};
    private static final float[] KEY_COLOR = {1.0f, 0.6f, 0.0f, 0.9f};

    public static boolean showBoxBoundaries = false;
    public static boolean showRtpSlots = false;
    public static boolean showKeyAssignments = false;

    private BoxDebugRenderer() {
    }

    public static void register() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(BoxDebugRenderer::onWorldRender);
    }

    private static void onWorldRender(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        MatrixStack matrices = context.matrixStack();
        if (matrices == null) return;

        Vec3d cam = context.camera().getPos();

        if (!showBoxBoundaries && !showRtpSlots && !showKeyAssignments) return;

        List<java.util.Map.Entry<Integer, TeleportationSlot>> slots = null;
        if (showRtpSlots) {
            try {
                slots = new java.util.ArrayList<>(WatheExtendedWorldComponent.KEY.get(client.world).getTeleportationSlots().entrySet());
            } catch (Throwable ignored) {
            }
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        matrices.push();
        matrices.translate(-cam.x, -cam.y, -cam.z);

        Tessellator tess = Tessellator.getInstance();

        if (showBoxBoundaries) {
            BufferBuilder fill = tess.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            Box playArea = MapVariables.getPlayArea(client.world);
            Box readyArea = MapVariables.getReadyArea(client.world);
            Box lobbyArea = MapVariables.getLobbyArea(client.world);
            if (playArea != null) drawBox(matrices, fill, playArea, PLAY_AREA_COLOR);
            if (readyArea != null) drawBox(matrices, fill, readyArea, READY_AREA_COLOR);
            drawBox(matrices, fill, lobbyArea, LOBBY_AREA_COLOR);
            BufferRenderer.drawWithGlobalProgram(fill.end());
        }

        if (slots != null && !slots.isEmpty()) {
            int viewDist = client.options.getViewDistance().getValue();
            double maxViewDist = (viewDist * 16.0) * (viewDist * 16.0);
            double posX = client.player.getX(), posY = client.player.getY(), posZ = client.player.getZ();

            RenderSystem.disableDepthTest();
            RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
            RenderSystem.lineWidth(1.5f);
            BufferBuilder line = tess.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);

            boolean anySlot = false;
            for (java.util.Map.Entry<Integer, TeleportationSlot> entry : slots) {
                TeleportationSlot slot = entry.getValue();
                double deltaX = slot.x - posX, deltaY = slot.y - posY, deltaZ = slot.z - posZ;
                if (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ > maxViewDist) continue;
                anySlot = true;

                drawLines(matrices, line, slot.x - HIT_W, slot.y, slot.z - HIT_W, slot.x + HIT_W, slot.y + HIT_H, slot.z + HIT_W, CUBE_COLOR[0], CUBE_COLOR[1], CUBE_COLOR[2], 0.4f);

                double eyeY = slot.y + 1.62;
                double yawRad = Math.toRadians(slot.yaw);
                double pitchRad = Math.toRadians(slot.pitch);
                double cosP = Math.cos(pitchRad);
                float dx = (float) (-Math.sin(yawRad) * cosP);
                float dy = (float) (-Math.sin(pitchRad));
                float dz = (float) (Math.cos(yawRad) * cosP);

                Matrix4f pose = matrices.peek().getPositionMatrix();
                float originX = (float) slot.x, originY = (float) eyeY, originZ = (float) slot.z;
                emitLine(line, pose, originX, originY, originZ, originX + dx * 2.0f, originY + dy * 2.0f, originZ + dz * 2.0f, dx, dy, dz, VIEW_COLOR[0], VIEW_COLOR[1], VIEW_COLOR[2], 0.4f);
            }

            if (anySlot) {
                BufferRenderer.drawWithGlobalProgram(line.end());
            } else {
                try (var buf = line.endNullable()) {
                }
            }

            VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();
            TextRenderer textRenderer = client.textRenderer;
            for (java.util.Map.Entry<Integer, TeleportationSlot> entry : slots) {
                TeleportationSlot slot = entry.getValue();
                double deltaX = slot.x - posX, deltaY = slot.y - posY, deltaZ = slot.z - posZ;
                if (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ > maxViewDist) continue;

                matrices.push();
                matrices.translate(slot.x, slot.y + HIT_H + 0.3, slot.z);
                matrices.multiply(context.camera().getRotation());
                matrices.scale(0.025f, -0.025f, 0.025f);

                Text label = Text.literal("Slot #" + entry.getKey()).styled(s -> s.withColor(0xFFFFFF));
                float offset = -textRenderer.getWidth(label) / 2f;
                textRenderer.draw(label, offset, 0f, 0xFFFFFF, false, matrices.peek().getPositionMatrix(), immediate, TextRenderer.TextLayerType.SEE_THROUGH, 0, 0xF000F0);

                matrices.pop();
            }
            immediate.draw();
            RenderSystem.enableDepthTest();
        }

        if (showKeyAssignments) {
            ClientChunkManager chunkManager = client.world.getChunkManager();
            int viewDist = client.options.getViewDistance().getValue();
            ChunkPos center = new ChunkPos(client.player.getBlockPos());

            RenderSystem.disableDepthTest();
            RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
            RenderSystem.lineWidth(1.5f);
            BufferBuilder line = tess.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);

            VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();
            VertexConsumer fill = immediate.getBuffer(RenderLayer.getDebugFilledBox());
            TextRenderer textRenderer = client.textRenderer;

            boolean drawn = false;
            for (int centerX = center.x - viewDist; centerX <= center.x + viewDist; centerX++) {
                for (int centerZ = center.z - viewDist; centerZ <= center.z + viewDist; centerZ++) {
                    WorldChunk chunk = chunkManager.getWorldChunk(centerX, centerZ);
                    if (chunk == null) continue;
                    for (BlockEntity be : chunk.getBlockEntities().values()) {
                        if (!(be instanceof DoorBlockEntity door)) continue;
                        String keyName = door.getKeyName();
                        if (keyName == null || keyName.isEmpty()) continue;

                        BlockPos pos = be.getPos();
                        double bx = pos.getX(), by = pos.getY(), bz = pos.getZ();

                        Direction facing = client.world.getBlockState(pos).get(Properties.HORIZONTAL_FACING);
                        double x0, x1, z0, z1;
                        if (facing.getAxis() == Direction.Axis.X) {
                            x0 = bx + 6.0 / 16.0;
                            x1 = bx + 10.0 / 16.0;
                            z0 = bz;
                            z1 = bz + 1.0;
                        } else {
                            x0 = bx;
                            x1 = bx + 1.0;
                            z0 = bz + 6.0 / 16.0;
                            z1 = bz + 10.0 / 16.0;
                        }
                        double y1 = by + 2.0;

                        drawLines(matrices, line, x0, by, z0, x1, y1, z1, KEY_COLOR[0], KEY_COLOR[1], KEY_COLOR[2], 1.0f);

                        WorldRenderer.renderFilledBox(matrices, fill, x0, by, z0, x1, y1, z1, KEY_COLOR[0], KEY_COLOR[1], KEY_COLOR[2], 0.25f);

                        matrices.push();
                        matrices.translate((x0 + x1) / 2.0, y1 + 0.1, (z0 + z1) / 2.0);
                        matrices.multiply(context.camera().getRotation());
                        matrices.scale(0.025f, -0.025f, 0.025f);

                        Text label = Text.literal(keyName).styled(s -> s.withColor(0xFFAA00).withBold(true));
                        float offset = -textRenderer.getWidth(label) / 2f;
                        textRenderer.draw(label, offset, 0f, 0xFFAA00, false, matrices.peek().getPositionMatrix(), immediate, TextRenderer.TextLayerType.SEE_THROUGH, 0, 0xF000F0);

                        matrices.pop();
                        drawn = true;
                    }
                }
            }

            if (drawn) {
                BufferRenderer.drawWithGlobalProgram(line.end());
            } else {
                try (var buf = line.endNullable()) {
                }
            }
            immediate.draw();
            RenderSystem.enableDepthTest();
        }

        matrices.pop();

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private static void drawBox(MatrixStack matrices, BufferBuilder buf, Box box, float[] color) {
        float r = color[0], g = color[1], b = color[2], a = color[3];
        float x0 = (float) box.minX, y0 = (float) box.minY, z0 = (float) box.minZ;
        float x1 = (float) box.maxX, y1 = (float) box.maxY, z1 = (float) box.maxZ;
        // bot
        cuboid(matrices, buf, x0, y0 - BEAM, z0 - BEAM, x1, y0 + BEAM, z0 + BEAM, r, g, b, a);
        cuboid(matrices, buf, x0, y0 - BEAM, z1 - BEAM, x1, y0 + BEAM, z1 + BEAM, r, g, b, a);
        cuboid(matrices, buf, x0 - BEAM, y0 - BEAM, z0, x0 + BEAM, y0 + BEAM, z1, r, g, b, a);
        cuboid(matrices, buf, x1 - BEAM, y0 - BEAM, z0, x1 + BEAM, y0 + BEAM, z1, r, g, b, a);
        // top
        cuboid(matrices, buf, x0, y1 - BEAM, z0 - BEAM, x1, y1 + BEAM, z0 + BEAM, r, g, b, a);
        cuboid(matrices, buf, x0, y1 - BEAM, z1 - BEAM, x1, y1 + BEAM, z1 + BEAM, r, g, b, a);
        cuboid(matrices, buf, x0 - BEAM, y1 - BEAM, z0, x0 + BEAM, y1 + BEAM, z1, r, g, b, a);
        cuboid(matrices, buf, x1 - BEAM, y1 - BEAM, z0, x1 + BEAM, y1 + BEAM, z1, r, g, b, a);
        // vert
        cuboid(matrices, buf, x0 - BEAM, y0, z0 - BEAM, x0 + BEAM, y1, z0 + BEAM, r, g, b, a);
        cuboid(matrices, buf, x1 - BEAM, y0, z0 - BEAM, x1 + BEAM, y1, z0 + BEAM, r, g, b, a);
        cuboid(matrices, buf, x1 - BEAM, y0, z1 - BEAM, x1 + BEAM, y1, z1 + BEAM, r, g, b, a);
        cuboid(matrices, buf, x0 - BEAM, y0, z1 - BEAM, x0 + BEAM, y1, z1 + BEAM, r, g, b, a);
    }

    private static void cuboid(MatrixStack matrices, BufferBuilder buffer, float x0, float y0, float z0, float x1, float y1, float z1, float r, float g, float b, float a) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        quad(buffer, matrix, x0, y1, z0, x0, y1, z1, x0, y0, z1, x0, y0, z0, r, g, b, a); // -x
        quad(buffer, matrix, x1, y1, z1, x1, y1, z0, x1, y0, z0, x1, y0, z1, r, g, b, a); // +x
        quad(buffer, matrix, x0, y0, z1, x1, y0, z1, x1, y0, z0, x0, y0, z0, r, g, b, a); // -y
        quad(buffer, matrix, x0, y1, z0, x1, y1, z0, x1, y1, z1, x0, y1, z1, r, g, b, a); // +y
        quad(buffer, matrix, x1, y1, z0, x0, y1, z0, x0, y0, z0, x1, y0, z0, r, g, b, a); // -z
        quad(buffer, matrix, x0, y1, z1, x1, y1, z1, x1, y0, z1, x0, y0, z1, r, g, b, a); // +z
    }

    private static void quad(BufferBuilder buffer, Matrix4f matrix, float ax, float ay, float az, float bx, float by, float bz, float cx, float cy, float cz, float dx, float dy, float dz, float r, float g, float b, float a) {
        buffer.vertex(matrix, ax, ay, az).color(r, g, b, a);
        buffer.vertex(matrix, bx, by, bz).color(r, g, b, a);
        buffer.vertex(matrix, cx, cy, cz).color(r, g, b, a);
        buffer.vertex(matrix, dx, dy, dz).color(r, g, b, a);
    }

    private static void drawLines(MatrixStack matrices, BufferBuilder buffer, double x0, double y0, double z0, double x1, double y1, double z1, float r, float g, float b, float a) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float fx0 = (float) x0, fy0 = (float) y0, fz0 = (float) z0;
        float fx1 = (float) x1, fy1 = (float) y1, fz1 = (float) z1;
        emitLine(buffer, matrix, fx0, fy0, fz0, fx1, fy0, fz0, 0, -1, 0, r, g, b, a);
        emitLine(buffer, matrix, fx0, fy0, fz1, fx1, fy0, fz1, 0, -1, 0, r, g, b, a);
        emitLine(buffer, matrix, fx0, fy1, fz0, fx1, fy1, fz0, 0, 1, 0, r, g, b, a);
        emitLine(buffer, matrix, fx0, fy1, fz1, fx1, fy1, fz1, 0, 1, 0, r, g, b, a);
        emitLine(buffer, matrix, fx0, fy0, fz0, fx0, fy1, fz0, -1, 0, 0, r, g, b, a);
        emitLine(buffer, matrix, fx1, fy0, fz0, fx1, fy1, fz0, 1, 0, 0, r, g, b, a);
        emitLine(buffer, matrix, fx0, fy0, fz1, fx0, fy1, fz1, -1, 0, 0, r, g, b, a);
        emitLine(buffer, matrix, fx1, fy0, fz1, fx1, fy1, fz1, 1, 0, 0, r, g, b, a);
        emitLine(buffer, matrix, fx0, fy0, fz0, fx0, fy0, fz1, 0, 0, -1, r, g, b, a);
        emitLine(buffer, matrix, fx1, fy0, fz0, fx1, fy0, fz1, 0, 0, 1, r, g, b, a);
        emitLine(buffer, matrix, fx0, fy1, fz0, fx0, fy1, fz1, 0, 0, -1, r, g, b, a);
        emitLine(buffer, matrix, fx1, fy1, fz0, fx1, fy1, fz1, 0, 0, 1, r, g, b, a);
    }

    private static void emitLine(BufferBuilder buffer, Matrix4f matrix, float x0, float y0, float z0, float x1, float y1, float z1, float nx, float ny, float nz, float r, float g, float b, float a) {
        buffer.vertex(matrix, x0, y0, z0).color(r, g, b, a).normal(nx, ny, nz);
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a).normal(nx, ny, nz);
    }

}

// i hated it here