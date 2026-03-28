package com.tankcommander.systems;

import com.badlogic.gdx.utils.Array;
import com.tankcommander.entities.Entity;

/**
 * Interfaz base para todos los sistemas del juego.
 * Los sistemas procesan entidades con componentes específicos.
 */
public interface GameSystem {

    /**
     * Actualiza la lógica del sistema para todas las entidades relevantes.
     * @param delta Tiempo transcurrido desde el último frame en segundos
     * @param entities Lista de entidades a procesar
     */
    void update(float delta, Array<Entity> entities);

    /**
     * Método opcional para verificar si una entidad debe ser procesada por este sistema.
     * @param entity La entidad a verificar
     * @return true si el sistema debe procesar esta entidad
     */
    default boolean shouldProcess(Entity entity) {
        return true;
    }

    /**
     * Método opcional para inicializar el sistema.
     */
    default void init() {
        // Implementación opcional
    }

    /**
     * Método opcional para liberar recursos del sistema.
     */
    default void dispose() {
        // Implementación opcional
    }
}
