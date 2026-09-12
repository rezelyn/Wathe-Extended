package cat.rezelyn.watheextended.game;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;
import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerMoodComponent;
import dev.doctor4t.wathe.cca.PlayerVariablesComponent;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.index.WatheItems;
import dev.doctor4t.wathe.index.WatheSounds;
import dev.doctor4t.wathe.index.tag.WatheItemTags;
import dev.doctor4t.wathe.network.GunDropPayload;
import dev.doctor4t.wathe.network.GunShootPayload;
import dev.doctor4t.wathe.network.ShootMuzzleS2CPayload;
import dev.doctor4t.wathe.util.Scheduler;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;

public final class ShooterPunishment {

    private ShooterPunishment() {}

    private static void dropAndBlockRevolver(ServerPlayerEntity shooter, boolean permanentBlock) {
        ItemStack revolverStack = ItemStack.EMPTY;
        for (int slot = 0; slot < shooter.getInventory().size(); slot++) {
            ItemStack stack = shooter.getInventory().getStack(slot);
            if (stack.isOf(WatheItems.REVOLVER)) {
                revolverStack = stack.copy();
                shooter.getInventory().removeOne(stack);
                break;
            }
        }

        if (!revolverStack.isEmpty()) {
            shooter.dropItem(revolverStack, false, true);
        }

        ServerPlayNetworking.send(shooter, new GunDropPayload());
        PlayerMoodComponent.KEY.get(shooter).setMood(0);

        WatheExtendedWorldComponent component = WatheExtendedWorldComponent.KEY.get(shooter.getWorld());
        component.blockRevolverPickup(shooter.getUuid());
        if (!permanentBlock) {
            Scheduler.schedule(() -> component.unblockRevolverPickup(shooter.getUuid()), 200);
        }
    }

    public static boolean shouldOverride(String mode) {
        return !"DEFAULT".equals(mode); // DEFAULT = let Wathe handle it normally (drop the revolver and kill the target)
    }

    // only applies when an innocent player shoots another innocent
    // KILLER and NEUTRAL sides are not affected by any custom logic (handled by Wathe normally)
    public static boolean shouldHandle(ServerPlayerEntity player, GunShootPayload payload) {
        if (!shouldOverride(WatheExtendedServerConfig.getShootInnocentPunishmentMode())) {
            return false;
        }
        GameWorldComponent game = GameWorldComponent.KEY.get(player.getWorld());
        if (!game.isInnocent(player)) {
            return false;
        }
        Entity entity = player.getWorld().getEntityById(payload.target());
        if (!(entity instanceof ServerPlayerEntity target) || target.squaredDistanceTo(player) > 65.0 * 65.0) {
            return false;
        }
        return game.isInnocent(target);
    }

    public static void handle(ServerPlayerEntity player, GunShootPayload payload, ServerPlayNetworking.Context context) {
        String mode = WatheExtendedServerConfig.getShootInnocentPunishmentMode();

        ItemStack mainHandStack = player.getMainHandStack();
        if (!mainHandStack.isIn(WatheItemTags.GUNS)) {
            return;
        }

        Entity entity = player.getWorld().getEntityById(payload.target());
        if (!(entity instanceof ServerPlayerEntity target) || target.squaredDistanceTo(player) > 65.0 * 65.0) {
            return;
        }

        GameWorldComponent game = GameWorldComponent.KEY.get(player.getWorld());
        if (!game.isInnocent(player)) {
            return;
        }
        if (!player.isSpectator() && mainHandStack.isOf(WatheItems.REVOLVER)) {
            PlayerVariablesComponent playerVariablesComponent = PlayerVariablesComponent.KEY.get(player);
            if (game.isInnocent(target)) {
                if ("DEFAULT".equals(mode)) {
                    dropAndBlockRevolver(player, false);
                    GameFunctions.killPlayer(target, true, player, GameConstants.DeathReasons.GUN);
                } else if ("PREVENT_PICKUP".equals(mode)) {
                    dropAndBlockRevolver(player, true);
                    GameFunctions.killPlayer(target, true, player, GameConstants.DeathReasons.GUN);
                } else if ("KILL_SHOOTER".equals(mode)) {
                    dropAndBlockRevolver(player, false);
                    GameFunctions.killPlayer(player, true, player, GameConstants.DeathReasons.GUN);
                    playerVariablesComponent.setInnocentKills(0);
                } else if ("KILL_BOTH".equals(mode)) {
                    dropAndBlockRevolver(player, false);
                    GameFunctions.killPlayer(player, true, player, GameConstants.DeathReasons.GUN);
                    GameFunctions.killPlayer(target, true, player, GameConstants.DeathReasons.GUN);
                    playerVariablesComponent.setInnocentKills(0);
                }
            } else {
                playerVariablesComponent.setInnocentKills(0);
            }
        }

        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), WatheSounds.ITEM_REVOLVER_SHOOT, SoundCategory.PLAYERS, 5f, 1f + player.getRandom().nextFloat() * .1f - .05f);
        for (ServerPlayerEntity tracking : PlayerLookup.tracking(player)) {
            ServerPlayNetworking.send(tracking, new ShootMuzzleS2CPayload(player.getUuid().toString()));
        }
        ServerPlayNetworking.send(player, new ShootMuzzleS2CPayload(player.getUuid().toString()));
        if (!player.isSpectator()) {
            player.getItemCooldownManager().set(mainHandStack.getItem(), GameConstants.ITEM_COOLDOWNS.getOrDefault(mainHandStack.getItem(), 0));
        }
    }
}