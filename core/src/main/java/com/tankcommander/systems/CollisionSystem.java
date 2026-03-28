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
    private float restitution = 0.3f; // Factor de rebote (0 = sin rebote, 1 = rebote perfecto)
    private float separationFactor = 1.2f; // Factor de separación (mayor = más separación)

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

        // Verificar si alguna entidad está en cooldown de colisión
        if (!collA.canProcessCollision() || !collB.canProcessCollision()) {
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
            // Activar cooldown para evitar múltiples colisiones
            collA.startCollisionCooldown();
            collB.startCollisionCooldown();

            collA.isColliding = true;
            collB.isColliding = true;
            collA.collidingEntity = b;
            collB.collidingEntity = a;

            // Calcular vector de colisión (desde A hacia B)
            Vector2 collisionVector = transformB.position.cpy().sub(transformA.position).nor();
            collA.collisionNormal.set(collisionVector);
            collB.collisionNormal.set(collisionVector.cpy().scl(-1));

            // Separar las entidades con factor de separación
            float overlap = minDistance - distance;
            Vector2 separation = collisionVector.cpy().scl(overlap * 0.5f * separationFactor);

            // Aplicar separación
            transformA.position.sub(separation);
            transformB.position.add(separation);

            // APLICAR REBOTE SUAVE (en lugar de detención completa)
            if (physicsA != null && physicsB != null) {
                // Calcular velocidad relativa
                Vector2 relativeVelocity = physicsB.velocity.cpy().sub(physicsA.velocity);
                float velocityAlong = relativeVelocity.dot(collisionVector);

                // Solo aplicar rebote si se están acercando
                if (velocityAlong < 0) {
                    // Calcular impulso de rebote
                    float impulse = (1 + restitution) * velocityAlong;

                    // Aplicar cambio de velocidad (dividido por masa, asumiendo masa = 1)
                    physicsA.velocity.add(collisionVector.cpy().scl(impulse));
                    physicsB.velocity.sub(collisionVector.cpy().scl(impulse));

                    // Limitar velocidad máxima después del rebote
                    float maxSpeed = Math.max(physicsA.maxSpeed, physicsB.maxSpeed);
                    if (physicsA.velocity.len() > maxSpeed) {
                        physicsA.velocity.setLength(maxSpeed);
                    }
                    if (physicsB.velocity.len() > maxSpeed) {
                        physicsB.velocity.setLength(maxSpeed);
                    }
                }
            }

            // También detener la dirección de movimiento deseada para evitar empuje continuo
            TankBodyComponent bodyA = a.getComponent(TankBodyComponent.class);
            TankBodyComponent bodyB = b.getComponent(TankBodyComponent.class);

            if (bodyA != null && physicsA != null && physicsA.velocity.len() < 10f) {
                bodyA.moveDirection.setZero();
            }

            if (bodyB != null && physicsB != null && physicsB.velocity.len() < 10f) {
                bodyB.moveDirection.setZero();
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
