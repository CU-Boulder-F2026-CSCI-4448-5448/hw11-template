package polymorphia.observers;

import ooad.gameobserver.intf.EventType;
import ooad.gameobserver.intf.IGame;
import ooad.gameobserver.intf.IGameObserver;

import java.util.*;

public class EventBus implements IGame, IPolymorphiaEventIssuer {
    private final Map<EventType, List<IGameObserver>> observers = new EnumMap<>(EventType.class);
    private List<IPolymorphiaEventsObserver> polymorphiaEventsObservers = new ArrayList<>();

    private static EventBus instance;

    private EventBus() {
    }

    public static EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    @Override
    public void attach(IGameObserver observer) {
        observers.putIfAbsent(EventType.ALL, new ArrayList<>());
        observers.get(EventType.ALL).add(observer);
    }

    @Override
    public void attach(IGameObserver observer, List<EventType> events) {
        for (EventType event : events) {
            observers.putIfAbsent(event, new ArrayList<>());
            observers.get(event).add(observer);
        }
    }

    @Override
    public void detach(IGameObserver observer) {
        for (List<IGameObserver> observers : observers.values()) {
            observers.remove(observer);
        }
    }

    @Override
    public void attach(IPolymorphiaEventsObserver observer) {
        polymorphiaEventsObservers.add(observer);
    }

    public void postEvent(EventType type, String event) {
        Set<IGameObserver> observersToNotify = new HashSet<>();
        observersToNotify.addAll(observers.getOrDefault(type, new ArrayList<>()));
        observersToNotify.addAll(observers.getOrDefault(EventType.ALL, new ArrayList<>()));

        for (IGameObserver observer : observersToNotify) {
            observer.update(event);
        }
    }

    public void postEvent(EventType eventType, Object eventObject) {
        for (IPolymorphiaEventsObserver observer : polymorphiaEventsObservers) {
            observer.update(eventType, eventObject);
        }
    }
}
