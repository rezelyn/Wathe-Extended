package cat.rezelyn.watheextended.mixin.balance;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.game.gamemode.MurderGameMode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

@Mixin(MurderGameMode.class)
public abstract class AdjustPassiveIncomeMixin{
  private double getDistanceToClosestInnocentPlayer(ServerPlayerEntity target){
    World world = target.getWorld();
    List<? extends PlayerEntity> players = world.getPlayers();
    GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(world);

    double closest = 999;
    for(PlayerEntity candidate : players){
      if(candidate == target)
        continue;

      Role role = gameWorldComponent.getRole(candidate);
      if(role != null && role.isInnocent())
        continue;
      double distance = target.distanceTo(candidate);
      if(distance < closest)
        closest = distance;
    }
    return closest;
  }

  private boolean playerIsInnocent(ServerPlayerEntity player){
    GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(player.getWorld());
    Role role = gameWorldComponent.getRole(player);
    return role.isInnocent();
  }

  @WrapOperation(
    method = "tickServerGameLoop",
    at = @At(value = "INVOKE",
      target = "Ldev/doctor4t/wathe/cca/PlayerShopComponent;addToBalance(I)V")
  )
  private void useDynamicIncome(PlayerShopComponent shop, int baseIncome, Operation<Void> mark,
  @Local ServerPlayerEntity player){
    baseIncome = WatheExtendedServerConfig.getBasePassiveIncome();

    // Granting a way to boost player income if you're innocent is broken
    // thus not allowed
    if(!WatheExtendedServerConfig.getAdjustPassiveIncome() || playerIsInnocent(player)){
      mark.call(shop, baseIncome);
      return;
    }

    double MAX_DISTANCE = WatheExtendedServerConfig.getMaxPassiveIncomeDistance();

    // linear scale where being on top of another player yields double income
    // and being at a distance greater than MAX_DISTANCE is 0 income
    int newAmount = (int) Math.round((double) 
      baseIncome * (1 - getDistanceToClosestInnocentPlayer(player) / MAX_DISTANCE) * 2
    );

    int MINIMUM_INCOME = WatheExtendedServerConfig.getMinPassiveIncome();
    if(newAmount < MINIMUM_INCOME)
      newAmount = MINIMUM_INCOME;


    mark.call(shop, newAmount);
  }
}
