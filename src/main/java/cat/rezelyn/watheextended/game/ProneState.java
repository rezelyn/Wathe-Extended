package cat.rezelyn.watheextended.game;

/** State used to keep a player in the vanilla swimming/crawling pose while proning is enabled. */
public interface ProneState {
  boolean watheextended$isProneRequested();

  void watheextended$setProneRequested(boolean prone);
}
