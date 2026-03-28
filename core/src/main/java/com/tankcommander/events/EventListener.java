package com.tankcommander.events;

/**
 * Interfaz para los listeners de eventos.
 * Los objetos que implementan esta interfaz pueden suscribirse al EventManager
 * para recibir notificaciones cuando ocurran eventos específicos.
 */
public interface EventListener {

    /**
     * Método llamado cuando ocurre un evento al que este listener está suscrito.
     * @param event El evento que ocurrió
     */
    void onEvent(GameEvent event);

    /**
     * Método opcional para verificar si el listener está interesado en un evento específico.
     * @param eventType La clase del evento
     * @return true si el listener quiere recibir este tipo de evento
     */
    default boolean isInterestedIn(Class<? extends GameEvent> eventType) {
        return true;
    }

    /**
     * Método opcional para establecer prioridad del listener.
     * Los listeners con mayor prioridad reciben el evento primero.
     * @return Prioridad (mayor número = mayor prioridad)
     */
    default int getPriority() {
        return 0;
    }

    /**
     * Método opcional para obtener un identificador único del listener.
     * @return Identificador del listener
     */
    default String getId() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode());
    }

    /**
     * Método opcional para manejar eventos de forma asíncrona.
     * @return true si el evento debe ser procesado en un hilo separado
     */
    default boolean isAsync() {
        return false;
    }
}
