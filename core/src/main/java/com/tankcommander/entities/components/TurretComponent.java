package com.tankcommander.entities.components;

import com.badlogic.gdx.math.Vector2;

public class TurretComponent implements Component {
    public float turretAngle;
    public float rotationSpeed;
    public Vector2 offsetFromBody;

    public TurretComponent() {
        this.turretAngle = 0f;
        this.rotationSpeed = 180f;
        this.offsetFromBody = new Vector2(0, 0);
    }

    public TurretComponent(Vector2 offset) {
        this.turretAngle = 0f;
        this.rotationSpeed = 180f;
        this.offsetFromBody = offset;
    }

    public void rotateTo(float angle) {
        this.turretAngle = angle;
    }

    /**
     * Obtiene la posición mundial de la torreta.
     * @param bodyPosition Posición del cuerpo del tanque
     * @param bodyRotation Rotación del cuerpo en GRADOS
     */
    public Vector2 getWorldPosition(Vector2 bodyPosition, float bodyRotation) {
        // Rotar el offset según la rotación del cuerpo (usando grados)
        Vector2 rotatedOffset = offsetFromBody.cpy();
        rotatedOffset.rotateDeg(bodyRotation);  // Cambiar rotateRad a rotateDeg
        return bodyPosition.cpy().add(rotatedOffset);
    }

    @Override
    public void update(float delta) {
        // Rotation logic handled by input system
    }
}
