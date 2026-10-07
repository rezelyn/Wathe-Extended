package cat.rezelyn.watheextended.client.render;

public final class ChatHudRenderHelper {

  private static boolean forcingRender = false;

  private ChatHudRenderHelper() {}

  public static boolean isForcingRender() {
    return forcingRender;
  }

  public static void setForcingRender(boolean forcing) {
    forcingRender = forcing;
  }
}
