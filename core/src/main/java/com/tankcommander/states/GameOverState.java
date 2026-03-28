package com.tankcommander.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.tankcommander.core.TankCommanderGame;

/**
 * Estado de fin del juego.
 * Muestra la puntuación final y opciones para reiniciar o salir.
 */
public class GameOverState implements GameState {
    private TankCommanderGame game;
    private BitmapFont font;
    private SpriteBatch batch;
    private OrthographicCamera uiCamera;
    private int score;
    private float elapsedTime;
    private float blinkTimer;
    private boolean showPrompt;

    public GameOverState(TankCommanderGame game, int score) {
        this.game = game;
        this.score = score;
        this.font = new BitmapFont();
        this.batch = new SpriteBatch();
        this.uiCamera = new OrthographicCamera();
        this.uiCamera.setToOrtho(false, TankCommanderGame.VIRTUAL_WIDTH, TankCommanderGame.VIRTUAL_HEIGHT);
        this.elapsedTime = 0f;
        this.blinkTimer = 0f;
        this.showPrompt = true;
    }

    @Override
    public void enter() {
        // Reproducir sonido de game over si existe
        // Gdx.audio.newSound(Gdx.files.internal("gameover.wav")).play();
    }

    @Override
    public void update(float delta) {
        elapsedTime += delta;
        blinkTimer += delta;

        if (blinkTimer >= 0.5f) {
            showPrompt = !showPrompt;
            blinkTimer = 0;
        }

        handleInput();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();

        // Efecto de fondo (opcional)
        if (game.assetManager.isLoaded("gameover_bg.png", Texture.class)) {
            batch.draw(game.assetManager.get("gameover_bg.png", Texture.class),
                0, 0, TankCommanderGame.VIRTUAL_WIDTH, TankCommanderGame.VIRTUAL_HEIGHT);
        }

        // Título GAME OVER con efecto de entrada
        font.getData().setScale(3);
        float alpha = Math.min(1f, elapsedTime / 1f);
        font.setColor(1, 0, 0, alpha);
        font.draw(batch, "GAME OVER",
            TankCommanderGame.VIRTUAL_WIDTH / 2f - 120,
            TankCommanderGame.VIRTUAL_HEIGHT / 2f + 100);

        // Mostrar puntuación
        font.getData().setScale(1.5f);
        font.setColor(1, 1, 1, alpha);
        font.draw(batch, "Final Score: " + score,
            TankCommanderGame.VIRTUAL_WIDTH / 2f - 100,
            TankCommanderGame.VIRTUAL_HEIGHT / 2f + 20);

        // Estadísticas adicionales (opcional)
        font.getData().setScale(1);
        font.draw(batch, "Enemies Destroyed: " + (score / 100),
            TankCommanderGame.VIRTUAL_WIDTH / 2f - 120,
            TankCommanderGame.VIRTUAL_HEIGHT / 2f - 20);

        font.draw(batch, "Time Survived: " + formatTime(elapsedTime),
            TankCommanderGame.VIRTUAL_WIDTH / 2f - 100,
            TankCommanderGame.VIRTUAL_HEIGHT / 2f - 60);

        // Prompt para reiniciar (parpadeante)
        if (showPrompt) {
            font.getData().setScale(0.8f);
            font.setColor(1, 1, 0, 1);
            font.draw(batch, "Press ENTER to Restart",
                TankCommanderGame.VIRTUAL_WIDTH / 2f - 130,
                TankCommanderGame.VIRTUAL_HEIGHT / 2f - 150);

            font.draw(batch, "Press ESC to Exit",
                TankCommanderGame.VIRTUAL_WIDTH / 2f - 90,
                TankCommanderGame.VIRTUAL_HEIGHT / 2f - 190);
        }

        batch.end();
    }

    private String formatTime(float seconds) {
        int minutes = (int)(seconds / 60);
        int secs = (int)(seconds % 60);
        return String.format("%02d:%02d", minutes, secs);
    }

    @Override
    public boolean handleInput()  {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            restartGame();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        return false;
    }

    private void restartGame() {
        // Crear nuevo estado de juego
        PlayingState newPlayingState = new PlayingState(game);
        game.stateMachine.addState("playing", newPlayingState);
        game.stateMachine.changeState("playing");
    }

    @Override
    public void exit() {
        // Limpiar recursos específicos si es necesario
    }

    @Override
    public void pause() {
        // No aplica para game over
    }

    @Override
    public void resume() {
        // No aplica para game over
    }

    public void dispose() {
        font.dispose();
        batch.dispose();
    }
}
