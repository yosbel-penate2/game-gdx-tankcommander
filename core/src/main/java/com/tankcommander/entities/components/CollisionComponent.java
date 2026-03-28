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

    @Override
    public void update(float delta) {
        // Reset colisión cada frame
        isColliding = false;
        collidingEntity = null;
    }
}
