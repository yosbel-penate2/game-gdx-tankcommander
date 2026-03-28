package com.tankcommander.input;

import com.badlogic.gdx.utils.ObjectMap;
import com.tankcommander.entities.Entity;

public class InputMapper {
    private ObjectMap<Integer, InputCommand> commandMap;

    public InputMapper() {
        this.commandMap = new ObjectMap<>();
    }

    public void mapInput(int inputCode, InputCommand command) {
        commandMap.put(inputCode, command);
    }

    public void handleInput(int inputCode, Entity entity) {
        InputCommand command = commandMap.get(inputCode);
        if (command != null) {
            command.execute(entity);
        }
    }

    public void clearMappings() {
        commandMap.clear();
    }
}
