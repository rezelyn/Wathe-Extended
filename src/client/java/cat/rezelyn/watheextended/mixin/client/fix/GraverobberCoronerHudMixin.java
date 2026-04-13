package cat.rezelyn.watheextended.mixin.client.fix;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.api.WatheRoles;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.client.gui.RoleNameRenderer;
import dev.doctor4t.wathe.entity.PlayerBodyEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.agmas.harpymodloader.component.WorldModifierComponent;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.agmas.noellesroles.coroner.BodyDeathReasonComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(RoleNameRenderer.class)
public class GraverobberCoronerHudMixin {

    // graverobber modifier fix
    @Inject(method = "renderHud", at = @At("TAIL"))
    private static void watheExtended$fixGraverobberCoronerHud(TextRenderer renderer, ClientPlayerEntity player, DrawContext context, RenderTickCounter tick, CallbackInfo ci) {
        try {
            PlayerBodyEntity targetBody = NoellesrolesClient.targetBody;
            if (targetBody == null) return;
            if (WatheClient.isPlayerSpectatingOrCreative()) return;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            // only run for graverobber modifier
            WorldModifierComponent modifier = WorldModifierComponent.KEY.get(client.player.getWorld());
            if (!modifier.isModifier(client.player.getUuid(), Noellesroles.GRAVEROBBER)) return;

            // avoid rendering duplication for coroner and vulture roles
            GameWorldComponent game = GameWorldComponent.KEY.get(client.player.getWorld());
            if (game.isRole(client.player, Noellesroles.CORONER)) return;
            if (game.isRole(client.player, Noellesroles.VULTURE)) return;

            BodyDeathReasonComponent bodyComp = BodyDeathReasonComponent.KEY.get(targetBody);

            MatrixStack matrices = context.getMatrices();
            matrices.push();
            matrices.translate(context.getScaledWindowWidth() / 2f, context.getScaledWindowHeight() / 2f + 6f, 0f);
            matrices.scale(0.6f, 0.6f, 1f);

            // death info line
            Text deathInfo;
            if (bodyComp.vultured) {
                // show scrambled text if killed by vulture
                String scrambled = "a".repeat(client.player.getRandom().nextBetween(12, 26));
                deathInfo = Text.literal(scrambled).formatted(Formatting.OBFUSCATED);
            } else {
                String reasonKey = bodyComp.deathReason.getNamespace() + "." + bodyComp.deathReason.getPath();
                deathInfo = Text.translatable("hud.coroner.death_info", targetBody.age / 20).append(Text.translatable("death_reason." + reasonKey));
            }
            context.drawTextWithShadow(renderer, deathInfo, -renderer.getWidth(deathInfo) / 2, 32, 0xFF0000);

            // role info line
            if (!bodyComp.vultured) {
                Role role = WatheRoles.CIVILIAN;
                for (Role r : WatheRoles.ROLES) {
                    if (r.identifier().equals(bodyComp.playerRole)) {
                        role = r;
                        break;
                    }
                }
                Text roleInfo = Text.translatable("hud.coroner.role_info").withColor(0xFF0000).append(org.agmas.harpymodloader.Harpymodloader.getRoleName(role).withColor(role.color()));
                context.drawTextWithShadow(renderer, roleInfo, -renderer.getWidth(roleInfo) / 2, 48, -1);
            }

            matrices.pop();
        } catch (Throwable ignored) {
        }
    }
}
