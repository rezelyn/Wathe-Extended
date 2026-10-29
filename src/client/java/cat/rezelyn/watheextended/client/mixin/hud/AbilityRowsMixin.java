package cat.rezelyn.watheextended.client.mixin.hud;

import cat.rezelyn.watheextended.api.AssignedRole;
import cat.rezelyn.watheextended.client.widget.PlayerRowLayout;
import cat.rezelyn.watheextended.client.widget.PlayerRowPagination;
import cat.rezelyn.watheextended.client.widget.RolePickerWidget;
import cat.rezelyn.watheextended.compat.AddonModCompat;
import dev.doctor4t.wathe.client.gui.screen.ingame.LimitedInventoryScreen;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

@Environment(EnvType.CLIENT)
@Mixin(value = LimitedInventoryScreen.class, priority = 2000)
public abstract class AbilityRowsMixin extends Screen {

  @Unique private List<ClickableWidget> watheextended$morphlingWidgets = List.of();
  @Unique private List<ClickableWidget> watheextended$swapperWidgets = List.of();
  @Unique private List<ClickableWidget> watheextended$voodooWidgets = List.of();
  @Unique private List<ClickableWidget> watheextended$judgeWidgets = List.of();
  @Unique private List<ClickableWidget> watheextended$bodymakerWidgets = List.of();
  @Unique private List<ClickableWidget> watheextended$bodymakerReasonWidgets = List.of();
  @Unique private List<ClickableWidget> watheextended$guesserWidgets = List.of();
  @Unique private PlayerRowPagination watheextended$pagination;

  protected AbilityRowsMixin() {
    super(Text.empty());
  }

  @Inject(method = "init", at = @At("TAIL"))
  private void watheextended$setUpAbilityRows(CallbackInfo ci) {
    watheextended$createPagination();
    watheextended$applyLayout();
  }

  @Inject(method = "render", at = @At("HEAD"))
  private void watheextended$onRender(
      DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
    watheextended$ensurePagination();
    watheextended$applyLayout();
    watheextended$drawLabels(context);
  }

  @Inject(method = "render", at = @At("TAIL"))
  private void watheextended$drawPageLabel(
      DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
    if (watheextended$pagination == null || watheextended$isRolePickerOpen()) return;
    watheextended$pagination.setLabelColor(watheextended$roleColor());
    watheextended$pagination.renderPageLabel(context);
  }

  @Unique
  private void watheextended$createPagination() {
    watheextended$pagination = new PlayerRowPagination();
    this.addDrawableChild(watheextended$pagination.previousButton());
    this.addDrawableChild(watheextended$pagination.nextButton());
  }

  @Unique
  private void watheextended$ensurePagination() {
    if (watheextended$pagination == null) watheextended$createPagination();
  }

  @Unique
  private void watheextended$applyLayout() {
    watheextended$ensurePagination();
    PlayerRowPagination pagination = watheextended$pagination;

    watheextended$morphlingWidgets = watheextended$collect(AddonModCompat.MORPHLING_WIDGET);
    watheextended$swapperWidgets = watheextended$collect(AddonModCompat.SWAPPER_WIDGET);
    watheextended$voodooWidgets = watheextended$collect(AddonModCompat.VOODOO_WIDGET);
    watheextended$guesserWidgets = watheextended$collect(AddonModCompat.GUESSER_WIDGET);
    watheextended$judgeWidgets = watheextended$collect(AddonModCompat.JUDGE_WIDGET);
    watheextended$bodymakerWidgets = watheextended$collect(AddonModCompat.BODYMAKER_WIDGET);
    watheextended$bodymakerReasonWidgets =
        watheextended$collect(AddonModCompat.BODYMAKER_REASON_WIDGET);

    // rows that share the same screen position and the same page control
    List<List<ClickableWidget>> playerRows =
        List.of(
            watheextended$morphlingWidgets,
            watheextended$swapperWidgets,
            watheextended$voodooWidgets,
            watheextended$judgeWidgets,
            watheextended$bodymakerWidgets);

    int rolePlayerCount = 0;
    for (List<ClickableWidget> row : playerRows) {
      rolePlayerCount = Math.max(rolePlayerCount, row.size());
    }
    int reasonEntryCount =
        PlayerRowPagination.pageEligibleCount(watheextended$bodymakerReasonWidgets);
    int playerCount = Math.max(rolePlayerCount, reasonEntryCount);
    pagination.setEntryCount(playerCount, this.width);

    boolean hasVisibleGuesserRow =
        watheextended$guesserWidgets.stream().anyMatch(widget -> widget.visible);
    int guesserGap = PlayerRowLayout.secondaryRowGap();
    int guesserSpace =
        hasVisibleGuesserRow
            ? guesserGap
                + PlayerRowLayout.rowGroupHeight(watheextended$guesserWidgets.size(), true)
            : 0;
    boolean hasVisibleAbilityRow =
        watheextended$isPlayerRowVisible() || reasonEntryCount > 0;
    int y =
        PlayerRowLayout.firstRowY(
            this.height, (this.height - 32) / 2 + 60, hasVisibleAbilityRow, guesserSpace);

    for (List<ClickableWidget> row : playerRows) {
      pagination.arrangeAt(row, this.width, y);
    }
    pagination.arrangeAt(watheextended$bodymakerReasonWidgets, this.width, y);

    boolean rolePickerOpen = watheextended$isRolePickerOpen();
    pagination.setControlsVisible(
        hasVisibleAbilityRow && !rolePickerOpen);
    pagination.positionControls(this.width, y);

    if (rolePickerOpen) {
      for (List<ClickableWidget> row : playerRows) {
        for (ClickableWidget widget : row) {
          PlayerRowPagination.setPageVisibility(widget, false);
        }
      }
      for (ClickableWidget widget : watheextended$bodymakerReasonWidgets) {
        PlayerRowPagination.setPageVisibility(widget, false);
      }
      for (ClickableWidget widget : watheextended$guesserWidgets) {
        PlayerRowPagination.setPageVisibility(widget, false);
      }
      for (Element child : this.children()) {
        if (child instanceof ClickableWidget widget && PlayerRowPagination.isPageButton(widget)) {
          widget.visible = false;
        }
      }
    }
  }

  @Unique
  private boolean watheextended$isPlayerRowVisible() {
    return PlayerRowPagination.pageEligibleCount(watheextended$morphlingWidgets) > 0
        || PlayerRowPagination.pageEligibleCount(watheextended$swapperWidgets) > 0
        || PlayerRowPagination.pageEligibleCount(watheextended$voodooWidgets) > 0
        || PlayerRowPagination.pageEligibleCount(watheextended$judgeWidgets) > 0
        || PlayerRowPagination.pageEligibleCount(watheextended$bodymakerWidgets) > 0;
  }

  @Unique
  private boolean watheextended$isRolePickerOpen() {
    return RolePickerWidget.isOpen(this) || AddonModCompat.isGuesserPickerOpen();
  }

  @Unique
  private List<ClickableWidget> watheextended$collect(Class<?> type) {
    if (type == null) return List.of();
    List<ClickableWidget> widgets = new ArrayList<>();
    for (Element element : this.children()) {
      if (type.isInstance(element) && element instanceof ClickableWidget widget) {
        widgets.add(widget);
      }
    }
    return widgets;
  }

  @Unique
  private static ClickableWidget watheextended$firstVisible(List<ClickableWidget> widgets) {
    for (ClickableWidget widget : widgets) {
      if (widget.visible) return widget;
    }
    return null;
  }

  @Unique
  private int watheextended$roleColor() {
    AssignedRole.Assigned role =
        this.client.player == null ? null : AssignedRole.getRole(this.client.player);
    return role == null ? 0xFFFFFF : role.color();
  }

  @Unique
  private void watheextended$drawLabels(DrawContext context) {
    if (watheextended$isRolePickerOpen()) return;

    int color = watheextended$roleColor();

    watheextended$drawLabel(
        context,
        watheextended$morphlingWidgets,
        "gui.watheextended.inventory.morphling.morph",
        color);
    watheextended$drawLabel(
        context, watheextended$swapperWidgets, "gui.watheextended.inventory.swapper.swap", color);
    watheextended$drawLabel(
        context, watheextended$judgeWidgets, "gui.watheextended.inventory.judge.judgement", color);
    watheextended$drawLabel(
        context,
        watheextended$bodymakerWidgets,
        "gui.watheextended.inventory.bodymaker.select",
        color);
    watheextended$drawLabel(
        context,
        watheextended$bodymakerReasonWidgets,
        "gui.watheextended.inventory.bodymaker.reason",
        color);
    watheextended$drawLabel(
        context,
        watheextended$voodooWidgets,
        AddonModCompat.voodooHasTarget(this.client.player)
            ? "hud.voodoo.player_deaths_only"
            : "gui.watheextended.inventory.voodoo.doll",
        color);
  }

  /** Draws a centered label above the first visible widget of the row, if there is one */
  @Unique
  private void watheextended$drawLabel(
      DrawContext context, List<ClickableWidget> widgets, String translationKey, int color) {
    ClickableWidget first = watheextended$firstVisible(widgets);
    if (first == null) return;

    Text label = Text.translatable(translationKey);
    int labelX = this.width / 2 - this.textRenderer.getWidth(label) / 2;
    int labelY = first.getY() - this.textRenderer.fontHeight - 12;
    context.drawText(this.textRenderer, label, labelX, labelY, color, true);
  }
}
