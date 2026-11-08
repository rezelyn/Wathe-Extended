package cat.rezelyn.watheextended.mixin.game;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.agmas.harpymodloader.modded_murder.ModdedMurderGameMode;
import org.spongepowered.asm.mixin.Mixin;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

@Mixin(ModdedMurderGameMode.class)
public class RoleChancesMixin {

    @WrapMethod(method = "findAndAssignPlayers")
    private static int watheExtended$findAndAssignPlayers (int desiredRoleCount, Role role, List<ServerPlayerEntity> players, GameWorldComponent gameWorldComponent, World world, Operation<Integer> original)
    {
        String stringID = role.identifier().toString();
        if(WatheExtendedServerConfig.roleChances.containsKey(stringID))
        {
            int finalRoleCount = desiredRoleCount;

            Random random = Random.create();
            float chance = WatheExtendedServerConfig.roleChances.get(stringID);

            for (int i = 0; i < desiredRoleCount; i++) {
                float randomValue = random.nextFloat();

                if(randomValue > chance)
                {
                    finalRoleCount--;
                }
            }

            return original.call(finalRoleCount, role, players, gameWorldComponent, world);
        }
        else {
            return original.call(desiredRoleCount, role, players, gameWorldComponent, world);
        }
    }

}
