package com.tankcommander.systems;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.tankcommander.entities.Entity;
import com.tankcommander.entities.components.PhysicsComponent;
import com.tankcommander.entities.components.TransformComponent;
import com.tankcommander.entities.components.TankBodyComponent;
import com.tankcommander.entities.components.CollisionComponent;

public class PhysicsSystem implements GameSystem {

    @Override
    public void update(float delta, Array<Entity> entities) {
        for (Entity entity : entities) {
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            PhysicsComponent physics = entity.getComponent(PhysicsComponent.class);
            TankBodyComponent body = entity.getComponent(TankBodyComponent.class);
            CollisionComponent collision = entity.getComponent(CollisionComponent.class);

            if (transform != null && physics != null) {

                // SI ESTÁ COLISIONANDO O EN COOLDOWN DE COLISIÓN, NO APLICAR MOVIMIENTO
                if (collision != null && (collision.isColliding || collision.collisionTimer > 0)) {
                    physics.velocity.setZero();
                    physics.force.setZero();
                    if (body != null) {
                        body.moveDirection.setZero();
                    }
                    continue;  // Saltar este frame, no mover
                }

                // Apply movement from tank body
                if (body != null && body.moveDirection.len() > 0.1f) {
                    Vector2 force = body.moveDirection.cpy().scl(physics.acceleration);
                    physics.applyForce(force);

                    // Apply rotation
                    float targetAngle = body.moveDirection.angleDeg();
                    float angleDiff = targetAngle - transform.rotation;
                    angleDiff = (angleDiff + 360) % 360;
                    if (angleDiff > 180) angleDiff -= 360;

                    float rotationDelta = body.turnSpeed * delta;
                    if (Math.abs(angleDiff) < rotationDelta) {
                        transform.rotation = targetAngle;
                    } else {
                        transform.rotation += Math.signum(angleDiff) * rotationDelta;
                    }
                } else if (body != null && physics.velocity.len() > 0) {
                    // Apply deceleration
                    Vector2 decelForce = physics.velocity.cpy().nor().scl(-physics.deceleration);
                    physics.applyForce(decelForce);
                }

                // Update physics
                physics.update(delta);

                // Update position
                transform.position.x += physics.velocity.x * delta;
                transform.position.y += physics.velocity.y * delta;
            }
        }
    }
}
