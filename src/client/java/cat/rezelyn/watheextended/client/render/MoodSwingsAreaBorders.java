package cat.rezelyn.watheextended.client.render;

import java.util.Map;
import net.ceziak.mood_swings.area.NamedArea;
import net.ceziak.mood_swings.client.AreaBorderRenderer;
import net.ceziak.mood_swings.client.ClientConfigState;
import net.ceziak.mood_swings.network.ConfigSnapshot;

// Only touch this class behind moodswings ConfigHelper.isLoaded()
public final class MoodSwingsAreaBorders {

  private static final String LOCKDOWN_PREFIX = "lockdown/";

  private MoodSwingsAreaBorders() {}

  public static boolean isVisible() {
    ConfigSnapshot snapshot = ClientConfigState.snapshot();
    for (String id : snapshot.areas().keySet()) {
      if (AreaBorderRenderer.isVisible(id)) return true;
    }
    for (String id : snapshot.lockdownAreas().keySet()) {
      if (AreaBorderRenderer.isVisible(LOCKDOWN_PREFIX + id)) return true;
    }
    return false;
  }

  public static void setVisible(boolean visible) {
    ConfigSnapshot snapshot = ClientConfigState.snapshot();
    apply(snapshot.areas(), "", visible);
    apply(snapshot.lockdownAreas(), LOCKDOWN_PREFIX, visible);
  }

  private static void apply(Map<String, NamedArea> areas, String prefix, boolean visible) {
    for (Map.Entry<String, NamedArea> entry : areas.entrySet()) {
      String id = prefix + entry.getKey();
      if (visible) AreaBorderRenderer.show(id, entry.getValue());
      else AreaBorderRenderer.hide(id);
    }
  }
}
