package cat.rezelyn.watheextended.modifiers;

import cat.rezelyn.watheextended.WatheExtendedServerConfig;

public final class TaxedModifier {

    private TaxedModifier() {
    }

    public static int applyTax(int amount) {
        float reduction = WatheExtendedServerConfig.getTaxedCoinReduction();
        return (int) Math.floor(amount * (1.0f - reduction));
    }
}
