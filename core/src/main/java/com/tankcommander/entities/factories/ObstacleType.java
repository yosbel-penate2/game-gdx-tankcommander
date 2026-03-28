package com.tankcommander.entities.factories;

import com.badlogic.gdx.graphics.Color;

/**
 * Enumeración de los diferentes tipos de obstáculos en el juego.
 * Define propiedades como destructibilidad, resistencia y puntos de puntuación.
 */
public enum ObstacleType {
    WALL("Concrete Wall", true, 200f, 0, 0, false, Color.GRAY, 32, 32),
    DESTRUCTIBLE_WALL("Wooden Wall", true, 50f, 10, 10, true, Color.BROWN, 32, 32),
    BARREL("Explosive Barrel", true, 30f, 0, 20, true, Color.RED, 24, 24),
    TREE("Tree", true, 40f, 5, 5, false, Color.GREEN, 20, 30),
    ROCK("Rock", true, 80f, 0, 0, false, Color.DARK_GRAY, 28, 24),
    INDESTRUCTIBLE_WALL("Reinforced Wall", false, Float.MAX_VALUE, 0, 0, false, Color.DARK_GRAY, 32, 32),
    CRATE("Supply Crate", true, 25f, 15, 0, true, Color.YELLOW, 24, 24);

    public final String name;
    public final boolean isDestructible;
    public final float health;
    public final int scoreValue;
    public final int explosionDamage;
    public final boolean dropsPowerUp;
    public final Color color;
    public final int width;
    public final int height;

    ObstacleType(String name, boolean isDestructible, float health, int scoreValue,
                 int explosionDamage, boolean dropsPowerUp, Color color, int width, int height) {
        this.name = name;
        this.isDestructible = isDestructible;
        this.health = health;
        this.scoreValue = scoreValue;
        this.explosionDamage = explosionDamage;
        this.dropsPowerUp = dropsPowerUp;
        this.color = color;
        this.width = width;
        this.height = height;
    }

    /**
     * Verifica si el obstáculo explota al ser destruido.
     */
    public boolean explodesOnDestroy() {
        return explosionDamage > 0;
    }

    /**
     * Obtiene el radio de explosión si el obstáculo explota.
     */
    public float getExplosionRadius() {
        return explosionDamage > 0 ? 40f : 0f;
    }

    /**
     * Verifica si el obstáculo bloquea el movimiento.
     */
    public boolean blocksMovement() {
        return true;
    }

    /**
     * Verifica si el obstáculo bloquea los proyectiles.
     */
    public boolean blocksProjectiles() {
        return this != TREE; // Los árboles no bloquean proyectiles completamente
    }
}
