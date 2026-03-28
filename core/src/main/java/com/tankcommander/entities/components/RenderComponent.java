package com.tankcommander.entities.components;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Color;

/**
 * Componente que gestiona el renderizado de una entidad.
 * Contiene la información visual necesaria para dibujar la entidad en pantalla.
 */
public class RenderComponent implements Component {
    public TextureRegion textureRegion;
    public TextureRegion turretTexture; // Para torretas separadas del cuerpo
    public int layer;
    public Color tintColor;
    public float alpha;
    public boolean visible;
    public float scale;
    public float rotationOffset;
    public boolean flipX;
    public boolean flipY;

    // Efectos visuales
    public boolean isFlashing;
    public float flashTimer;
    public float flashDuration;
    public Color flashColor;

    // Animación
    public boolean isAnimating;
    public float animationTimer;
    public TextureRegion[] animationFrames;
    public int currentFrame;
    public float frameDuration;

    public RenderComponent() {
        this.textureRegion = null;
        this.turretTexture = null;
        this.layer = 0;
        this.tintColor = Color.WHITE.cpy();
        this.alpha = 1f;
        this.visible = true;
        this.scale = 1f;
        this.rotationOffset = 0f;
        this.flipX = false;
        this.flipY = false;
        this.isFlashing = false;
        this.flashTimer = 0f;
        this.flashDuration = 0.1f;
        this.flashColor = Color.RED.cpy();
        this.isAnimating = false;
        this.animationFrames = null;
        this.currentFrame = 0;
        this.frameDuration = 0.1f;
        this.animationTimer = 0f;
    }

    public RenderComponent(TextureRegion texture) {
        this();
        this.textureRegion = texture;
    }

    public RenderComponent(TextureRegion bodyTexture, TextureRegion turretTexture) {
        this(bodyTexture);
        this.turretTexture = turretTexture;
    }

    /**
     * Establece textura para el cuerpo del tanque.
     */
    public void setBodyTexture(TextureRegion texture) {
        this.textureRegion = texture;
    }

    /**
     * Establece textura para la torreta.
     */
    public void setTurretTexture(TextureRegion texture) {
        this.turretTexture = texture;
    }

    /**
     * Activa efecto de flash (cuando recibe daño).
     */
    public void startFlash() {
        startFlash(0.1f, Color.RED);
    }

    public void startFlash(float duration, Color color) {
        this.isFlashing = true;
        this.flashTimer = duration;
        this.flashDuration = duration;
        this.flashColor = color;
    }

    /**
     * Inicia animación con frames.
     */
    public void startAnimation(TextureRegion[] frames, float frameDuration, boolean loop) {
        this.animationFrames = frames;
        this.frameDuration = frameDuration;
        this.isAnimating = true;
        this.currentFrame = 0;
        this.animationTimer = 0f;
        if (frames != null && frames.length > 0) {
            this.textureRegion = frames[0];
        }
    }

    /**
     * Actualiza animación (llamar cada frame).
     */
    @Override
    public void update(float delta) {
        // Actualizar flash
        if (isFlashing) {
            flashTimer -= delta;
            if (flashTimer <= 0) {
                isFlashing = false;
                tintColor.set(Color.WHITE);
            } else {
                float flashIntensity = (flashTimer / flashDuration);
                tintColor.set(
                    flashColor.r * (1 - flashIntensity) + Color.WHITE.r * flashIntensity,
                    flashColor.g * (1 - flashIntensity) + Color.WHITE.g * flashIntensity,
                    flashColor.b * (1 - flashIntensity) + Color.WHITE.b * flashIntensity,
                    1f
                );
            }
        }

        // Actualizar animación
        if (isAnimating && animationFrames != null && animationFrames.length > 0) {
            animationTimer += delta;
            if (animationTimer >= frameDuration) {
                animationTimer = 0;
                currentFrame++;
                if (currentFrame >= animationFrames.length) {
                    currentFrame = 0;
                    // Si solo una vuelta, detener animación
                    // isAnimating = false;
                }
                textureRegion = animationFrames[currentFrame];
            }
        }
    }

    /**
     * Establece opacidad (alpha).
     */
    public void setAlpha(float alpha) {
        this.alpha = Math.max(0f, Math.min(1f, alpha));
    }

    /**
     * Obtiene el ancho de la textura.
     */
    public float getWidth() {
        return textureRegion != null ? textureRegion.getRegionWidth() * scale : 0;
    }

    /**
     * Obtiene el alto de la textura.
     */
    public float getHeight() {
        return textureRegion != null ? textureRegion.getRegionHeight() * scale : 0;
    }
}
