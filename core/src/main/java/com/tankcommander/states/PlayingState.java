package com.tankcommander.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.tankcommander.core.TankCommanderGame;
import com.tankcommander.entities.Entity;
import com.tankcommander.entities.factories.EntityFactory;
import com.tankcommander.entities.factories.EnemyType;
import com.tankcommander.entities.factories.ObstacleType;
import com.badlogic.gdx.math.Vector2;

public class PlayingState implements GameState {
    private TankCommanderGame game;
    private Entity player;
    private float enemySpawnTimer;
    private float enemySpawnDelay;

    public PlayingState(TankCommanderGame game) {
        this.game = game;
        this.enemySpawnTimer = 0f;
        this.enemySpawnDelay = 3f;
    }

    @Override
    public void enter() {
        // Usar las instancias que ya existen en TankCommanderGame
        player = game.player;

        // Asegurar que el controlador tiene la entidad del jugador
        game.gameController.setPlayerEntity(player);

        // Crear algunos obstáculos alrededor del jugador
        float centerX = TankCommanderGame.VIRTUAL_WIDTH / 2f;
        float centerY = TankCommanderGame.VIRTUAL_HEIGHT / 2f;

        // Obstáculos en cruz
        game.gameWorld.addEntity(game.entityFactory.createObstacle(
            ObstacleType.WALL,
            new Vector2(centerX + 150, centerY)
        ));
        game.gameWorld.addEntity(game.entityFactory.createObstacle(
            ObstacleType.WALL,
            new Vector2(centerX - 150, centerY)
        ));
        game.gameWorld.addEntity(game.entityFactory.createObstacle(
            ObstacleType.WALL,
            new Vector2(centerX, centerY + 150)
        ));
        game.gameWorld.addEntity(game.entityFactory.createObstacle(
            ObstacleType.WALL,
            new Vector2(centerX, centerY - 150)
        ));

        // Obstáculos en diagonal
        game.gameWorld.addEntity(game.entityFactory.createObstacle(
            ObstacleType.BARREL,
            new Vector2(centerX + 100, centerY + 100)
        ));
        game.gameWorld.addEntity(game.entityFactory.createObstacle(
            ObstacleType.TREE,
            new Vector2(centerX - 100, centerY - 100)
        ));

        Gdx.app.log("PlayingState", "Entered playing state");
    }

    @Override
    public void update(float delta) {
        // Actualizar entrada (ya se hace en TankCommanderGame.render())
        // No llamar a controller.processInput aquí porque ya se llama en TankCommanderGame

        // Spawn de enemigos
        enemySpawnTimer += delta;
        if (enemySpawnTimer >= enemySpawnDelay) {
            enemySpawnTimer = 0;

            // Generar enemigo alrededor del jugador
            float centerX = game.player.getComponent(
                com.tankcommander.entities.components.TransformComponent.class).position.x;
            float centerY = game.player.getComponent(
                com.tankcommander.entities.components.TransformComponent.class).position.y;

            float angle = (float) (Math.random() * Math.PI * 2);
            float radius = 400f;
            float x = centerX + (float) Math.cos(angle) * radius;
            float y = centerY + (float) Math.sin(angle) * radius;

            Entity enemy = game.entityFactory.createEnemy(
                EnemyType.BASIC,
                new Vector2(x, y)
            );
            game.gameWorld.addEntity(enemy);
        }

        // Verificar game over
        if (player.isMarkedForRemoval()) {
            game.stateMachine.changeState("gameover");
        }

        // Pausa
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.stateMachine.changeState("pause");
        }
    }

    @Override
    public void render() {
        // NO hacer nada aquí - el renderizado ya se hace en TankCommanderGame.render()
        // Este método se mantiene vacío o solo para UI
    }

    @Override
    public void exit() {
        Gdx.app.log("PlayingState", "Exited playing state");
    }

    @Override
    public void pause() {
        Gdx.app.log("PlayingState", "Paused");
    }

    @Override
    public void resume() {
        Gdx.app.log("PlayingState", "Resumed");
    }
}
