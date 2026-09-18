package cat.rezelyn.watheextended.mixin.fix;

import org.spongepowered.asm.mixin.Mixin;

/**
 * fix: add legacy add-on classes to the compatibility layer of {@link cat.rezelyn.watheextended.mixin.WatheExtendedMixinPlugin} rewrite pass
 */
@Mixin(targets = {
        "org.BsXinQin.kinswathe.roles.dreamer.DreamerKillerComponent",
        "pro.fazeclan.river.stupid_express.role.amnesiac.RoleSelectionHandler",
        "pro.fazeclan.river.stupid_express.role.necromancer.RevivalSelectionHandler"
}, remap = false)
public abstract class LegacyAnnounceWelcomePayloadMixin {}
