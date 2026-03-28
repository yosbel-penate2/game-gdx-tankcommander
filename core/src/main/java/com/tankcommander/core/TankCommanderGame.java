package com.tankcommander.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tankcommander.entities.Entity;
import com.tankcommander.entities.components.TransformComponent;
import com.tankcommander.entities.factories.EntityFactory;
import com.tankcommander.entities.factories.ObstacleType;
import com.tankcommander.events.EventManager;
import com.tankcommander.states.StateMachine;
import com.tankcommander.states.PlayingState;

public class TankCommanderGame extends Game {
    public static final int VIRTUAL_WIDTH = 1280;
    public static final int VIRTUAL_HEIGHT = 720;
    public static final float PPM = 32f;

    public SpriteBatch batch;
    public OrthographicCamera camera;
    public Viewport viewport;
    public AssetManager assetManager;
    public StateMachine stateMachine;

    // Referencias principales
    public GameWorld gameWorld;
    public GameController gameController;
    public EntityFactory entityFactory;
    public EventManager eventManager;
    public Entity player;

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        camera.position.set(VIRTUAL_WIDTH / 2f, VIRTUAL_HEIGHT / 2f, 0);
        assetManager = new AssetManager();

        // Inicializar sistemas
        eventManager = new EventManager();
        gameWorld = new GameWorld(camera, batch);
        gameController = new GameController();
        entityFactory = new EntityFactory(assetManager, eventManager);

        // NUEVO: conectar GameController con GameWorld
        gameController.setGameWorld(gameWorld);

        entityFactory = new EntityFactory(assetManager, eventManager);

        // Crear jugador en el centro de la pantalla
        player = entityFactory.createPlayer();
        TransformComponent playerTransform = player.getComponent(TransformComponent.class);
        if (playerTransform != null) {
            playerTransform.position.set(VIRTUAL_WIDTH / 2f, VIRTUAL_HEIGHT / 2f);
        }

        // Añadir jugador al mundo
        gameWorld.addEntity(player);
        gameWorld.setPlayer(player);
        gameController.setPlayerEntity(player);

        // Agregar algunos obstáculos de prueba alrededor del jugador
        crearObstaculosPrueba();

        stateMachine = new StateMachine();
        stateMachine.addState("playing", new PlayingState(this));
        stateMachine.changeState("playing");
    }

    private void crearObstaculosPrueba() {
        float centerX = VIRTUAL_WIDTH / 2f;
        float centerY = VIRTUAL_HEIGHT / 2f;

        // Obstáculos alrededor del jugador
        gameWorld.addEntity(entityFactory.createObstacle(
            ObstacleType.WALL,
            new Vector2(centerX + 150, centerY)
        ));
        gameWorld.addEntity(entityFactory.createObstacle(
            ObstacleType.WALL,
            new Vector2(centerX - 150, centerY)
        ));
        gameWorld.addEntity(entityFactory.createObstacle(
            ObstacleType.WALL,
            new Vector2(centerX, centerY + 150)
        ));
        gameWorld.addEntity(entityFactory.createObstacle(
            ObstacleType.WALL,
            new Vector2(centerX, centerY - 150)
        ));
        gameWorld.addEntity(entityFactory.createObstacle(
            ObstacleType.BARREL,
            new Vector2(centerX + 80, centerY + 80)
        ));
        gameWorld.addEntity(entityFactory.createObstacle(
            ObstacleType.TREE,
            new Vector2(centerX - 80, centerY - 80)
        ));
    }

    @Override
    public void render() {
        float delta = Math.min(1/30f, Gdx.graphics.getDeltaTime());

        // LIMPIAR LA PANTALLA - ESTO EVITA EL RASTRO
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Procesar entrada del jugador
        gameController.processInput(delta);

        // Actualizar mundo (físicas, IA, cámara)
        gameWorld.update(delta);

        // Actualizar máquina de estados
        stateMachine.update(delta);

        // Renderizar el mundo
        gameWorld.render(batch, camera);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        assetManager.dispose();
        gameWorld.dispose();
        entityFactory.dispose();
    }
}
