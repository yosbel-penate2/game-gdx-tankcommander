package com.tankcommander.events;

import com.tankcommander.entities.Entity;

/**
 * Evento que se dispara cuando cambia la puntuación del jugador.
 * Utilizado para actualizar UI, desbloquear logros, y efectos visuales.
 */
public class ScoreEvent extends GameEvent {
    private Entity player;
    private int pointsAdded;
    private int totalScore;
    private ScoreReason reason;
    private int comboCount;
    private boolean isNewHighScore;
    private String sourceDescription;

    public enum ScoreReason {
        ENEMY_DESTROYED,
        OBSTACLE_DESTROYED,
        POWERUP_COLLECTED,
        TIME_BONUS,
        COMBO_BONUS,
        PERFECT_KILL,
        HEADSHOT,
        ACHIEVEMENT_UNLOCKED,
        LEVEL_COMPLETED,
        BOSS_DEFEATED,
        SECRET_FOUND
    }

    /**
     * Constructor completo para evento de puntuación.
     * @param player Entidad del jugador
     * @param pointsAdded Puntos añadidos (puede ser negativo para penalizaciones)
     * @param totalScore Puntuación total después del cambio
     * @param reason Razón del cambio de puntuación
     * @param sourceDescription Descripción de la fuente de puntos
     */
    public ScoreEvent(Entity player, int pointsAdded, int totalScore,
                      ScoreReason reason, String sourceDescription) {
        super(player != null ? player.id.toString() : "player");
        this.player = player;
        this.pointsAdded = pointsAdded;
        this.totalScore = totalScore;
        this.reason = reason;
        this.sourceDescription = sourceDescription;
        this.comboCount = 0;
        this.isNewHighScore = false;
    }

    /**
     * Constructor simplificado para destrucción de enemigos.
     */
    public ScoreEvent(Entity player, int pointsAdded, int totalScore, ScoreReason reason) {
        this(player, pointsAdded, totalScore, reason, getDefaultDescription(reason));
    }

    private static String getDefaultDescription(ScoreReason reason) {
        switch (reason) {
            case ENEMY_DESTROYED:
                return "Enemy destroyed";
            case OBSTACLE_DESTROYED:
                return "Obstacle destroyed";
            case POWERUP_COLLECTED:
                return "Power-up collected";
            case TIME_BONUS:
                return "Time bonus";
            case COMBO_BONUS:
                return "Combo bonus";
            case PERFECT_KILL:
                return "Perfect kill";
            case HEADSHOT:
                return "Headshot";
            case ACHIEVEMENT_UNLOCKED:
                return "Achievement unlocked";
            case LEVEL_COMPLETED:
                return "Level completed";
            case BOSS_DEFEATED:
                return "Boss defeated";
            default:
                return "Score changed";
        }
    }

    public Entity getPlayer() {
        return player;
    }

    public int getPointsAdded() {
        return pointsAdded;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public ScoreReason getReason() {
        return reason;
    }

    public String getSourceDescription() {
        return sourceDescription;
    }

    public int getComboCount() {
        return comboCount;
    }

    public void setComboCount(int comboCount) {
        this.comboCount = comboCount;
    }

    public boolean isNewHighScore() {
        return isNewHighScore;
    }

    public void setNewHighScore(boolean newHighScore) {
        isNewHighScore = newHighScore;
    }

    public boolean isBonus() {
        return pointsAdded > 0 && (reason == ScoreReason.COMBO_BONUS ||
            reason == ScoreReason.TIME_BONUS ||
            reason == ScoreReason.PERFECT_KILL);
    }

    public boolean isPenalty() {
        return pointsAdded < 0;
    }

    public String getFormattedPoints() {
        if (pointsAdded > 0) {
            return "+" + pointsAdded;
        } else if (pointsAdded < 0) {
            return String.valueOf(pointsAdded);
        }
        return "0";
    }

    public String getScoreMessage() {
        String message;

        if (isBonus()) {
            message = "BONUS! " + getFormattedPoints();
        } else if (isPenalty()) {
            message = "PENALTY: " + getFormattedPoints();
        } else {
            message = getFormattedPoints() + " points";
        }

        if (comboCount > 1) {
            message += " x" + comboCount + " COMBO!";
        }

        if (isNewHighScore) {
            message += " - NEW HIGH SCORE!";
        }

        return message;
    }

    /**
     * Calcula puntos de combo basado en el tiempo desde el último evento.
     */
    public int calculateComboBonus(int currentCombo, float timeSinceLastScore) {
        if (timeSinceLastScore < 1.5f) { // Menos de 1.5 segundos entre puntos
            int comboBonus = (currentCombo + 1) * 10;
            this.pointsAdded += comboBonus;
            this.comboCount = currentCombo + 1;
            return comboBonus;
        }
        this.comboCount = 0;
        return 0;
    }

    @Override
    public String getEventName() {
        return "ScoreEvent";
    }

    @Override
    public GameEvent copy() {
        ScoreEvent copy = new ScoreEvent(player, pointsAdded, totalScore, reason, sourceDescription);
        copy.setComboCount(comboCount);
        copy.setNewHighScore(isNewHighScore);
        return copy;
    }

    @Override
    public String toString() {
        return String.format("ScoreEvent[player=%s, points=%+d, total=%d, reason=%s, combo=%d]",
            player != null ? player.id : "null",
            pointsAdded,
            totalScore,
            reason,
            comboCount);
    }
}
