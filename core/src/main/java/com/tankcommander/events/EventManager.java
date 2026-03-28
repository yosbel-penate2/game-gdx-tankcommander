package com.tankcommander.events;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class EventManager {
    private ObjectMap<Class<? extends GameEvent>, Array<EventListener>> listeners;

    public EventManager() {
        this.listeners = new ObjectMap<>();
    }

    public void subscribe(Class<? extends GameEvent> eventType, EventListener listener) {
        if (!listeners.containsKey(eventType)) {
            listeners.put(eventType, new Array<>());
        }
        listeners.get(eventType).add(listener);
    }

    public void unsubscribe(Class<? extends GameEvent> eventType, EventListener listener) {
        if (listeners.containsKey(eventType)) {
            listeners.get(eventType).removeValue(listener, true);
        }
    }

    public void dispatch(GameEvent event) {
        Class<? extends GameEvent> eventClass = event.getClass();
        if (listeners.containsKey(eventClass)) {
            for (EventListener listener : listeners.get(eventClass)) {
                listener.onEvent(event);
            }
        }
    }

    public void clear() {
        listeners.clear();
    }
}
