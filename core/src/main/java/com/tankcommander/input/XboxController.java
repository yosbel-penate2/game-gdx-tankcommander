package com.tankcommander.input;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class XboxController implements ControllerListener {
    private Controller controller;
    private float deadZone;

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

    public XboxController(Controller controller) {
        this.controller = controller;
        this.deadZone = 0.2f;
        controller.addListener(this);
    }

    public Vector2 getLeftStick() {
        float x = controller.getAxis(0);
        float y = -controller.getAxis(1);
        return applyDeadZone(new Vector2(x, y));
    }

    public Vector2 getRightStick() {
        float x = controller.getAxis(2);
        float y = -controller.getAxis(3);
        return applyDeadZone(new Vector2(x, y));
    }

    private Vector2 applyDeadZone(Vector2 input) {
        if (input.len() < deadZone) {
            return Vector2.Zero;
        }
        return input;
    }

    public float getLeftTrigger() {
        return controller.getAxis(4);
    }

    public float getRightTrigger() {
        return controller.getAxis(5);
    }

    public boolean isButtonPressed(int buttonCode) {
        return controller.getButton(buttonCode);
    }

    @Override
    public void connected(Controller controller) {
        // Controller connected
    }

    @Override
    public void disconnected(Controller controller) {
        // Controller disconnected
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        return false;
    }

}
