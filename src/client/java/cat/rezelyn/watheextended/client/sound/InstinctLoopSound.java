package cat.rezelyn.watheextended.client.sound;

import cat.rezelyn.watheextended.index.WatheExtendedSounds;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;

public final class InstinctLoopSound extends MovingSoundInstance {

    private static final float TARGET_VOLUME = 0.7f;
    private static final float FADE_STEP = 0.05f;
    private boolean fadingOut;

    public InstinctLoopSound() {
        super(WatheExtendedSounds.INSTINCT_LOOP, SoundCategory.AMBIENT, SoundInstance.createRandom());
        this.repeat = true;
        this.repeatDelay = 0;
        this.relative = true;
        this.volume = 0.01f;
    }

    public void fadeOut() {
        this.fadingOut = true;
    }

    public void fadeIn() {
        this.fadingOut = false;
    }

    @Override
    public void tick() {
        if (this.fadingOut) {
            this.volume = Math.max(0.0f, this.volume - FADE_STEP);
            if (this.volume <= 0.0f) {
                this.setDone();
            }
        } else {
            this.volume = Math.min(TARGET_VOLUME, this.volume + FADE_STEP);
        }
    }
}
