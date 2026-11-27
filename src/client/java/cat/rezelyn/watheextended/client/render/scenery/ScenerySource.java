package cat.rezelyn.watheextended.client.render.scenery;

import net.caffeinemc.mods.sodium.client.render.viewport.frustum.Frustum;
import org.joml.FrustumIntersection;

public enum ScenerySource implements Frustum {
  INSTANCE;

  @Override
  public boolean testAab(float x, float y, float z, float maxX, float maxY, float maxZ) {
    return true;
  }

  @Override
  public int intersectAab(float x, float y, float z, float maxX, float maxY, float maxZ) {
    return FrustumIntersection.INSIDE;
  }

  @Override
  public boolean testSection(float x, float y, float z) {
    return true;
  }

  @Override
  public boolean testSectionExpanded(float x, float y, float z, float margin) {
    return true;
  }
}
