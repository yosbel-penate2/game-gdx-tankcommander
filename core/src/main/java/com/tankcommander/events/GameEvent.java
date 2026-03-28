package com.tankcommander.events;

/**
 * Clase base para todos los eventos del juego.
 * Implementa el patrón Observer para comunicación desacoplada entre sistemas.
 */
public abstract class GameEvent {
    private long timestamp;
    private boolean consumed;
    private String sourceId;

    public GameEvent() {
        this.timestamp = System.currentTimeMillis();
        this.consumed = false;
        this.sourceId = "";
    }

    public GameEvent(String sourceId) {
        this.timestamp = System.currentTimeMillis();
        this.consumed = false;
        this.sourceId = sourceId;
    }

    /**
     * Obtiene el timestamp del evento en milisegundos.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Verifica si el evento ya fue consumido por algún listener.
     */
    public boolean isConsumed() {
        return consumed;
    }

    /**
     * Marca el evento como consumido, evitando que otros listeners lo procesen.
     */
    public void consume() {
        this.consumed = true;
    }

    /**
     * Obtiene el identificador de la entidad que generó el evento.
     */
    public String getSourceId() {
        return sourceId;
    }

    /**
     * Establece el identificador de la entidad que generó el evento.
     */
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    /**
     * Devuelve el nombre del evento para debugging.
     */
    public abstract String getEventName();

    /**
     * Crea una copia del evento.
     */
    public abstract GameEvent copy();
}
