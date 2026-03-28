package com.tankcommander.weapons;

import com.badlogic.gdx.math.Vector2;

public class MachineGun implements Weapon {
    private float damage;
    private float fireRate;
    private float cooldown;
    private int maxAmmo;
    private int currentAmmo;

    public MachineGun() {
        this.damage = 10f;
        this.fireRate = 0.1f;
        this.cooldown = fireRate;
        this.maxAmmo = 100;
        this.currentAmmo = 100;
    }

    public MachineGun(float damage, float fireRate, int maxAmmo) {
        this.damage = damage;
        this.fireRate = fireRate;
        this.cooldown = fireRate;
        this.maxAmmo = maxAmmo;
        this.currentAmmo = maxAmmo;
    }

    @Override
    public Projectile fire(Vector2 origin, Vector2 direction) {
        if (currentAmmo <= 0) return null;

        currentAmmo--;
        Projectile projectile = new Projectile(
            origin.cpy(),
            direction.cpy().nor().scl(600f),
            damage,
            5f,
            1.5f,
            Projectile.ProjectileType.KINETIC
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
        return "Machine Gun";
    }
}
