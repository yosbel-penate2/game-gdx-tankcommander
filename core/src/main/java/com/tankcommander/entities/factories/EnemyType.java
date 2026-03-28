package com.tankcommander.entities.factories;

import com.badlogic.gdx.graphics.Color;

/**
 * Enumeración de los diferentes tipos de enemigos en el juego.
 * Cada tipo tiene estadísticas y comportamientos específicos.
 */
public enum EnemyType {
    BASIC("Basic Tank", 100f, 50f, 80f, 30f, 2f, 100, Color.WHITE),
    FAST("Scout Tank", 60f, 80f, 120f, 20f, 1f, 75, Color.CYAN),
    HEAVY("Heavy Tank", 200f, 30f, 60f, 50f, 3f, 200, Color.ORANGE),
    ARTILLERY("Artillery Tank", 80f, 20f, 40f, 80f, 4f, 150, Color.PURPLE),
    BOSS("Boss Tank", 500f, 40f, 50f, 40f, 2.5f, 500, Color.RED);

    public final String name;
    public final float maxHealth;
    public final float speed;
    public final float rotationSpeed;
    public final float damage;
    public final float fireRate;
    public final int scoreValue;
    public final Color color;

    EnemyType(String name, float maxHealth, float speed, float rotationSpeed,
              float damage, float fireRate, int scoreValue, Color color) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.speed = speed;
        this.rotationSpeed = rotationSpeed;
        this.damage = damage;
        this.fireRate = fireRate;
        this.scoreValue = scoreValue;
        this.color = color;
    }

    /**
     * Obtiene el tipo de enemigo basado en el nivel del jugador.
     */
    public static EnemyType getEnemyForLevel(int level) {
        if (level < 3) {
            return BASIC;
        } else if (level < 5) {
            return FAST;
        } else if (level < 8) {
            return HEAVY;
        } else if (level < 10) {
            return ARTILLERY;
        } else {
            return BOSS;
        }
    }

    /**
     * Calcula las estadísticas escaladas para el nivel actual.
     */
    public EnemyStats getScaledStats(int level) {
        float scale = 1f + (level - 1) * 0.1f;
        return new EnemyStats(
            maxHealth * scale,
            speed * (1f + (level - 1) * 0.05f),
            rotationSpeed,
            damage * scale,
            fireRate,
            (int)(scoreValue * scale)
        );
    }

    /**
     * Clase interna para estadísticas escaladas.
     */
    public static class EnemyStats {
        public final float health;
        public final float speed;
        public final float rotationSpeed;
        public final float damage;
        public final float fireRate;
        public final int scoreValue;

        public EnemyStats(float health, float speed, float rotationSpeed,
                          float damage, float fireRate, int scoreValue) {
            this.health = health;
            this.speed = speed;
            this.rotationSpeed = rotationSpeed;
            this.damage = damage;
            this.fireRate = fireRate;
            this.scoreValue = scoreValue;
        }
    }
}
