package com.tankcommander.input;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.tankcommander.entities.Entity;
import com.tankcommander.entities.components.TransformComponent;
import com.tankcommander.entities.components.TurretComponent;
import com.tankcommander.entities.components.WeaponComponent;
import com.tankcommander.weapons.Projectile;

/**
 * Comando para disparar la ametralladora del tanque.
 * Soporta disparo continuo mientras se mantiene presionado el botón.
 */
public class FireMachineGunCommand implements InputCommand {
    private static final int MACHINEGUN_WEAPON_INDEX = 1;
    private boolean isFiring;
    private float lastShotTime;
    private float fireDelay;

    public FireMachineGunCommand() {
        this.isFiring = false;
        this.lastShotTime = 0;
        this.fireDelay = 0.1f; // 10 disparos por segundo
    }

    @Override
    public void execute(Entity entity) {
        WeaponComponent weapons = entity.getComponent(WeaponComponent.class);
        TransformComponent transform = entity.getComponent(TransformComponent.class);
        TurretComponent turret = entity.getComponent(TurretComponent.class);

        if (weapons == null || transform == null || turret == null) {
            return;
        }

        // Seleccionar ametralladora (índice 1)
        weapons.switchWeapon(MACHINEGUN_WEAPON_INDEX);

        // Calcular origen y dirección del disparo
        Vector2 fireOrigin = turret.getWorldPosition(transform.position, transform.rotation);

        float angleRad = (float)Math.toRadians(turret.turretAngle);
        Vector2 fireDirection = new Vector2(
            (float)Math.cos(angleRad),
            (float)Math.sin(angleRad)
        ).nor();

        // Disparar
        Projectile projectile = weapons.fireCurrent(fireOrigin, fireDirection);

        // Efectos de la ametralladora
        createTracerEffect(fireOrigin, fireDirection);
        playMachineGunSound();
        addScreenShake();

        // Si hay un proyectil, se puede añadir al mundo con spread (dispersión)
        if (projectile != null) {
            applySpread(projectile);
            // gameWorld.addProjectile(projectile);
        }
    }

    /**
     * Para disparo continuo, se puede llamar este método en cada frame
     * mientras el botón está presionado.
     */
    public void executeContinuous(Entity entity, float deltaTime) {
        if (!isFiring) return;

        float currentTime = System.currentTimeMillis() / 1000f;
        if (currentTime - lastShotTime >= fireDelay) {
            lastShotTime = currentTime;
            execute(entity);
        }
    }

    public void startFiring() {
        this.isFiring = true;
        this.lastShotTime = System.currentTimeMillis() / 1000f;
    }

    public void stopFiring() {
        this.isFiring = false;
    }

    private void createTracerEffect(Vector2 origin, Vector2 direction) {
        // TODO: Crear efecto visual de trazador
        // Partículas luminosas que siguen la trayectoria del proyectil
    }

    private void playMachineGunSound() {
        // TODO: Reproducir sonido de ametralladora
        // Gdx.audio.newSound(Gdx.files.internal("machinegun.wav")).play(0.5f);
    }

    private void addScreenShake() {
        // TODO: Añadir pequeña vibración a la cámara
        // camera.position.add(MathUtils.random(-2, 2), MathUtils.random(-1, 1), 0);
    }

    private void applySpread(Projectile projectile) {
        // Añadir dispersión aleatoria para la ametralladora
        float spreadAngle = (float)Math.toRadians(MathUtils.random(-5f, 5f));
        Vector2 spreadDirection = projectile.velocity.cpy().rotateRad(spreadAngle);
        projectile.velocity = spreadDirection;
    }

    public void setFireRate(float shotsPerSecond) {
        this.fireDelay = 1f / shotsPerSecond;
    }

    public boolean isFiring() {
        return isFiring;
    }
}
