package cat.rezelyn.watheextended.game.roles.swapper;

import cat.rezelyn.watheextended.WatheExtended;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Vec3d;

public record SwapperTeleportPayload(int partnerId, Vec3d partnerDestination, Vec3d destination)
    implements CustomPayload {
  public static final Id<SwapperTeleportPayload> ID =
      new Id<>(WatheExtended.id("swapper_teleport"));
  public static final PacketCodec<RegistryByteBuf, SwapperTeleportPayload> CODEC =
      PacketCodec.of(
          (swap, buf) -> {
            buf.writeVarInt(swap.partnerId());
            buf.writeVec3d(swap.partnerDestination());
            buf.writeVec3d(swap.destination());
          },
          buf -> new SwapperTeleportPayload(buf.readVarInt(), buf.readVec3d(), buf.readVec3d()));

  @Override
  public Id<SwapperTeleportPayload> getId() {
    return ID;
  }
}
