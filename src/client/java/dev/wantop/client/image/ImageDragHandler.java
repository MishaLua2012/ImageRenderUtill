package dev.wantop.client.image;

/**
 * Обрабатывает перетаскивание draggable-картинок мышкой, пока открыт экран чата.
 * Вызывается из {@link dev.wantop.client.mixin.ChatScreenDragMixin}.
 * <p>
 * Логика: при нажатии ЛКМ проверяем, попал ли клик в одну из draggable-картинок
 * (сверху вниз по слоям, чтобы верхняя картинка перехватывала клик первой).
 * Пока кнопка зажата и идёт mouseDragged — двигаем картинку вместе с курсором,
 * сохраняя смещение клика внутри картинки, чтобы она не "прыгала" под курсор.
 * При отпускании кнопки — сохраняем новую позицию в конфиг.
 */
public final class ImageDragHandler {

    private static final int LEFT_MOUSE_BUTTON = 0;

    private static RenderedImage dragging;
    private static double grabOffsetX;
    private static double grabOffsetY;

    private ImageDragHandler() {
    }

    /**
     * @return true, если клик был "поглощён" перетаскиваемой картинкой
     *         (тогда ванильная обработка клика в чате должна быть отменена).
     */
    public static boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (button != LEFT_MOUSE_BUTTON) {
            return false;
        }

        RenderedImage target = findTopmostDraggableAt(mouseX, mouseY);
        if (target == null) {
            return false;
        }

        dragging = target;
        grabOffsetX = mouseX - target.getX();
        grabOffsetY = mouseY - target.getY();
        return true;
    }

    public static boolean onMouseDragged(double mouseX, double mouseY, int button) {
        if (dragging == null || button != LEFT_MOUSE_BUTTON) {
            return false;
        }

        int newX = (int) Math.round(mouseX - grabOffsetX);
        int newY = (int) Math.round(mouseY - grabOffsetY);
        dragging.setPosition(newX, newY);
        return true;
    }

    public static boolean onMouseReleased(double mouseX, double mouseY, int button) {
        if (dragging == null || button != LEFT_MOUSE_BUTTON) {
            return false;
        }

        ImageManager.persistPosition(dragging.getId());
        dragging = null;
        return true;
    }

    private static RenderedImage findTopmostDraggableAt(double mouseX, double mouseY) {
        RenderedImage best = null;
        for (RenderedImage image : ImageManager.getAll()) {
            if (!image.isDraggable() || !image.isVisible()) {
                continue;
            }
            if (image.isPointInside(mouseX, mouseY)) {
                if (best == null || image.getLayer() >= best.getLayer()) {
                    best = image;
                }
            }
        }
        return best;
    }
}
