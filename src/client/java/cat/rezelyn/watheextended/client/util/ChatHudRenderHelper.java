package cat.rezelyn.watheextended.client.util;

public final class ChatHudRenderHelper {

    private static boolean forcingRender = false;

    private ChatHudRenderHelper() {}

    public static void setForcingRender(boolean forcing) {
        forcingRender = forcing;
    }

    public static boolean isForcingRender() {
        return forcingRender;
    }
}
