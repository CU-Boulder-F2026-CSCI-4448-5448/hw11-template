package polymorphia.observers;

import ooad.gameobserver.intf.IGameObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestObserver implements IGameObserver {
    private static final List<String> EVENTS = List.of("fought", "died", "game end");

    private final Map<String, List<String>> eventMessages = new HashMap<>();

    @Override
    public void update(String message) {
        for (String event : EVENTS) {
            if (message.contains(event)) {
                if (!eventMessages.containsKey(event)) {
                    eventMessages.put(event, new ArrayList<>());
                }
                eventMessages.get(event).add(message);
            }
        }
    }

    public List<String> getEvents(String event) {
        return eventMessages.getOrDefault(event, List.of());
    }
    public boolean anEventOccurred(String event) {
        return !getEvents(event).isEmpty();
    }

    public int getEventCount(String event) {
        return getEvents(event).size();
    }

    public boolean aFightOccurred() {
        return anEventOccurred("fought");
    }

    public boolean aDeathOccurred() {
        return anEventOccurred("died");
    }

    public boolean aGameEndOccurred() {
        return anEventOccurred("game end");
    }

    public boolean aCharacterHadAFight(String characterName) {
        return eventMessages.get("fought").stream().anyMatch(message -> message.contains(characterName));
    }
}
