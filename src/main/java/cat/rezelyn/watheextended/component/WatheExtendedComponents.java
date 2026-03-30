package cat.rezelyn.watheextended.component;

import org.ladysnake.cca.api.v3.world.WorldComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentInitializer;

public class WatheExtendedComponents implements WorldComponentInitializer {

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(WatheExtendedWorldComponent.KEY, WatheExtendedWorldComponent::new);
    }
}
