package com.tankcommander.entities.components;

import com.badlogic.gdx.math.Vector2;

public class TransformComponent implements Component {
    public Vector2 position;
    public float rotation;
    public Vector2 scale;

    public TransformComponent() {
        this.position = new Vector2(0, 0);
        this.rotation = 0f;
        this.scale = new Vector2(1, 1);
    }

    public TransformComponent(Vector2 position, float rotation) {
        this.position = position.cpy();
        this.rotation = rotation;
        this.scale = new Vector2(1, 1);
    }

    @Override
    public void update(float delta) {
        // Transform updates handled by systems
    }
}
