package com.tankcommander;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tankcommander.core.GameController;
import com.tankcommander.core.GameWorld;
import com.tankcommander.entities.Entity;
import com.tankcommander.entities.factories.EntityFactory;
import com.tankcommander.events.EventManager;
import com.tankcommander.states.StateMachine;
import com.tankcommander.states.PlayingState;

public class TankCommanderGame extends ApplicationAdapter {
    public static final int VIRTUAL_WIDTH = 1280;
    public static final int VIRTUAL_HEIGHT = 720;

    public SpriteBatch batch;
    public OrthographicCamera camera;
    public Viewport viewport;
    public GameWorld gameWorld;
    public GameController gameController;
    public EntityFactory entityFactory;
    public EventManager eventManager;
    public StateMachine stateMachine;

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        camera.position.set(VIRTUAL_WIDTH / 2f, VIRTUAL_HEIGHT / 2f, 0);

        eventManager = new EventManager();
        gameWorld = new GameWorld(camera, batch);
        gameController = new GameController();
        entityFactory = new EntityFactory(null, eventManager); // Cargar assets después

        // Configurar mundo
        Entity player = entityFactory.createPlayer();
        gameWorld.addEntity(player);
        gameWorld.setPlayer(player);
        gameController.setPlayerEntity(player);

        // Estado de juego
        stateMachine = new StateMachine();
        PlayingState playingState = new PlayingState(this);
        stateMachine.addState("playing", playingState);
        stateMachine.changeState("playing");
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        // Procesar entrada
        gameController.processInput(delta);

        // Actualizar mundo
        gameWorld.update(delta);

        // Actualizar máquina de estados
        stateMachine.update(delta);

        // Renderizar
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        gameWorld.render(batch, camera);

        // Renderizar estado actual
        stateMachine.render();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        gameWorld.dispose();
        entityFactory.dispose();
    }
}
