package cat.rezelyn.watheextended.item;

import cat.rezelyn.watheextended.api.GameStatus;
import cat.rezelyn.watheextended.game.TeleportationHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class TriggerRtpItem extends Item {

    public TriggerRtpItem(Settings settings) {
        super(settings);
    }

    @Override
    public Text getName(ItemStack stack) {
        return super.getName(stack).copy().setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF55FF)));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (world.isClient()) {
            return TypedActionResult.pass(stack);
        }
        if (GameStatus.State(world)) {
            return TypedActionResult.fail(stack);
        }

        if (!(user instanceof ServerPlayerEntity serverPlayer)) {
            return TypedActionResult.pass(stack);
        }

        if (!user.isCreative() || !serverPlayer.hasPermissionLevel(2)) {
            return TypedActionResult.fail(stack);
        }

        int count = TeleportationHandler.teleportAll(serverPlayer.getServerWorld());
        if (count == 0) {
            serverPlayer.sendMessage(Text.translatable("command.watheextended.rtp_slot.list_empty"), false);
            return TypedActionResult.fail(stack);
        }

        serverPlayer.sendMessage(Text.translatable("command.watheextended.rtp_slot.teleported", count), false);
        return TypedActionResult.success(stack);
    }
}
