package dev.wantop.client.image;

import net.minecraft.resources.ResourceLocation;

/**
 * Представляет одну картинку, отображаемую на экране (HUD).
 * <p>
 * Все координаты — это координаты левого верхнего угла картинки
 * в экранных пикселях (в системе координат HUD, т.е. с учётом gui scale).
 */
public class RenderedImage {

    private final String id;
    private final ResourceLocation texture;

    private int x;
    private int y;
    private int width;
    private int height;

    private boolean visible = true;
    private boolean draggable = false;

    /**
     * Слой отрисовки. Картинки с большим значением layer рисуются позже
     * (то есть визуально поверх картинок с меньшим layer). Используется
     * для эффекта "картинка в картинке" (overlay).
     */
    private int layer = 0;

    private float opacity = 1.0f;

    public RenderedImage(String id, ResourceLocation texture, int x, int y, int width, int height) {
        this.id = id;
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public String getId() {
        return id;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isDraggable() {
        return draggable;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = Math.max(0.0f, Math.min(1.0f, opacity));
    }

    /**
     * Проверяет, попадает ли точка экрана (px, py) внутрь прямоугольника картинки.
     */
    public boolean isPointInside(double px, double py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }
}
