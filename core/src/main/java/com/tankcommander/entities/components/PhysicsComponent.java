package com.tankcommander.entities.components;

import com.badlogic.gdx.math.Vector2;

public class PhysicsComponent implements Component {
    public Vector2 velocity;
    public float maxSpeed;
    public float acceleration;
    public float deceleration;
    public float rotationSpeed;
    public Vector2 force;

    public PhysicsComponent() {
        this.velocity = new Vector2(0, 0);
        this.force = new Vector2(0, 0);
        this.maxSpeed = 200f;
        this.acceleration = 500f;
        this.deceleration = 300f;
        this.rotationSpeed = 180f;
    }

    public void applyForce(Vector2 forceToApply) {
        force.add(forceToApply);
    }

    @Override
    public void update(float delta) {
        velocity.add(force.x * delta, force.y * delta);

        if (velocity.len() > maxSpeed) {
            velocity.setLength(maxSpeed);
        }

        force.setZero();
    }
}
