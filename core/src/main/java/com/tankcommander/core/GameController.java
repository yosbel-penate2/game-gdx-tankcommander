package com.tankcommander.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.Vector2;
import com.tankcommander.entities.Entity;
import com.tankcommander.entities.components.TankBodyComponent;
import com.tankcommander.input.*;

/**
 * Controlador principal del juego que maneja la entrada del usuario.
 * Traduce las entradas del teclado y joystick en comandos para el tanque.
 */
public class GameController {
    private XboxController xboxController;
    private InputMapper inputMapper;
    private Entity playerEntity;
    private boolean useController;

    // Estados de entrada para disparo continuo
    private boolean isFiringCannon;
    private boolean isFiringMachineGun;
    private FireMachineGunCommand machineGunCommand;
    private float fireCooldownTimer;

    // Sensibilidad de controles
    private float movementDeadZone;
    private float turretDeadZone;
    private float mouseSensitivity;

    public GameController() {
        this.inputMapper = new InputMapper();
        this.useController = false;
        this.isFiringCannon = false;
        this.isFiringMachineGun = false;
        this.movementDeadZone = 0.2f;
        this.turretDeadZone = 0.15f;
        this.mouseSensitivity = 0.5f;

        initializeInputMappings();
        initializeController();
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
        // Buscar controlador Xbox conectado
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

    /**
     * Establece la entidad del jugador que será controlada.
     */
    public void setPlayerEntity(Entity player) {
        this.playerEntity = player;
        this.machineGunCommand = new FireMachineGunCommand();
    }

    /**
     * Procesa toda la entrada del usuario cada frame.
     */
    public void processInput(float delta) {
        if (playerEntity == null) return;

        if (useController && xboxController != null) {
            processControllerInput(delta);
        } else {
            processKeyboardMouseInput(delta);
        }

        // Procesar disparo continuo
        processContinuousFire(delta);
    }

    private void processControllerInput(float delta) {
        // Movimiento del cuerpo con palanca izquierda
        Vector2 leftStick = xboxController.getLeftStick();
        if (leftStick.len() > movementDeadZone) {
            MoveCommand moveCommand = new MoveCommand(leftStick);
            moveCommand.execute(playerEntity);
        } else {
            // Detener movimiento
            TankBodyComponent body = playerEntity.getComponent(TankBodyComponent.class);
            if (body != null) {
                body.moveDirection.setZero();
            }
        }

        // Rotación de torreta con palanca derecha
        Vector2 rightStick = xboxController.getRightStick();
        if (rightStick.len() > turretDeadZone) {
            RotateTurretCommand turretCommand = new RotateTurretCommand(rightStick);
            turretCommand.execute(playerEntity);
        }

        // Disparo del cañón (botón X / Cuadrado)
        if (xboxController.isButtonPressed(XboxController.BUTTON_X)) {
            if (!isFiringCannon) {
                isFiringCannon = true;
                FireCannonCommand fireCommand = new FireCannonCommand();
                fireCommand.execute(playerEntity);
            }
        } else {
            isFiringCannon = false;
        }

        // Disparo de ametralladora (botón B / Círculo)
        if (xboxController.isButtonPressed(XboxController.BUTTON_B)) {
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

        // Gatillos para disparos alternativos (opcional)
        float leftTrigger = xboxController.getLeftTrigger();
        if (leftTrigger > 0.5f) {
            FireCannonCommand fireCommand = new FireCannonCommand();
            fireCommand.execute(playerEntity);
        }

        float rightTrigger = xboxController.getRightTrigger();
        if (rightTrigger > 0.5f) {
            machineGunCommand.startFiring();
            machineGunCommand.executeContinuous(playerEntity, delta);
        } else if (!xboxController.isButtonPressed(XboxController.BUTTON_B)) {
            machineGunCommand.stopFiring();
        }
    }

    private void processKeyboardMouseInput(float delta) {
        // Movimiento con teclado (WASD)
        Vector2 moveDirection = new Vector2(0, 0);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) moveDirection.y += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) moveDirection.y -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) moveDirection.x -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) moveDirection.x += 1;

        if (moveDirection.len() > 0) {
            moveDirection.nor();
            MoveCommand moveCommand = new MoveCommand(moveDirection);
            moveCommand.execute(playerEntity);
        } else {
            TankBodyComponent body = playerEntity.getComponent(TankBodyComponent.class);
            if (body != null) {
                body.moveDirection.setZero();
            }
        }

        // Rotación de torreta con mouse
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.input.getY();

        // Convertir coordenadas de pantalla a mundo
        Vector2 mouseWorldPos = new Vector2(mouseX, mouseY);
        // Asumiendo que tienes acceso a la cámara, aquí simplificado
        // En implementación real, deberías proyectar las coordenadas

        TransformComponent transform = playerEntity.getComponent(TransformComponent.class);
        if (transform != null) {
            Vector2 direction = mouseWorldPos.cpy().sub(transform.position).nor();
            RotateTurretCommand turretCommand = new RotateTurretCommand(direction);
            turretCommand.execute(playerEntity);
        }

        // Disparos con mouse
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (!isFiringCannon) {
                isFiringCannon = true;
                FireCannonCommand fireCommand = new FireCannonCommand();
                fireCommand.execute(playerEntity);
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

        // Disparo continuo con teclado
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if (!isFiringCannon) {
                isFiringCannon = true;
                FireCannonCommand fireCommand = new FireCannonCommand();
                fireCommand.execute(playerEntity);
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

    /**
     * Cambia entre controlador y teclado/mouse.
     */
    public void toggleController() {
        if (Controllers.getControllers().size > 0) {
            useController = !useController;
            Gdx.app.log("GameController", "Controller mode: " + (useController ? "ON" : "OFF"));
        }
    }

    /**
     * Vibra el controlador si está disponible.
     */
    public void vibrate(float duration, float intensity) {
        if (useController && xboxController != null) {
            // Implementar vibración cuando LibGDX lo soporte
            // xboxController.vibrate(duration, intensity);
        }
    }

    /**
     * Establece sensibilidad del mouse para rotación de torreta.
     */
    public void setMouseSensitivity(float sensitivity) {
        this.mouseSensitivity = Math.max(0.1f, Math.min(2f, sensitivity));
    }

    /**
     * Obtiene el estado actual del controlador.
     */
    public boolean isUsingController() {
        return useController;
    }

    /**
     * Libera recursos.
     */
    public void dispose() {
        // Limpiar recursos si es necesario
    }
}
