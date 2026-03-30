package com.tankcommander.entities.components;

import com.tankcommander.entities.Entity;

/**
 * Componente que almacena los datos específicos de un proyectil.
 * Este componente permite que el proyectil cause daño, tenga duración limitada,
 * y sepa quién lo disparó (para evitar autodaño).
 */
public class ProjectileComponent implements Component {

    // ========== PROPIEDADES BÁSICAS ==========

    /** Cantidad de daño que causa este proyectil al impactar */
    public float damage;

    /** Radio de explosión (para daño en área) */
    public float blastRadius;

    /** Tiempo de vida máximo del proyectil en segundos */
    public float lifeTime;

    /** Tiempo de vida restante del proyectil */
    public float currentLife;

    /** ¿Ya impactó? (true = ya golpeó algo, debe desaparecer) */
    public boolean hasHit;

    /** Entidad que disparó este proyectil (para evitar autodaño) */
    public Entity owner;

    /** Velocidad del proyectil (para cálculos) */
    public float speed;

    /** Tipo de proyectil (KINETIC, EXPLOSIVE, ARMOR_PIERCING) */
    public ProjectileType type;

    // ========== TIPOS DE PROYECTIL ==========

    public enum ProjectileType {
        KINETIC,        // Daño cinético, perfora armadura
        EXPLOSIVE,      // Daño en área, menos perforación
        ARMOR_PIERCING  // Alta perforación, bajo daño en área
    }

    // ========== CONSTRUCTORES ==========

    /**
     * Constructor principal
     * @param damage Daño que causa
     * @param blastRadius Radio de explosión
     * @param lifeTime Tiempo de vida en segundos
     * @param owner Entidad que disparó
     */
    public ProjectileComponent(float damage, float blastRadius, float lifeTime, Entity owner) {
        this.damage = damage;
        this.blastRadius = blastRadius;
        this.lifeTime = lifeTime;
        this.currentLife = lifeTime;
        this.hasHit = false;
        this.owner = owner;
        this.speed = 400f;  // Velocidad por defecto
        this.type = ProjectileType.KINETIC;
    }

    /**
     * Constructor completo con tipo y velocidad
     */
    public ProjectileComponent(float damage, float blastRadius, float lifeTime,
                               float speed, Entity owner, ProjectileType type) {
        this.damage = damage;
        this.blastRadius = blastRadius;
        this.lifeTime = lifeTime;
        this.currentLife = lifeTime;
        this.hasHit = false;
        this.owner = owner;
        this.speed = speed;
        this.type = type;
    }

    // ========== MÉTODOS ==========

    /**
     * Actualiza el tiempo de vida del proyectil
     * Se llama automáticamente cada frame desde Entity.update()
     * @param delta Tiempo transcurrido desde el último frame
     */
    @Override
    public void update(float delta) {
        // Reducir tiempo de vida
        if (currentLife > 0) {
            currentLife -= delta;
        }

        // Si se acabó el tiempo, marcar como impactado (para eliminar)
        if (currentLife <= 0 && !hasHit) {
            hasHit = true;
        }
    }

    /**
     * Verifica si el proyectil sigue activo
     * @return true si está vivo y no ha impactado
     */
    public boolean isActive() {
        return !hasHit && currentLife > 0;
    }

    /**
     * Marca el proyectil como impactado (para eliminarlo)
     */
    public void markHit() {
        this.hasHit = true;
        this.currentLife = 0;
    }

    /**
     * Calcula el porcentaje de vida restante (0-1)
     * @return Porcentaje de vida (1 = recién disparado, 0 = a punto de expirar)
     */
    public float getLifePercent() {
        return currentLife / lifeTime;
    }

    /**
     * Verifica si el proyectil puede dañar a una entidad
     * @param target Entidad objetivo
     * @return true si puede dañar, false si es el dueño o ya impactó
     */
    public boolean canDamage(Entity target) {
        // No dañar si ya impactó
        if (hasHit) return false;

        // No dañar al dueño
        if (target == owner) return false;

        // No dañar entidades ya marcadas para eliminar
        if (target.isMarkedForRemoval()) return false;

        return true;
    }

    // ========== MÉTODOS ESTÁTICOS UTILITARIOS ==========

    /**
     * Crea un proyectil de tipo KINETIC (bala normal)
     */
    public static ProjectileComponent createKinetic(float damage, Entity owner) {
        return new ProjectileComponent(damage, 5f, 3f, 500f, owner, ProjectileType.KINETIC);
    }

    /**
     * Crea un proyectil de tipo EXPLOSIVE (cohete, granada)
     */
    public static ProjectileComponent createExplosive(float damage, float blastRadius, Entity owner) {
        return new ProjectileComponent(damage, blastRadius, 2f, 300f, owner, ProjectileType.EXPLOSIVE);
    }

    /**
     * Crea un proyectil de tipo ARMOR_PIERCING (antitanque)
     */
    public static ProjectileComponent createArmorPiercing(float damage, Entity owner) {
        return new ProjectileComponent(damage, 3f, 4f, 600f, owner, ProjectileType.ARMOR_PIERCING);
    }

    @Override
    public String toString() {
        return String.format("ProjectileComponent[damage=%.1f, life=%.2f/%.2f, hasHit=%s, owner=%s]",
            damage, currentLife, lifeTime, hasHit, owner != null ? owner.id : "null");
    }
}
