package cat.rezelyn.watheextended.mixin;

import cat.rezelyn.watheextended.mixin.compat.LegacyWatheImportsMixin;
import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * Mixin config plugin with two responsibilities:
 *
 * <ol>
 *   <li><b>Conditional mixins:</b> mixins that target an optional add-on are only applied when that
 *       add-on is installed, see {@link #shouldApplyMixin(String, String)}.
 *   <li><b>Legacy compatibility:</b> rewrites outdated Wathe payload references after Wathe 1.4
 *       moved them from the util package to the network package, see {@link #postApply}.
 * </ol>
 *
 * <p>Legacy targets are registered through {@link LegacyWatheImportsMixin}
 *
 * <p>Mixin class names are matched by <em>suffix</em> (package-relative, e.g.{@code
 * game.AbilityCancelMixin}), so the sets below must be kept in sync with the mixin config JSON. A
 * mixin that is not listed in any set is always applied.
 */
public class WatheExtendedMixinPlugin implements IMixinConfigPlugin {

  /** Internal paths before Wathe {@code 1.4} */
  private static final List<String> OLD_PAYLOADS =
      List.of(
          // "dev/doctor4t/wathe/util/GunShootPayload",
          "dev/doctor4t/wathe/util/AnnounceWelcomePayload");

  /** Corresponding internal paths since Wathe {@code 1.4} */
  private static final List<String> NEW_PAYLOADS =
      List.of(
          // "dev/doctor4t/wathe/network/GunShootPayload",
          "dev/doctor4t/wathe/network/AnnounceWelcomePayload");

  /** Mixins that need the Noelle's Roles add-on (mod id {@code noellesroles}) */
  private static final Set<String> NOELLES_ROLES_MIXINS =
      Set.of(
          "client.game.shop.BinglusShopMixin",
          "client.fix.GraverobberCoronerHudMixin",
          "client.game.roles.infected.InfectedInstinctMixin",
          "client.game.roles.morphling.MorphlingCancelHudMixin",
          "client.game.roles.morphling.MorphlingSlotTextureMixin",
          "client.game.roles.swapper.SwapperSlotTextureMixin",
          "client.game.modifiers.guesser.GuesserRolePickerMixin",
          "client.game.modifiers.guesser.GuesserSlotTextureMixin",
          "client.game.roles.voodoo.VoodooSlotTextureMixin",
          "client.game.roles.awesomebinglus.BinglusShopRendererMixin",
          "client.game.roles.awesomebinglus.BinglusInstinctMixin",
          "fix.InfectedNullGuardMixin",
          "game.AbilityCancelMixin",
          "game.AbilityDurationMixin",
          "game.mood.DepressedAbilityMixin",
          "game.shop.ShopEntryBinglusMixin",
          "game.roles.morphling.MorphlingCooldownMixin",
          "game.roles.recaller.RecallerTeleportMixin",
          "game.roles.swapper.SwapperTeleportMixin");

  /** Mixins that need the Starry Express add-on (mod id {@code starexpress}) */
  private static final Set<String> STARRY_EXPRESS_MIXINS =
      Set.of(
          "client.hud.GuidebookButtonMixin",
          "game.mood.StarryExpressAbilityMixin",
          "game.roles.starstruck.StarstruckAbilityMixin",
          "game.roles.starstruck.StarstruckComponentMixin");

  /** Mixins that need the Stupid Express add-on (mod id {@code stupid_express}) */
  private static final Set<String> STUPID_EXPRESS_MIXINS =
      Set.of(
          "client.game.roles.arsonist.ArsonistHudMixin",
          "client.game.roles.arsonist.ArsonistDousedLabelMixin",
          "fix.InitiateRemoteDeathMixin",
          "fix.RevivalItemGivingMixin",
          "game.item.ArsonistItemsUseMixin",
          "game.item.ThiefItemRulesMixin",
          "game.roles.arsonist.DousedPlayerNotificationMixin");

  /** Mixins that need the Kin's Wathe add-on (mod id {@code kinswathe}) */
  private static final Set<String> KINS_WATHE_MIXINS =
      Set.of(
          "client.hud.DreamImprintOverlayMixin",
          "client.game.roles.bodymaker.BodymakerDeathReasonSlotTextureMixin",
          "client.game.roles.bodymaker.BodymakerRolePickerMixin",
          "client.game.roles.bodymaker.BodymakerSlotTextureMixin",
          "client.game.roles.dreamer.DreamerInstinctMixin",
          "client.game.roles.judge.JudgeSlotTextureMixin",
          "game.roles.bellringer.BellringerAbilityMixin",
          "game.roles.cleaner.CleanerAbilityMixin",
          "game.mood.KinsBellringerAbilityMixin",
          "game.mood.KinsDetectiveAbilityMixin",
          "game.mood.KinsJudgeAbilityMixin",
          "game.mood.KinsTechnicianPurchaseMixin",
          "game.roles.dreamer.DreamImprintNotificationMixin",
          "game.roles.dreamer.DreamerImprintRecoveryMixin",
          "game.roles.dreamer.DreamerKillerConversionMixin",
          "game.roles.robot.RobotAbilityMixin",
          "game.shop.AddonsPlayerShopComponentMixin");

  /** Mixins that need the Wathe Extra Items add-on (mod id {@code watheextraitems} */
  private static final Set<String> WATHE_EXTRA_ITEMS_MIXINS =
      Set.of("compat.ExtraItemsRoundStartMixin");

  private static final String LEGACY_IMPORTS_MIXIN = "compat.LegacyWatheImportsMixin";

  private static boolean matches(String mixinClassName, Set<String> suffixes) {
    return suffixes.stream().anyMatch(mixinClassName::endsWith);
  }

  /**
   * Decides whether a mixin should be applied, based on which add-ons are installed
   *
   * <p>A mixin belonging to one of the add-on sets above is skipped if that add-on is missing,
   * which avoids errors from targeting classes that do not exist. The legacy import marker is
   * skipped unless Kin's Wathe is installed.
   *
   * @param targetClassName the class being mixed into
   * @param mixinClassName the fully qualified name of the mixin
   * @return {@code true} if the mixin should be applied
   */
  @Override
  public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
    FabricLoader loader = FabricLoader.getInstance();
    if (matches(mixinClassName, NOELLES_ROLES_MIXINS) && !loader.isModLoaded("noellesroles"))
      return false;
    if (matches(mixinClassName, STARRY_EXPRESS_MIXINS) && !loader.isModLoaded("starexpress"))
      return false;
    if (matches(mixinClassName, STUPID_EXPRESS_MIXINS) && !loader.isModLoaded("stupid_express"))
      return false;
    if (matches(mixinClassName, WATHE_EXTRA_ITEMS_MIXINS) && !loader.isModLoaded("watheextraitems"))
      return false;
    if (matches(mixinClassName, KINS_WATHE_MIXINS) && !loader.isModLoaded("kinswathe"))
      return false;

    if (mixinClassName.endsWith(LEGACY_IMPORTS_MIXIN) && !loader.isModLoaded("kinswathe"))
      return false;

    return true;
  }

  @Override
  public void onLoad(String mixinPackage) {}

  @Override
  public String getRefMapperConfig() {
    return null;
  }

  @Override
  public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

  @Override
  public List<String> getMixins() {
    return null;
  }

  @Override
  public void preApply(
      String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

  /**
   * Rewrites references to payload classes moved in Wathe {@code 1.4} in the target's bytecode, so
   * outdated add-ons can link against their new package.
   *
   * <p>The following instruction operands are redirected from the old to the new class:
   *
   * <ul>
   *   <li>Type instructions ({@code new}, {@code checkcast}, {@code instanceof}, {@code anewarray})
   *   <li>The owner of method calls
   *   <li>The owner of field accesses
   *   <li>The element type of multidimensional array creation
   * </ul>
   *
   * <p>Payload references in method and field descriptors are rewritten as well. Mixin target
   * annotations are resolved before this callback and cannot be repaired here.
   *
   * @param targetClassName the class that was just mixed into
   * @param targetClass the transformed class node, modified in place
   * @param mixinClassName the mixin that was applied
   * @param mixinInfo metadata of the applied mixin
   */
  @Override
  public void postApply(
      String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    rewriteAnnotations(targetClass.visibleAnnotations);
    rewriteAnnotations(targetClass.invisibleAnnotations);

    for (var field : targetClass.fields) {
      rewriteAnnotations(field.visibleAnnotations);
      rewriteAnnotations(field.invisibleAnnotations);
    }

    for (var method : targetClass.methods) {
      rewriteAnnotations(method.visibleAnnotations);
      rewriteAnnotations(method.invisibleAnnotations);

      for (var insn : method.instructions) {
        if (insn instanceof TypeInsnNode typeInsn) {
          typeInsn.desc = replacePayloadNames(typeInsn.desc);
        }

        if (insn instanceof MethodInsnNode methodInsn) {
          methodInsn.owner = replacePayloadNames(methodInsn.owner);
          methodInsn.desc = replacePayloadNames(methodInsn.desc);
        }

        if (insn instanceof FieldInsnNode fieldInsn) {
          fieldInsn.owner = replacePayloadNames(fieldInsn.owner);
          fieldInsn.desc = replacePayloadNames(fieldInsn.desc);
        }

        if (insn instanceof MultiANewArrayInsnNode arrayInsn) {
          arrayInsn.desc = replacePayloadNames(arrayInsn.desc);
        }
      }
    }
  }

  private static String replacePayloadNames(String value) {
    if (value == null) return null;
    String result = value;
    for (int i = 0; i < OLD_PAYLOADS.size(); i++) {
      result = result.replace(OLD_PAYLOADS.get(i), NEW_PAYLOADS.get(i));
      result =
          result.replace(
              OLD_PAYLOADS.get(i).replace('/', '.'), NEW_PAYLOADS.get(i).replace('/', '.'));
    }
    return result;
  }

  private static void rewriteAnnotations(List<AnnotationNode> annotations) {
    if (annotations == null) return;
    for (AnnotationNode annotation : annotations) {
      if (annotation.values == null) continue;
      for (int i = 1; i < annotation.values.size(); i += 2) {
        annotation.values.set(i, rewriteAnnotationValue(annotation.values.get(i)));
      }
    }
  }

  private static Object rewriteAnnotationValue(Object value) {
    if (value instanceof String string) return replacePayloadNames(string);
    if (value instanceof Type type) return Type.getType(replacePayloadNames(type.getDescriptor()));
    if (value instanceof AnnotationNode annotation) {
      rewriteAnnotations(List.of(annotation));
      return annotation;
    }
    if (value instanceof List<?> values) {
      for (int i = 0; i < values.size(); i++) {
        @SuppressWarnings("unchecked")
        List<Object> mutableValues = (List<Object>) values;
        mutableValues.set(i, rewriteAnnotationValue(values.get(i)));
      }
      return value;
    }
    return value;
  }
}
