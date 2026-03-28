package com.tankcommander.entities.components;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.tankcommander.entities.Entity;

/**
 * Componente que maneja la detección de colisiones.
 */
public class CollisionComponent implements Component {
    public Rectangle boundingBox;
    public float collisionRadius;
    public boolean isColliding;
    public Vector2 collisionNormal;
    public Entity collidingEntity;
    public CollisionLayer layer;
    public CollisionLayer[] collidesWith;

    // NUEVO: temporizador para evitar múltiples colisiones
    public float collisionCooldown;
    public float collisionTimer;
    public static final float DEFAULT_COOLDOWN = 0.2f; // 200ms de enfriamiento

    public enum CollisionLayer {
        PLAYER,
        ENEMY,
        OBSTACLE,
        PROJECTILE,
        WALL
    }

    public CollisionComponent() {
        this.boundingBox = new Rectangle();
        this.collisionRadius = 20f;
        this.isColliding = false;
        this.collisionNormal = new Vector2();
        this.layer = CollisionLayer.OBSTACLE;
        this.collidesWith = new CollisionLayer[0];
        this.collisionCooldown = DEFAULT_COOLDOWN;
        this.collisionTimer = 0f;
    }

    public CollisionComponent(float width, float height, CollisionLayer layer) {
        this();
        this.boundingBox.setSize(width, height);
        this.layer = layer;
    }

    public void updateBounds(Vector2 position) {
        boundingBox.setPosition(
            position.x - boundingBox.width / 2,
            position.y - boundingBox.height / 2
        );
    }

    public boolean canCollideWith(CollisionLayer otherLayer) {
        for (CollisionLayer layer : collidesWith) {
            if (layer == otherLayer) {
                return true;
            }
        }
        return false;
    }

    // NUEVO: verifica si puede procesar una nueva colisión
    public boolean canProcessCollision() {
        return collisionTimer <= 0;
    }

    // NUEVO: activa el temporizador de enfriamiento
    public void startCollisionCooldown() {
        collisionTimer = collisionCooldown;
    }

    // NUEVO: actualiza el temporizador
    public void updateCooldown(float delta) {
        if (collisionTimer > 0) {
            collisionTimer -= delta;
        }
    }

    @Override
    public void update(float delta) {
        // Actualizar temporizador de enfriamiento
        updateCooldown(delta);

        // Reset colisión cada frame
        isColliding = false;
        collidingEntity = null;
    }
}
