package cat.rezelyn.watheextended.mixin.game;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.math.random.Random;
import org.agmas.harpymodloader.modded_murder.ModdedMurderGameMode;
import org.agmas.harpymodloader.modifiers.Modifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ModdedMurderGameMode.class)
public class ModifierChancesMixin {


    @ModifyVariable(method = "lambda$assignModifiers$1", at = @At(value="INVOKE", target = "Ljava/util/Collections;shuffle(Ljava/util/List;)V"), argsOnly = false, name="specificDesiredRoleCount")
    private static int ModifierCount(int specificDesiredRoleCount, @Local(name = "mod", argsOnly = true) Modifier mod)
    {
        String stringID = mod.identifier().toString();

        int count = specificDesiredRoleCount;

        Random random = Random.create();
        float chance = WatheExtendedServerConfig.getModifierChance(stringID);

        for (int i = 0; i < specificDesiredRoleCount; i++) {
            float randomValue = random.nextFloat();

            if(randomValue > chance)
            {
                count--;
            }
        }

        return count;
    }
}
