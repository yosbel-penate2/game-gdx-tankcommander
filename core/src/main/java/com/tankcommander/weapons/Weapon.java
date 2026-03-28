package com.tankcommander.weapons;

import com.badlogic.gdx.math.Vector2;

public interface Weapon {
    Projectile fire(Vector2 origin, Vector2 direction);
    float getCooldown();
    int getAmmo();
    void reload();
    String getName();
}
