package cat.rezelyn.watheextended.game;

import cat.rezelyn.watheextended.WatheExtended;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/** Server synchronization for the client-controlled prone pose */
public final class PlayerMovement {
  private PlayerMovement() {}

  public static void handleSetProne(
      SetPronePayload payload, ServerPlayNetworking.Context context) {
    context.server()
        .execute(
            () -> {
              PlayerEntity player = context.player();
              if (player instanceof ProneState proneState) {
                proneState.watheextended$setProneRequested(payload.prone());
              }
            });
  }

  public record SetPronePayload(boolean prone) implements CustomPayload {
    public static final Id<SetPronePayload> ID = new Id<>(WatheExtended.id("set_prone"));
    public static final PacketCodec<RegistryByteBuf, SetPronePayload> CODEC =
        PacketCodec.of(
            (payload, buffer) -> buffer.writeBoolean(payload.prone()),
            buffer -> new SetPronePayload(buffer.readBoolean()));

    @Override
    public Id<? extends CustomPayload> getId() {
      return ID;
    }
  }
}
