package cat.rezelyn.watheextended.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class WatheExtendedMixinPlugin implements IMixinConfigPlugin {
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
        for (var method : targetClass.methods) {
            for (var insn : method.instructions) {
                if (insn instanceof TypeInsnNode typeInsn && OLD.equals(typeInsn.desc)) {
                    typeInsn.desc = NEW;
                }

                if (insn instanceof MethodInsnNode methodInsn && OLD.equals(methodInsn.owner)) {
                    methodInsn.owner = NEW;
                }

                if (insn instanceof FieldInsnNode fieldInsn && OLD.equals(fieldInsn.owner)) {
                    fieldInsn.owner = NEW;
                }

                if (insn instanceof MultiANewArrayInsnNode arrayInsn) {
                    arrayInsn.desc = arrayInsn.desc.replace(OLD, NEW);
                }
            }
        }
    }
}
