package cat.rezelyn.watheextended.network;

import cat.rezelyn.watheextended.component.WatheExtendedWorldComponent;
import dev.doctor4t.wathe.cca.AutoStartComponent;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.MapVariablesWorldComponent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

final class CommandSettings {
  private CommandSettings() {}

  private static final String RTP = "command.rtp.";
  private static final Map<String, Function<World, String>> READERS =
      Map.ofEntries(
          Map.entry(
              "command.autoStart",
              world -> Integer.toString(AutoStartComponent.KEY.get(world).startTime / 20)),
          Map.entry(
              "command.backfire",
              world ->
                  Integer.toString(
                      Math.round(
                          GameWorldComponent.KEY.get(world).getBackfireChancePerInnocentKill()
                              * 100))),
          Map.entry(
              "command.killerDividend",
              world -> Integer.toString(GameWorldComponent.KEY.get(world).getKillerDividend())),
          Map.entry(
              "command.vigilanteDividend",
              world -> Integer.toString(GameWorldComponent.KEY.get(world).getVigilanteDividend())),
          Map.entry(
              "command.lobbyArea",
              world -> box(WatheExtendedWorldComponent.KEY.get(world).getLobbyArea())),
          Map.entry(
              "command.playArea",
              world -> box(MapVariablesWorldComponent.KEY.get(world).getPlayArea())),
          Map.entry(
              "command.playAreaOffset",
              world -> vec(MapVariablesWorldComponent.KEY.get(world).getPlayAreaOffset())),
          Map.entry(
              "command.readyArea",
              world -> box(MapVariablesWorldComponent.KEY.get(world).getReadyArea())),
          Map.entry(
              "command.resetPasteOffset",
              world -> vec(MapVariablesWorldComponent.KEY.get(world).getResetPasteOffset())),
          Map.entry(
              "command.spawnPosition",
              world -> pos(MapVariablesWorldComponent.KEY.get(world).getSpawnPos())),
          Map.entry(
              "command.readyAreaSpawnPosition",
              world -> pos(WatheExtendedWorldComponent.KEY.get(world).getReadyAreaSpawnPos())),
          Map.entry(
              "command.spectatorSpawnPosition",
              world -> pos(MapVariablesWorldComponent.KEY.get(world).getSpectatorSpawnPos())));

  static Map<String, String> snapshot(World world) {
    var values = new LinkedHashMap<String, String>();
    READERS.forEach((key, reader) -> values.put(key, reader.apply(world)));
    WatheExtendedWorldComponent.KEY
        .get(world)
        .getTeleportationSlots()
        .forEach((id, slot) -> values.put(RTP + id, slot.toCommandArgs()));
    return values;
  }

  static String read(World world, String key) {
    if (!key.startsWith(RTP)) return READERS.get(key).apply(world);
    var slot =
        WatheExtendedWorldComponent.KEY
            .get(world)
            .getTeleportationSlots()
            .get(Integer.valueOf(key.substring(RTP.length())));
    return slot == null ? "" : slot.toCommandArgs();
  }

  private static String number(double n) {
    return BigDecimal.valueOf(n)
        .setScale(2, RoundingMode.DOWN)
        .stripTrailingZeros()
        .toPlainString();
  }

  private static String box(Box b) {
    return b == null
        ? "0 0 0 0 0 0"
        : number(b.minX)
            + " "
            + number(b.minY)
            + " "
            + number(b.minZ)
            + " "
            + number(b.maxX)
            + " "
            + number(b.maxY)
            + " "
            + number(b.maxZ);
  }

  private static String vec(Vec3i v) {
    return v == null ? "0 0 0" : v.getX() + " " + v.getY() + " " + v.getZ();
  }

  private static String pos(MapVariablesWorldComponent.PosWithOrientation p) {
    return p == null
        ? "0 0 0 0 0"
        : number(p.pos.x)
            + " "
            + number(p.pos.y)
            + " "
            + number(p.pos.z)
            + " "
            + number(Double.parseDouble(Float.toString(p.yaw)))
            + " "
            + number(Double.parseDouble(Float.toString(p.pitch)));
  }

  static String normalize(String key, String raw) {
    if (key.startsWith(RTP) && raw.isBlank()) return "";
    String[] parts = raw.strip().split("\\s+");
    int arity =
        key.endsWith("Area")
            ? 6
            : key.endsWith("Offset") ? 3 : key.endsWith("Position") || key.startsWith(RTP) ? 5 : 1;
    if (parts.length != arity) throw new IllegalArgumentException("Wrong number of coordinates");
    if (arity == 1 || arity == 3) {
      for (int i = 0; i < parts.length; i++)
        parts[i] = Integer.toString(Integer.parseInt(parts[i]));
      return String.join(" ", parts);
    }
    double[] values = Arrays.stream(parts).mapToDouble(Double::parseDouble).toArray();
    if (arity == 6)
      return box(new Box(values[0], values[1], values[2], values[3], values[4], values[5]));
    return Arrays.stream(values)
        .mapToObj(CommandSettings::number)
        .collect(java.util.stream.Collectors.joining(" "));
  }

  static String command(String key, String desired) {
    String prefix =
        switch (key) {
          case "command.autoStart" -> "wathe:gameSettings set autoStart ";
          case "command.backfire" -> "wathe:gameSettings set backfireChancePerInnocentKill ";
          case "command.killerDividend" -> "wathe:gameSettings set roleDividend killer ";
          case "command.vigilanteDividend" -> "wathe:gameSettings set roleDividend vigilante ";
          case "command.lobbyArea" -> "watheextended:mapVariables set lobbyArea ";
          case "command.playArea" -> "wathe:mapVariables set playArea ";
          case "command.playAreaOffset" -> "wathe:mapVariables set playAreaOffset ";
          case "command.readyArea" -> "wathe:mapVariables set readyArea ";
          case "command.resetPasteOffset" -> "wathe:mapVariables set resetPasteOffset ";
          case "command.spawnPosition" -> "wathe:mapVariables set spawnPosition ";
          case "command.readyAreaSpawnPosition" ->
              "watheextended:mapVariables set readyAreaSpawnPosition ";
          case "command.spectatorSpawnPosition" -> "wathe:mapVariables set spectatorSpawnPosition ";

          default ->
              key.matches("command\\.rtp\\.[0-9]+")
                  ? "watheextended:rtp slot edit " + key.substring(12) + " "
                  : null;
        };

    if (key.matches("command\\.rtp\\.[0-9]+") && desired.isBlank())
      return "watheextended:rtp slot remove " + key.substring(12);

    if (prefix == null) throw new IllegalArgumentException("Unknown command setting");
    if (key.equals("command.backfire")) return prefix + (Integer.parseInt(desired) / 100f);

    // Vec3 arguments otherwise center bare integer X/Z coordinates within their blocks
    if (desired.indexOf(' ') >= 0) {
      String[] parts = desired.split(" ");
      int coords = parts.length == 5 ? 3 : parts.length;
      for (int i = 0; i < coords; i++) {
        if (!parts[i].contains(".")) parts[i] += ".0";
      }
      return prefix + String.join(" ", parts);
    }
    return prefix + desired;
  }
}
