package com.tankcommander.weapons;

import com.badlogic.gdx.math.Vector2;

public class Cannon implements Weapon {
    private float damage;
    private float blastRadius;
    private float cooldown;
    private int maxAmmo;
    private int currentAmmo;

    public Cannon() {
        this.damage = 50f;
        this.blastRadius = 40f;
        this.cooldown = 1.5f;
        this.maxAmmo = 10;
        this.currentAmmo = 10;
    }

    public Cannon(float damage, float blastRadius, float cooldown) {
        this.damage = damage;
        this.blastRadius = blastRadius;
        this.cooldown = cooldown;
        this.maxAmmo = 10;
        this.currentAmmo = 10;
    }

    @Override
    public Projectile fire(Vector2 origin, Vector2 direction) {
        if (currentAmmo <= 0) return null;

        currentAmmo--;
        Projectile projectile = new Projectile(
            origin.cpy(),
            direction.cpy().nor().scl(400f),
            damage,
            blastRadius,
            3f,
            Projectile.ProjectileType.EXPLOSIVE
        );
        return projectile;
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }

    @Override
    public int getAmmo() {
        return currentAmmo;
    }

    @Override
    public void reload() {
        currentAmmo = maxAmmo;
    }

    @Override
    public String getName() {
        return "Cannon";
    }
}
