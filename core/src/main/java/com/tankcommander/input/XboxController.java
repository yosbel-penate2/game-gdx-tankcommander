package com.tankcommander.input;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.math.Vector2;

public class XboxController implements ControllerListener {
    private Controller controller;
    private float deadZone;

    // Almacenar estado de los botones
    private boolean[] buttonStates;
    private boolean[] previousButtonStates;

    // Botones estándar de Xbox
    public static final int BUTTON_A = 0;
    public static final int BUTTON_B = 1;
    public static final int BUTTON_X = 2;
    public static final int BUTTON_Y = 3;
    public static final int BUTTON_LB = 4;
    public static final int BUTTON_RB = 5;
    public static final int BUTTON_BACK = 6;
    public static final int BUTTON_START = 7;
    public static final int BUTTON_LS = 8;
    public static final int BUTTON_RS = 9;

    // Constantes para los ejes
    private static final int AXIS_LEFT_X = 0;
    private static final int AXIS_LEFT_Y = 1;
    private static final int AXIS_RIGHT_X = 2;
    private static final int AXIS_RIGHT_Y = 3;
    private static final int AXIS_LEFT_TRIGGER = 4;
    private static final int AXIS_RIGHT_TRIGGER = 5;

    public XboxController(Controller controller) {
        this.controller = controller;
        this.deadZone = 0.2f;
        this.buttonStates = new boolean[16];
        this.previousButtonStates = new boolean[16];
        controller.addListener(this);

        System.out.println("XboxController initialized: " + controller.getName());
    }

    public Vector2 getLeftStick() {
        float x = controller.getAxis(AXIS_LEFT_X);
        float y = -controller.getAxis(AXIS_LEFT_Y);
        return applyDeadZone(new Vector2(x, y));
    }

    public Vector2 getRightStick() {
        float x = controller.getAxis(AXIS_RIGHT_X);
        float y = -controller.getAxis(AXIS_RIGHT_Y);
        return applyDeadZone(new Vector2(x, y));
    }

    private Vector2 applyDeadZone(Vector2 input) {
        if (input.len() < deadZone) {
            return Vector2.Zero;
        }
        return input;
    }

    public float getLeftTrigger() {
        return controller.getAxis(AXIS_LEFT_TRIGGER);
    }

    public float getRightTrigger() {
        return controller.getAxis(AXIS_RIGHT_TRIGGER);
    }

    public boolean isButtonPressed(int buttonCode) {
        if (buttonCode < 0 || buttonCode >= buttonStates.length) {
            return false;
        }
        return buttonStates[buttonCode];
    }

    public boolean isButtonJustPressed(int buttonCode) {
        if (buttonCode < 0 || buttonCode >= buttonStates.length) {
            return false;
        }
        return buttonStates[buttonCode] && !previousButtonStates[buttonCode];
    }

    // NUEVO: método update que necesita GameController
    public void update() {
        System.arraycopy(buttonStates, 0, previousButtonStates, 0, buttonStates.length);
    }

    @Override
    public void connected(Controller controller) {
        System.out.println("Controller connected: " + controller.getName());
    }

    @Override
    public void disconnected(Controller controller) {
        System.out.println("Controller disconnected: " + controller.getName());
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        if (buttonCode >= 0 && buttonCode < buttonStates.length) {
            buttonStates[buttonCode] = true;
            System.out.println("Button pressed: " + buttonCode + " (" + getButtonName(buttonCode) + ")");
        }
        return true;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        if (buttonCode >= 0 && buttonCode < buttonStates.length) {
            buttonStates[buttonCode] = false;
            System.out.println("Button released: " + buttonCode + " (" + getButtonName(buttonCode) + ")");
        }
        return true;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        return false;
    }

    private String getButtonName(int buttonCode) {
        switch (buttonCode) {
            case BUTTON_A: return "A";
            case BUTTON_B: return "B";
            case BUTTON_X: return "X";
            case BUTTON_Y: return "Y";
            case BUTTON_LB: return "LB (Z Izquierdo)";
            case BUTTON_RB: return "RB (Z Derecho)";
            case BUTTON_BACK: return "BACK";
            case BUTTON_START: return "START";
            case BUTTON_LS: return "LEFT STICK";
            case BUTTON_RS: return "RIGHT STICK";
            default: return "UNKNOWN";
        }
    }
}
