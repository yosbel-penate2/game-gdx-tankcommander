package com.tankcommander.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tankcommander.states.StateMachine;
import com.tankcommander.states.PlayingState;

public class TankCommanderGame extends Game {
    public static final int VIRTUAL_WIDTH = 1280;
    public static final int VIRTUAL_HEIGHT = 720;
    public static final float PPM = 32f; // Pixels per meter for Box2D

    public SpriteBatch batch;
    public OrthographicCamera camera;
    public Viewport viewport;
    public AssetManager assetManager;
    public StateMachine stateMachine;

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        camera.position.set(VIRTUAL_WIDTH / 2f, VIRTUAL_HEIGHT / 2f, 0);
        assetManager = new AssetManager();

        stateMachine = new StateMachine();
        stateMachine.addState("playing", new PlayingState(this));
        stateMachine.changeState("playing");
    }

    @Override
    public void render() {
        float delta = Math.min(1/30f, Gdx.graphics.getDeltaTime());
        stateMachine.update(delta);
        super.render();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        assetManager.dispose();
    }
}
