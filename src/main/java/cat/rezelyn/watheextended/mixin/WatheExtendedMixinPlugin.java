package cat.rezelyn.watheextended.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class WatheExtendedMixinPlugin implements IMixinConfigPlugin {
    private static final String TARGET = "dev.doctor4t.wathe.game.GameFunctions";
    private static final String OLD = "dev/doctor4t/wathe/util/AnnounceWelcomePayload";
    private static final String NEW = "dev/doctor4t/wathe/network/AnnounceWelcomePayload";

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        if (!TARGET.equals(targetClassName)) {
            return;
        }

        for (var method : targetClass.methods) {
            if (!method.name.matches("handler\\$[^$]+\\$stupid_express\\$(initiateKill|initiateKillNonInitiate)")) {
                continue;
            }

            for (var insn : method.instructions) {
                if (insn instanceof TypeInsnNode typeInsn && OLD.equals(typeInsn.desc)) {
                    typeInsn.desc = NEW;
                }

                if (insn instanceof MethodInsnNode methodInsn && OLD.equals(methodInsn.owner)) {
                    methodInsn.owner = NEW;
                }
            }
        }
    }
}
