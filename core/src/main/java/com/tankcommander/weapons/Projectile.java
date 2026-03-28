package com.tankcommander.weapons;

import com.badlogic.gdx.math.Vector2;

public class Projectile {
    public enum ProjectileType {
        KINETIC,
        EXPLOSIVE,
        ARMOR_PIERCING
    }

    public Vector2 position;
    public Vector2 velocity;
    public float damage;
    public float blastRadius;
    public float lifeTime;
    public float currentLife;
    public ProjectileType type;
    public boolean isActive;

    public Projectile(Vector2 position, Vector2 velocity, float damage,
                      float blastRadius, float lifeTime, ProjectileType type) {
        this.position = position;
        this.velocity = velocity;
        this.damage = damage;
        this.blastRadius = blastRadius;
        this.lifeTime = lifeTime;
        this.currentLife = lifeTime;
        this.type = type;
        this.isActive = true;
    }

    public void update(float delta) {
        currentLife -= delta;
        if (currentLife <= 0) {
            isActive = false;
        }
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;
    }
}
