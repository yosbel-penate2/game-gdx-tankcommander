package com.tankcommander.systems;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.tankcommander.entities.Entity;
import com.tankcommander.entities.components.PhysicsComponent;
import com.tankcommander.entities.components.TransformComponent;
import com.tankcommander.entities.components.TankBodyComponent;
import com.tankcommander.entities.components.TurretComponent;
import com.tankcommander.entities.components.WeaponComponent;

public class AISystem implements GameSystem {
    private Entity target;

    public AISystem(Entity target) {
        this.target = target;
    }

    public void setTarget(Entity target) {
        this.target = target;
    }

    @Override
    public void update(float delta, Array<Entity> entities) {
        if (target == null) return;

        TransformComponent targetTransform = target.getComponent(TransformComponent.class);
        if (targetTransform == null) return;

        for (Entity entity : entities) {
            if (entity == target) continue;

            TransformComponent aiTransform = entity.getComponent(TransformComponent.class);
            PhysicsComponent aiPhysics = entity.getComponent(PhysicsComponent.class);
            TankBodyComponent aiBody = entity.getComponent(TankBodyComponent.class);
            TurretComponent aiTurret = entity.getComponent(TurretComponent.class);
            WeaponComponent aiWeapon = entity.getComponent(WeaponComponent.class);

            if (aiTransform != null && aiPhysics != null && aiBody != null) {
                // Move towards player
                Vector2 directionToTarget = targetTransform.position.cpy()
                    .sub(aiTransform.position)
                    .nor();

                aiBody.moveDirection = directionToTarget;

                // Rotate turret towards player
                if (aiTurret != null) {
                    float angleToTarget = directionToTarget.angleDeg();
                    aiTurret.rotateTo(angleToTarget);
                }

                // Fire when close enough and facing player
                float distanceToTarget = aiTransform.position.dst(targetTransform.position);
                if (distanceToTarget < 200f && aiTurret != null && aiWeapon != null) {
                    Vector2 direction = targetTransform.position.cpy()
                        .sub(aiTurret.getWorldPosition(aiTransform.position, aiTransform.rotation))
                        .nor();

                    aiWeapon.fireCurrent(
                        aiTurret.getWorldPosition(aiTransform.position, aiTransform.rotation),
                        direction
                    );
                }
            }
        }
    }
}
