package cat.rezelyn.watheextended.item;

import cat.rezelyn.watheextended.api.cca.GameStatus;
import dev.doctor4t.wathe.cca.MapVariablesWorldComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

public class TeleportToSceneryItem extends Item {

    public TeleportToSceneryItem(Settings settings) {
        super(settings);
    }

    @Override
    public Text getName(ItemStack stack) {
        return super.getName(stack).copy().setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x55AAFF)));
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

        MapVariablesWorldComponent.PosWithOrientation dest = MapVariablesWorldComponent.KEY.get(world).getSpectatorSpawnPos();

        TeleportTarget target = new TeleportTarget(serverPlayer.getServerWorld(), dest.pos, net.minecraft.util.math.Vec3d.ZERO, dest.yaw, dest.pitch, TeleportTarget.NO_OP);
        serverPlayer.teleportTo(target);

        return TypedActionResult.success(stack);
    }
}
