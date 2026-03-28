package com.tankcommander.entities.components;

import com.badlogic.gdx.math.Vector2;

public class TankBodyComponent implements Component {
    public Vector2 moveDirection;
    public float trackSpeed;
    public float turnSpeed;
    public float currentSpeed;
    public boolean isBlocked;

    public TankBodyComponent() {
        this.moveDirection = new Vector2(0, 0);
        this.trackSpeed = 150f;
        this.turnSpeed = 180f;  // Grados por segundo
        this.currentSpeed = 0f;
        this.isBlocked = false;
    }

    @Override
    public void update(float delta) {
        // Movement logic handled by PhysicsSystem
    }
}
