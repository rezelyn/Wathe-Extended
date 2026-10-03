package cat.rezelyn.watheextended.client.render;

import cat.rezelyn.watheextended.api.ModifiersDisplay;
import cat.rezelyn.watheextended.api.RolesDisplay;
import cat.rezelyn.watheextended.api.config.noellesroles.ConfigHelper;
import cat.rezelyn.watheextended.client.compat.KinsWatheAccess;
import cat.rezelyn.watheextended.client.compat.StupidExpressAccess;
import cat.rezelyn.watheextended.client.pronouns.PronounsCache;
import cat.rezelyn.watheextended.game.compat.KillerCohortAccess;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.api.WatheGameModes;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerPsychoComponent;
import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.game.GameFunctions;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import org.agmas.harpymodloader.Harpymodloader;
import org.agmas.harpymodloader.component.WorldModifierComponent;
import org.agmas.harpymodloader.modifiers.Modifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;

/**
 * Draws the nametag under the crosshair when looking at a player. Shows role, modifier(s),
 * username, pronouns (if any) and extra info, stacked from top to bottom
 */
public final class UnifiedNameRenderer {
  private static final float SECTION_GAP = 2.5f;
  private static final int LINE_SPACING = 2;
  private static final float NORMAL_RANGE = 2f;
  private static final float PRIVILEGED_RANGE = 8f;

  // Roles that should not see the Killer Cohort text
  private static final Set<Identifier> KILLER_COHORT_DENYLIST =
      Set.of(
          Identifier.of("stupid_express", "arsonist"),
          Identifier.of("noellesroles", "executioner"),
          Identifier.of("stupid_express", "thief"),
          Identifier.of("noellesroles", "jester"));

  private static float fade;
  private static PlayerEntity lastTarget;
  private static boolean drawingCompatibilityStatus;
  private static boolean displayCacheInitialized;
  private static Map<String, RolesDisplay.RoleDisplay> roleDisplays = Map.of();
  private static Map<String, ModifiersDisplay.ModifierDisplay> modifierDisplays = Map.of();

  private UnifiedNameRenderer() {}

  /**
   * Draws the nametag for the player the viewer is looking at
   *
   * @param renderer the text renderer
   * @param viewer the player who is looking
   * @param context the draw context
   * @param tickCounter used to time the fading animation
   */
  public static void render(
      TextRenderer renderer,
      ClientPlayerEntity viewer,
      DrawContext context,
      RenderTickCounter tickCounter) {
    PlayerEntity looked = getTarget(viewer);
    if (looked != null) lastTarget = looked;
    PlayerEntity target = lastTarget;

    float fadeStep = MathHelper.clamp(tickCounter.getLastFrameDuration() / 4f, 0f, 1f);
    fade = MathHelper.lerp(fadeStep, fade, looked != null ? 1f : 0f);
    if (fade <= 0.05f || target == null) return;

    int alphaMask = (int) (fade * 255f) << 24;
    GameWorldComponent game = GameWorldComponent.KEY.get(viewer.getWorld());
    boolean privileged = isSpectating(viewer);

    context.getMatrices().push();
    context
        .getMatrices()
        .translate(
            context.getScaledWindowWidth() / 2f, context.getScaledWindowHeight() / 2f + 6f, 0f);
    context.getMatrices().scale(0.6f, 0.6f, 1f);

    int y = 0;
    if (privileged) y = drawRoleAndModifiers(renderer, context, game, target, y, alphaMask);
    y = drawNameAndPronouns(renderer, context, viewer, target, y, alphaMask);

    addSectionGap(context);
    if (shouldShowKillerCohort(game, viewer, target)) {
      y =
          drawLine(
              renderer, context, Text.translatable("game.tip.cohort"), y, 0xFF0000 | alphaMask);
      addSectionGap(context);
    }

    if (privileged) y = drawSpectatorInfo(renderer, context, game, target, y, alphaMask);
    drawCompatibilityStatus(renderer, context, game, viewer, target, y, alphaMask);

    context.getMatrices().pop();
  }

  /**
   * Draws the target's role and modifiers. Only used for spectators and creative players
   *
   * @param renderer the text renderer
   * @param context the draw context
   * @param game the current game
   * @param target the player being looked at
   * @param y the y position to start at
   * @param alphaMask the fade value to apply to the colors
   * @return the y position for the next line
   */
  private static int drawRoleAndModifiers(
      TextRenderer renderer,
      DrawContext context,
      GameWorldComponent game,
      PlayerEntity target,
      int y,
      int alphaMask) {
    int startY = y;
    loadDisplayCache();

    Role role = game.getRole(target);
    if (role != null) {
      RolesDisplay.RoleDisplay display =
          role.identifier() == null ? null : roleDisplays.get(role.identifier().toString());
      Text name =
          display != null
              ? display.display()
              : RolesDisplay.cleanName(Harpymodloader.getRoleName(role));
      int color = display != null ? display.color() : role.color();
      y = drawLine(renderer, context, name, y, withAlpha(color, alphaMask));
    }

    List<Modifier> modifiers = getModifiers(target);
    if (!modifiers.isEmpty()) {
      List<Segment> segments = new ArrayList<>(modifiers.size());
      for (Modifier modifier : modifiers) {
        ModifiersDisplay.ModifierDisplay display =
            modifier.identifier == null
                ? null
                : modifierDisplays.get(modifier.identifier.toString());
        Text name =
            display != null ? display.display() : ModifiersDisplay.cleanName(modifier.getName());
        segments.add(new Segment(name, display != null ? display.color() : modifier.color));
      }
      y = drawSegments(renderer, context, segments, renderer.getWidth(" "), y, alphaMask);
    }

    if (y != startY) addSectionGap(context);
    return y;
  }

  /**
   * Draws the target's name and pronouns
   *
   * @param renderer the text renderer
   * @param context the draw context
   * @param viewer the player who is looking
   * @param target the player being looked at
   * @param y the y position to start at
   * @param alphaMask the fade value to apply to the colors
   * @return the y position for the next line
   */
  private static int drawNameAndPronouns(
      TextRenderer renderer,
      DrawContext context,
      ClientPlayerEntity viewer,
      PlayerEntity target,
      int y,
      int alphaMask) {
    Text username;
    try {
      username = target.getDisplayName();
    } catch (Throwable ignored) {
      username = Text.literal(target.getGameProfile().getName());
    }
    if (isPsycho(target)) {
      username =
          Text.literal("urscrewed" + "X".repeat(viewer.getRandom().nextInt(8)))
              .styled(style -> style.withObfuscated(true).withColor(0xAA0000));
    }
    y = drawLine(renderer, context, username, y, 0xFFFFFF | alphaMask);

    // Compat: Morphling' ability shows the pronouns of the player they took the appearance from, if
    // any
    UUID pronounsUuid = getMorphlingDisguise(target);
    if (pronounsUuid == null) pronounsUuid = target.getUuid();
    String pronouns = PronounsCache.get(pronounsUuid);
    if (!target.isInvisible()
        && !shouldHidePronouns(viewer)
        && pronouns != null
        && !pronouns.isBlank()) {
      y = drawLine(renderer, context, Text.literal(pronouns), y, 0xAAAAAA | alphaMask);
    }
    return y;
  }

  /**
   * Draws the Executioner's target and the Lovers' partner. Only used for spectators and creative
   * players.
   *
   * @param renderer the text renderer
   * @param context the draw context
   * @param game the current game
   * @param target the player being looked at
   * @param y the y position to start at
   * @param alphaMask the fade value to apply to the colors
   * @return the y position for the next line
   */
  private static int drawSpectatorInfo(
      TextRenderer renderer,
      DrawContext context,
      GameWorldComponent game,
      PlayerEntity target,
      int y,
      int alphaMask) {
    String executionerTarget =
        game.isRunning() && isExecutioner(game, target) ? getExecutionerTarget(target) : null;
    if (executionerTarget != null) {
      y =
          drawLine(
              renderer,
              context,
              Text.literal("Target: " + executionerTarget),
              (int) (y + renderer.fontHeight / SECTION_GAP),
              withAlpha(Formatting.DARK_RED.getColorValue(), alphaMask));
    }

    LoversPair lovers = getLoversPair(target);
    if (lovers != null) {
      y =
          drawLine(
              renderer,
              context,
              lovers.text(),
              (int) (y + renderer.fontHeight / SECTION_GAP),
              withAlpha(lovers.color(), alphaMask));
    }
    return y;
  }

  /**
   * Draws the hacker and arsonist status lines from KinsWathe and Stupid Express, if they apply.
   *
   * @param renderer the text renderer
   * @param context the draw context
   * @param game the current game
   * @param viewer the player who is looking
   * @param target the player being looked at
   * @param y the y position to start at
   * @param alphaMask the fade value to apply to the colors
   */
  private static void drawCompatibilityStatus(
      TextRenderer renderer,
      DrawContext context,
      GameWorldComponent game,
      ClientPlayerEntity viewer,
      PlayerEntity target,
      int y,
      int alphaMask) {
    KinsWatheAccess.HackerStatus hacker = KinsWatheAccess.getHackerStatus(game, viewer, target);
    StupidExpressAccess.DousedStatus doused =
        StupidExpressAccess.getArsonistDousedStatus(game, viewer, target);
    if (hacker == null && doused == null) return;

    addSectionGap(context);
    drawingCompatibilityStatus = true;
    try {
      if (hacker != null) {
        List<Segment> segments = new ArrayList<>(2);
        segments.add(new Segment(hacker.primary(), hacker.primaryColor()));
        if (hacker.suffix() != null)
          segments.add(new Segment(hacker.suffix(), hacker.suffixColor()));
        y = drawSegments(renderer, context, segments, 0, y, alphaMask);
      }
      if (doused != null) {
        int color =
            doused.doused() ? game.getRole(viewer).color() : Formatting.DARK_GRAY.getColorValue();
        drawLine(renderer, context, doused.text(), y, withAlpha(color, alphaMask));
      }
    } finally {
      drawingCompatibilityStatus = false;
    }
  }

  private static int drawLine(
      TextRenderer renderer, DrawContext context, Text text, int y, int color) {
    context.drawTextWithShadow(renderer, text, -renderer.getWidth(text) / 2, y, color);
    return y + renderer.fontHeight + LINE_SPACING;
  }

  private static int drawSegments(
      TextRenderer renderer,
      DrawContext context,
      List<Segment> segments,
      int gap,
      int y,
      int alphaMask) {
    int totalWidth = gap * (segments.size() - 1);
    for (Segment segment : segments) totalWidth += renderer.getWidth(segment.text());

    int x = -totalWidth / 2;
    for (Segment segment : segments) {
      context.drawTextWithShadow(
          renderer, segment.text(), x, y, withAlpha(segment.color(), alphaMask));
      x += renderer.getWidth(segment.text()) + gap;
    }
    return y + renderer.fontHeight + LINE_SPACING;
  }

  private static int withAlpha(int color, int alphaMask) {
    return (color & 0x00FFFFFF) | alphaMask;
  }

  private static void addSectionGap(DrawContext context) {
    context.getMatrices().translate(0f, SECTION_GAP, 0f);
  }

  private static boolean isSpectating(PlayerEntity viewer) {
    return viewer.isSpectator() || viewer.isCreative();
  }

  /**
   * Loads the role and modifier display info once. It is loaded late because the registries must be
   * ready first!
   */
  private static void loadDisplayCache() {
    if (displayCacheInitialized) return;
    roleDisplays = RolesDisplay.get();
    modifierDisplays = ModifiersDisplay.get();
    displayCacheInitialized = true;
  }

  private static PlayerEntity findTarget(
      ClientPlayerEntity viewer, Predicate<Entity> filter, float range) {
    return ProjectileUtil.getCollision(viewer, filter, range) instanceof EntityHitResult hit
            && hit.getEntity() instanceof PlayerEntity player
        ? player
        : null;
  }

  private static PlayerEntity getTarget(ClientPlayerEntity viewer) {
    return findTarget(
        viewer,
        entity -> entity instanceof PlayerEntity,
        isSpectating(viewer) ? PRIVILEGED_RANGE : NORMAL_RANGE);
  }

  /** Finds the player Kin's Wathe would target */
  private static PlayerEntity getTargetCompat(ClientPlayerEntity viewer) {
    return findTarget(
        viewer,
        entity ->
            entity instanceof PlayerEntity player && GameFunctions.isPlayerAliveAndSurvival(player),
        NORMAL_RANGE);
  }

  private static boolean isPsycho(PlayerEntity target) {
    try {
      return PlayerPsychoComponent.KEY.get(target).getPsychoTicks() > 0;
    } catch (Throwable ignored) {
      return false;
    }
  }

  private static boolean isExecutioner(GameWorldComponent game, PlayerEntity player) {
    Role role = game.getRole(player);
    return role != null && Identifier.of("noellesroles", "executioner").equals(role.identifier());
  }

  private static boolean shouldHidePronouns(PlayerEntity viewer) {
    try {
      return ConfigHelper.isLoaded()
          && ConfigHelper.getInsanePlayersSeeMorphs(viewer.getWorld())
          && WatheClient.moodComponent != null
          && WatheClient.moodComponent.isLowerThanDepressed();
    } catch (Throwable ignored) {
      return false;
    }
  }

  private static boolean canSeeKillerCohort(GameWorldComponent game, PlayerEntity viewer) {
    Role viewerRole = game.getRole(viewer);
    return (viewerRole == null || !KILLER_COHORT_DENYLIST.contains(viewerRole.identifier()))
        && KillerCohortAccess.canSeeCohorts(game, viewer);
  }

  private static boolean shouldShowKillerCohort(
      GameWorldComponent game, PlayerEntity viewer, PlayerEntity target) {
    if (!game.isRunning() || !canSeeKillerCohort(game, viewer)) return false;
    if (KinsWatheAccess.isHackerCohortTarget(game, viewer, target)) return true;

    var mode = game.getGameMode();
    boolean murderMode = mode == WatheGameModes.MURDER || mode == Harpymodloader.MODDED_GAMEMODE;
    boolean secretMode =
        mode == WatheGameModes.SECRET_MURDER || mode == Harpymodloader.SECRET_MODDED_GAMEMODE;
    if (murderMode && KillerCohortAccess.isKillerCohort(game, target)) return true;
    return secretMode && WatheClient.SECRET_MURDER_FAKE_COHORTS.contains(target.getUuid());
  }

  /** Checks for Kin's Wathe texts that should be hidden, to avoid duplicates */
  public static boolean shouldSuppressKinsOverlayDraw(Text text) {
    if (!FabricLoader.getInstance().isModLoaded("kinswathe")
        || !(text.getContent() instanceof TranslatableTextContent translatable)) return false;

    ClientPlayerEntity viewer = MinecraftClient.getInstance().player;
    if (viewer == null) return false;
    PlayerEntity target = getTargetCompat(viewer);
    if (target == null) return false;

    GameWorldComponent game = GameWorldComponent.KEY.get(viewer.getWorld());
    String key = translatable.getKey();
    if ("game.tip.cohort".equals(key)) {
      return KinsWatheAccess.isHackerCohortTarget(game, viewer, target)
          && game.isRunning()
          && canSeeKillerCohort(game, viewer);
    }
    return key.startsWith("hud.kinswathe.hacker.target")
        && KinsWatheAccess.getHackerStatus(game, viewer, target) != null;
  }

  /** Checks for Stupid Express texts that should be hidden, to avoid duplicates */
  public static boolean shouldSuppressStupidExpressOverlayDraw(Text text) {
    if (drawingCompatibilityStatus
        || !FabricLoader.getInstance().isModLoaded("stupid_express")
        || !(text.getContent() instanceof TranslatableTextContent translatable)) return false;

    ClientPlayerEntity viewer = MinecraftClient.getInstance().player;
    if (viewer == null) return false;

    String key = translatable.getKey();
    boolean lovers = "hud.stupid_express.lovers.in_love".equals(key);
    boolean arsonist =
        "hud.stupid_express.arsonist.doused.true".equals(key)
            || "hud.stupid_express.arsonist.doused.false".equals(key);
    if (!lovers && !arsonist) return false;
    if (lovers && !isSpectating(viewer)) return false;

    PlayerEntity target = getTarget(viewer);
    if (target == null) return false;

    GameWorldComponent game = GameWorldComponent.KEY.get(viewer.getWorld());
    return lovers
        ? getLoversPair(target) != null
        : StupidExpressAccess.getArsonistDousedStatus(game, viewer, target) != null;
  }

  private static List<Modifier> getModifiers(PlayerEntity target) {
    try {
      List<Modifier> modifiers =
          WorldModifierComponent.KEY.get(target.getWorld()).getModifiers(target);
      return modifiers == null ? List.of() : modifiers;
    } catch (Throwable ignored) {
      return List.of();
    }
  }

  private static Modifier findModifier(PlayerEntity player, String path) {
    return getModifiers(player).stream()
        .filter(
            modifier -> modifier.identifier != null && modifier.identifier.getPath().equals(path))
        .findFirst()
        .orElse(null);
  }

  private static LoversPair getLoversPair(PlayerEntity player) {
    Modifier lovers = findModifier(player, "lovers");
    if (lovers == null) return null;

    for (PlayerEntity other : player.getWorld().getPlayers()) {
      if (!other.getUuid().equals(player.getUuid()) && findModifier(other, "lovers") != null) {
        return new LoversPair(
            Text.literal("In love with: " + other.getDisplayName().getString()), lovers.color);
      }
    }
    return null;
  }

  private static Object getNoellesComponent(String className, PlayerEntity player)
      throws ReflectiveOperationException {
    Field keyField = Class.forName(className).getDeclaredField("KEY");
    keyField.setAccessible(true);
    return ((ComponentKey<?>) keyField.get(null)).get(player);
  }

  private static UUID getMorphlingDisguise(PlayerEntity target) {
    if (!FabricLoader.getInstance().isModLoaded("noellesroles")) return null;
    try {
      Object component =
          getNoellesComponent("org.agmas.noellesroles.morphling.MorphlingPlayerComponent", target);
      if ((int) readField(component, "morphTicks") <= 0) return null;
      return (UUID) readField(component, "disguise");
    } catch (Throwable ignored) {
      return null;
    }
  }

  private static String getExecutionerTarget(PlayerEntity executioner) {
    if (!FabricLoader.getInstance().isModLoaded("noellesroles")) return null;
    try {
      Object component =
          getNoellesComponent(
              "org.agmas.noellesroles.executioner.ExecutionerPlayerComponent", executioner);
      UUID targetUuid = (UUID) readField(component, "target");
      if (targetUuid == null) return null;
      PlayerEntity target = executioner.getWorld().getPlayerByUuid(targetUuid);
      return target == null ? targetUuid.toString() : target.getDisplayName().getString();
    } catch (Throwable ignored) {
      return null;
    }
  }

  private static Object readField(Object owner, String name) throws ReflectiveOperationException {
    Field field = owner.getClass().getDeclaredField(name);
    field.setAccessible(true);
    return field.get(owner);
  }

  private record Segment(Text text, int color) {}

  private record LoversPair(Text text, int color) {}
}
