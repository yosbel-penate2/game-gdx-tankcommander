package com.tankcommander.events;

import com.badlogic.gdx.math.Vector2;
import com.tankcommander.entities.Entity;
import com.tankcommander.entities.components.HealthComponent;

/**
 * Evento que se dispara cuando una entidad muere.
 * Utilizado para sistemas de puntuación, spawn de power-ups, efectos visuales, etc.
 */
public class DeathEvent extends GameEvent {
    private Entity deceased;
    private Entity killer;
    private DeathCause deathCause;
    private Vector2 deathPosition;
    private int scoreValue;
    private boolean shouldDropLoot;
    private String deathAnimation;
    private float explosionRadius;
    private boolean isPlayerDeath;

    public DeathEvent(HealthComponent healthComponent, Entity source, DeathCause deathCause, Vector2 deathPosition) {

    }

    public enum DeathCause {
        COMBAT,
        EXPLOSION,
        FIRE,
        POISON,
        FALL_DAMAGE,
        OUT_OF_BOUNDS,
        ENVIRONMENT,
        SACRIFICE,
        UNKNOWN
    }

    /**
     * Constructor completo para evento de muerte.
     * @param deceased Entidad que murió
     * @param killer Entidad que causó la muerte (puede ser null)
     * @param deathCause Causa de la muerte
     * @param deathPosition Posición donde murió la entidad
     */
    public DeathEvent(Entity deceased, Entity killer, DeathCause deathCause, Vector2 deathPosition) {
        super(deceased != null ? deceased.id.toString() : "unknown");
        this.deceased = deceased;
        this.killer = killer;
        this.deathCause = deathCause;
        this.deathPosition = deathPosition != null ? deathPosition.cpy() : null;
        this.scoreValue = 0;
        this.shouldDropLoot = true;
        this.deathAnimation = "default_explosion";
        this.explosionRadius = 50f;
        this.isPlayerDeath = false;

        // Calcular valor de puntuación basado en el tipo de entidad
        calculateScoreValue();

        // Verificar si es muerte del jugador
        if (deceased != null && deceased.hasComponent(HealthComponent.class)) {
            // Asumimos que el jugador tiene un componente especial o podemos verificar por tipo
            // isPlayerDeath = deceased instanceof PlayerEntity;
        }
    }

    /**
     * Constructor simplificado para muerte por combate.
     */
    public DeathEvent(Entity deceased, Entity killer, Vector2 deathPosition) {
        this(deceased, killer, DeathCause.COMBAT, deathPosition);
    }

    private void calculateScoreValue() {
        if (deceased == null) return;

        // Valor base de puntuación
        scoreValue = 100;

        // Incrementar según el tipo de entidad
        // Esto se puede mejorar con un componente de puntuación específico
        if (deceased.hasComponent(HealthComponent.class)) {
            HealthComponent health = deceased.getComponent(HealthComponent.class);
            scoreValue += (int)(health.maxHealth * 2);
        }

        // Bonus por muerte crítica (si fue por el jugador)
        if (killer != null && isPlayerDeath) {
            scoreValue += 50;
        }
    }

    public Entity getDeceased() {
        return deceased;
    }

    public Entity getKiller() {
        return killer;
    }

    public DeathCause getDeathCause() {
        return deathCause;
    }

    public Vector2 getDeathPosition() {
        return deathPosition != null ? deathPosition.cpy() : null;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(int scoreValue) {
        this.scoreValue = scoreValue;
    }

    public boolean shouldDropLoot() {
        return shouldDropLoot;
    }

    public void setShouldDropLoot(boolean shouldDropLoot) {
        this.shouldDropLoot = shouldDropLoot;
    }

    public String getDeathAnimation() {
        return deathAnimation;
    }

    public void setDeathAnimation(String deathAnimation) {
        this.deathAnimation = deathAnimation;
    }

    public float getExplosionRadius() {
        return explosionRadius;
    }

    public void setExplosionRadius(float explosionRadius) {
        this.explosionRadius = explosionRadius;
    }

    public boolean isPlayerDeath() {
        return isPlayerDeath;
    }

    public void setPlayerDeath(boolean playerDeath) {
        isPlayerDeath = playerDeath;
    }

    /**
     * Verifica si la muerte fue causada por el jugador.
     */
    public boolean wasKilledByPlayer() {
        if (killer == null) return false;
        // Aquí se debería verificar si el killer es el jugador
        return isPlayerDeath;
    }

    /**
     * Obtiene el mensaje de muerte para mostrar en pantalla.
     */
    public String getDeathMessage() {
        if (deceased == null) return "";

        String deceasedName = getEntityName(deceased);
        String killerName = killer != null ? getEntityName(killer) : "the environment";

        switch (deathCause) {
            case COMBAT:
                return deceasedName + " was destroyed by " + killerName;
            case EXPLOSION:
                return deceasedName + " was obliterated by an explosion";
            case FIRE:
                return deceasedName + " burned to ashes";
            case POISON:
                return deceasedName + " succumbed to poison";
            case FALL_DAMAGE:
                return deceasedName + " crashed from a great height";
            case OUT_OF_BOUNDS:
                return deceasedName + " ventured out of bounds";
            case ENVIRONMENT:
                return deceasedName + " was destroyed by the environment";
            default:
                return deceasedName + " was destroyed";
        }
    }

    private String getEntityName(Entity entity) {
        // Por ahora usar el ID, luego se puede mejorar con un componente de nombre
        return entity.id.toString().substring(0, 8);
    }

    @Override
    public String getEventName() {
        return "DeathEvent";
    }

    @Override
    public GameEvent copy() {
        DeathEvent copy = new DeathEvent(deceased, killer, deathCause, deathPosition);
        copy.setScoreValue(scoreValue);
        copy.setShouldDropLoot(shouldDropLoot);
        copy.setDeathAnimation(deathAnimation);
        copy.setExplosionRadius(explosionRadius);
        copy.setPlayerDeath(isPlayerDeath);
        return copy;
    }

    @Override
    public String toString() {
        return String.format("DeathEvent[deceased=%s, killer=%s, cause=%s, score=%d, position=%s]",
            deceased != null ? deceased.id : "null",
            killer != null ? killer.id : "null",
            deathCause,
            scoreValue,
            deathPosition);
    }
}
