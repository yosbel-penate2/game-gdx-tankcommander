package com.tankcommander.entities.components;

import com.tankcommander.events.DamageEvent;
import com.tankcommander.events.DeathEvent;
import com.tankcommander.events.EventManager;

/**
 * Componente que gestiona la salud de una entidad.
 * Maneja daño, curación, invulnerabilidad y eventos relacionados.
 */
public class HealthComponent implements Component {
    public float currentHealth;
    public float maxHealth;
    public boolean isAlive;
    public boolean isInvulnerable;
    public float invulnerabilityTime;
    public float invulnerabilityTimer;
    public float armor;
    public EventManager eventManager;

    private float lastDamageTime;
    private float damageCooldown;

    public HealthComponent() {
        this(100f, 100f);
    }

    public HealthComponent(float maxHealth) {
        this(maxHealth, maxHealth);
    }

    public HealthComponent(float currentHealth, float maxHealth) {
        this.currentHealth = currentHealth;
        this.maxHealth = maxHealth;
        this.isAlive = true;
        this.isInvulnerable = false;
        this.invulnerabilityTime = 0f;
        this.invulnerabilityTimer = 0f;
        this.armor = 0f;
        this.eventManager = null;
        this.lastDamageTime = 0f;
        this.damageCooldown = 0.5f;
    }

    /**
     * Aplica daño a la entidad.
     * @param amount Cantidad de daño
     * @param source Entidad que causa el daño
     * @param damageType Tipo de daño
     * @param hitPoint Punto de impacto
     * @return true si el daño fue aplicado
     */
    public boolean takeDamage(float amount, Entity source, DamageEvent.DamageType damageType, Vector2 hitPoint) {
        if (!isAlive || isInvulnerable || invulnerabilityTimer > 0) {
            return false;
        }

        // Aplicar reducción de daño por armadura
        float actualDamage = calculateDamage(amount, damageType);
        actualDamage = Math.max(1f, actualDamage); // Mínimo 1 de daño

        float previousHealth = currentHealth;
        currentHealth = Math.max(0, currentHealth - actualDamage);
        lastDamageTime = System.currentTimeMillis() / 1000f;

        // Activar invulnerabilidad temporal
        if (invulnerabilityTime > 0) {
            invulnerabilityTimer = invulnerabilityTime;
        }

        // Disparar evento de daño
        if (eventManager != null) {
            DamageEvent damageEvent = new DamageEvent(
                this, source, actualDamage, currentHealth, maxHealth, hitPoint, damageType
            );
            eventManager.dispatch(damageEvent);
        }

        // Verificar si la entidad murió
        if (currentHealth <= 0 && isAlive) {
            die(source, hitPoint);
        }

        return true;
    }

    private float calculateDamage(float amount, DamageEvent.DamageType damageType) {
        // Diferentes tipos de daño interactúan diferente con la armadura
        float effectiveArmor = armor;

        switch (damageType) {
            case KINETIC:
                effectiveArmor = armor * 1.2f; // La armadura es más efectiva contra proyectiles
                break;
            case EXPLOSIVE:
                effectiveArmor = armor * 0.7f; // La armadura es menos efectiva contra explosiones
                break;
            case ENERGY:
                effectiveArmor = armor * 0.5f; // La armadura es muy poco efectiva contra energía
                break;
            case TRUE_DAMAGE:
                effectiveArmor = 0f; // El daño verdadero ignora armadura
                break;
            default:
                break;
        }

        float damageReduction = Math.min(0.75f, effectiveArmor / 100f);
        return amount * (1f - damageReduction);
    }

    /**
     * Aplica curación a la entidad.
     * @param amount Cantidad de curación
     * @return true si se aplicó curación
     */
    public boolean heal(float amount) {
        if (!isAlive) return false;

        float previousHealth = currentHealth;
        currentHealth = Math.min(maxHealth, currentHealth + amount);
        return currentHealth > previousHealth;
    }

    /**
     * Mata instantáneamente a la entidad.
     */
    public void kill(Entity source) {
        if (!isAlive) return;

        currentHealth = 0;
        die(source, null);
    }

    private void die(Entity source, Vector2 deathPosition) {
        isAlive = false;

        // Disparar evento de muerte
        if (eventManager != null) {
            DeathEvent deathEvent = new DeathEvent(
                this, source, DeathEvent.DeathCause.COMBAT, deathPosition
            );
            eventManager.dispatch(deathEvent);
        }
    }

    /**
     * Verifica si la entidad está en cooldown de daño.
     */
    public boolean isDamageCooldown() {
        float currentTime = System.currentTimeMillis() / 1000f;
        return (currentTime - lastDamageTime) < damageCooldown;
    }

    /**
     * Obtiene el porcentaje de salud (0-1).
     */
    public float getHealthPercent() {
        return currentHealth / maxHealth;
    }

    /**
     * Establece tiempo de invulnerabilidad después de recibir daño.
     */
    public void setInvulnerabilityTime(float seconds) {
        this.invulnerabilityTime = seconds;
    }

    /**
     * Actualiza temporizadores (llamar cada frame).
     */
    @Override
    public void update(float delta) {
        if (invulnerabilityTimer > 0) {
            invulnerabilityTimer -= delta;
            if (invulnerabilityTimer < 0) {
                invulnerabilityTimer = 0;
            }
        }
    }

    /**
     * Verifica si la entidad está actualmente invulnerable.
     */
    public boolean isCurrentlyInvulnerable() {
        return isInvulnerable || invulnerabilityTimer > 0;
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }
}
