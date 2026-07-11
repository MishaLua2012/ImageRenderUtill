package dev.wantop.client;

import dev.wantop.ImageRenderUtill;
import dev.wantop.client.image.ImageHudRenderer;
import dev.wantop.client.image.ImageManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class ImageRenderUtillClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Регистрируем отрисовку всех картинок, добавленных через ImageManager, поверх HUD.
        // Это единственное, что обязательно нужно сделать, чтобы утилита заработала.
        HudRenderCallback.EVENT.register((graphics, tickCounter) -> ImageHudRenderer.render(graphics));

        // -----------------------------------------------------------------
        // Ниже — демонстрационные вызовы всех трёх функций утилиты.
        // Удалите или закомментируйте этот блок в реальном проекте и вызывайте
        // методы ImageManager там, где вам нужно (по событию, по команде и т.д.).
        // Текстуры для примера лежат в assets/imagerenderutill/textures/gui/.
        // -----------------------------------------------------------------

        // 1) Статичная картинка с заданной позицией и размером.
        ImageManager.showImage(
                "imagerenderutill:example_static",
                ImageRenderUtill.id("textures/gui/example_static.png"),
                10, 10, 64, 64
        );

        // 2) Перетаскиваемая мышкой картинка (двигается зажатием ЛКМ в открытом чате).
        //    Позиция сохраняется в config/imagerenderutill.json между запусками игры.
        ImageManager.showDraggableImage(
                "imagerenderutill:example_draggable",
                ImageRenderUtill.id("textures/gui/example_draggable.png"),
                100, 10, 48, 48
        );

        // 3) Наложение картинки на картинку: большая базовая внизу, меньшая — по центру сверху.
        ImageManager.showOverlayImages(
                "imagerenderutill:example_base", ImageRenderUtill.id("textures/gui/example_base.png"),
                "imagerenderutill:example_overlay", ImageRenderUtill.id("textures/gui/example_overlay.png"),
                10, 100, 96, 96, 48, 48
        );
    }
}
