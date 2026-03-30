package com.tankcommander.core;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.tankcommander.entities.Entity;
import com.tankcommander.entities.components.TransformComponent;
import com.tankcommander.entities.components.HealthComponent;
import com.tankcommander.entities.factories.EntityFactory;
import com.tankcommander.events.EventManager;
import com.tankcommander.events.DeathEvent;
import com.tankcommander.events.EventListener;
import com.tankcommander.events.GameEvent;
import com.tankcommander.systems.*;
import com.badlogic.gdx.Gdx;
import com.tankcommander.entities.components.Component;


/**
 * Mundo del juego que gestiona todas las entidades y sistemas.
 * Centraliza la lógica de actualización y renderizado.
 */
public class GameWorld implements EventListener {
    private DelayedRemovalArray<Entity> entities;
    private Array<GameSystem> systems;
    private EventManager eventManager;

    // Sistemas específicos
    private PhysicsSystem physicsSystem;
    private RenderSystem renderSystem;
    private AISystem aiSystem;
    private CollisionSystem collisionSystem;

    // Cámara que sigue al jugador
    private OrthographicCamera camera;
    private Entity player;
    private Vector2 cameraOffset;
    private float cameraSmoothing;

    // Estadísticas del mundo
    private int enemiesDefeated;
    private int totalScore;
    private float worldTime;

    private EntityFactory entityFactory;


    public GameWorld() {
        this.entities = new DelayedRemovalArray<>(true, 100);
        this.systems = new Array<>();
        this.eventManager = new EventManager();
        this.cameraOffset = new Vector2(0, 0);
        this.cameraSmoothing = 5f;
        this.enemiesDefeated = 0;
        this.totalScore = 0;
        this.worldTime = 0f;

        this.entityFactory = new EntityFactory(null, eventManager);

        initializeSystems();
        subscribeToEvents();
    }

    // ========== NUEVO: MÉTODO GETTER ==========
    public EntityFactory getEntityFactory() {
        return entityFactory;
    }

    public GameWorld(OrthographicCamera camera, SpriteBatch batch) {
        this();
        this.camera = camera;
        this.renderSystem = new RenderSystem(batch, camera);
        systems.add(renderSystem);
    }

    private void initializeSystems() {
        physicsSystem = new PhysicsSystem();
        collisionSystem = new CollisionSystem();
        aiSystem = new AISystem(null);

        // IMPORTANTE: Orden correcto: Física -> Colisiones -> IA
        systems.add(physicsSystem);
        systems.add(collisionSystem);
        systems.add(aiSystem);
    }

    private void subscribeToEvents() {
        eventManager.subscribe(DeathEvent.class, this);
    }

    /**
     * Añade una entidad al mundo.
     */
    public void addEntity(Entity entity) {
        if (entity != null && !entities.contains(entity, true)) {
            entities.add(entity);

            // Configurar EventManager en componentes que lo necesiten
            HealthComponent health = entity.getComponent(HealthComponent.class);
            if (health != null) {
                health.setEventManager(eventManager);
            }
        }
    }

    /**
     * Elimina una entidad del mundo.
     */
    public void removeEntity(Entity entity) {
        if (entity != null) {
            entities.removeValue(entity, true);
        }
    }

    /**
     * Marca una entidad para ser eliminada al final del frame.
     */
    public void markForRemoval(Entity entity) {
        if (entity != null) {
            entity.markForRemoval();
        }
    }

    /**
     * Establece la entidad del jugador.
     */
    public void setPlayer(Entity player) {
        this.player = player;
        aiSystem.setTarget(player);

        // Centrar la cámara inmediatamente en la posición del jugador
        if (camera != null && player != null) {
            TransformComponent playerTransform = player.getComponent(TransformComponent.class);
            if (playerTransform != null) {
                camera.position.set(playerTransform.position.x, playerTransform.position.y, 0);
                cameraOffset.set(playerTransform.position.x, playerTransform.position.y);
                camera.update();
            }
        }
    }

    /**
     * Obtiene la entidad del jugador.
     */
    public Entity getPlayer() {
        return player;
    }

    /**
     * Actualiza toda la lógica del mundo.
     */
    public void update(float delta) {
        worldTime += delta;

        // Actualizar todas las entidades (componentes)
        for (int i = 0; i < entities.size; i++) {
            Entity entity = entities.get(i);
            if (entity != null) {
                entity.update(delta);
            }
        }

        // Actualizar sistemas en orden
        for (GameSystem system : systems) {
            system.update(delta, entities);
        }

        // Eliminar entidades marcadas
        entities.begin();
        for (int i = 0; i < entities.size; i++) {
            Entity entity = entities.get(i);
            if (entity != null && entity.isMarkedForRemoval()) {
                entities.removeIndex(i);
                i--;
            }
        }
        entities.end();

        // Actualizar cámara para seguir al jugador
        updateCamera(delta);
    }

    /**
     * Actualiza la cámara para seguir al jugador.
     */
    private void updateCamera(float delta) {
        if (camera != null && player != null) {
            TransformComponent playerTransform = player.getComponent(TransformComponent.class);
            if (playerTransform != null) {
                Vector2 targetPosition = playerTransform.position.cpy();

                // Suavizado de cámara
                cameraOffset.lerp(targetPosition, cameraSmoothing * delta);
                camera.position.set(cameraOffset.x, cameraOffset.y, 0);
                camera.update();
            }
        }
    }

    /**
     * Renderiza el mundo.
     */
    public void render(SpriteBatch batch, OrthographicCamera camera) {
        if (renderSystem != null) {
            renderSystem.update(0f, entities);
        }
    }

    /**
     * Verifica colisiones entre dos entidades.
     */
    public boolean checkCollision(Entity a, Entity b) {
        TransformComponent transformA = a.getComponent(TransformComponent.class);
        TransformComponent transformB = b.getComponent(TransformComponent.class);

        if (transformA == null || transformB == null) return false;

        float distance = transformA.position.dst(transformB.position);
        float collisionDistance = 32f;

        return distance < collisionDistance;
    }

    /**
     * Obtiene todas las entidades cercanas a una posición.
     */
    public Array<Entity> getEntitiesNear(Vector2 position, float radius) {
        Array<Entity> nearby = new Array<>();

        for (Entity entity : entities) {
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            if (transform != null && transform.position.dst(position) <= radius) {
                nearby.add(entity);
            }
        }

        return nearby;
    }

    /**
     * Obtiene entidades por tipo de componente.
     */
    public <T extends Component> Array<Entity> getEntitiesWithComponent(Class<T> componentClass) {
        Array<Entity> result = new Array<>();

        for (Entity entity : entities) {
            if (entity.hasComponent(componentClass)) {
                result.add(entity);
            }
        }

        return result;
    }

    /**
     * Añade puntos a la puntuación total.
     */
    public void addScore(int points) {
        totalScore += points;
    }

    /**
     * Incrementa el contador de enemigos derrotados.
     */
    public void addEnemyDefeated() {
        enemiesDefeated++;
    }

    // Getters
    public int getTotalScore() {
        return totalScore;
    }

    public int getEnemiesDefeated() {
        return enemiesDefeated;
    }

    public float getWorldTime() {
        return worldTime;
    }

    public Array<Entity> getAllEntities() {
        return entities;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public PhysicsSystem getPhysicsSystem() {
        return physicsSystem;
    }

    public AISystem getAiSystem() {
        return aiSystem;
    }

    /**
     * Manejo de eventos del mundo.
     */
    @Override
    public void onEvent(GameEvent event) {
        if (event instanceof DeathEvent) {
            DeathEvent deathEvent = (DeathEvent) event;

            if (deathEvent.getKiller() == player && deathEvent.getDeceased() != player) {
                addScore(deathEvent.getScoreValue());
                addEnemyDefeated();
            }

            if (deathEvent.getDeceased() == player) {
                Gdx.app.log("GameWorld", "Player destroyed!");
            }
        }
    }

    /**
     * Limpia todas las entidades del mundo.
     */
    public void clear() {
        entities.clear();
        enemiesDefeated = 0;
        totalScore = 0;
        worldTime = 0f;
    }

    /**
     * Configura el modo debug para renderizado.
     */
    public void setDebugMode(boolean enabled) {
        if (renderSystem != null) {
            renderSystem.setDebugMode(enabled);
        }
    }

    /**
     * Libera recursos del mundo.
     */
    public void dispose() {
        for (GameSystem system : systems) {
            system.dispose();
        }

        entities.clear();
        eventManager.clear();
    }

    /**
     * Clase interna para manejar colisiones.
     */
    public static class CollisionInfo {
        public Entity entityA;
        public Entity entityB;
        public Vector2 collisionPoint;
        public float penetrationDepth;

        public CollisionInfo(Entity a, Entity b, Vector2 point, float depth) {
            this.entityA = a;
            this.entityB = b;
            this.collisionPoint = point;
            this.penetrationDepth = depth;
        }
    }
}
