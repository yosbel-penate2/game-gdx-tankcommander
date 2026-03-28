package com.tankcommander.input;

import com.badlogic.gdx.math.Vector2;
import com.tankcommander.entities.Entity;
import com.tankcommander.entities.components.TransformComponent;
import com.tankcommander.entities.components.TurretComponent;
import com.tankcommander.entities.components.WeaponComponent;
import com.tankcommander.weapons.Projectile;

/**
 * Comando para disparar el cañón principal del tanque.
 */
public class FireCannonCommand implements InputCommand {
    private static final int CANNON_WEAPON_INDEX = 0;

    @Override
    public void execute(Entity entity) {
        WeaponComponent weapons = entity.getComponent(WeaponComponent.class);
        TransformComponent transform = entity.getComponent(TransformComponent.class);
        TurretComponent turret = entity.getComponent(TurretComponent.class);

        if (weapons == null || transform == null || turret == null) {
            return;
        }

        // Seleccionar el cañón (índice 0)
        weapons.switchWeapon(CANNON_WEAPON_INDEX);

        // Calcular origen del disparo (punta de la torreta)
        Vector2 fireOrigin = turret.getWorldPosition(transform.position, transform.rotation);

        // Calcular dirección basada en el ángulo de la torreta
        float angleRad = (float)Math.toRadians(turret.turretAngle);
        Vector2 fireDirection = new Vector2(
            (float)Math.cos(angleRad),
            (float)Math.sin(angleRad)
        ).nor();

        // Disparar
        Projectile projectile = weapons.fireCurrent(fireOrigin, fireDirection);

        // Aquí se podría agregar el proyectil al mundo
        // gameWorld.addProjectile(projectile);

        // Efectos adicionales
        createMuzzleFlash(fireOrigin, turret.turretAngle);
        playCannonSound();
        addRecoilEffect(entity);
    }

    private void createMuzzleFlash(Vector2 position, float angle) {
        // TODO: Implementar efecto visual de destello
        // Se puede agregar una entidad temporal con partículas
    }

    private void playCannonSound() {
        // TODO: Reproducir sonido del cañón
        // Gdx.audio.newSound(Gdx.files.internal("cannon.wav")).play();
    }

    private void addRecoilEffect(Entity entity) {
        // TODO: Añadir efecto de retroceso visual
        // Por ejemplo, mover ligeramente la torreta hacia atrás momentáneamente
    }
}
