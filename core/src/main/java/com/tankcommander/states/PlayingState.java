package com.tankcommander.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.tankcommander.core.TankCommanderGame;
import com.tankcommander.core.GameWorld;
import com.tankcommander.core.GameController;
import com.tankcommander.entities.Entity;
import com.tankcommander.entities.factories.EntityFactory;

public class PlayingState implements GameState {
    private TankCommanderGame game;
    private GameWorld world;
    private GameController controller;
    private EntityFactory entityFactory;
    private Entity player;
    private float enemySpawnTimer;
    private float enemySpawnDelay;

    public PlayingState(TankCommanderGame game) {
        this.game = game;
        this.world = new GameWorld();
        this.controller = new GameController();
        this.entityFactory = new EntityFactory(game.assetManager);
        this.enemySpawnTimer = 0f;
        this.enemySpawnDelay = 3f;
    }

    @Override
    public void enter() {
        player = entityFactory.createPlayer();
        world.addEntity(player);
        controller.setPlayerEntity(player);

        // Create some obstacles
        for (int i = 0; i < 10; i++) {
            world.addEntity(entityFactory.createObstacle(
                com.tankcommander.entities.factories.ObstacleType.WALL,
                new com.badlogic.gdx.math.Vector2(200 + i * 80, 300)
            ));
        }
    }

    @Override
    public void update(float delta) {
        // Update input
        controller.processInput(delta);

        // Spawn enemies
        enemySpawnTimer += delta;
        if (enemySpawnTimer >= enemySpawnDelay) {
            enemySpawnTimer = 0;
            Entity enemy = entityFactory.createEnemy(
                com.tankcommander.entities.factories.EnemyType.BASIC,
                new com.badlogic.gdx.math.Vector2(
                    (float) Math.random() * TankCommanderGame.VIRTUAL_WIDTH,
                    (float) Math.random() * TankCommanderGame.VIRTUAL_HEIGHT
                )
            );
            world.addEntity(enemy);
        }

        // Update world
        world.update(delta);

        // Check game over
        if (player.isMarkedForRemoval()) {
            game.stateMachine.changeState("gameover");
        }

        // Pause
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.stateMachine.changeState("pause");
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);

        world.render(game.batch, game.camera);
    }

    @Override
    public void exit() {
        // Clean up
    }
}
