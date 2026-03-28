package com.tankcommander.entities.components;

import com.badlogic.gdx.utils.Array;
import com.tankcommander.weapons.Weapon;
import com.tankcommander.weapons.Projectile;

public class WeaponComponent implements Component {
    public Array<Weapon> weapons;
    public int currentWeaponIndex;
    private float[] cooldownTimers;

    public WeaponComponent() {
        this.weapons = new Array<>();
        this.currentWeaponIndex = 0;
        this.cooldownTimers = new float[0];
    }

    public void addWeapon(Weapon weapon) {
        weapons.add(weapon);
        float[] newTimers = new float[weapons.size];
        System.arraycopy(cooldownTimers, 0, newTimers, 0, cooldownTimers.length);
        cooldownTimers = newTimers;
    }

    public Projectile fireCurrent(Vector2 origin, Vector2 direction) {
        if (currentWeaponIndex < weapons.size) {
            Weapon currentWeapon = weapons.get(currentWeaponIndex);
            if (cooldownTimers[currentWeaponIndex] <= 0) {
                cooldownTimers[currentWeaponIndex] = currentWeapon.getCooldown();
                return currentWeapon.fire(origin, direction);
            }
        }
        return null;
    }

    public void switchWeapon(int index) {
        if (index >= 0 && index < weapons.size) {
            currentWeaponIndex = index;
        }
    }

    public void switchToNextWeapon() {
        currentWeaponIndex = (currentWeaponIndex + 1) % weapons.size;
    }

    @Override
    public void update(float delta) {
        for (int i = 0; i < cooldownTimers.length; i++) {
            if (cooldownTimers[i] > 0) {
                cooldownTimers[i] -= delta;
            }
        }
    }
}
