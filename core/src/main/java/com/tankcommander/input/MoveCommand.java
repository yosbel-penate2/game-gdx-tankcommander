package com.tankcommander.input;

import com.badlogic.gdx.math.Vector2;
import com.tankcommander.entities.Entity;
import com.tankcommander.entities.components.TankBodyComponent;

public class MoveCommand implements InputCommand {
    private Vector2 direction;

    public MoveCommand(Vector2 direction) {
        this.direction = direction;
    }

    public void setDirection(Vector2 direction) {
        this.direction = direction;
    }

    @Override
    public void execute(Entity entity) {
        TankBodyComponent body = entity.getComponent(TankBodyComponent.class);
        if (body != null) {
            body.moveDirection = direction;
        }
    }
}
