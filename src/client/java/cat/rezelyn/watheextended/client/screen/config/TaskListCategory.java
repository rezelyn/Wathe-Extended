package cat.rezelyn.watheextended.client.screen.config;

import cat.rezelyn.watheextended.api.config.moodswings.ConfigHelper;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import net.ceziak.mood_swings.client.ClientConfigState;
import net.ceziak.mood_swings.network.ConfigSnapshot;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class TaskListCategory {

  private static final String KEY = "gui.watheextended.config.category.tasks";

  private TaskListCategory() {}

  public static OptionGroup build(Screen parent, BiConsumer<String, Screen> sendCommand) {
    OptionGroup.Builder group =
        OptionGroup.createBuilder()
            .name(Text.translatable(KEY + ".title"))
            .description(OptionDescription.of(Text.translatable(KEY + ".tooltip")))
            .collapsed(true);

    Map<String, ConfigSnapshot.TaskSnapshot> tasks = ClientConfigState.snapshot().tasks();
    if (tasks.isEmpty()) {
      group.option(
          LabelOption.create(
              Text.translatable(KEY + ".none").styled(style -> style.withColor(0xFF5555))));
      return group.build();
    }

    tasks.entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .forEach(
            entry -> {
              String id = entry.getKey();
              boolean nativeTask = entry.getValue().nativeTask();
              group.option(
                  Option.<Boolean>createBuilder()
                      .name(Text.translatableWithFallback(KEY + ".task." + id, title(id)))
                      .description(
                          OptionDescription.of(
                              Text.translatable(
                                  KEY + (nativeTask ? ".desc.native" : ".desc.custom"))))
                      .binding(
                          true,
                          () -> isEnabled(id),
                          value -> sendCommand.accept(ConfigHelper.setTaskEnabled(id, value), parent))
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
            });
    return group.build();
  }

  private static boolean isEnabled(String id) {
    ConfigSnapshot.TaskSnapshot task = ClientConfigState.snapshot().tasks().get(id);
    return task == null || task.enabled();
  }

  private static String title(String id) {
    StringBuilder out = new StringBuilder();
    for (String word : id.split("_")) {
      if (word.isEmpty()) continue;
      if (out.length() > 0) out.append(' ');
      out.append(word.substring(0, 1).toUpperCase(Locale.ROOT)).append(word.substring(1));
    }
    return out.toString();
  }
}
