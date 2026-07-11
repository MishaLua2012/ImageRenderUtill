package dev.wantop.client.image;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;

import java.util.ArrayList;
import java.util.List;

/**
 * Отвечает за фактическую отрисовку всех картинок, зарегистрированных в {@link ImageManager},
 * поверх HUD. Картинки рисуются в порядке возрастания {@code layer}, поэтому картинки
 * с большим layer оказываются визуально сверху (используется для overlay-эффекта).
 */
public final class ImageHudRenderer {

    private ImageHudRenderer() {
    }

    public static void render(GuiGraphics graphics) {
        List<RenderedImage> images = new ArrayList<>();
        for (RenderedImage image : ImageManager.getAll()) {
            if (image.isVisible()) {
                images.add(image);
            }
        }
        images.sort((a, b) -> Integer.compare(a.getLayer(), b.getLayer()));

        for (RenderedImage image : images) {
            drawImage(graphics, image);
        }
    }

    private static void drawImage(GuiGraphics graphics, RenderedImage image) {
        float opacity = image.getOpacity();
        if (opacity <= 0f) {
            return;
        }

        boolean useAlpha = opacity < 1.0f;
        if (useAlpha) {
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, opacity);
        }

        // blit(renderTypeGetter, texture, x, y, u, v, uWidth, vHeight, width, height, textureWidth, textureHeight)
        // Растягиваем всю текстуру (0,0 -> textureWidth,textureHeight) на заданный width/height экрана.
        graphics.blit(
                RenderType::guiTextured,
                image.getTexture(),
                image.getX(), image.getY(),
                0f, 0f,
                image.getWidth(), image.getHeight(),
                image.getWidth(), image.getHeight(),
                image.getWidth(), image.getHeight()
        );

        if (useAlpha) {
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.disableBlend();
        }
    }
}
