package cat.rezelyn.watheextended.item;

import cat.rezelyn.watheextended.api.GameStatus;
import cat.rezelyn.watheextended.api.MapVariables;
import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import cat.rezelyn.watheextended.game.TeleportationSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CreateRtpSlotItem extends Item {

    public CreateRtpSlotItem(Settings settings) {
        super(settings);
    }

    @Override
    public Text getName(ItemStack stack) {
        return super.getName(stack).copy().setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFAA00)));
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

        Vec3d pos = serverPlayer.getPos();
        Box readyArea = MapVariables.getReadyArea(world);
        if (readyArea != null && !readyArea.contains(pos.x, pos.y, pos.z)) {
            serverPlayer.sendMessage(Text.translatable("command.watheextended.rtp_slot.outside_ready_area",
                    String.format("%.2f %.2f %.2f", pos.x, pos.y, pos.z)), false);
            return TypedActionResult.fail(stack);
        }

        TeleportationSlot slot = new TeleportationSlot(pos.x, pos.y, pos.z, serverPlayer.getYaw(), serverPlayer.getPitch());
        int id = WatheExtendedWorldComponent.KEY.get(world).addTeleportationSlot(slot);
        serverPlayer.sendMessage(Text.translatable("command.watheextended.rtp_slot.added", id, slot.toString()), false);

        return TypedActionResult.success(stack);
    }
}
