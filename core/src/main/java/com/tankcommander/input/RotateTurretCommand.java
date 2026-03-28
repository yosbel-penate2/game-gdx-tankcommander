package com.tankcommander.input;

import com.badlogic.gdx.math.Vector2;
import com.tankcommander.entities.Entity;
import com.tankcommander.entities.components.TurretComponent;

/**
 * Comando para rotar la torreta del tanque de forma independiente al cuerpo.
 */
public class RotateTurretCommand implements InputCommand {
    private Vector2 aimDirection;
    private float targetAngle;
    private boolean useDirectionVector;
    private boolean useAbsoluteAngle;

    /**
     * Constructor para rotación basada en vector de dirección.
     * @param aimDirection Vector de dirección hacia donde apuntar
     */
    public RotateTurretCommand(Vector2 aimDirection) {
        this.aimDirection = aimDirection;
        this.useDirectionVector = true;
        this.useAbsoluteAngle = false;
    }

    /**
     * Constructor para rotación basada en ángulo absoluto.
     * @param angle Ángulo en grados
     */
    public RotateTurretCommand(float angle) {
        this.targetAngle = angle;
        this.useDirectionVector = false;
        this.useAbsoluteAngle = true;
    }

    /**
     * Constructor para rotación diferencial (por ejemplo, desde el joystick).
     * @param deltaAngle Cambio de ángulo en grados
     * @param incremental true para rotación incremental
     */
    public RotateTurretCommand(float deltaAngle, boolean incremental) {
        if (incremental) {
            this.targetAngle = deltaAngle;
            this.useDirectionVector = false;
            this.useAbsoluteAngle = false;
        } else {
            this.targetAngle = deltaAngle;
            this.useDirectionVector = false;
            this.useAbsoluteAngle = true;
        }
    }

    @Override
    public void execute(Entity entity) {
        TurretComponent turret = entity.getComponent(TurretComponent.class);

        if (turret == null) {
            return;
        }

        if (useDirectionVector && aimDirection != null) {
            // Rotar basado en vector de dirección (joystick derecho)
            if (aimDirection.len() > 0.1f) {
                float angle = aimDirection.angleDeg();
                turret.rotateTo(angle);
            }
        } else if (useAbsoluteAngle) {
            // Rotar a un ángulo absoluto específico
            turret.rotateTo(targetAngle);
        } else {
            // Rotación incremental (para teclado o ajustes finos)
            float newAngle = turret.turretAngle + targetAngle;
            // Normalizar ángulo entre 0 y 360
            newAngle = (newAngle + 360) % 360;
            turret.rotateTo(newAngle);
        }
    }

    /**
     * Actualiza la dirección de apuntado para rotación continua.
     * Útil cuando se usa con joystick que se actualiza cada frame.
     */
    public void updateAimDirection(Vector2 newDirection) {
        this.aimDirection = newDirection;
        this.useDirectionVector = true;
        this.useAbsoluteAngle = false;
    }

    /**
     * Establece el ángulo objetivo para rotación continua.
     */
    public void setTargetAngle(float angle) {
        this.targetAngle = angle;
        this.useDirectionVector = false;
        this.useAbsoluteAngle = true;
    }

    /**
     * Rota la torreta suavemente hacia el objetivo (útil para IA).
     */
    public void executeSmooth(Entity entity, float delta, float rotationSpeed) {
        TurretComponent turret = entity.getComponent(TurretComponent.class);

        if (turret == null) return;

        float currentAngle = turret.turretAngle;
        float targetAngleToUse = targetAngle;

        if (useDirectionVector && aimDirection != null && aimDirection.len() > 0.1f) {
            targetAngleToUse = aimDirection.angleDeg();
        }

        // Calcular la diferencia más corta entre ángulos
        float angleDiff = targetAngleToUse - currentAngle;
        angleDiff = (angleDiff + 360) % 360;
        if (angleDiff > 180) angleDiff -= 360;

        // Limitar velocidad de rotación
        float maxRotation = rotationSpeed * delta;
        float finalAngle;

        if (Math.abs(angleDiff) <= maxRotation) {
            finalAngle = targetAngleToUse;
        } else {
            finalAngle = currentAngle + Math.signum(angleDiff) * maxRotation;
        }

        turret.rotateTo(finalAngle);
    }

    /**
     * Limita la rotación de la torreta a un arco específico (útil para ciertos tanques).
     */
    public void executeWithConstraints(Entity entity, float minAngle, float maxAngle) {
        TurretComponent turret = entity.getComponent(TurretComponent.class);

        if (turret == null) return;

        float constrainedAngle = turret.turretAngle;

        if (useAbsoluteAngle) {
            constrainedAngle = targetAngle;
        } else if (useDirectionVector && aimDirection != null) {
            constrainedAngle = aimDirection.angleDeg();
        }

        // Aplicar restricciones de ángulo
        constrainedAngle = Math.max(minAngle, Math.min(maxAngle, constrainedAngle));
        turret.rotateTo(constrainedAngle);
    }
}
