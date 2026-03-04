package cat.rezelyn.watheextended.client.debug;

import cat.rezelyn.watheextended.api.cca.MapVariables;
import cat.rezelyn.watheextended.cca.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.teleport.TeleportationSlot;
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

    private static void onWorldRender(WorldRenderContext ctx) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        MatrixStack matrices = ctx.matrixStack();
        if (matrices == null) return;

        Vec3d cam = ctx.camera().getPos();

        if (!showBoxBoundaries && !showRtpSlots && !showKeyAssignments) return;

        List<TeleportationSlot> slots = null;
        if (showRtpSlots) {
            try {
                slots = WatheExtendedWorldComponent.KEY.get(client.world).getTeleportationSlots();
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
            BufferBuilder filledBuf = tess.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            Box playArea = MapVariables.getPlayArea(client.world);
            Box readyArea = MapVariables.getReadyArea(client.world);
            Box lobbyArea = MapVariables.getLobbyArea(client.world);
            if (playArea != null) drawBox(matrices, filledBuf, playArea, PLAY_AREA_COLOR);
            if (readyArea != null) drawBox(matrices, filledBuf, readyArea, READY_AREA_COLOR);
            drawBox(matrices, filledBuf, lobbyArea, LOBBY_AREA_COLOR);
            BufferRenderer.drawWithGlobalProgram(filledBuf.end());
        }

        if (slots != null && !slots.isEmpty()) {
            int viewDist = client.options.getViewDistance().getValue();
            double maxDistSq = (viewDist * 16.0) * (viewDist * 16.0);
            double px = client.player.getX(), py = client.player.getY(), pz = client.player.getZ();

            RenderSystem.disableDepthTest();
            RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
            RenderSystem.lineWidth(1.5f);
            BufferBuilder linesBuf = tess.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);

            boolean anySlot = false;
            for (TeleportationSlot slot : slots) {
                double ddx = slot.x - px, ddy = slot.y - py, ddz = slot.z - pz;
                if (ddx * ddx + ddy * ddy + ddz * ddz > maxDistSq) continue;
                anySlot = true;

                drawLines(matrices, linesBuf,
                        slot.x - HIT_W, slot.y, slot.z - HIT_W,
                        slot.x + HIT_W, slot.y + HIT_H, slot.z + HIT_W,
                        CUBE_COLOR[0], CUBE_COLOR[1], CUBE_COLOR[2], 0.4f);

                double eyeY = slot.y + 1.62;
                double yawRad = Math.toRadians(slot.yaw);
                double pitchRad = Math.toRadians(slot.pitch);
                double cosP = Math.cos(pitchRad);
                float dx = (float) (-Math.sin(yawRad) * cosP);
                float dy = (float) (-Math.sin(pitchRad));
                float dz = (float) (Math.cos(yawRad) * cosP);

                Matrix4f pose = matrices.peek().getPositionMatrix();
                float ox = (float) slot.x, oy = (float) eyeY, oz = (float) slot.z;
                emitLine(linesBuf, pose,
                        ox, oy, oz, ox + dx * 2.0f, oy + dy * 2.0f, oz + dz * 2.0f,
                        dx, dy, dz,
                        VIEW_COLOR[0], VIEW_COLOR[1], VIEW_COLOR[2], 0.4f);
            }

            if (anySlot) {
                BufferRenderer.drawWithGlobalProgram(linesBuf.end());
            } else {
                try (var buf = linesBuf.endNullable()) {
                }
            }

            VertexConsumerProvider.Immediate immediate =
                    client.getBufferBuilders().getEntityVertexConsumers();
            TextRenderer textRenderer = client.textRenderer;
            for (int i = 0; i < slots.size(); i++) {
                TeleportationSlot slot = slots.get(i);
                double ddx = slot.x - px, ddy = slot.y - py, ddz = slot.z - pz;
                if (ddx * ddx + ddy * ddy + ddz * ddz > maxDistSq) continue;

                matrices.push();
                matrices.translate(slot.x, slot.y + HIT_H + 0.3, slot.z);
                matrices.multiply(ctx.camera().getRotation());
                matrices.scale(0.025f, -0.025f, 0.025f);

                Text label = Text.literal("Slot #" + (i + 1)).styled(s -> s.withColor(0xFFFFFF));
                float xOff = -textRenderer.getWidth(label) / 2f;
                textRenderer.draw(label, xOff, 0f, 0xFFFFFF, false,
                        matrices.peek().getPositionMatrix(), immediate,
                        TextRenderer.TextLayerType.SEE_THROUGH, 0, 0xF000F0);

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
            BufferBuilder linesBuf = tess.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);

            VertexConsumerProvider.Immediate immediate =
                    client.getBufferBuilders().getEntityVertexConsumers();
            VertexConsumer fillBuf = immediate.getBuffer(RenderLayer.getDebugFilledBox());
            TextRenderer textRenderer = client.textRenderer;

            boolean anyDrawn = false;
            for (int cx = center.x - viewDist; cx <= center.x + viewDist; cx++) {
                for (int cz = center.z - viewDist; cz <= center.z + viewDist; cz++) {
                    WorldChunk chunk = chunkManager.getWorldChunk(cx, cz);
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

                        drawLines(matrices, linesBuf, x0, by, z0, x1, y1, z1,
                                KEY_COLOR[0], KEY_COLOR[1], KEY_COLOR[2], 1.0f);

                        WorldRenderer.renderFilledBox(matrices, fillBuf,
                                x0, by, z0, x1, y1, z1,
                                KEY_COLOR[0], KEY_COLOR[1], KEY_COLOR[2], 0.25f);

                        matrices.push();
                        matrices.translate((x0 + x1) / 2.0, y1 + 0.1, (z0 + z1) / 2.0);
                        matrices.multiply(ctx.camera().getRotation());
                        matrices.scale(0.025f, -0.025f, 0.025f);

                        Text label = Text.literal(keyName).styled(s -> s.withColor(0xFFAA00).withBold(true));
                        float xOff = -textRenderer.getWidth(label) / 2f;
                        textRenderer.draw(label, xOff, 0f, 0xFFAA00, false,
                                matrices.peek().getPositionMatrix(), immediate,
                                TextRenderer.TextLayerType.SEE_THROUGH, 0, 0xF000F0);

                        matrices.pop();
                        anyDrawn = true;
                    }
                }
            }

            if (anyDrawn) {
                BufferRenderer.drawWithGlobalProgram(linesBuf.end());
            } else {
                try (var buf = linesBuf.endNullable()) {
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

    private static void cuboid(MatrixStack matrices, BufferBuilder buf,
                               float x0, float y0, float z0,
                               float x1, float y1, float z1,
                               float r, float g, float b, float a) {
        Matrix4f m = matrices.peek().getPositionMatrix();
        quad(buf, m, x0, y0, z1, x1, y0, z1, x1, y0, z0, x0, y0, z0, r, g, b, a); // -y
        quad(buf, m, x0, y1, z0, x1, y1, z0, x1, y1, z1, x0, y1, z1, r, g, b, a); // +y
        quad(buf, m, x1, y1, z0, x0, y1, z0, x0, y0, z0, x1, y0, z0, r, g, b, a); // -z
        quad(buf, m, x0, y1, z1, x1, y1, z1, x1, y0, z1, x0, y0, z1, r, g, b, a); // +z
        quad(buf, m, x0, y1, z0, x0, y1, z1, x0, y0, z1, x0, y0, z0, r, g, b, a); // -x
        quad(buf, m, x1, y1, z1, x1, y1, z0, x1, y0, z0, x1, y0, z1, r, g, b, a); // +x
    }

    private static void quad(BufferBuilder buf, Matrix4f m,
                             float ax, float ay, float az,
                             float bx, float by, float bz,
                             float cx, float cy, float cz,
                             float dx, float dy, float dz,
                             float r, float g, float b, float a) {
        buf.vertex(m, ax, ay, az).color(r, g, b, a);
        buf.vertex(m, bx, by, bz).color(r, g, b, a);
        buf.vertex(m, cx, cy, cz).color(r, g, b, a);
        buf.vertex(m, dx, dy, dz).color(r, g, b, a);
    }

    private static void drawLines(MatrixStack matrices, BufferBuilder buf,
                                  double x0, double y0, double z0,
                                  double x1, double y1, double z1,
                                  float r, float g, float b, float a) {
        Matrix4f m = matrices.peek().getPositionMatrix();
        float fx0 = (float) x0, fy0 = (float) y0, fz0 = (float) z0;
        float fx1 = (float) x1, fy1 = (float) y1, fz1 = (float) z1;
        emitLine(buf, m, fx0, fy0, fz0, fx1, fy0, fz0, 0, -1, 0, r, g, b, a);
        emitLine(buf, m, fx0, fy0, fz1, fx1, fy0, fz1, 0, -1, 0, r, g, b, a);
        emitLine(buf, m, fx0, fy1, fz0, fx1, fy1, fz0, 0, 1, 0, r, g, b, a);
        emitLine(buf, m, fx0, fy1, fz1, fx1, fy1, fz1, 0, 1, 0, r, g, b, a);
        emitLine(buf, m, fx0, fy0, fz0, fx0, fy1, fz0, -1, 0, 0, r, g, b, a);
        emitLine(buf, m, fx1, fy0, fz0, fx1, fy1, fz0, 1, 0, 0, r, g, b, a);
        emitLine(buf, m, fx0, fy0, fz1, fx0, fy1, fz1, -1, 0, 0, r, g, b, a);
        emitLine(buf, m, fx1, fy0, fz1, fx1, fy1, fz1, 1, 0, 0, r, g, b, a);
        emitLine(buf, m, fx0, fy0, fz0, fx0, fy0, fz1, 0, 0, -1, r, g, b, a);
        emitLine(buf, m, fx1, fy0, fz0, fx1, fy0, fz1, 0, 0, 1, r, g, b, a);
        emitLine(buf, m, fx0, fy1, fz0, fx0, fy1, fz1, 0, 0, -1, r, g, b, a);
        emitLine(buf, m, fx1, fy1, fz0, fx1, fy1, fz1, 0, 0, 1, r, g, b, a);
    }

    private static void emitLine(BufferBuilder buf, Matrix4f m,
                                 float x0, float y0, float z0,
                                 float x1, float y1, float z1,
                                 float nx, float ny, float nz,
                                 float r, float g, float b, float a) {
        buf.vertex(m, x0, y0, z0).color(r, g, b, a).normal(nx, ny, nz);
        buf.vertex(m, x1, y1, z1).color(r, g, b, a).normal(nx, ny, nz);
    }

}

// i hated it here