package com.tankcommander.entities;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.tankcommander.entities.components.Component;
import java.util.UUID;

public class Entity {
    public final UUID id;
    private ObjectMap<Class<? extends Component>, Component> components;
    private boolean markedForRemoval;

    public Entity() {
        this.id = UUID.randomUUID();
        this.components = new ObjectMap<>();
        this.markedForRemoval = false;
    }

    public <T extends Component> T addComponent(T component) {
        components.put(component.getClass(), component);
        return component;
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(Class<T> componentClass) {
        return (T) components.get(componentClass);
    }

    public boolean hasComponent(Class<? extends Component> componentClass) {
        return components.containsKey(componentClass);
    }

    public void removeComponent(Class<? extends Component> componentClass) {
        components.remove(componentClass);
    }

    public void update(float delta) {
        for (Component component : components.values()) {
            component.update(delta);
        }
    }

    public void markForRemoval() {
        this.markedForRemoval = true;
    }

    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }

    public Array<Component> getAllComponents() {
        return components.values().toArray();
    }
}
