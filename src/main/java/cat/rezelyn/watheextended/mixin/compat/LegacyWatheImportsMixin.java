package cat.rezelyn.watheextended.mixin.compat;

import cat.rezelyn.watheextended.mixin.WatheExtendedMixinPlugin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

/**
 * Registers classes that may contain references to Wathe payloads moved in Wathe 1.4. {@link
 * WatheExtendedMixinPlugin} rewrites those legacy references after the target is transformed.
 */
@Pseudo
@Mixin(
    targets = {
      // "org.BsXinQin.kinswathe.mixin.host.KillerNoBackfireMixin",
      // "org.BsXinQin.kinswathe.mixin.roles.licensed_villain.LicensedVillainNoBackfireMixin",
      // "org.BsXinQin.kinswathe.mixin.host.NeutralAnnouncementMixin",
      "org.BsXinQin.kinswathe.roles.dreamer.DreamerKillerComponent"
    },
    remap = false,
    priority = 2000)
public abstract class LegacyWatheImportsMixin {}
