package cat.rezelyn.watheextended.mixin.client.kinswathe.bodymaker;

import cat.rezelyn.watheextended.api.hml.ConfigHelper;
import cat.rezelyn.watheextended.api.wathe.RolesDisplay;
import cat.rezelyn.watheextended.client.widget.RolePickerWidget;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.api.WatheRoles;
import dev.doctor4t.wathe.client.gui.screen.ingame.LimitedInventoryScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.BsXinQin.kinswathe.packet.roles.BodymakerC2SPacket;
import org.agmas.harpymodloader.Harpymodloader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Environment(EnvType.CLIENT)
@Mixin(LimitedInventoryScreen.class)
public abstract class BodymakerRolePickerMixin extends Screen {

    @Unique
    private RolePickerWidget watheextended$bodymakerPicker = null;

    protected BodymakerRolePickerMixin() {
        super(Text.empty());
    }

    private static List<RolePickerWidget.RoleEntry> buildBodymakerEntries() {
        List<String> disabled = ConfigHelper.getDisabledRoles();
        Map<String, RolesDisplay.RoleDisplay> display = RolesDisplay.get();

        List<RolePickerWidget.RoleEntry> killers = new ArrayList<>();
        List<RolePickerWidget.RoleEntry> neutrals = new ArrayList<>();
        List<RolePickerWidget.RoleEntry> innocents = new ArrayList<>();

        for (Role role : WatheRoles.ROLES) {
            if (role == null || role.identifier() == null) continue;
            if (Harpymodloader.SPECIAL_ROLES.contains(role)) continue;
            String id = role.identifier().toString();
            if (disabled.contains(id)) continue;

            RolesDisplay.RoleDisplay d = display.get(id);
            Text label = d != null ? d.display() : Text.literal(RolesDisplay.localName(id));
            int color = d != null ? d.color() : 0xFFFFFF;
            RolesDisplay.Side side = d != null ? d.side() : RolesDisplay.Side.NEUTRAL;

            var entry = new RolePickerWidget.RoleEntry(label, 0xFF000000 | color, id);
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
    private void watheextended$bodymakerPickerInit(CallbackInfo ci) {
        watheextended$bodymakerPicker = null;
        if (!FabricLoader.getInstance().isModLoaded("kinswathe")) return;

        try {
            Class<?> brwClass = Class.forName("org.BsXinQin.kinswathe.client.roles.bodymaker.BodymakerRoleWidget");
            Class<?> bpwClass = Class.forName("org.BsXinQin.kinswathe.client.roles.bodymaker.BodymakerPlayerWidget");
            Class<?> bdwClass = Class.forName("org.BsXinQin.kinswathe.client.roles.bodymaker.BodymakerDeathReasonWidget");

            Element roleWidget = null;
            boolean isBodymakerUI = false;

            for (Element child : List.copyOf(this.children())) {
                if (brwClass.isInstance(child)) {
                    roleWidget = child;
                    isBodymakerUI = true;
                } else if (bpwClass.isInstance(child) || bdwClass.isInstance(child)) {
                    isBodymakerUI = true;
                }
            }

            if (!isBodymakerUI) return;

            if (roleWidget != null && FabricLoader.getInstance().isModLoaded("noellesroles")) {
                try {
                    Class<?> gpwClass = Class.forName("org.agmas.noellesroles.client.ui.guesser.GuesserPlayerWidget");
                    Class<?> grwClass = Class.forName("org.agmas.noellesroles.client.ui.guesser.GuesserRoleWidget");
                    for (Element child : List.copyOf(this.children())) {
                        if (gpwClass.isInstance(child) || grwClass.isInstance(child)) {
                            this.remove(child);
                        }
                    }
                    Field selectedPlayerField = gpwClass.getDeclaredField("selectedPlayer");
                    selectedPlayerField.setAccessible(true);
                    selectedPlayerField.set(null, null);
                } catch (Exception ignored) {
                }
            }

            if (roleWidget == null) return;

            Field uuidField = brwClass.getDeclaredField("targetPlayerUuid");
            uuidField.setAccessible(true);
            UUID targetPlayerUuid = (UUID) uuidField.get(roleWidget);

            Field reasonField = brwClass.getDeclaredField("deathReason");
            reasonField.setAccessible(true);
            String deathReason = (String) reasonField.get(roleWidget);

            this.remove(roleWidget);

            int pickerX = this.width / 2 - 100;
            int pickerY = (this.height - 32) / 2 + 32 + 20;
            int pickerH = Math.max(50, Math.min(100, this.height - pickerY - 5));

            final UUID finalUUID = targetPlayerUuid;
            final String finalReason = deathReason;
            LimitedInventoryScreen screen = (LimitedInventoryScreen) (Object) this;

            RolePickerWidget picker = new RolePickerWidget(
                    pickerX, pickerY, 200, pickerH, buildBodymakerEntries(),
                    sendValue -> {
                        ClientPlayNetworking.send(
                                new BodymakerC2SPacket(finalUUID, finalReason, sendValue));
                        screen.close();
                    });

            this.addDrawableChild(picker);
            this.setFocused(picker);
            watheextended$bodymakerPicker = picker;

        } catch (Exception ignored) {
        }
    }
}
