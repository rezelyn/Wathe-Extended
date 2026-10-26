package cat.rezelyn.watheextended.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class CopyPositionItem extends Item {

  public CopyPositionItem(Settings settings) {
    super(settings);
  }

  @Override
  public Text getName(ItemStack stack) {
    return super.getName(stack).copy().setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x55AAFF)));
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    ItemStack stack = user.getStackInHand(hand);
    if (!user.isCreative()) {
      return TypedActionResult.fail(stack);
    }
    return TypedActionResult.success(stack, world.isClient());
  }
}
