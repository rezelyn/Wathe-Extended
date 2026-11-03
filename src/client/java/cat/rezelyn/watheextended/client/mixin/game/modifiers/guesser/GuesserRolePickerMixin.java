package cat.rezelyn.watheextended.client.mixin.game.modifiers.guesser;

import cat.rezelyn.watheextended.api.ModifiersDisplay;
import cat.rezelyn.watheextended.api.RolesDisplay;
import cat.rezelyn.watheextended.client.widget.PlayerRowLayout;
import cat.rezelyn.watheextended.client.widget.PlayerRowPagination;
import cat.rezelyn.watheextended.client.widget.RolePickerWidget;
import cat.rezelyn.watheextended.compat.AddonModCompat;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.api.WatheRoles;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.gui.screen.ingame.LimitedInventoryScreen;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
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

@Environment(EnvType.CLIENT)
@Mixin(LimitedInventoryScreen.class)
public abstract class GuesserRolePickerMixin extends Screen {

  private RolePickerWidget watheextended$guesserPicker = null;
  private List<ClickableWidget> watheextended$bodymakerWidgets = new ArrayList<>();
  private List<ClickableWidget> watheextended$bodymakerReasonWidgets = new ArrayList<>();
  private List<ClickableWidget> watheextended$morphlingWidgets = new ArrayList<>();
  private List<ClickableWidget> watheextended$swapperWidgets = new ArrayList<>();
  private List<ClickableWidget> watheextended$voodooWidgets = new ArrayList<>();
  private List<ClickableWidget> watheextended$judgeWidgets = new ArrayList<>();
  private List<ClickableWidget> watheextended$guesserPlayerWidgets = new ArrayList<>();
  private PlayerRowPagination watheextended$guesserPageControl;
  private int watheextended$guesserPlayersY;

  protected GuesserRolePickerMixin() {
    super(Text.empty());
  }

  @Inject(method = "init", at = @At("HEAD"))
  private void watheextended$resetGuesserPagination(CallbackInfo ci) {
    watheextended$guesserPageControl = null;
    watheextended$guesserPlayersY = 0;
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
      Text label = d != null ? d.display() : Text.literal(RolesDisplay.prettyName(id));
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
    watheextended$bodymakerReasonWidgets = new ArrayList<>();
    watheextended$guesserPlayerWidgets = new ArrayList<>();
    watheextended$morphlingWidgets = new ArrayList<>();
    watheextended$swapperWidgets = new ArrayList<>();
    watheextended$voodooWidgets = new ArrayList<>();
    watheextended$judgeWidgets = new ArrayList<>();
    if (!FabricLoader.getInstance().isModLoaded("noellesroles")) return;

    try {
      Class<?> guesserRole =
          Class.forName("org.agmas.noellesroles.client.ui.guesser.GuesserRoleWidget");
      Class<?> guesserPicker =
          Class.forName("org.agmas.noellesroles.client.ui.guesser.GuesserPlayerWidget");

      Element roleWidget = null;
      boolean isGuesserUI = false;

      for (Element child : List.copyOf(this.children())) {
        if (guesserRole.isInstance(child)) {
          roleWidget = child;
          isGuesserUI = true;
        } else if (guesserPicker.isInstance(child)) {
          isGuesserUI = true;
        }
      }

      if (!isGuesserUI) return;
      GuesserPlayerWidget.selectedPlayer = null;

      if (FabricLoader.getInstance().isModLoaded("kinswathe")) {
        try {
          Class<?> bodymakerPicker =
              Class.forName("org.BsXinQin.kinswathe.client.roles.bodymaker.BodymakerPlayerWidget");
          Class<?> bodymakerReason =
              Class.forName(
                  "org.BsXinQin.kinswathe.client.roles.bodymaker.BodymakerDeathReasonWidget");
          for (Element child : this.children()) {
            if (bodymakerPicker.isInstance(child) && child instanceof ClickableWidget widget) {
              watheextended$bodymakerWidgets.add(widget);
            } else if (bodymakerReason.isInstance(child)
                && child instanceof ClickableWidget widget) {
              watheextended$bodymakerReasonWidgets.add(widget);
            }
          }
        } catch (Exception ignored) {
        }
        try {
          Class<?> judgePicker =
              Class.forName("org.BsXinQin.kinswathe.client.roles.judge.JudgePlayerWidget");
          for (Element child : this.children()) {
            if (judgePicker.isInstance(child) && child instanceof ClickableWidget widget) {
              watheextended$judgeWidgets.add(widget);
            }
          }
        } catch (Exception ignored) {
        }
      }

      try {
        Class<?> morphlingPicker =
            Class.forName("org.agmas.noellesroles.client.ui.MorphlingPlayerWidget");
        for (Element child : this.children()) {
          if (morphlingPicker.isInstance(child) && child instanceof ClickableWidget widget) {
            watheextended$morphlingWidgets.add(widget);
          }
        }
      } catch (Exception ignored) {
      }

      try {
        Class<?> swapperPicker =
            Class.forName("org.agmas.noellesroles.client.ui.SwapperPlayerWidget");
        for (Element child : this.children()) {
          if (swapperPicker.isInstance(child) && child instanceof ClickableWidget widget) {
            watheextended$swapperWidgets.add(widget);
          }
        }
      } catch (Exception ignored) {
      }

      try {
        Class<?> voodooPicker =
            Class.forName("org.agmas.noellesroles.client.ui.VoodooPlayerWidget");
        for (Element child : this.children()) {
          if (voodooPicker.isInstance(child) && child instanceof ClickableWidget widget) {
            watheextended$voodooWidgets.add(widget);
          }
        }
      } catch (Exception ignored) {
      }

      for (Element child : List.copyOf(this.children())) {
        if (guesserPicker.isInstance(child) && child instanceof ClickableWidget widget) {
          boolean isMimic = false;
          try {
            Field targetUUIDField = guesserPicker.getField("targetUUID");
            UUID targetUUID = (UUID) targetUUIDField.get(widget);
            ClientWorld world = MinecraftClient.getInstance().world;
            if (world != null && targetUUID != null) {
              PlayerEntity targetPlayer = world.getPlayerByUuid(targetUUID);
              if (targetPlayer != null) {
                isMimic =
                    GameWorldComponent.KEY.get(world).isRole(targetPlayer, Noellesroles.MIMIC);
              }
            }
          } catch (Exception ignored) {
          }
          if (isMimic) {
            this.remove(widget);
          } else {
            PlayerRowPagination.resetWidgetVisibility(widget, true);
            watheextended$guesserPlayerWidgets.add(widget);
          }
        }
      }

      if (!watheextended$guesserPlayerWidgets.isEmpty()) {
        watheextended$guesserPlayersY = (this.height - 32) / 2 + 60;
        watheextended$guesserPageControl = new PlayerRowPagination();
        this.addDrawableChild(watheextended$guesserPageControl.previousButton());
        this.addDrawableChild(watheextended$guesserPageControl.nextButton());
        watheextended$layoutGuesserPlayers(false);
      }

      if (roleWidget == null) return;

      this.remove(roleWidget);

      MinecraftClient client = MinecraftClient.getInstance();
      boolean guesserIsInnocent =
          client.player != null
              && GameWorldComponent.KEY.get(client.player.getWorld()).isInnocent(client.player);

      List<RolePickerWidget.RoleEntry> entries = buildGuesserEntries(guesserIsInnocent);

      int pickerX = this.width / 2 - 100;
      int pickerY = (this.height - 32) / 2 + 32 + 20;
      int pickerH = Math.max(50, Math.min(100, this.height - pickerY - 5));

      LimitedInventoryScreen screen = (LimitedInventoryScreen) (Object) this;
      RolePickerWidget picker =
          new RolePickerWidget(
              pickerX,
              pickerY,
              200,
              pickerH,
              entries,
              sendValue -> {
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
  private void watheextended$updateGuesserPickerVisibility(
      DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
    boolean playerSelected = GuesserPlayerWidget.selectedPlayer != null;
    boolean rolePickerOpen =
        RolePickerWidget.isOpen(this) || AddonModCompat.isGuesserPickerOpen();
    watheextended$layoutGuesserPlayers(rolePickerOpen);

    RolePickerWidget picker = watheextended$guesserPicker;
    if (picker == null) return;

    picker.visible = playerSelected;

    if (playerSelected && this.getFocused() != picker) {
      this.setFocused(picker);
    }

    for (List<ClickableWidget> row : List.of(
        watheextended$bodymakerWidgets,
        watheextended$bodymakerReasonWidgets,
        watheextended$morphlingWidgets,
        watheextended$swapperWidgets,
        watheextended$voodooWidgets,
        watheextended$judgeWidgets)) {
      for (ClickableWidget widget : row) {
        PlayerRowPagination.setPageVisibility(widget, !rolePickerOpen);
      }
    }

    for (Element child : this.children()) {
      if (child instanceof ClickableWidget widget
          && PlayerRowPagination.isPageButton(widget)
          && rolePickerOpen) {
        widget.visible = false;
      }
    }

    if (!rolePickerOpen && !watheextended$guesserPlayerWidgets.isEmpty()) {
      ClickableWidget widget =
          watheextended$guesserPlayerWidgets.stream()
              .filter(element -> element.visible)
              .findFirst()
              .orElse(null);
      if (widget != null && this.children().contains(widget)) {
        MinecraftClient client = MinecraftClient.getInstance();
        Text label = Text.translatable("gui.watheextended.inventory.guesser.guess");
        int labelX = this.width / 2 - client.textRenderer.getWidth(label) / 2;
        int labelY = widget.getY() - client.textRenderer.fontHeight - 12;
        context.drawText(client.textRenderer, label, labelX, labelY, 0x9E2B19, true);
      }
    }
  }

  @Inject(method = "render", at = @At("TAIL"))
  private void watheextended$drawGuesserPageLabel(
      DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
    if (watheextended$guesserPageControl != null
        && !RolePickerWidget.isOpen(this)
        && !AddonModCompat.isGuesserPickerOpen()) {
      ModifiersDisplay.ModifierDisplay guesserDisplay =
          ModifiersDisplay.get().get(Noellesroles.GUESSER.identifier.toString());
      if (guesserDisplay != null) {
        watheextended$guesserPageControl.setLabelColor(guesserDisplay.color());
      }
      watheextended$guesserPageControl.renderPageLabel(context);
    }
  }

  @Unique
  private void watheextended$layoutGuesserPlayers(boolean rolePickerOpen) {
    if (watheextended$guesserPlayerWidgets.isEmpty()
        || watheextended$guesserPageControl == null) {
      return;
    }

    PlayerRowPagination controls = watheextended$guesserPageControl;
    controls.setEntryCount(watheextended$guesserPlayerWidgets.size(), this.width);
    int roleRowsBottom = watheextended$roleRowsBottom();
    int secondaryRowGap = PlayerRowLayout.secondaryRowGap();
    int secondaryRowSpace =
        secondaryRowGap
            + PlayerRowLayout.rowGroupHeight(watheextended$guesserPlayerWidgets.size(), true);
    int fallbackAnchorY =
        PlayerRowLayout.firstRowY(
            this.height, watheextended$guesserPlayersY, false, secondaryRowSpace);
    int fallbackY =
        fallbackAnchorY + PlayerRowLayout.rowGroupHeight(1, false) + secondaryRowGap;
    int preferredY = PlayerRowLayout.secondaryRowY(roleRowsBottom, fallbackY);
    int y = Math.max(4, preferredY);
    controls.arrangeAt(watheextended$guesserPlayerWidgets, this.width, y);
    controls.positionControls(this.width, y);
    controls.setControlsVisible(!rolePickerOpen);
    for (ClickableWidget widget : watheextended$guesserPlayerWidgets) {
      PlayerRowPagination.setPageVisibility(widget, !rolePickerOpen);
    }
  }

  @Unique
  private int watheextended$roleRowsBottom() {
    int bottom = -1;
    List<List<ClickableWidget>> roleRows =
        List.of(
            watheextended$bodymakerWidgets,
            watheextended$bodymakerReasonWidgets,
            watheextended$morphlingWidgets,
            watheextended$swapperWidgets,
            watheextended$voodooWidgets,
            watheextended$judgeWidgets);
    for (List<ClickableWidget> row : roleRows) {
      for (ClickableWidget widget : row) {
        if (widget.visible && this.children().contains(widget)) {
          bottom = Math.max(bottom, widget.getY() + widget.getHeight());
        }
      }
    }
    for (Element child : this.children()) {
      if (child instanceof ClickableWidget widget
          && widget.visible
          && PlayerRowPagination.isPageButton(widget)
          && !PlayerRowPagination.belongsTo(widget, watheextended$guesserPageControl)) {
        bottom = Math.max(bottom, widget.getY() + widget.getHeight());
      }
    }
    return bottom;
  }
}
