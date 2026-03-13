package cat.rezelyn.watheextended.client.screen.guidebook;

import java.util.List;

@FunctionalInterface
public interface GuidebookEntrySource {

    List<GuidebookEntry> build();
}
