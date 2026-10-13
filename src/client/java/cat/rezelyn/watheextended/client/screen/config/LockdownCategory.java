package cat.rezelyn.watheextended.client.screen.config;

import cat.rezelyn.watheextended.api.config.moodswings.ConfigHelper;
import cat.rezelyn.watheextended.client.screen.ScreenUtils;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import java.util.Map;
import java.util.function.BiConsumer;
import net.ceziak.mood_swings.area.NamedArea;
import net.ceziak.mood_swings.client.ClientConfigState;
import net.ceziak.mood_swings.network.ConfigSnapshot;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class LockdownCategory {

  private static final String KEY = "gui.watheextended.config.category.lockdown";

  private LockdownCategory() {}

  public static OptionGroup build(Screen parent, BiConsumer<String, Screen> sendCommand) {
    ConfigSnapshot snapshot = ClientConfigState.snapshot();

    OptionGroup.Builder group =
        OptionGroup.createBuilder()
            .name(Text.translatable(KEY + ".title"))
            .description(OptionDescription.of(Text.translatable(KEY + ".tooltip")))
            .collapsed(true);

    /// ENABLED
    group.option(
        Option.<Boolean>createBuilder()
            .name(Text.translatable(KEY + ".opt.enabled"))
            .description(OptionDescription.of(Text.translatable(KEY + ".opt.enabled.desc")))
            .binding(
                true,
                () -> ClientConfigState.snapshot().lockdownEnabled(),
                value -> sendCommand.accept(ConfigHelper.setLockdownEnabled(value), parent))
            .controller(
                option ->
                    BooleanControllerBuilder.create(option)
                        .formatValue(
                            value ->
                                Text.translatable(
                                    value
                                        ? "gui.watheextended.config.text.on"
                                        : "gui.watheextended.config.text.off")))
            .build());

    addNewAreaOptions(group, parent, sendCommand);
    addAreaOptions(group, snapshot.lockdownAreas());
    return group.build();
  }

  private static void addNewAreaOptions(
      OptionGroup.Builder group, Screen parent, BiConsumer<String, Screen> sendCommand) {
    ScreenUtils.NewAreaDraft draft =
        new ScreenUtils.NewAreaDraft(parent, sendCommand, ConfigHelper::saveLockdownArea);

    group.option(LabelOption.create(Text.translatable(KEY + ".group.new.tooltip")));
    group.option(draft.nameField(newField("name"), newFieldDesc("name")));
    group.option(draft.boxField(newField("box"), newFieldDesc("box")));
  }

  private static void addAreaOptions(OptionGroup.Builder group, Map<String, NamedArea> areas) {
    if (areas.isEmpty()) {
      group.option(
          LabelOption.create(
              Text.translatable(KEY + ".group.areas.none")
                  .styled(style -> style.withColor(0xFF5555))));
      return;
    }

    for (Map.Entry<String, NamedArea> entry : areas.entrySet()) {
      String id = entry.getKey();
      NamedArea area = entry.getValue();
      String label = ScreenUtils.areaLabel(id, area.displayName());

      group.option(
          ScreenUtils.deleteButton(
              Text.translatable(KEY + ".group.areas.opt.delete", label),
              Text.translatable(
                      KEY + ".group.areas.opt.delete.desc", area.dimension(), area.cornersString())
                  .styled(style -> style.withColor(0x757575)),
              Text.translatable(KEY + ".group.areas.opt.delete.confirm.title"),
              Text.translatable(KEY + ".group.areas.opt.delete.confirm", label),
              ConfigHelper.removeLockdownArea(id)));
    }
  }

  private static Text newField(String key) {
    return Text.translatable(KEY + ".group.new.opt." + key);
  }

  private static Text newFieldDesc(String key) {
    return Text.translatable(KEY + ".group.new.opt." + key + ".desc");
  }
}
