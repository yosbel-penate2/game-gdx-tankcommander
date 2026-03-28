package com.tankcommander.states;

import com.badlogic.gdx.utils.ObjectMap;

public class StateMachine {
    private ObjectMap<String, GameState> states;
    private GameState currentState;
    private String currentStateId;

    public StateMachine() {
        this.states = new ObjectMap<>();
    }

    public void addState(String id, GameState state) {
        states.put(id, state);
    }

    public void changeState(String id) {
        if (states.containsKey(id)) {
            if (currentState != null) {
                currentState.exit();
            }
            currentState = states.get(id);
            currentStateId = id;
            currentState.enter();
        }
    }

    public void update(float delta) {
        if (currentState != null) {
            currentState.update(delta);
        }
    }

    public void render() {
        if (currentState != null) {
            currentState.render();
        }
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public String getCurrentStateId() {
        return currentStateId;
    }
}
