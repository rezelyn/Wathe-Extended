package cat.rezelyn.watheextended.client.render.hud.ability;

import cat.rezelyn.watheextended.client.render.hud.ability.AbilityKey.ProgressStyle;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.BsXinQin.kinswathe.client.KinsWatheInitializeClient;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.aussiebox.starexpress.client.StarryExpressClient;

/**
 * Decides what to do with text the game is about to draw. either leave it alone, hide it, or
 * replace it with our own {@link AbilityTip}
 */
public final class AbilityOverride {
  private static final Set<String> HIDDEN_KEYS =
      Set.of(
          "hud.kinswathe.detective.target",
          "hud.stupid_express.necromancer.possible_revive",
          "hud.stupid_express.necromancer.no_possible_revive",
          "hud.stupid_express.necromancer.cooldown");

  private static final int HIDDEN_RESULT = 0;
  private static final Object[] NO_ARGS = {};

  private AbilityOverride() {}

  public static OptionalInt tryOverride(
      DrawContext context, TextRenderer renderer, Text text, int color) {
    if (!(text.getContent() instanceof TranslatableTextContent content)) {
      return OptionalInt.empty();
    }

    String key = content.getKey();
    if (HIDDEN_KEYS.contains(key)) return OptionalInt.of(HIDDEN_RESULT);

    Optional<AbilityKey> hudKey = AbilityKey.find(key);
    return hudKey
        .map(abilityKey ->
            switch (resolve(abilityKey, argsOf(content), color)) {
              case Outcome.Ignore ignore -> OptionalInt.empty();
              case Outcome.Hide hide -> OptionalInt.of(HIDDEN_RESULT);
              case Outcome.Show show ->
                  OptionalInt.of(AbilityTip.draw(context, renderer, show.tip(), show.color()));
              case Outcome.NotReady blocked ->
                  OptionalInt.of(
                      AbilityTip.drawNotReady(
                          context, renderer, blocked.detail(), blocked.keybind(), color));
            })
        .orElseGet(OptionalInt::empty);
  }

  private static Outcome resolve(AbilityKey key, Object[] args, int color) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (AbilityTip.isHidden(client)) return Outcome.HIDE;
    if (AbilityConditions.isDisabled(key)) return Outcome.show(AbilityTip.disabled(), color);

    return switch (key.kind()) {
      case READY -> ready(key, args, color);
      case COOLDOWN -> cooldown(key, args);
      case COST -> cost(key, args);
      case PROGRESS -> progress(key, args);
    };
  }

  private static Outcome ready(AbilityKey key, Object[] args, int color) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (key == AbilityKey.KINS_READY && AbilityConditions.isDetective(client)) {
      return Outcome.show(
          AbilityTip.ready(
              client.options.useKey.getBoundKeyLocalizedText().getString(),
              AbilityConditions.hasInvestigationTarget(client)),
          color);
    }

    if (args.length == 0) return Outcome.IGNORE;

    boolean usable = key != AbilityKey.THIEF_READY || AbilityConditions.canThiefSteal();
    return Outcome.show(AbilityTip.ready(argText(args[0]), usable), color);
  }

  private static Outcome cooldown(AbilityKey key, Object[] args) {
    if (args.length == 0) return Outcome.IGNORE;
    if (key == AbilityKey.NOELLES_COOLDOWN && AbilityConditions.isPhantomInvisible()) {
      return Outcome.HIDE;
    }

    return notReady(key, AbilityTip.cooldown(argText(args[0])));
  }

  private static Outcome cost(AbilityKey key, Object[] args) {
    String price;
    if (args.length > 0) {
      price = argText(args[0]);
    } else if (key == AbilityKey.RECALLER_NOT_ENOUGH_MONEY) {
      price = String.valueOf(100);
    } else {
      return Outcome.IGNORE;
    }
    return notReady(key, AbilityTip.price(price));
  }

  private static Outcome notReady(AbilityKey key, Text detail) {
    KeyBinding binding =
        switch (key) {
          case THIEF_COOLDOWN -> MinecraftClient.getInstance().options.useKey;
          case STARSTRUCK_COOLDOWN -> StarryExpressClient.abilityBind;
          case KINS_COOLDOWN, KINS_NOT_ENOUGH_MONEY ->
              AbilityConditions.isDetective(MinecraftClient.getInstance())
                  ? MinecraftClient.getInstance().options.useKey
                  : KinsWatheInitializeClient.abilityBind;
          case NOELLES_COOLDOWN, RECALLER_NOT_ENOUGH_MONEY -> NoellesrolesClient.abilityBind;
          default -> throw new IllegalArgumentException("No cooldown or price binding for " + key);
        };
    return new Outcome.NotReady(
        detail, binding == null ? "?" : binding.getBoundKeyLocalizedText().getString());
  }

  private static Outcome progress(AbilityKey key, Object[] args) {
    if (args.length < 2) return Outcome.IGNORE;
    ProgressStyle style = key.progressStyle();
    Text tip =
        AbilityTip.progress(
            style.symbol(),
            argText(args[0]),
            argText(args[1]),
            Text.translatable(style.labelKey()));

    return Outcome.show(tip, style.color());
  }

  private static Object[] argsOf(TranslatableTextContent content) {
    Object[] args = content.getArgs();
    return args == null ? NO_ARGS : args;
  }

  private static String argText(Object arg) {
    return arg instanceof Text text ? text.getString() : String.valueOf(arg);
  }

  private sealed interface Outcome {
    Outcome IGNORE = new Ignore();
    Outcome HIDE = new Hide();

    static Outcome show(Text tip, int color) {
      return new Show(tip, color);
    }

    // leave original text
    record Ignore() implements Outcome {}

    // suppress original text
    record Hide() implements Outcome {}

    // suppress original text and draw tip
    record Show(Text tip, int color) implements Outcome {}

    record NotReady(Text detail, String keybind) implements Outcome {}
  }
}
