package cat.rezelyn.watheextended.mixin.client.noellesroles;

import cat.rezelyn.watheextended.api.wathe.RolesDisplay;
import cat.rezelyn.watheextended.client.widget.RolePickerWidget;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.api.WatheRoles;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.gui.screen.ingame.LimitedInventoryScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.agmas.harpymodloader.Harpymodloader;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.client.ui.guesser.GuesserPlayerWidget;
import org.agmas.noellesroles.packet.GuessC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Environment(EnvType.CLIENT)
@Mixin(LimitedInventoryScreen.class)
public abstract class GuesserRolePickerMixin extends Screen {

    @Unique
    private RolePickerWidget watheextended$guesserPicker = null;

    @Unique
    private List<ClickableWidget> watheextended$bodymakerWidgets = new ArrayList<>();

    @Unique
    private List<ClickableWidget> watheextended$guesserPlayerWidgets = new ArrayList<>();

    protected GuesserRolePickerMixin() {
        super(Text.empty());
    }

    private static List<RolePickerWidget.RoleEntry> buildGuesserEntries(boolean guesserIsInnocent) {
        Map<String, RolesDisplay.RoleDisplay> display = RolesDisplay.get();

        List<RolePickerWidget.RoleEntry> killers = new ArrayList<>();
        List<RolePickerWidget.RoleEntry> neutrals = new ArrayList<>();
        List<RolePickerWidget.RoleEntry> innocents = new ArrayList<>();

        for (Role role : WatheRoles.ROLES) {
            if (role == null || role.identifier() == null) continue;
            if (Harpymodloader.SPECIAL_ROLES.contains(role)) continue;

            if (guesserIsInnocent) {
                if (role.canUseKiller()) continue;
                try {
                    if (Noellesroles.KILLER_SIDED_NEUTRALS.contains(role)) continue;
                } catch (Throwable ignored) {
                }
            }

            String id = role.identifier().toString();
            String path = role.identifier().getPath();

            RolesDisplay.RoleDisplay d = display.get(id);
            Text label = d != null ? d.display() : Text.literal(RolesDisplay.localName(id));
            int color = d != null ? d.color() : 0xFFFFFF;
            RolesDisplay.Side side = d != null ? d.side() : RolesDisplay.Side.NEUTRAL;

            var entry = new RolePickerWidget.RoleEntry(label, 0xFF000000 | color, path);
            switch (side) {
                case KILLER -> killers.add(entry);
                case NEUTRAL -> neutrals.add(entry);
                case INNOCENT -> innocents.add(entry);
            }
        }

        List<RolePickerWidget.RoleEntry> result = new ArrayList<>();
        result.addAll(killers);
        result.addAll(neutrals);
        result.addAll(innocents);
        return result;
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void watheextended$guesserPickerInit(CallbackInfo ci) {
        watheextended$guesserPicker = null;
        watheextended$bodymakerWidgets = new ArrayList<>();
        watheextended$guesserPlayerWidgets = new ArrayList<>();
        if (!FabricLoader.getInstance().isModLoaded("noellesroles")) return;

        try {
            Class<?> grwClass = Class.forName("org.agmas.noellesroles.client.ui.guesser.GuesserRoleWidget");
            Class<?> gpwClass = Class.forName("org.agmas.noellesroles.client.ui.guesser.GuesserPlayerWidget");

            Element roleWidget = null;
            boolean isGuesserUI = false;

            for (Element child : List.copyOf(this.children())) {
                if (grwClass.isInstance(child)) {
                    roleWidget = child;
                    isGuesserUI = true;
                } else if (gpwClass.isInstance(child)) {
                    isGuesserUI = true;
                }
            }

            if (!isGuesserUI) return;

            if (FabricLoader.getInstance().isModLoaded("kinswathe")) {
                try {
                    Class<?> bpwClass = Class.forName("org.BsXinQin.kinswathe.client.roles.bodymaker.BodymakerPlayerWidget");
                    Class<?> bdwClass = Class.forName("org.BsXinQin.kinswathe.client.roles.bodymaker.BodymakerDeathReasonWidget");
                    boolean bodymakerPresent = false;
                    for (Element child : this.children()) {
                        if ((bpwClass.isInstance(child) || bdwClass.isInstance(child)) && child instanceof ClickableWidget cw) {
                            watheextended$bodymakerWidgets.add(cw);
                            bodymakerPresent = true;
                        }
                    }
                } catch (Exception ignored) {
                }
            }

            for (Element child : this.children()) {
                if (gpwClass.isInstance(child) && child instanceof ClickableWidget cw) {
                    cw.setY(cw.getY() + 35);
                    watheextended$guesserPlayerWidgets.add(cw);
                }
            }

            if (roleWidget == null) return;

            this.remove(roleWidget);

            MinecraftClient client = MinecraftClient.getInstance();
            boolean guesserIsInnocent = client.player != null && GameWorldComponent.KEY.get(client.player.getWorld()).isInnocent(client.player);

            List<RolePickerWidget.RoleEntry> entries = buildGuesserEntries(guesserIsInnocent);

            int pickerX = this.width / 2 - 100;
            int pickerY = (this.height - 32) / 2 + 32 + 20;
            int pickerH = Math.max(50, Math.min(100, this.height - pickerY - 5));

            LimitedInventoryScreen screen = (LimitedInventoryScreen) (Object) this;
            RolePickerWidget picker = new RolePickerWidget(pickerX, pickerY, 200, pickerH, entries, sendValue -> {
                UUID selected = GuesserPlayerWidget.selectedPlayer;
                if (selected == null) return;
                ClientPlayNetworking.send(new GuessC2SPacket(selected, sendValue));
                screen.close();
            });

            picker.visible = false;
            watheextended$guesserPicker = picker;
            this.addDrawableChild(picker);

        } catch (Exception ignored) {
            watheextended$guesserPicker = null;
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void watheextended$updateGuesserPickerVisibility(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        RolePickerWidget picker = watheextended$guesserPicker;
        if (picker == null) return;

        boolean playerSelected = GuesserPlayerWidget.selectedPlayer != null;
        picker.visible = playerSelected;

        if (playerSelected && this.getFocused() != picker) {
            this.setFocused(picker);
        }

        for (ClickableWidget bmWidget : watheextended$bodymakerWidgets) {
            bmWidget.visible = !playerSelected;
        }

        if (!playerSelected && !watheextended$guesserPlayerWidgets.isEmpty()) {
            ClickableWidget first = watheextended$guesserPlayerWidgets.get(0);
            if (this.children().contains(first)) {
                MinecraftClient mc = MinecraftClient.getInstance();
                Text label = Text.translatable("gui.watheextended.inventory.guesser.guess");
                int labelX = this.width / 2 - mc.textRenderer.getWidth(label) / 2;
                int labelY = first.getY() - mc.textRenderer.fontHeight - 12;
                context.drawText(mc.textRenderer, label, labelX, labelY, 0x9E2B19, true);
            }
        }
    }
}
