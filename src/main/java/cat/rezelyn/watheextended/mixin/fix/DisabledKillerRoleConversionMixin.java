package cat.rezelyn.watheextended.mixin.fix;

import cat.rezelyn.watheextended.api.config.hml.ConfigHelper;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.api.WatheRoles;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.agmas.harpymodloader.events.ModdedRoleAssigned;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// fix: player turning into a disabled role when their conversion condition is achieved
@Mixin(GameFunctions.class)
public class DisabledKillerRoleConversionMixin {

    @Inject(method = "killPlayer(Lnet/minecraft/entity/player/PlayerEntity;ZLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Identifier;)V", at = @At("RETURN"))
    private static void watheextended$fixDisabledKillerRoleConversion(PlayerEntity victim, boolean spawnBody, @Nullable PlayerEntity killer, Identifier deathReason, CallbackInfo ci) {
        if (!(killer instanceof ServerPlayerEntity serverKiller)) return;

        GameWorldComponent component;
        try {
            component = GameWorldComponent.KEY.get(serverKiller.getServerWorld());
        } catch (Throwable ignored) {
            return;
        }

        if (component == null || !component.isRunning()) return;

        Role currentRole = component.getRole(killer);
        if (currentRole == null || !currentRole.canUseKiller()) return;

        List<String> disabled;
        try {
            disabled = ConfigHelper.getDisabledRoles();
        } catch (Throwable ignored) {
            return;
        }

        if (!disabled.contains(currentRole.identifier().toString())) return;

        List<Role> enabledKillerRoles = new ArrayList<>(WatheRoles.ROLES);
        enabledKillerRoles.removeIf(role -> !role.canUseKiller() || role.identifier() == null || disabled.contains(role.identifier().toString()));

        Role replacement;
        if (enabledKillerRoles.isEmpty()) {
            replacement = WatheRoles.KILLER;
        } else {
            Collections.shuffle(enabledKillerRoles);
            replacement = enabledKillerRoles.get(0);
        }

        component.addRole(killer, replacement);
        component.sync();

        ModdedRoleAssigned.EVENT.invoker().assignModdedRole(killer, replacement);
    }
}
