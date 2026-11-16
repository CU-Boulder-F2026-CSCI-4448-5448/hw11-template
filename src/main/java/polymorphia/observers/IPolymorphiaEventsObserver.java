package polymorphia.observers;

import ooad.gameobserver.intf.EventType;

public interface IPolymorphiaEventsObserver {
    void update(EventType eventType, Object eventObject);
}
