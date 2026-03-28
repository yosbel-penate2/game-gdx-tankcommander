package com.tankcommander.entities.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.tankcommander.weapons.Weapon;
import com.tankcommander.weapons.Projectile;

public class WeaponComponent implements Component {
    public Array<Weapon> weapons;
    public int currentWeaponIndex;
    public float[] cooldownTimers;

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
        Gdx.app.log("WeaponComponent", "Weapon added: " + weapon.getName() + " - Total weapons: " + weapons.size);
    }

    public Projectile fireCurrent(Vector2 origin, Vector2 direction) {
        Gdx.app.log("WeaponComponent", "fireCurrent called - weapons size: " + weapons.size +
            ", currentIndex: " + currentWeaponIndex);

        if (currentWeaponIndex < weapons.size) {
            Weapon currentWeapon = weapons.get(currentWeaponIndex);
            Gdx.app.log("WeaponComponent", "Current weapon: " + currentWeapon.getName() +
                ", cooldown: " + cooldownTimers[currentWeaponIndex]);

            if (cooldownTimers[currentWeaponIndex] <= 0) {
                cooldownTimers[currentWeaponIndex] = currentWeapon.getCooldown();
                Gdx.app.log("WeaponComponent", "FIRING! Cooldown set to: " + currentWeapon.getCooldown());
                Projectile projectile = currentWeapon.fire(origin, direction);
                Gdx.app.log("WeaponComponent", "Projectile created: " + (projectile != null));
                return projectile;
            } else {
                Gdx.app.log("WeaponComponent", "Weapon on cooldown: " + cooldownTimers[currentWeaponIndex] + "s remaining");
            }
        } else {
            Gdx.app.log("WeaponComponent", "ERROR: currentWeaponIndex (" + currentWeaponIndex +
                ") >= weapons.size (" + weapons.size + ")");
        }
        return null;
    }

    public void switchWeapon(int index) {
        if (index >= 0 && index < weapons.size) {
            currentWeaponIndex = index;
            Gdx.app.log("WeaponComponent", "Switched to weapon index: " + index +
                " - " + weapons.get(index).getName());
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
