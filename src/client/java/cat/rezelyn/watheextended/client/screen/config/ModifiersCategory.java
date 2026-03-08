package cat.rezelyn.watheextended.client.screen.config;

import cat.rezelyn.watheextended.api.hml.ConfigHelper;
import cat.rezelyn.watheextended.api.hml.ModifiersDisplay;
import cat.rezelyn.watheextended.api.hml.ModifiersId;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public final class ModifiersCategory {

    private ModifiersCategory() {
    }

    public static ConfigCategory build(Screen parent, Map<String, Boolean> pendingState,
                                       BiConsumer<String, Screen> sendCommand) {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("gui.watheextended.config.category.modifiers"))
                .tooltip(Text.translatable("gui.watheextended.config.category.modifiers.tooltip"));

        try {
            Set<String> modifierId = new LinkedHashSet<>(ModifiersId.get());

            if (modifierId.isEmpty()) {
                builder.option(LabelOption.create(
                        Text.translatable("gui.watheextended.config.category.modifiers.empty")
                                .styled(style -> style.withColor(0xFF5555))));
            } else {
                Map<String, ModifiersDisplay.ModifierDisplay> modifierName = ModifiersDisplay.get();

                for (Map.Entry<String, List<String>> entry : RolesCategory.sortByMods(modifierId).entrySet()) {
                    OptionGroup.Builder group = OptionGroup.createBuilder()
                            .name(Text.literal(RolesCategory.modsNamespace(entry.getKey())))
                            .collapsed(false);

                    for (String id : entry.getValue()) {
                        ModifiersDisplay.ModifierDisplay display = modifierName.get(id);
                        Text label = display != null
                                ? display.display().copy().styled(style -> style.withColor(display.color()))
                                : Text.literal(ModifiersDisplay.localName(id));

                        boolean modCurrentValue = pendingState.containsKey(id)
                                ? pendingState.get(id)
                                : !ConfigHelper.getDisabledModifiers().contains(id);

                        group.option(Option.<Boolean>createBuilder()
                                .name(label)
                                .description(OptionDescription.of(
                                        Text.literal(id).styled(style -> style.withColor(0x505050))))
                                .binding(modCurrentValue,
                                        () -> pendingState.containsKey(id)
                                                ? pendingState.get(id)
                                                : !ConfigHelper.getDisabledModifiers().contains(id),
                                        enabled -> {
                                            pendingState.put(id, enabled);
                                            sendCommand.accept("setEnabledModifier " + id + " " + enabled, parent);
                                        })
                                .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true)
                                        .formatValue(v -> Text.translatable(v ? "text.watheextended.enabled" : "text.watheextended.disabled")))
                                .build());
                    }
                    builder.group(group.build());
                }
            }
        } catch (Throwable t) {
            builder.option(LabelOption.create(
                    Text.translatable("gui.watheextended.config.category.modifiers.error")
                            .styled(style -> style.withColor(0xFF5555))));
        }

        return builder.build();
    }
}

