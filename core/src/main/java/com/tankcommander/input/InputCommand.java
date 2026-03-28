package com.tankcommander.input;

import com.tankcommander.entities.Entity;

public interface InputCommand {
    void execute(Entity entity);
}
