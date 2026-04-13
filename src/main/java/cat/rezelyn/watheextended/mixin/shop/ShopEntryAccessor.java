package cat.rezelyn.watheextended.mixin.shop;

import dev.doctor4t.wathe.util.ShopEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShopEntry.class)
public interface ShopEntryAccessor {
    @Mutable
    @Accessor("price")
    void watheextended$setPrice(int price);
}
