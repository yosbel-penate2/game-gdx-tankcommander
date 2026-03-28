package com.tankcommander.systems;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.tankcommander.entities.Entity;
import com.tankcommander.entities.components.RenderComponent;
import com.tankcommander.entities.components.TransformComponent;
import com.tankcommander.entities.components.TurretComponent;
import com.tankcommander.entities.components.HealthComponent;
import com.badlogic.gdx.math.Vector2;

/**
 * Sistema de renderizado que dibuja todas las entidades en pantalla.
 */
public class RenderSystem implements GameSystem {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private boolean debugMode;

    public RenderSystem(SpriteBatch batch, OrthographicCamera camera) {
        this.batch = batch;
        this.camera = camera;
        this.shapeRenderer = new ShapeRenderer();
        this.debugMode = false;
    }

    public void setDebugMode(boolean enabled) {
        this.debugMode = enabled;
    }

    @Override
    public void update(float delta, Array<Entity> entities) {
        // Filtrar entidades que tienen componentes de renderizado y transformación
        Array<Entity> renderableEntities = new Array<>();
        for (Entity entity : entities) {
            if (entity.hasComponent(RenderComponent.class) &&
                entity.hasComponent(TransformComponent.class)) {
                renderableEntities.add(entity);
            }
        }

        // Ordenar por capa (menor capa se renderiza primero - fondo)
        renderableEntities.sort((e1, e2) -> {
            RenderComponent r1 = e1.getComponent(RenderComponent.class);
            RenderComponent r2 = e2.getComponent(RenderComponent.class);
            return Integer.compare(r1.layer, r2.layer);
        });

        // Iniciar batch
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Renderizar todas las entidades
        for (Entity entity : renderableEntities) {
            renderEntity(entity);
        }

        batch.end();

        // Renderizar modo debug si está activado
        if (debugMode) {
            renderDebug(entities);
        }
    }

    private void renderEntity(Entity entity) {
        RenderComponent render = entity.getComponent(RenderComponent.class);
        TransformComponent transform = entity.getComponent(TransformComponent.class);

        if (render.textureRegion == null) return;

        float width = render.textureRegion.getRegionWidth() * transform.scale.x;
        float height = render.textureRegion.getRegionHeight() * transform.scale.y;
        float originX = width / 2f;
        float originY = height / 2f;

        // Aplicar color con alpha
        batch.setColor(render.tintColor.r, render.tintColor.g, render.tintColor.b, render.alpha);

        // Dibujar cuerpo
        batch.draw(
            render.textureRegion,
            transform.position.x - originX,
            transform.position.y - originY,
            originX,
            originY,
            width,
            height,
            render.flipX ? -1f : 1f,
            render.flipY ? -1f : 1f,
            transform.rotation
        );

        // Renderizar torreta si existe
        TurretComponent turret = entity.getComponent(TurretComponent.class);
        if (turret != null && render.turretTexture != null) {
            Vector2 turretWorldPos = turret.getWorldPosition(
                transform.position,
                transform.rotation
            );

            float turretWidth = render.turretTexture.getRegionWidth() * transform.scale.x;
            float turretHeight = render.turretTexture.getRegionHeight() * transform.scale.y;
            float turretOriginX = turretWidth / 2f;
            float turretOriginY = turretHeight / 2f;

            batch.draw(
                render.turretTexture,
                turretWorldPos.x - turretOriginX,
                turretWorldPos.y - turretOriginY,
                turretOriginX,
                turretOriginY,
                turretWidth,
                turretHeight,
                1f,
                1f,
                turret.turretAngle
            );
        }

        // Restaurar color
        batch.setColor(1, 1, 1, 1);
    }

    private void renderDebug(Array<Entity> entities) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (Entity entity : entities) {
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            if (transform == null) continue;

            // Dibujar bounding box
            shapeRenderer.setColor(0, 1, 0, 1);
            shapeRenderer.rect(
                transform.position.x - 16,
                transform.position.y - 16,
                32,
                32
            );

            // Dibujar línea de dirección
            shapeRenderer.setColor(1, 0, 0, 1);
            float angleRad = transform.rotation * MathUtils.degreesToRadians;
            shapeRenderer.line(
                transform.position.x,
                transform.position.y,
                transform.position.x + MathUtils.cos(angleRad) * 30,
                transform.position.y + MathUtils.sin(angleRad) * 30
            );

            // Dibujar hitbox de salud si tiene componente de salud
            HealthComponent health = entity.getComponent(HealthComponent.class);
            if (health != null) {
                float healthPercent = health.currentHealth / health.maxHealth;
                shapeRenderer.setColor(0, 1, 0, 1);
                shapeRenderer.rect(
                    transform.position.x - 20,
                    transform.position.y + 25,
                    40 * healthPercent,
                    5
                );
                shapeRenderer.setColor(1, 0, 0, 1);
                shapeRenderer.rect(
                    transform.position.x - 20 + (40 * healthPercent),
                    transform.position.y + 25,
                    40 * (1 - healthPercent),
                    5
                );
            }

            // Dibujar punto central
            shapeRenderer.setColor(1, 1, 0, 1);
            shapeRenderer.circle(transform.position.x, transform.position.y, 3);
        }

        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
