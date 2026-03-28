package com.tankcommander.systems;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.tankcommander.entities.Entity;
import com.tankcommander.entities.components.*;

/**
 * Sistema que maneja la detección y resolución de colisiones.
 */
public class CollisionSystem implements GameSystem {
    private Vector2 tempVector;
    private float minCollisionDistance = 32f;

    public CollisionSystem() {
        this.tempVector = new Vector2();
    }

    @Override
    public void update(float delta, Array<Entity> entities) {
        // Detectar colisiones entre todas las entidades
        for (int i = 0; i < entities.size; i++) {
            for (int j = i + 1; j < entities.size; j++) {
                Entity a = entities.get(i);
                Entity b = entities.get(j);

                checkAndResolveCollision(a, b, delta);
            }
        }
    }

    private void checkAndResolveCollision(Entity a, Entity b, float delta) {
        CollisionComponent collA = a.getComponent(CollisionComponent.class);
        CollisionComponent collB = b.getComponent(CollisionComponent.class);

        // Si alguna entidad no tiene componente de colisión, ignorar
        if (collA == null || collB == null) return;

        // Verificar si pueden colisionar entre sí
        if (!collA.canCollideWith(collB.layer) && !collB.canCollideWith(collA.layer)) {
            return;
        }

        TransformComponent transformA = a.getComponent(TransformComponent.class);
        TransformComponent transformB = b.getComponent(TransformComponent.class);
        PhysicsComponent physicsA = a.getComponent(PhysicsComponent.class);
        PhysicsComponent physicsB = b.getComponent(PhysicsComponent.class);

        if (transformA == null || transformB == null) return;

        // Calcular distancia entre los centros
        float distance = transformA.position.dst(transformB.position);
        float minDistance = collA.collisionRadius + collB.collisionRadius;

        // Si hay colisión
        if (distance < minDistance) {
            collA.isColliding = true;
            collB.isColliding = true;
            collA.collidingEntity = b;
            collB.collidingEntity = a;

            // Calcular vector de colisión
            Vector2 collisionVector = transformB.position.cpy().sub(transformA.position).nor();
            collA.collisionNormal.set(collisionVector);
            collB.collisionNormal.set(collisionVector.scl(-1));

            // Separar las entidades
            float overlap = minDistance - distance;
            Vector2 separation = collisionVector.cpy().scl(overlap * 0.5f);

            transformA.position.sub(separation);
            transformB.position.add(separation);

            // DETENER EL MOVIMIENTO - ESTO ES LO IMPORTANTE
            if (physicsA != null) {
                // Detener completamente al tanque A
                physicsA.velocity.setZero();
                physicsA.force.setZero();

                // También detener la dirección de movimiento deseada
                TankBodyComponent bodyA = a.getComponent(TankBodyComponent.class);
                if (bodyA != null) {
                    bodyA.moveDirection.setZero();
                }
            }

            if (physicsB != null) {
                // Detener completamente al tanque B
                physicsB.velocity.setZero();
                physicsB.force.setZero();

                // También detener la dirección de movimiento deseada
                TankBodyComponent bodyB = b.getComponent(TankBodyComponent.class);
                if (bodyB != null) {
                    bodyB.moveDirection.setZero();
                }
            }

            // Efecto visual de colisión (opcional)
            // Notificar evento de colisión
            if (collA.layer == CollisionComponent.CollisionLayer.PLAYER ||
                collB.layer == CollisionComponent.CollisionLayer.PLAYER) {
                // Vibrar controlador si es el jugador
                // Gdx.input.vibrate(100);
            }
        }
    }

    /**
     * Verifica si dos entidades están colisionando.
     */
    public boolean isColliding(Entity a, Entity b) {
        CollisionComponent collA = a.getComponent(CollisionComponent.class);
        CollisionComponent collB = b.getComponent(CollisionComponent.class);

        if (collA == null || collB == null) return false;

        TransformComponent transformA = a.getComponent(TransformComponent.class);
        TransformComponent transformB = b.getComponent(TransformComponent.class);

        if (transformA == null || transformB == null) return false;

        float distance = transformA.position.dst(transformB.position);
        float minDistance = collA.collisionRadius + collB.collisionRadius;

        return distance < minDistance;
    }
}
