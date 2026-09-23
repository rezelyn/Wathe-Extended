package cat.rezelyn.watheextended.mixin.fix;

import dev.doctor4t.wathe.api.Role;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

// fix: Dreamer conversion should choose randomly from the enabled killer roles
@Mixin(targets = "org.BsXinQin.kinswathe.roles.dreamer.DreamerKillerComponent", remap = false)
public abstract class DreamerKillerConversionMixin {

    @Redirect(method = "triggerBecomeKiller", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;getFirst()Ljava/lang/Object;", ordinal = 0), require = 0, remap = false)
    private Object watheextended$chooseRandomEnabledKiller(ArrayList<Role> enabledKillerRoles) {
        if (enabledKillerRoles.size() <= 1) return enabledKillerRoles.getFirst();

        int selectedIndex = ThreadLocalRandom.current().nextInt(enabledKillerRoles.size());
        Role selected = enabledKillerRoles.get(selectedIndex);
        
        enabledKillerRoles.set(0, selected);
        return selected;
    }
}
