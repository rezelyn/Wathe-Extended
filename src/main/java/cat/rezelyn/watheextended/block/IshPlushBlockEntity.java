package cat.rezelyn.watheextended.block;

import cat.rezelyn.watheextended.index.WatheExtendedBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class IshPlushBlockEntity extends BlockEntity {

    public double squash = 0.0;

    public IshPlushBlockEntity(BlockPos pos, BlockState state) {
        super(WatheExtendedBlockEntities.ISH_PLUSH, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, IshPlushBlockEntity be) {
        if (be.squash > 0.0) {
            be.squash /= 3.0;
            if (be.squash < 0.01) {
                be.squash = 0.0;
                if (world != null) {
                    world.updateListeners(pos, state, state, 2);
                }
            }
        }
    }

    public void squish(int amount) {
        this.squash += amount;
        if (this.world != null) {
            this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 2);
        }
        this.markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        nbt.putDouble("squash", this.squash);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        this.squash = nbt.getDouble("squash");
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return createNbt(registries);
    }
}
