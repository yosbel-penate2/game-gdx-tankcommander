package com.tankcommander.states;

/**
 * Interfaz base para todos los estados del juego.
 * Implementa el patrón State para manejar diferentes modos de juego.
 */
public interface GameState {

    /**
     * Se llama cuando se entra a este estado.
     * Ideal para inicializar recursos y configurar el estado.
     */
    void enter();

    /**
     * Actualiza la lógica del estado.
     * @param delta Tiempo transcurrido desde el último frame en segundos
     */
    void update(float delta);

    /**
     * Renderiza el contenido del estado.
     */
    void render();

    /**
     * Se llama cuando se sale de este estado.
     * Ideal para limpiar recursos y guardar datos.
     */
    void exit();

    /**
     * Método opcional para manejar eventos de entrada específicos del estado.
     * @return true si el evento fue procesado
     */
    default boolean handleInput() {
        return false;
    }

    /**
     * Método opcional para pausar el estado.
     */
    default void pause() {
        // Implementación opcional
    }

    /**
     * Método opcional para reanudar el estado.
     */
    default void resume() {
        // Implementación opcional
    }
}
