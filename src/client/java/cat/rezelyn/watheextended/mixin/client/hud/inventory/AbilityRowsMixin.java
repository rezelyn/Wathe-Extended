package cat.rezelyn.watheextended.mixin.client.hud.inventory;

import dev.doctor4t.wathe.client.gui.screen.ingame.LimitedInventoryScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Environment(EnvType.CLIENT)
@Mixin(value = LimitedInventoryScreen.class, priority = 2000)
public abstract class AbilityRowsMixin extends Screen {

    private static final int APART = 36;
    private static final int STEP = 32;

    private List<ClickableWidget> watheextended$morphlingWidgets = Collections.emptyList();
    private List<ClickableWidget> watheextended$swapperWidgets = Collections.emptyList();
    private List<ClickableWidget> watheextended$voodooWidgets = Collections.emptyList();
    private List<ClickableWidget> watheextended$judgeWidgets = Collections.emptyList();
    private List<ClickableWidget> watheextended$bmakerWidgets = Collections.emptyList();
    private List<ClickableWidget> watheextended$bmakerReasonWidgets = Collections.emptyList();

    protected AbilityRowsMixin() {
        super(Text.empty());
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void watheextended$onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        watheextended$applyLayout();
        watheextended$drawLabels(context);
    }

    @Unique
    private static boolean watheextended$isMorphlingActive() {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return false;
            Class<?> cls = Class.forName("org.agmas.noellesroles.morphling.MorphlingPlayerComponent");
            Field keyField = cls.getDeclaredField("KEY");
            keyField.setAccessible(true);
            @SuppressWarnings({"unchecked", "rawtypes"})
            ComponentKey key = (ComponentKey) keyField.get(null);
            Object component = key.get(client.player);
            Field morphTicksField = cls.getDeclaredField("morphTicks");
            morphTicksField.setAccessible(true);
            return ((int) morphTicksField.get(component)) > 0;
        } catch (Exception exception) {
            return false;
        }
    }

    @Unique
    private void watheextended$applyLayout() {
        int rowMax = Math.max(1, (this.width - 36) / APART);
        int y = (this.height - 32) / 2 + 60;

        if (FabricLoader.getInstance().isModLoaded("noellesroles")) {
            watheextended$morphlingWidgets = watheextended$collectAndArrange("org.agmas.noellesroles.client.ui.MorphlingPlayerWidget", y, rowMax);
            watheextended$swapperWidgets = watheextended$collectAndArrange("org.agmas.noellesroles.client.ui.SwapperPlayerWidget", y, rowMax);
            watheextended$voodooWidgets = watheextended$collectAndArrange("org.agmas.noellesroles.client.ui.VoodooPlayerWidget", y, rowMax);
        }

        if (FabricLoader.getInstance().isModLoaded("kinswathe")) {
            watheextended$judgeWidgets = watheextended$collectAndArrange("org.BsXinQin.kinswathe.client.roles.judge.JudgePlayerWidget", y, rowMax);
            watheextended$bmakerWidgets = watheextended$collectAndArrange("org.BsXinQin.kinswathe.client.roles.bodymaker.BodymakerPlayerWidget", y, rowMax);
            watheextended$bmakerReasonWidgets = watheextended$collectAndArrange("org.BsXinQin.kinswathe.client.roles.bodymaker.BodymakerDeathReasonWidget", y, rowMax);
        }
    }

    @Unique
    private List<ClickableWidget> watheextended$collectAndArrange(String className, int y, int rowMax) {
        try {
            Class<?> cls = Class.forName(className);
            List<ClickableWidget> list = new ArrayList<>();
            for (Element element : this.children()) {
                if (cls.isInstance(element) && element instanceof ClickableWidget widget) {
                    list.add(widget);
                }
            }
            if (!list.isEmpty()) {
                watheextended$arrangeRows(list, y, rowMax);
            }
            return list;
        } catch (ClassNotFoundException ignored) {
            return Collections.emptyList();
        }
    }

    @Unique
    private void watheextended$arrangeRows(List<ClickableWidget> widgets, int y, int rowMax) {
        int size = widgets.size();
        for (int i = 0; i < size; i++) {
            int row = i / rowMax;
            int colon = i % rowMax;
            int rowStart = row * rowMax;
            int rowCount = Math.min(rowMax, size - rowStart);
            int rowX = this.width / 2 - rowCount * APART / 2 + 9;
            widgets.get(i).setX(rowX + colon * APART);
            widgets.get(i).setY(y + row * STEP);
        }
    }

    @Unique
    private void watheextended$drawLabels(DrawContext context) {
        TextRenderer text = MinecraftClient.getInstance().textRenderer;
        if (text == null) return;

        if (!watheextended$isMorphlingActive()) {
            watheextended$drawLabel(context, text, watheextended$morphlingWidgets, "gui.watheextended.inventory.morphling.morph", 0xAA023D);
        }
        watheextended$drawLabel(context, text, watheextended$swapperWidgets, "gui.watheextended.inventory.swapper.swap", 0x3904AA);
        watheextended$drawLabel(context, text, watheextended$judgeWidgets, "gui.watheextended.inventory.judge.judgement", 0xECECF7);
        watheextended$drawLabel(context, text, watheextended$bmakerWidgets, "gui.watheextended.inventory.bodymaker.select", 0x2148D1);
        watheextended$drawLabel(context, text, watheextended$bmakerReasonWidgets, "gui.watheextended.inventory.bodymaker.reason", 0x2148D1);

        if (!watheextended$voodooWidgets.isEmpty() && watheextended$voodooWidgets.getFirst().visible) {
            boolean hasTarget = watheextended$voodooHasTarget();
            String voodooKey = hasTarget ? "hud.voodoo.player_deaths_only" : "gui.watheextended.inventory.voodoo.doll";
            int voodooColor = hasTarget ? 0x555555 : 0x8072FD;
            watheextended$drawLabel(context, text, watheextended$voodooWidgets, voodooKey, voodooColor);
        }
    }

    @Unique
    private static boolean watheextended$voodooHasTarget() {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return false;

            Class<?> cls = Class.forName("org.agmas.noellesroles.voodoo.VoodooPlayerComponent");
            java.lang.reflect.Field keyField = cls.getDeclaredField("KEY");
            keyField.setAccessible(true);

            @SuppressWarnings({"unchecked", "rawtypes"})
            ComponentKey key = (ComponentKey) keyField.get(null);
            Object component = key.get(client.player);
            Field targetField = cls.getDeclaredField("target");
            targetField.setAccessible(true);
            UUID target = (java.util.UUID) targetField.get(component);

            if (target == null) return false;
            return !target.equals(client.player.getUuid());
        } catch (Exception exception) {
            return false;
        }
    }

    @Unique
    private void watheextended$drawLabel(DrawContext ctx, TextRenderer text, List<ClickableWidget> widget, String translationKey, int color) {
        if (widget.isEmpty()) return;
        ClickableWidget first = widget.getFirst();
        if (!first.visible) return;

        Text label = Text.translatable(translationKey);
        int labelX = this.width / 2 - text.getWidth(label) / 2;
        int labelY = first.getY() - text.fontHeight - 12;
        ctx.drawText(text, label, labelX, labelY, color, true);
    }
}
