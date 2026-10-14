package cat.rezelyn.watheextended.client;

import cat.rezelyn.watheextended.index.WatheExtendedItems;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

// Need to decouple CopyPositionItem from behavior, hence this class
// CopyPosition is not running actually serverSide events like TPs, but actually client ones, like the clipboard.
public final class CopyPositionHandler {

  private static boolean boxMode;
  private static String firstPos;

  private CopyPositionHandler() {}

  public static boolean canUse(PlayerEntity player) {
    return player != null
        && player.isCreative()
        && player.getMainHandStack().isOf(WatheExtendedItems.COPY_POSITION);
  }

  public static void toggleMode(PlayerEntity player) {
    boxMode = !boxMode;
    firstPos = null;
    String mode = boxMode ? "box" : "point";
    feedback(player, Text.translatable("message.watheextended.copy_position.mode." + mode), 1.0f);
  }

  public static void use(PlayerEntity player) {
    String pos =
        String.format(Locale.ROOT, "%.2f %.2f %.2f", player.getX(), player.getY(), player.getZ());

    if (boxMode && firstPos == null) {
      firstPos = pos;
      feedback(player, Text.translatable("message.watheextended.copy_position.first", pos), 1.0f);
      return;
    }

    String copied = boxMode ? firstPos + " " + pos : pos;
    firstPos = null;
    MinecraftClient.getInstance().keyboard.setClipboard(copied);
    feedback(player, Text.translatable("message.watheextended.copy_position", copied), 1.5f);
  }

  private static void feedback(PlayerEntity player, Text message, float pitch) {
    player.sendMessage(message, true);
    SoundEvent sound = SoundEvents.UI_BUTTON_CLICK.value();
    MinecraftClient.getInstance()
        .getSoundManager()
        .play(PositionedSoundInstance.master(sound, pitch));
  }
}
