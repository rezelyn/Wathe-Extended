package cat.rezelyn.watheextended.client.render;

import cat.rezelyn.watheextended.api.MapVariables;
import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.game.utils.TeleportationSlot;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.doctor4t.wathe.block_entity.DoorBlockEntity;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.*;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;

public final class BoxDebugRenderer {

  private static final float BEAM = 0.04f;
  private static final float SPAWN_BEAM = 0.012f;
  private static final double HIT_W = 0.3, HIT_H = 1.8;
  private static final Color PLAY = new Color(1.0f, 0.22f, 0.22f, 0.9f);
  private static final Color READY = new Color(0.22f, 1.0f, 0.22f, 0.9f);
  private static final Color LOBBY = new Color(0.22f, 0.55f, 1.0f, 0.9f);
  private static final Color ORANGE = new Color(1.0f, 0.6f, 0.0f, 0.9f);
  private static final Color WHITE = new Color(1.0f, 1.0f, 1.0f, 0.4f);
  private static final Color CYAN = new Color(0.0f, 0.9f, 1.0f, 0.4f);
  public static boolean showBoxBoundaries = false;
  public static boolean showRtpSlots = false;
  public static boolean showKeyAssignments = false;
  public static boolean showSpawnPositions = false;

  private BoxDebugRenderer() {}

  public static void register() {
    WorldRenderEvents.AFTER_TRANSLUCENT.register(BoxDebugRenderer::onWorldRender);
  }

  private static void onWorldRender(WorldRenderContext ctx) {
    if (!(showBoxBoundaries || showRtpSlots || showKeyAssignments || showSpawnPositions)) return;

    MinecraftClient client = MinecraftClient.getInstance();
    MatrixStack m = ctx.matrixStack();
    if (client.world == null || client.player == null || m == null) return;

    Vec3d cam = ctx.camera().getPos();
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    RenderSystem.disableCull();
    RenderSystem.enableDepthTest();

    m.push();
    m.translate(-cam.x, -cam.y, -cam.z);

    if (showBoxBoundaries) renderAreas(client, m);
    if (showSpawnPositions) renderSpawns(client, ctx, m);
    if (showRtpSlots) renderSlots(client, ctx, m);
    if (showKeyAssignments) renderKeys(client, ctx, m);

    m.pop();
    RenderSystem.enableDepthTest();
    RenderSystem.enableCull();
    RenderSystem.disableBlend();
  }

  private static void renderAreas(MinecraftClient client, MatrixStack m) {
    BufferBuilder buf = beginFill();
    beams(m, buf, MapVariables.getPlayArea(client.world), PLAY, BEAM);
    beams(m, buf, MapVariables.getReadyArea(client.world), READY, BEAM);
    beams(m, buf, MapVariables.getLobbyArea(client.world), LOBBY, BEAM);
    flush(buf);
  }

  private static void renderSpawns(MinecraftClient client, WorldRenderContext ctx, MatrixStack m) {
    var lobby = MapVariables.getSpawnPosition(client.world);
    var ready = MapVariables.getReadyAreaSpawnPosition(client.world);
    var spectator = MapVariables.getSpectatorSpawnPosition(client.world);

    List<Spawn> spawns = new ArrayList<>();
    if (lobby != null) spawns.add(new Spawn(lobby.pos, "Lobby Spawn", LOBBY));
    if (ready != null) spawns.add(new Spawn(ready.pos, "Ready Area Spawn", READY));
    if (spectator != null) spawns.add(new Spawn(spectator.pos, "Spectator Spawn", ORANGE));
    if (spawns.isEmpty()) return;

    BufferBuilder buf = beginFill();
    for (Spawn s : spawns) {
      Vec3d p = s.pos();
      Box box = new Box(p.x - 0.5, p.y, p.z - 0.5, p.x + 0.5, p.y + 1.0, p.z + 0.5);
      fill(m, buf, box, s.color().withAlpha(0.22f));
      beams(m, buf, box, s.color(), SPAWN_BEAM);
    }
    RenderSystem.depthMask(false);
    flush(buf);
    RenderSystem.depthMask(true);

    var text = client.getBufferBuilders().getEntityVertexConsumers();
    for (Spawn s : spawns) {
      label(
          ctx,
          m,
          text,
          s.pos().add(0, 1.6, 0),
          Text.literal(s.label()),
          s.color().rgb(),
          TextRenderer.TextLayerType.SEE_THROUGH);
    }
    text.draw();
  }

  private static void renderSlots(MinecraftClient client, WorldRenderContext ctx, MatrixStack m) {
    var component = WatheExtendedWorldComponent.KEY.maybeGet(client.world);
    if (component.isEmpty()) return;
    var slots = component.get().getTeleportationSlots();
    if (slots.isEmpty()) return;

    double maxDistSq = Math.pow(client.options.getViewDistance().getValue() * 16.0, 2);
    Vec3d player = client.player.getPos();
    Matrix4f pose = m.peek().getPositionMatrix();

    RenderSystem.disableDepthTest();
    BufferBuilder lines = beginLines();
    var text = client.getBufferBuilders().getEntityVertexConsumers();

    for (var entry : slots.entrySet()) {
      TeleportationSlot s = entry.getValue();
      if (player.squaredDistanceTo(s.x, s.y, s.z) > maxDistSq) continue;

      outline(
          m,
          lines,
          new Box(s.x - HIT_W, s.y, s.z - HIT_W, s.x + HIT_W, s.y + HIT_H, s.z + HIT_W),
          WHITE);

      double yaw = Math.toRadians(s.yaw), pitch = Math.toRadians(s.pitch), cosP = Math.cos(pitch);
      float dx = (float) (-Math.sin(yaw) * cosP);
      float dy = (float) -Math.sin(pitch);
      float dz = (float) (Math.cos(yaw) * cosP);
      float ox = (float) s.x, oy = (float) (s.y + 1.62), oz = (float) s.z;
      lines
          .vertex(pose, ox, oy, oz)
          .color(CYAN.r(), CYAN.g(), CYAN.b(), CYAN.a())
          .normal(dx, dy, dz);
      lines
          .vertex(pose, ox + dx * 2, oy + dy * 2, oz + dz * 2)
          .color(CYAN.r(), CYAN.g(), CYAN.b(), CYAN.a())
          .normal(dx, dy, dz);

      label(
          ctx,
          m,
          text,
          new Vec3d(s.x, s.y + HIT_H + 0.3, s.z),
          Text.literal("Slot #" + entry.getKey()),
          0xFFFFFF,
          TextRenderer.TextLayerType.SEE_THROUGH);
    }

    flush(lines);
    text.draw();
  }

  private static void renderKeys(MinecraftClient client, WorldRenderContext ctx, MatrixStack m) {
    int viewDist = client.options.getViewDistance().getValue();
    ChunkPos center = new ChunkPos(client.player.getBlockPos());

    RenderSystem.disableDepthTest();
    BufferBuilder lines = beginLines();
    var text = client.getBufferBuilders().getEntityVertexConsumers();
    VertexConsumer fill = text.getBuffer(RenderLayer.getDebugFilledBox());

    for (int cx = center.x - viewDist; cx <= center.x + viewDist; cx++) {
      for (int cz = center.z - viewDist; cz <= center.z + viewDist; cz++) {
        WorldChunk chunk = client.world.getChunkManager().getWorldChunk(cx, cz);
        if (chunk == null) continue;

        for (var be : chunk.getBlockEntities().values()) {
          if (!(be instanceof DoorBlockEntity door)) continue;
          String keyName = door.getKeyName();
          if (keyName == null || keyName.isEmpty()) continue;

          BlockPos pos = be.getPos();
          double x = pos.getX(), y = pos.getY(), z = pos.getZ();
          Box box =
              client.world.getBlockState(pos).get(Properties.HORIZONTAL_FACING).getAxis()
                      == Direction.Axis.X
                  ? new Box(x + 6 / 16.0, y, z, x + 10 / 16.0, y + 2, z + 1)
                  : new Box(x, y, z + 6 / 16.0, x + 1, y + 2, z + 10 / 16.0);

          outline(m, lines, box, ORANGE.withAlpha(1.0f));
          stripFill(m, fill, box, ORANGE.withAlpha(0.25f));

          Vec3d top = box.getCenter().withAxis(Direction.Axis.Y, box.maxY + 0.1);
          label(
              ctx,
              m,
              text,
              top.add(0, 0.2, 0),
              Text.literal(keyName).formatted(Formatting.BOLD),
              0xFFAA00,
              TextRenderer.TextLayerType.SEE_THROUGH);
        }
      }
    }

    flush(lines);
    text.draw();
  }

  private static BufferBuilder beginFill() {
    RenderSystem.setShader(GameRenderer::getPositionColorProgram);
    return Tessellator.getInstance()
        .begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
  }

  private static BufferBuilder beginLines() {
    RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
    RenderSystem.lineWidth(1.5f);
    return Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
  }

  private static void flush(BufferBuilder buf) {
    BuiltBuffer built = buf.endNullable();
    if (built != null) BufferRenderer.drawWithGlobalProgram(built);
  }

  private static void fill(MatrixStack m, VertexConsumer vc, Box b, Color c) {
    Matrix4f p = m.peek().getPositionMatrix();
    float x0 = (float) b.minX, y0 = (float) b.minY, z0 = (float) b.minZ;
    float x1 = (float) b.maxX, y1 = (float) b.maxY, z1 = (float) b.maxZ;
    quad(vc, p, c, x0, y1, z0, x0, y1, z1, x0, y0, z1, x0, y0, z0); // -x
    quad(vc, p, c, x1, y1, z1, x1, y1, z0, x1, y0, z0, x1, y0, z1); // +x
    quad(vc, p, c, x0, y0, z1, x1, y0, z1, x1, y0, z0, x0, y0, z0); // -y
    quad(vc, p, c, x0, y1, z0, x1, y1, z0, x1, y1, z1, x0, y1, z1); // +y
    quad(vc, p, c, x1, y1, z0, x0, y1, z0, x0, y0, z0, x1, y0, z0); // -z
    quad(vc, p, c, x0, y1, z1, x1, y1, z1, x1, y0, z1, x0, y0, z1); // +z
  }

  private static void quad(
      VertexConsumer vc,
      Matrix4f p,
      Color c,
      float ax,
      float ay,
      float az,
      float bx,
      float by,
      float bz,
      float cx,
      float cy,
      float cz,
      float dx,
      float dy,
      float dz) {
    vc.vertex(p, ax, ay, az).color(c.r(), c.g(), c.b(), c.a());
    vc.vertex(p, bx, by, bz).color(c.r(), c.g(), c.b(), c.a());
    vc.vertex(p, cx, cy, cz).color(c.r(), c.g(), c.b(), c.a());
    vc.vertex(p, dx, dy, dz).color(c.r(), c.g(), c.b(), c.a());
  }

  private static void stripFill(MatrixStack m, VertexConsumer vc, Box b, Color c) {
    WorldRenderer.renderFilledBox(
        m, vc, b.minX, b.minY, b.minZ, b.maxX, b.maxY, b.maxZ, c.r(), c.g(), c.b(), c.a());
  }

  private static void outline(MatrixStack m, VertexConsumer vc, Box b, Color c) {
    WorldRenderer.drawBox(
        m, vc, b.minX, b.minY, b.minZ, b.maxX, b.maxY, b.maxZ, c.r(), c.g(), c.b(), c.a());
  }

  private static void beams(MatrixStack m, VertexConsumer vc, Box b, Color c, double t) {
    if (b == null) return;
    double[] xs = {b.minX, b.maxX}, ys = {b.minY, b.maxY}, zs = {b.minZ, b.maxZ};
    for (double y : ys)
      for (double z : zs) fill(m, vc, new Box(b.minX, y - t, z - t, b.maxX, y + t, z + t), c);
    for (double x : xs)
      for (double z : zs) fill(m, vc, new Box(x - t, b.minY, z - t, x + t, b.maxY, z + t), c);
    for (double x : xs)
      for (double y : ys) fill(m, vc, new Box(x - t, y - t, b.minZ, x + t, y + t, b.maxZ), c);
  }

  private static void label(
      WorldRenderContext ctx,
      MatrixStack m,
      VertexConsumerProvider vcp,
      Vec3d pos,
      Text text,
      int rgb,
      TextRenderer.TextLayerType layer) {
    TextRenderer tr = MinecraftClient.getInstance().textRenderer;
    m.push();
    m.translate(pos.x, pos.y, pos.z);
    m.multiply(ctx.camera().getRotation());
    m.scale(0.025f, -0.025f, 0.025f);
    tr.draw(
        text,
        -tr.getWidth(text) / 2f,
        0f,
        rgb,
        false,
        m.peek().getPositionMatrix(),
        vcp,
        layer,
        0,
        0xF000F0);
    m.pop();
  }

  private record Color(float r, float g, float b, float a) {
    Color withAlpha(float alpha) {
      return new Color(r, g, b, alpha);
    }

    int rgb() {
      return (Math.round(r * 255) << 16) | (Math.round(g * 255) << 8) | Math.round(b * 255);
    }
  }

  private record Spawn(Vec3d pos, String label, Color color) {}
}

// i hated it here