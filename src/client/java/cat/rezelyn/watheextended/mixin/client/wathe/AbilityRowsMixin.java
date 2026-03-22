package cat.rezelyn.watheextended.mixin.client.wathe;

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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private void watheextended$onRender(DrawContext ctx, int mx, int my, float delta, CallbackInfo ci) {
        watheextended$applyLayout();
        watheextended$drawLabels(ctx);
    }

    @Unique
    private static boolean watheextended$isMorphlingActive() {
        try {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null) return false;
            Class<?> cls = Class.forName("org.agmas.noellesroles.morphling.MorphlingPlayerComponent");
            java.lang.reflect.Field keyField = cls.getDeclaredField("KEY");
            keyField.setAccessible(true);
            @SuppressWarnings({"unchecked", "rawtypes"})
            org.ladysnake.cca.api.v3.component.ComponentKey key =
                    (org.ladysnake.cca.api.v3.component.ComponentKey) keyField.get(null);
            Object comp = key.get(mc.player);
            java.lang.reflect.Field morphTicksField = cls.getDeclaredField("morphTicks");
            morphTicksField.setAccessible(true);
            return ((int) morphTicksField.get(comp)) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Unique
    private void watheextended$applyLayout() {
        int rowMax = Math.max(1, (this.width - 36) / APART);
        int baseY = (this.height - 32) / 2 + 60;

        if (FabricLoader.getInstance().isModLoaded("noellesroles")) {
            watheextended$morphlingWidgets = watheextended$collectAndArrange("org.agmas.noellesroles.client.ui.MorphlingPlayerWidget", baseY, rowMax);
            watheextended$swapperWidgets = watheextended$collectAndArrange("org.agmas.noellesroles.client.ui.SwapperPlayerWidget", baseY, rowMax);
            watheextended$voodooWidgets = watheextended$collectAndArrange("org.agmas.noellesroles.client.ui.VoodooPlayerWidget", baseY, rowMax);
        }

        if (FabricLoader.getInstance().isModLoaded("kinswathe")) {
            watheextended$judgeWidgets = watheextended$collectAndArrange("org.BsXinQin.kinswathe.client.roles.judge.JudgePlayerWidget", baseY, rowMax);
            watheextended$bmakerWidgets = watheextended$collectAndArrange("org.BsXinQin.kinswathe.client.roles.bodymaker.BodymakerPlayerWidget", baseY, rowMax);
            watheextended$bmakerReasonWidgets = watheextended$collectAndArrange("org.BsXinQin.kinswathe.client.roles.bodymaker.BodymakerDeathReasonWidget", baseY, rowMax);
        }
    }

    @Unique
    private List<ClickableWidget> watheextended$collectAndArrange(String className, int baseY, int rowMax) {
        try {
            Class<?> clazz = Class.forName(className);
            List<ClickableWidget> found = new ArrayList<>();
            for (Element e : this.children()) {
                if (clazz.isInstance(e) && e instanceof ClickableWidget cw) {
                    found.add(cw);
                }
            }
            if (!found.isEmpty()) {
                watheextended$arrangeRows(found, baseY, rowMax);
            }
            return found;
        } catch (ClassNotFoundException ignored) {
            return Collections.emptyList();
        }
    }

    @Unique
    private void watheextended$arrangeRows(List<ClickableWidget> widgets, int baseY, int rowMax) {
        int total = widgets.size();
        for (int i = 0; i < total; i++) {
            int row = i / rowMax;
            int col = i % rowMax;
            int rowStart = row * rowMax;
            int rowCount = Math.min(rowMax, total - rowStart);
            int rowX = this.width / 2 - rowCount * APART / 2 + 9;
            widgets.get(i).setX(rowX + col * APART);
            widgets.get(i).setY(baseY + row * STEP);
        }
    }

    @Unique
    private void watheextended$drawLabels(DrawContext ctx) {
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;
        if (tr == null) return;

        if (!watheextended$isMorphlingActive()) {
            watheextended$drawLabel(ctx, tr, watheextended$morphlingWidgets, "gui.watheextended.inventory.morphling.morph", 0xAA023D);
        }
        watheextended$drawLabel(ctx, tr, watheextended$swapperWidgets, "gui.watheextended.inventory.swapper.swap", 0x3904AA);
        watheextended$drawLabel(ctx, tr, watheextended$judgeWidgets, "gui.watheextended.inventory.judge.judgement", 0xECECF7);
        watheextended$drawLabel(ctx, tr, watheextended$bmakerWidgets, "gui.watheextended.inventory.bodymaker.select", 0x2148D1);
        watheextended$drawLabel(ctx, tr, watheextended$bmakerReasonWidgets, "gui.watheextended.inventory.bodymaker.reason", 0x2148D1);

        if (!watheextended$voodooWidgets.isEmpty() && watheextended$voodooWidgets.getFirst().visible) {
            boolean hasTarget = watheextended$voodooHasTarget();
            String voodooKey = hasTarget ? "hud.voodoo.player_deaths_only" : "gui.watheextended.inventory.voodoo.doll";
            int voodooColor = hasTarget ? 0x555555 : 0x8072FD;
            watheextended$drawLabel(ctx, tr, watheextended$voodooWidgets, voodooKey, voodooColor);
        }
    }

    @Unique
    private static boolean watheextended$voodooHasTarget() {
        try {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null) return false;

            Class<?> cls = Class.forName("org.agmas.noellesroles.voodoo.VoodooPlayerComponent");

            java.lang.reflect.Field keyField = cls.getDeclaredField("KEY");
            keyField.setAccessible(true);
            @SuppressWarnings({"unchecked", "rawtypes"}) org.ladysnake.cca.api.v3.component.ComponentKey key = (org.ladysnake.cca.api.v3.component.ComponentKey) keyField.get(null);

            Object comp = key.get(mc.player);

            java.lang.reflect.Field targetField = cls.getDeclaredField("target");
            targetField.setAccessible(true);
            java.util.UUID target = (java.util.UUID) targetField.get(comp);

            if (target == null) return false;
            return !target.equals(mc.player.getUuid());
        } catch (Exception e) {
            return false;
        }
    }

    @Unique
    private void watheextended$drawLabel(DrawContext ctx, TextRenderer tr, List<ClickableWidget> widgets, String translationKey, int color) {
        if (widgets.isEmpty()) return;
        ClickableWidget first = widgets.getFirst();
        if (!first.visible) return;

        Text label = Text.translatable(translationKey);
        int labelX = this.width / 2 - tr.getWidth(label) / 2;
        int labelY = first.getY() - tr.fontHeight - 12;
        ctx.drawText(tr, label, labelX, labelY, color, true);
    }
}
