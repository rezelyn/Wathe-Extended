package cat.rezelyn.watheextended.index;

import cat.rezelyn.watheextended.WatheExtended;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class WatheExtendedSounds {

  // Guidebook SFX
  public static final SoundEvent GUIDEBOOK_OPEN = register("guidebook.open");
  public static final SoundEvent GUIDEBOOK_CLOSE = register("guidebook.close");
  public static final SoundEvent GUIDEBOOK_PAGE = register("guidebook.page");
  // Plush custom sounds
  public static final SoundEvent ISH_PLUSH = register("ish.plush");
  public static final SoundEvent JOCHOIS_PLUSH = register("jochois.plush");
  public static final SoundEvent OWNY_PLUSH = register("owny.plush");
  public static final SoundEvent REZELYN_PLUSH = register("rezelyn.plush");
  public static final SoundEvent NAGANEKI_PLUSH = register("naganeki.plush");
  // Instinct audio cues
  public static final SoundEvent INSTINCT_IN = register("instinct.in");
  public static final SoundEvent INSTINCT_OUT = register("instinct.out");
  public static final SoundEvent INSTINCT_LOOP = register("instinct.loop");

  private static SoundEvent register(String name) {
    Identifier id = WatheExtended.id(name);
    return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
  }

  public static void initialize() {}
}
