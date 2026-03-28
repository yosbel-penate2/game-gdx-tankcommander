package com.tankcommander.events;

import com.badlogic.gdx.math.Vector2;
import com.tankcommander.entities.Entity;
import com.tankcommander.entities.components.HealthComponent;

/**
 * Evento que se dispara cuando una entidad recibe daño.
 * Utilizado por sistemas de combate, UI, efectos visuales y audio.
 */
public class DamageEvent extends GameEvent {
    private Entity target;
    private Entity source;
    private float amount;
    private float remainingHealth;
    private float maxHealth;
    private Vector2 hitPoint;
    private DamageType damageType;
    private boolean isCritical;
    private boolean isFatal;

    public DamageEvent(HealthComponent healthComponent, Entity source, float actualDamage, float currentHealth, float maxHealth, Vector2 hitPoint, DamageType damageType) {

    }

    public enum DamageType {
        KINETIC,
        EXPLOSIVE,
        FIRE,
        POISON,
        ENERGY,
        TRUE_DAMAGE
    }

    /**
     * Constructor completo para evento de daño.
     * @param target Entidad que recibe el daño
     * @param source Entidad que causa el daño (puede ser null)
     * @param amount Cantidad de daño infligido
     * @param remainingHealth Salud restante después del daño
     * @param maxHealth Salud máxima del objetivo
     * @param hitPoint Punto de impacto en coordenadas mundiales
     * @param damageType Tipo de daño
     */
    public DamageEvent(Entity target, Entity source, float amount,
                       float remainingHealth, float maxHealth,
                       Vector2 hitPoint, DamageType damageType) {
        super(source != null ? source.id.toString() : "environment");
        this.target = target;
        this.source = source;
        this.amount = amount;
        this.remainingHealth = remainingHealth;
        this.maxHealth = maxHealth;
        this.hitPoint = hitPoint != null ? hitPoint.cpy() : null;
        this.damageType = damageType;
        this.isCritical = false;
        this.isFatal = remainingHealth <= 0;
    }

    /**
     * Constructor simplificado para evento de daño.
     */
    public DamageEvent(Entity target, float amount, float remainingHealth) {
        this(target, null, amount, remainingHealth, remainingHealth + amount, null, DamageType.KINETIC);
    }

    public Entity getTarget() {
        return target;
    }

    public Entity getSource() {
        return source;
    }

    public float getAmount() {
        return amount;
    }

    public float getRemainingHealth() {
        return remainingHealth;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public float getHealthPercent() {
        return remainingHealth / maxHealth;
    }

    public Vector2 getHitPoint() {
        return hitPoint != null ? hitPoint.cpy() : null;
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public boolean isCritical() {
        return isCritical;
    }

    public void setCritical(boolean critical) {
        isCritical = critical;
        if (isCritical) {
            this.amount *= 1.5f; // 50% más de daño en crítico
        }
    }

    public boolean isFatal() {
        return isFatal;
    }

    public boolean isOverkill() {
        return remainingHealth < 0;
    }

    public float getOverkillAmount() {
        return isOverkill() ? Math.abs(remainingHealth) : 0;
    }

    /**
     * Verifica si el daño fue mitigado por armadura/resistencia.
     */
    public boolean isMitigated() {
        return amount < getOriginalDamage();
    }

    private float getOriginalDamage() {
        // Este valor debería ser calculado antes de aplicar resistencias
        return amount;
    }

    @Override
    public String getEventName() {
        return "DamageEvent";
    }

    @Override
    public GameEvent copy() {
        DamageEvent copy = new DamageEvent(target, source, amount, remainingHealth,
            maxHealth, hitPoint, damageType);
        copy.setCritical(isCritical);
        return copy;
    }

    @Override
    public String toString() {
        return String.format("DamageEvent[target=%s, amount=%.2f, remaining=%.2f, type=%s, fatal=%s]",
            target != null ? target.id : "null", amount, remainingHealth, damageType, isFatal);
    }
}
