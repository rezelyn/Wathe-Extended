package cat.rezelyn.watheextended.client.screen.config;

import cat.rezelyn.orelib.api.setting.Validation;
import cat.rezelyn.watheextended.client.screen.ScreenUtils;
import java.util.List;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

final class ConfigText {
  private ConfigText() {}

  static MutableText text(String key, Object... args) {
    return Text.translatable("gui.watheextended.config." + key, args);
  }

  static Text onOff(boolean value) {
    return ScreenUtils.icon(value ? "enabled" : "disabled");
  }

  static Text percent(float value) {
    return Text.literal(Math.round(value * 100f) + "%");
  }

  static <T extends Number> Validation<T> range(T value, double min, double max) {
    double number = value.doubleValue();
    return Double.isFinite(number) && number >= min && number <= max
        ? Validation.accepted(value)
        : Validation.rejected(Text.literal("Value is outside the allowed range."));
  }

  static <T> Validation<T> choice(T value, List<T> choices) {
    return choices.contains(value)
        ? Validation.accepted(value)
        : Validation.rejected(Text.literal("Select a supported value."));
  }
}
