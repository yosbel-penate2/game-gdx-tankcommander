package com.tankcommander.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.Vector2;
import com.tankcommander.entities.Entity;
import com.tankcommander.entities.components.*;
import com.tankcommander.input.*;
import com.tankcommander.weapons.Projectile;

import com.tankcommander.entities.components.PhysicsComponent;
import com.tankcommander.entities.components.TransformComponent;

/**
 * Controlador principal del juego que maneja la entrada del usuario.
 */
public class GameController {
    private XboxController xboxController;
    private InputMapper inputMapper;
    private Entity playerEntity;
    private GameWorld gameWorld;  // NUEVO: referencia al mundo
    private boolean useController;

    // Estados de entrada para disparo continuo
    private boolean isFiringCannon;
    private boolean isFiringMachineGun;
    private FireMachineGunCommand machineGunCommand;
    private float lastCannonShotTime;
    private float cannonCooldown;

    // Sensibilidad de controles
    private float movementDeadZone;
    private float turretDeadZone;
    private float mouseSensitivity;

    // Para rotación suave
    private float currentBodyAngle;
    private float currentTurretAngle;

    public GameController() {
        this.inputMapper = new InputMapper();
        this.useController = false;
        this.isFiringCannon = false;
        this.isFiringMachineGun = false;
        this.movementDeadZone = 0.2f;
        this.turretDeadZone = 0.15f;
        this.mouseSensitivity = 0.5f;
        this.cannonCooldown = 0.5f;
        this.lastCannonShotTime = 0;
        this.currentBodyAngle = 0;
        this.currentTurretAngle = 0;

        initializeInputMappings();
        initializeController();
    }

    // NUEVO: establecer referencia al mundo
    public void setGameWorld(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    private void initializeInputMappings() {
        // Mapeos para teclado (se pueden personalizar)
        // Movimiento - WASD
        inputMapper.mapInput(Input.Keys.W, new MoveCommand(new Vector2(0, 1)));
        inputMapper.mapInput(Input.Keys.S, new MoveCommand(new Vector2(0, -1)));
        inputMapper.mapInput(Input.Keys.A, new MoveCommand(new Vector2(-1, 0)));
        inputMapper.mapInput(Input.Keys.D, new MoveCommand(new Vector2(1, 0)));

        // Rotación de torreta - Flechas o mouse
        inputMapper.mapInput(Input.Keys.LEFT, new RotateTurretCommand(-5f, true));
        inputMapper.mapInput(Input.Keys.RIGHT, new RotateTurretCommand(5f, true));

        // Disparos
        inputMapper.mapInput(Input.Keys.SPACE, new FireCannonCommand());
        inputMapper.mapInput(Input.Keys.CONTROL_LEFT, new FireMachineGunCommand());

        // Alternativa con mouse (botones)
        inputMapper.mapInput(Input.Buttons.LEFT, new FireCannonCommand());
        inputMapper.mapInput(Input.Buttons.RIGHT, new FireMachineGunCommand());
    }

    private void initializeController() {
        if (Controllers.getControllers().size > 0) {
            xboxController = new XboxController(Controllers.getControllers().first());
            useController = true;
            Gdx.app.log("GameController", "Xbox Controller connected: " +
                Controllers.getControllers().first().getName());
        } else {
            useController = false;
            Gdx.app.log("GameController", "No controller found, using keyboard/mouse");
        }
    }

    public void setPlayerEntity(Entity player) {
        this.playerEntity = player;
        this.machineGunCommand = new FireMachineGunCommand();

        TransformComponent transform = playerEntity.getComponent(TransformComponent.class);
        if (transform != null) {
            this.currentBodyAngle = transform.rotation;
        }

        TurretComponent turret = playerEntity.getComponent(TurretComponent.class);
        if (turret != null) {
            this.currentTurretAngle = turret.turretAngle;
        }
    }

    public void processInput(float delta) {
        if (playerEntity == null) return;

        if (useController && xboxController != null) {
            processControllerInput(delta);
        } else {
            processKeyboardMouseInput(delta);
        }

        processContinuousFire(delta);
    }

    private void processControllerInput(float delta) {
        // NUEVO: actualizar el estado del controlador
        if (xboxController != null) {
            xboxController.update();
        }

        TankBodyComponent body = playerEntity.getComponent(TankBodyComponent.class);
        TurretComponent turret = playerEntity.getComponent(TurretComponent.class);
        WeaponComponent weapons = playerEntity.getComponent(WeaponComponent.class);
        TransformComponent transform = playerEntity.getComponent(TransformComponent.class);

        if (body == null || transform == null) return;

        // ========== MOVIMIENTO DEL CUERPO (Palanca izquierda) ==========
        Vector2 leftStick = xboxController.getLeftStick();

        if (leftStick.len() > movementDeadZone) {
            Vector2 moveDir = leftStick.cpy().nor();
            body.moveDirection = moveDir;

            float targetAngle = moveDir.angleDeg();
            float angleDiff = targetAngle - currentBodyAngle;
            angleDiff = (angleDiff + 360) % 360;
            if (angleDiff > 180) angleDiff -= 360;

            float rotationSpeed = body.turnSpeed;
            float maxRotation = rotationSpeed * delta;

            if (Math.abs(angleDiff) <= maxRotation) {
                currentBodyAngle = targetAngle;
            } else {
                currentBodyAngle += Math.signum(angleDiff) * maxRotation;
            }

            currentBodyAngle = (currentBodyAngle + 360) % 360;
            transform.rotation = currentBodyAngle;

        } else {
            body.moveDirection.setZero();
        }

        // ========== ROTACIÓN DE TORRETA (Palanca derecha) ==========
        if (turret != null) {
            Vector2 rightStick = xboxController.getRightStick();

            if (rightStick.len() > turretDeadZone) {
                float targetTurretAngle = rightStick.angleDeg();
                float angleDiff = targetTurretAngle - currentTurretAngle;
                angleDiff = (angleDiff + 360) % 360;
                if (angleDiff > 180) angleDiff -= 360;

                float turretRotationSpeed = turret.rotationSpeed;
                float maxTurretRotation = turretRotationSpeed * delta;

                if (Math.abs(angleDiff) <= maxTurretRotation) {
                    currentTurretAngle = targetTurretAngle;
                } else {
                    currentTurretAngle += Math.signum(angleDiff) * maxTurretRotation;
                }

                currentTurretAngle = (currentTurretAngle + 360) % 360;
                turret.rotateTo(currentTurretAngle);
            }
        }

        // ========== DISPARO DEL CAÑÓN (Botón LB / Z izquierdo) ==========
        // MODIFICADO: usar BUTTON_LB en lugar de BUTTON_X
        if (xboxController.isButtonPressed(XboxController.BUTTON_LB) ||      // Botón LB (Z izquierdo)
            xboxController.getLeftTrigger() > 0.5f) {
            float currentTime = System.currentTimeMillis() / 1000f;
            if (currentTime - lastCannonShotTime >= cannonCooldown && weapons != null && turret != null) {
                lastCannonShotTime = currentTime;

                // Calcular origen y dirección del disparo
                Vector2 fireOrigin = turret.getWorldPosition(transform.position, transform.rotation);
                float angleRad = (float)Math.toRadians(currentTurretAngle);
                Vector2 fireDirection = new Vector2(
                    (float)Math.cos(angleRad),
                    (float)Math.sin(angleRad)
                ).nor();

                weapons.switchWeapon(0); // Cañón
                Projectile projectile = weapons.fireCurrent(fireOrigin, fireDirection);

                // NUEVO: agregar el proyectil al mundo si existe
                if (projectile != null && gameWorld != null) {
                    addProjectileToWorld(projectile, fireOrigin, fireDirection);
                }

                Gdx.app.log("GameController", "Cannon fired from LB button!");
            }
        }

        // ========== DISPARO DE AMETRALLADORA (Botón RB / B) ==========
        if (xboxController.isButtonPressed(XboxController.BUTTON_B) ||
            xboxController.getRightTrigger() > 0.5f) {
            if (!isFiringMachineGun) {
                isFiringMachineGun = true;
                machineGunCommand.startFiring();
            }
            machineGunCommand.executeContinuous(playerEntity, delta);
        } else {
            if (isFiringMachineGun) {
                isFiringMachineGun = false;
                machineGunCommand.stopFiring();
            }
        }
    }

    // NUEVO: método para agregar proyectil al mundo
    private void addProjectileToWorld(Projectile projectile, Vector2 origin, Vector2 direction) {
        // Calcular velocidad del proyectil (400 unidades por segundo en la dirección)
        Vector2 velocity = direction.cpy().scl(400f);

        // Crear entidad proyectil
        Entity projectileEntity = new Entity();

        // Transformación
        TransformComponent transform = new TransformComponent(origin.cpy(), direction.angleDeg());
        projectileEntity.addComponent(transform);

        // Física
        PhysicsComponent physics = new PhysicsComponent();
        physics.velocity = velocity;
        physics.maxSpeed = velocity.len();
        projectileEntity.addComponent(physics);

        // Renderizado (opcional, puedes crear una textura pequeña)
        // Por ahora usamos una textura temporal

        // Componente de vida útil (se eliminará después de un tiempo)
        // Esto se manejará en un sistema de proyectiles más adelante

        gameWorld.addEntity(projectileEntity);

        Gdx.app.log("GameController", "Projectile added to world at: " + origin);
    }

    private void processKeyboardMouseInput(float delta) {
        TankBodyComponent body = playerEntity.getComponent(TankBodyComponent.class);
        TurretComponent turret = playerEntity.getComponent(TurretComponent.class);
        WeaponComponent weapons = playerEntity.getComponent(WeaponComponent.class);
        TransformComponent transform = playerEntity.getComponent(TransformComponent.class);

        if (body == null || transform == null) return;

        Vector2 moveDirection = new Vector2(0, 0);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) moveDirection.y += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) moveDirection.y -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) moveDirection.x -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) moveDirection.x += 1;

        if (moveDirection.len() > 0) {
            moveDirection.nor();
            body.moveDirection = moveDirection;

            float targetAngle = moveDirection.angleDeg();
            float angleDiff = targetAngle - currentBodyAngle;
            angleDiff = (angleDiff + 360) % 360;
            if (angleDiff > 180) angleDiff -= 360;

            float rotationSpeed = body.turnSpeed;
            float maxRotation = rotationSpeed * delta;

            if (Math.abs(angleDiff) <= maxRotation) {
                currentBodyAngle = targetAngle;
            } else {
                currentBodyAngle += Math.signum(angleDiff) * maxRotation;
            }

            currentBodyAngle = (currentBodyAngle + 360) % 360;
            transform.rotation = currentBodyAngle;
        } else {
            body.moveDirection.setZero();
        }

        if (turret != null) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.input.getY();

            Vector2 mouseWorldPos = new Vector2(mouseX, mouseY);
            Vector2 direction = mouseWorldPos.cpy().sub(transform.position).nor();
            if (direction.len() > 0) {
                float targetTurretAngle = direction.angleDeg();
                float angleDiff = targetTurretAngle - currentTurretAngle;
                angleDiff = (angleDiff + 360) % 360;
                if (angleDiff > 180) angleDiff -= 360;

                float turretRotationSpeed = turret.rotationSpeed;
                float maxTurretRotation = turretRotationSpeed * delta;

                if (Math.abs(angleDiff) <= maxTurretRotation) {
                    currentTurretAngle = targetTurretAngle;
                } else {
                    currentTurretAngle += Math.signum(angleDiff) * maxTurretRotation;
                }

                currentTurretAngle = (currentTurretAngle + 360) % 360;
                turret.rotateTo(currentTurretAngle);
            }
        }

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (!isFiringCannon && weapons != null && turret != null) {
                isFiringCannon = true;

                Vector2 fireOrigin = turret.getWorldPosition(transform.position, transform.rotation);
                float angleRad = (float)Math.toRadians(currentTurretAngle);
                Vector2 fireDirection = new Vector2(
                    (float)Math.cos(angleRad),
                    (float)Math.sin(angleRad)
                ).nor();

                weapons.switchWeapon(0);
                Projectile projectile = weapons.fireCurrent(fireOrigin, fireDirection);

                if (projectile != null && gameWorld != null) {
                    addProjectileToWorld(projectile, fireOrigin, fireDirection);
                }
            }
        } else {
            isFiringCannon = false;
        }

        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            if (!isFiringMachineGun) {
                isFiringMachineGun = true;
                machineGunCommand.startFiring();
            }
        } else {
            if (isFiringMachineGun) {
                isFiringMachineGun = false;
                machineGunCommand.stopFiring();
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if (!isFiringCannon && weapons != null && turret != null) {
                isFiringCannon = true;

                Vector2 fireOrigin = turret.getWorldPosition(transform.position, transform.rotation);
                float angleRad = (float)Math.toRadians(currentTurretAngle);
                Vector2 fireDirection = new Vector2(
                    (float)Math.cos(angleRad),
                    (float)Math.sin(angleRad)
                ).nor();

                weapons.switchWeapon(0);
                Projectile projectile = weapons.fireCurrent(fireOrigin, fireDirection);

                if (projectile != null && gameWorld != null) {
                    addProjectileToWorld(projectile, fireOrigin, fireDirection);
                }
            }
        } else {
            isFiringCannon = false;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            machineGunCommand.startFiring();
        } else {
            machineGunCommand.stopFiring();
        }
    }

    private void processContinuousFire(float delta) {
        if (machineGunCommand != null && machineGunCommand.isFiring()) {
            machineGunCommand.executeContinuous(playerEntity, delta);
        }
    }

    public void updateAngles() {
        if (playerEntity == null) return;

        TransformComponent transform = playerEntity.getComponent(TransformComponent.class);
        if (transform != null) {
            currentBodyAngle = transform.rotation;
        }

        TurretComponent turret = playerEntity.getComponent(TurretComponent.class);
        if (turret != null) {
            currentTurretAngle = turret.turretAngle;
        }
    }

    public void toggleController() {
        if (Controllers.getControllers().size > 0) {
            useController = !useController;
            Gdx.app.log("GameController", "Controller mode: " + (useController ? "ON" : "OFF"));
        }
    }

    public void vibrate(float duration, float intensity) {
        if (useController && xboxController != null) {
            Gdx.app.log("GameController", "Vibrate: " + duration + "s, intensity: " + intensity);
        }
    }

    public void setMouseSensitivity(float sensitivity) {
        this.mouseSensitivity = Math.max(0.1f, Math.min(2f, sensitivity));
    }

    public boolean isUsingController() {
        return useController;
    }

    public void dispose() {
        // Limpiar recursos
    }
}
