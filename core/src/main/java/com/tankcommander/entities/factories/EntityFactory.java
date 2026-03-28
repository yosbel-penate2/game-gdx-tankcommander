package com.tankcommander.entities.factories;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.tankcommander.entities.Entity;
import com.tankcommander.entities.components.*;
import com.tankcommander.events.EventManager;
import com.tankcommander.weapons.Cannon;
import com.tankcommander.weapons.MachineGun;

/**
 * Fábrica para crear todas las entidades del juego.
 * Centraliza la creación y configuración de entidades.
 */
public class EntityFactory {
    private AssetManager assetManager;
    private EventManager eventManager;
    private TextureRegion tankBodyTexture;
    private TextureRegion tankTurretTexture;
    private TextureRegion enemyBodyTexture;
    private TextureRegion enemyTurretTexture;

    public EntityFactory(AssetManager assetManager) {
        this.assetManager = assetManager;
        this.eventManager = null;
        loadTextures();
    }

    public EntityFactory(AssetManager assetManager, EventManager eventManager) {
        this.assetManager = assetManager;
        this.eventManager = eventManager;
        loadTextures();
    }

    private void loadTextures() {
        // Cargar texturas (deberías tener estos archivos en assets)
        // Por ahora creamos texturas de placeholder
        Texture placeholderBody = new Texture("placeholder_body.png");
        Texture placeholderTurret = new Texture("placeholder_turret.png");

        tankBodyTexture = new TextureRegion(placeholderBody);
        tankTurretTexture = new TextureRegion(placeholderTurret);
        enemyBodyTexture = new TextureRegion(placeholderBody);
        enemyTurretTexture = new TextureRegion(placeholderTurret);
    }

    /**
     * Crea la entidad del jugador.
     */
    public Entity createPlayer() {
        Entity player = new Entity();

        // Transformación
        TransformComponent transform = new TransformComponent(new Vector2(400, 300), 0f);
        player.addComponent(transform);

        // Física
        PhysicsComponent physics = new PhysicsComponent();
        physics.maxSpeed = 250f;
        physics.acceleration = 400f;
        physics.deceleration = 250f;
        physics.rotationSpeed = 180f;
        player.addComponent(physics);

        // Renderizado
        RenderComponent render = new RenderComponent(tankBodyTexture, tankTurretTexture);
        render.layer = 1;
        player.addComponent(render);

        // Salud
        HealthComponent health = new HealthComponent(150f);
        health.armor = 20f;
        health.invulnerabilityTime = 1f;
        if (eventManager != null) {
            health.setEventManager(eventManager);
        }
        player.addComponent(health);

        // Cuerpo del tanque
        TankBodyComponent body = new TankBodyComponent();
        body.trackSpeed = 200f;
        body.turnSpeed = 150f;
        player.addComponent(body);

        // Torreta
        TurretComponent turret = new TurretComponent(new Vector2(0, 0));
        turret.rotationSpeed = 240f;
        player.addComponent(turret);

        // Armas
        WeaponComponent weapons = new WeaponComponent();
        weapons.addWeapon(new Cannon(50f, 40f, 1.2f));
        weapons.addWeapon(new MachineGun(12f, 0.12f, 200));
        player.addComponent(weapons);

        // ========== NUEVO: COMPONENTE DE COLISIÓN ==========
        CollisionComponent collision = new CollisionComponent(32f, 32f, CollisionComponent.CollisionLayer.PLAYER);
        collision.collisionRadius = 24f;
        collision.collidesWith = new CollisionComponent.CollisionLayer[] {
            CollisionComponent.CollisionLayer.ENEMY,
            CollisionComponent.CollisionLayer.OBSTACLE,
            CollisionComponent.CollisionLayer.WALL
        };
        player.addComponent(collision);

        return player;
    }
    /**
     * Crea un enemigo de un tipo específico.
     */
    public Entity createEnemy(EnemyType type, Vector2 position) {
        Entity enemy = new Entity();

        // Transformación
        TransformComponent transform = new TransformComponent(position.cpy(), 0f);
        enemy.addComponent(transform);

        // Física
        PhysicsComponent physics = new PhysicsComponent();
        physics.maxSpeed = type.speed;
        physics.acceleration = type.speed * 1.5f;
        physics.deceleration = type.speed;
        physics.rotationSpeed = type.rotationSpeed;
        enemy.addComponent(physics);

        // Renderizado
        RenderComponent render = new RenderComponent(enemyBodyTexture, enemyTurretTexture);
        render.tintColor = type.color.cpy();
        render.layer = 1;
        enemy.addComponent(render);

        // Salud
        HealthComponent health = new HealthComponent(type.maxHealth);
        health.armor = 10f;
        if (eventManager != null) {
            health.setEventManager(eventManager);
        }
        enemy.addComponent(health);

        // Cuerpo del tanque
        TankBodyComponent body = new TankBodyComponent();
        body.trackSpeed = type.speed * 0.8f;
        body.turnSpeed = type.rotationSpeed;
        enemy.addComponent(body);

        // Torreta
        TurretComponent turret = new TurretComponent(new Vector2(0, 0));
        turret.rotationSpeed = type.rotationSpeed * 0.8f;
        enemy.addComponent(turret);

        // Armas
        WeaponComponent weapons = new WeaponComponent();
        weapons.addWeapon(new Cannon(type.damage, 30f, type.fireRate));
        enemy.addComponent(weapons);

        // ========== NUEVO: COMPONENTE DE COLISIÓN ==========
        CollisionComponent collision = new CollisionComponent(32f, 32f, CollisionComponent.CollisionLayer.ENEMY);
        collision.collisionRadius = 30f;
        collision.collidesWith = new CollisionComponent.CollisionLayer[] {
            CollisionComponent.CollisionLayer.PLAYER,
            CollisionComponent.CollisionLayer.ENEMY,
            CollisionComponent.CollisionLayer.OBSTACLE,
            CollisionComponent.CollisionLayer.WALL
        };
        enemy.addComponent(collision);

        return enemy;
    }
    /**
     * Crea un obstáculo de un tipo específico.
     */
    public Entity createObstacle(ObstacleType type, Vector2 position) {
        Entity obstacle = new Entity();

        // Transformación
        TransformComponent transform = new TransformComponent(position.cpy(), 0f);
        obstacle.addComponent(transform);

        // Renderizado
        RenderComponent render = new RenderComponent(createPlaceholderTexture(type.width, type.height));
        render.tintColor = type.color.cpy();
        render.layer = 0;
        obstacle.addComponent(render);

        // Salud (si es destructible)
        if (type.isDestructible) {
            HealthComponent health = new HealthComponent(type.health);
            if (eventManager != null) {
                health.setEventManager(eventManager);
            }
            obstacle.addComponent(health);
        }

        return obstacle;
    }

    /**
     * Crea un proyectil.
     */
    public Entity createProjectile(Vector2 position, Vector2 velocity, float damage,
                                   float blastRadius, float lifeTime,
                                   com.tankcommander.weapons.Projectile.ProjectileType type) {
        Entity projectile = new Entity();

        // Transformación
        TransformComponent transform = new TransformComponent(position.cpy(), velocity.angleDeg());
        projectile.addComponent(transform);

        // Física
        PhysicsComponent physics = new PhysicsComponent();
        physics.velocity = velocity.cpy();
        physics.maxSpeed = velocity.len();
        projectile.addComponent(physics);

        // Renderizado (pequeño círculo o textura)
        RenderComponent render = new RenderComponent(createProjectileTexture(type));
        render.layer = 2;
        render.scale = 0.5f;
        projectile.addComponent(render);

        // Componente de vida útil (se puede implementar como un componente específico)
        // Por ahora se manejará en el sistema de proyectiles

        return projectile;
    }

    /**
     * Crea una textura de placeholder para obstáculos.
     */
    private TextureRegion createPlaceholderTexture(int width, int height) {
        // En una implementación real, cargarías texturas desde assetManager
        // Por ahora creamos una textura de placeholder
        Texture texture = new Texture(width, height, Pixmap.Format.RGBA8888);
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fillRectangle(0, 0, width, height);
        pixmap.setColor(0.5f, 0.5f, 0.5f, 1);
        pixmap.drawRectangle(0, 0, width, height);
        texture.draw(pixmap, 0, 0);
        pixmap.dispose();
        return new TextureRegion(texture);
    }

    /**
     * Crea una textura de placeholder para proyectiles.
     */
    private TextureRegion createProjectileTexture(com.tankcommander.weapons.Projectile.ProjectileType type) {
        int size = 8;
        Texture texture = new Texture(size, size, Pixmap.Format.RGBA8888);
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);

        switch (type) {
            case EXPLOSIVE:
                pixmap.setColor(1, 0.5f, 0, 1);
                break;
            case KINETIC:
                pixmap.setColor(1, 1, 0.5f, 1);
                break;
            case ARMOR_PIERCING:
                pixmap.setColor(0.8f, 0.8f, 1, 1);
                break;
        }
        pixmap.fillCircle(size / 2, size / 2, size / 2);
        texture.draw(pixmap, 0, 0);
        pixmap.dispose();
        return new TextureRegion(texture);
    }

    /**
     * Establece el EventManager para todas las entidades creadas.
     */
    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    /**
     * Libera recursos de texturas.
     */
    public void dispose() {
        if (tankBodyTexture != null && tankBodyTexture.getTexture() != null) {
            tankBodyTexture.getTexture().dispose();
        }
        if (tankTurretTexture != null && tankTurretTexture.getTexture() != null) {
            tankTurretTexture.getTexture().dispose();
        }
    }
}
