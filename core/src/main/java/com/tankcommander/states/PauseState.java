package com.tankcommander.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tankcommander.core.TankCommanderGame;

/**
 * Estado de pausa del juego.
 * Congela la lógica del juego y muestra un menú de pausa.
 */
public class PauseState implements GameState {
    private TankCommanderGame game;
    private GameState previousState;
    private BitmapFont font;
    private SpriteBatch batch;
    private OrthographicCamera uiCamera;
    private float menuSelector;
    private String[] menuOptions;
    private int selectedOption;

    public PauseState(TankCommanderGame game, GameState previousState) {
        this.game = game;
        this.previousState = previousState;
        this.font = new BitmapFont();
        this.batch = new SpriteBatch();
        this.uiCamera = new OrthographicCamera();
        this.uiCamera.setToOrtho(false, TankCommanderGame.VIRTUAL_WIDTH, TankCommanderGame.VIRTUAL_HEIGHT);
        this.menuOptions = new String[]{"Resume", "Restart", "Options", "Main Menu"};
        this.selectedOption = 0;
    }

    @Override
    public void enter() {
        // Pausar el estado anterior
        if (previousState != null) {
            previousState.pause();
        }
        Gdx.input.setInputProcessor(null); // Deshabilitar input del juego temporalmente
    }

    @Override
    public void update(float delta) {
        handleInput();
    }

    @Override
    public void render() {
        // Renderizar el estado anterior con un filtro semitransparente
        if (previousState != null) {
            previousState.render();
        }

        // Renderizar overlay de pausa
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();

        // Fondo semitransparente
        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(game.assetManager.get("pixel.png", Texture.class),
            0, 0, TankCommanderGame.VIRTUAL_WIDTH, TankCommanderGame.VIRTUAL_HEIGHT);
        batch.setColor(1, 1, 1, 1);

        // Título
        font.getData().setScale(2);
        font.draw(batch, "PAUSED",
            TankCommanderGame.VIRTUAL_WIDTH / 2f - 60,
            TankCommanderGame.VIRTUAL_HEIGHT / 2f + 100);

        // Opciones del menú
        font.getData().setScale(1);
        for (int i = 0; i < menuOptions.length; i++) {
            float x = TankCommanderGame.VIRTUAL_WIDTH / 2f - 50;
            float y = TankCommanderGame.VIRTUAL_HEIGHT / 2f - i * 40;

            if (i == selectedOption) {
                font.setColor(1, 1, 0, 1);
                font.draw(batch, "> " + menuOptions[i] + " <", x, y);
            } else {
                font.setColor(1, 1, 1, 1);
                font.draw(batch, menuOptions[i], x, y);
            }
        }

        font.setColor(1, 1, 1, 1);
        font.getData().setScale(0.5f);
        font.draw(batch, "Press ESC to resume | Arrow keys to navigate",
            TankCommanderGame.VIRTUAL_WIDTH / 2f - 200, 50);

        batch.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public boolean handleInput() {
        // Navegación del menú
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedOption = (selectedOption - 1 + menuOptions.length) % menuOptions.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedOption = (selectedOption + 1) % menuOptions.length;
        }

        // Seleccionar opción
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
            Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            executeSelectedOption();
        }

        // Reanudar juego
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            resumeGame();
        }
        return false;
    }

    private void executeSelectedOption() {
        switch (selectedOption) {
            case 0: // Resume
                resumeGame();
                break;
            case 1: // Restart
                restartGame();
                break;
            case 2: // Options
                // TODO: Cambiar a estado de opciones
                break;
            case 3: // Main Menu
                game.stateMachine.changeState("mainmenu");
                break;
        }
    }

    private void resumeGame() {
        if (previousState != null) {
            previousState.resume();
        }
        game.stateMachine.changeState("playing");
    }

    private void restartGame() {
        // Crear nuevo estado de juego
        PlayingState newPlayingState = new PlayingState(game);
        game.stateMachine.addState("playing", newPlayingState);
        game.stateMachine.changeState("playing");
    }

    @Override
    public void exit() {
        if (previousState != null) {
            previousState.resume();
        }
    }

    @Override
    public void pause() {
        // Ya está en pausa, no hacer nada
    }

    @Override
    public void resume() {
        // No aplica para este estado
    }

    public void dispose() {
        font.dispose();
        batch.dispose();
    }
}
