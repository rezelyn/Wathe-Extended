package cat.rezelyn.watheextended.item;

import cat.rezelyn.watheextended.api.GameStatus;
import cat.rezelyn.watheextended.game.FakePlayerHandler;
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

public class RemovePlayersItem extends Item {

    public RemovePlayersItem(Settings settings) {
        super(settings);
    }

    @Override
    public Text getName(ItemStack stack) {
        return super.getName(stack).copy().setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xB4202A)));
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

        if (FakePlayerHandler.removeAll(serverPlayer.getServerWorld()) == 0) {
            serverPlayer.sendMessage(Text.translatable("message.watheextended.fake_players.unavailable"), true);
            return TypedActionResult.fail(stack);
        }

        serverPlayer.sendMessage(Text.translatable("message.watheextended.fake_players.removed"), true);
        return TypedActionResult.success(stack);
    }
}
