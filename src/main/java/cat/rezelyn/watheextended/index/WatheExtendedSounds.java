package cat.rezelyn.watheextended.index;

import cat.rezelyn.watheextended.WatheExtended;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class WatheExtendedSounds {

    public static final SoundEvent GUIDEBOOK_OPEN = register("guidebook.open");
    public static final SoundEvent GUIDEBOOK_CLOSE = register("guidebook.close");
    public static final SoundEvent GUIDEBOOK_PAGE = register("guidebook.page");
    public static final SoundEvent ISH_PLUSH = register("ish.plush");

    private static SoundEvent register(String name) {
        Identifier id = WatheExtended.id(name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void initialize() {}
}
