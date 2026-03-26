package cat.rezelyn.watheextended.mixin.wathe;

import dev.doctor4t.wathe.cca.PlayerMoodComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(PlayerMoodComponent.class)
public class PlayerMoodComponentNbtMixin {

    @Shadow @Final
    public Map<PlayerMoodComponent.Task, Integer> timesGotten;

    @Shadow @Final
    public Map<PlayerMoodComponent.Task, PlayerMoodComponent.TrainTask> tasks;

    @Shadow
    private int nextTaskTimer;

    @Inject(method = "writeToNbt", at = @At("TAIL"))
    private void watheextended$writeTaskNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {
        tag.putInt("nextTaskTimerExt", this.nextTaskTimer);

        NbtCompound timesGottenTag = new NbtCompound();
        for (Map.Entry<PlayerMoodComponent.Task, Integer> entry : this.timesGotten.entrySet()) {
            timesGottenTag.putInt(String.valueOf(entry.getKey().ordinal()), entry.getValue());
        }
        tag.put("timesGottenExt", timesGottenTag);

        if (this.tasks.get(PlayerMoodComponent.Task.EAT) instanceof PlayerMoodComponent.EatTask eatTask) {
            tag.putBoolean("eatFulfilledExt", eatTask.fulfilled);
        }
        if (this.tasks.get(PlayerMoodComponent.Task.DRINK) instanceof PlayerMoodComponent.DrinkTask drinkTask) {
            tag.putBoolean("drinkFulfilledExt", drinkTask.fulfilled);
        }
    }

    @Inject(method = "readFromNbt", at = @At("TAIL"))
    private void watheextended$readTaskNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {
        if (tag.contains("nextTaskTimerExt", NbtElement.INT_TYPE)) {
            this.nextTaskTimer = tag.getInt("nextTaskTimerExt");
        }

        if (tag.contains("timesGottenExt", NbtElement.COMPOUND_TYPE)) {
            this.timesGotten.clear();
            NbtCompound timesGottenTag = tag.getCompound("timesGottenExt");
            for (PlayerMoodComponent.Task task : PlayerMoodComponent.Task.values()) {
                String key = String.valueOf(task.ordinal());
                if (timesGottenTag.contains(key, NbtElement.INT_TYPE)) {
                    this.timesGotten.put(task, timesGottenTag.getInt(key));
                }
            }
        }
    }
}
