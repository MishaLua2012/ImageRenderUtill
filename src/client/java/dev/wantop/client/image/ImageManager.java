package dev.wantop.client.image;

import dev.wantop.client.config.ImageUtillConfig;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Публичный API утилиты. Это единственный класс, с которым нужно работать,
 * чтобы вывести/двигать/накладывать друг на друга картинки на HUD.
 * <p>
 * Все методы статические и вызывать их можно из любого места клиентского кода
 * (например, из onInitializeClient() вашего мода или по любому игровому событию).
 * <p>
 * Пример использования смотри в README.md репозитория.
 */
public final class ImageManager {

    // LinkedHashMap — чтобы порядок добавления был предсказуем при равном layer.
    private static final Map<String, RenderedImage> IMAGES = new LinkedHashMap<>();

    private ImageManager() {
    }

    // ------------------------------------------------------------------
    // Функция 1: статичная картинка с заданной позицией/размером
    // ------------------------------------------------------------------

    /**
     * Показывает картинку на экране в указанной позиции и размере.
     *
     * @param id       уникальный идентификатор картинки (например, "mymod:logo")
     * @param texture  путь к текстуре, например ResourceLocation.fromNamespaceAndPath("mymod", "textures/gui/logo.png").
     *                 Файл должен лежать в assets/<namespace>/textures/gui/logo.png ресурсов мода
     *                 (см. README про размещение .png в resources).
     * @param x        координата X левого верхнего угла в пикселях экрана
     * @param y        координата Y левого верхнего угла в пикселях экрана
     * @param width    ширина в пикселях
     * @param height   высота в пикселях
     * @return созданный/обновлённый объект картинки для дальнейшей настройки
     */
    public static RenderedImage showImage(String id, ResourceLocation texture, int x, int y, int width, int height) {
        RenderedImage image = new RenderedImage(id, texture, x, y, width, height);
        IMAGES.put(id, image);
        return image;
    }

    /**
     * Удобный оверлоад: путь к текстуре как строка "namespace:path" или просто "path"
     * (тогда используется namespace "minecraft" — обычно вам нужен свой namespace,
     * так что предпочитайте вариант с ResourceLocation).
     */
    public static RenderedImage showImage(String id, String texturePath, int x, int y, int width, int height) {
        return showImage(id, ResourceLocation.parse(texturePath), x, y, width, height);
    }

    /**
     * Меняет позицию уже существующей картинки. Ничего не делает, если такой картинки нет.
     */
    public static void setPosition(String id, int x, int y) {
        RenderedImage image = IMAGES.get(id);
        if (image != null) {
            image.setPosition(x, y);
        }
    }

    /**
     * Меняет размер уже существующей картинки. Ничего не делает, если такой картинки нет.
     */
    public static void setSize(String id, int width, int height) {
        RenderedImage image = IMAGES.get(id);
        if (image != null) {
            image.setSize(width, height);
        }
    }

    /**
     * Показать/скрыть картинку без удаления её из менеджера.
     */
    public static void setVisible(String id, boolean visible) {
        RenderedImage image = IMAGES.get(id);
        if (image != null) {
            image.setVisible(visible);
        }
    }

    /**
     * Устанавливает прозрачность картинки (0.0 - полностью прозрачная, 1.0 - непрозрачная).
     */
    public static void setOpacity(String id, float opacity) {
        RenderedImage image = IMAGES.get(id);
        if (image != null) {
            image.setOpacity(opacity);
        }
    }

    /**
     * Полностью убирает картинку из рендера.
     */
    public static void remove(String id) {
        IMAGES.remove(id);
    }

    /**
     * Возвращает объект картинки по id, если такая зарегистрирована.
     */
    public static Optional<RenderedImage> get(String id) {
        return Optional.ofNullable(IMAGES.get(id));
    }

    // ------------------------------------------------------------------
    // Функция 2: перетаскиваемая мышкой картинка (в открытом чате)
    // ------------------------------------------------------------------

    /**
     * Показывает картинку, которую можно перетаскивать мышкой, зажав ЛКМ,
     * пока открыт экран чата. Позиция автоматически сохраняется в конфиг
     * (config/imagerenderutill.json) и восстанавливается при следующем запуске игры.
     *
     * @param id            уникальный идентификатор картинки, используется и как ключ конфига
     * @param texture       текстура картинки
     * @param defaultX      позиция X по умолчанию, если для этого id ещё нет сохранённой позиции
     * @param defaultY      позиция Y по умолчанию
     * @param width         ширина в пикселях
     * @param height        высота в пикселях
     * @return созданный объект картинки
     */
    public static RenderedImage showDraggableImage(String id, ResourceLocation texture,
                                                     int defaultX, int defaultY, int width, int height) {
        ImageUtillConfig.Entry saved = ImageUtillConfig.get(id);

        int x = defaultX;
        int y = defaultY;
        int w = width;
        int h = height;
        if (saved != null) {
            x = saved.x;
            y = saved.y;
            w = saved.width;
            h = saved.height;
        }

        RenderedImage image = new RenderedImage(id, texture, x, y, w, h);
        image.setDraggable(true);
        IMAGES.put(id, image);

        // Гарантируем, что запись в конфиге существует с текущими значениями.
        ImageUtillConfig.put(id, x, y, w, h);
        return image;
    }

    public static RenderedImage showDraggableImage(String id, String texturePath,
                                                     int defaultX, int defaultY, int width, int height) {
        return showDraggableImage(id, ResourceLocation.parse(texturePath), defaultX, defaultY, width, height);
    }

    /**
     * Вызывается внутренним обработчиком перетаскивания после того, как пользователь
     * отпустил мышь — сохраняет новую позицию в конфиг. Обычно вызывать вручную не требуется.
     */
    public static void persistPosition(String id) {
        RenderedImage image = IMAGES.get(id);
        if (image != null) {
            ImageUtillConfig.put(id, image.getX(), image.getY(), image.getWidth(), image.getHeight());
        }
    }

    // ------------------------------------------------------------------
    // Функция 3: наложение одной картинки на другую (overlay)
    // ------------------------------------------------------------------

    /**
     * Создаёт две картинки друг на друге: базовую (большую, нижний слой)
     * и накладываемую (меньшую, верхний слой), выровненную по центру базовой.
     *
     * @param baseId       id базовой (нижней, большей) картинки
     * @param baseTexture  текстура базовой картинки
     * @param overlayId    id накладываемой (верхней, меньшей) картинки
     * @param overlayTexture текстура накладываемой картинки
     * @param x            X базовой картинки
     * @param y            Y базовой картинки
     * @param baseWidth    ширина базовой картинки
     * @param baseHeight   высота базовой картинки
     * @param overlayWidth  ширина накладываемой картинки (должна быть меньше baseWidth)
     * @param overlayHeight высота накладываемой картинки (должна быть меньше baseHeight)
     */
    public static void showOverlayImages(String baseId, ResourceLocation baseTexture,
                                          String overlayId, ResourceLocation overlayTexture,
                                          int x, int y, int baseWidth, int baseHeight,
                                          int overlayWidth, int overlayHeight) {
        RenderedImage base = showImage(baseId, baseTexture, x, y, baseWidth, baseHeight);
        base.setLayer(0);

        int overlayX = x + (baseWidth - overlayWidth) / 2;
        int overlayY = y + (baseHeight - overlayHeight) / 2;

        RenderedImage overlay = showImage(overlayId, overlayTexture, overlayX, overlayY, overlayWidth, overlayHeight);
        overlay.setLayer(1);
    }

    public static void showOverlayImages(String baseId, String baseTexturePath,
                                          String overlayId, String overlayTexturePath,
                                          int x, int y, int baseWidth, int baseHeight,
                                          int overlayWidth, int overlayHeight) {
        showOverlayImages(baseId, ResourceLocation.parse(baseTexturePath),
                overlayId, ResourceLocation.parse(overlayTexturePath),
                x, y, baseWidth, baseHeight, overlayWidth, overlayHeight);
    }

    // ------------------------------------------------------------------
    // Внутреннее использование (рендер / drag-обработчик)
    // ------------------------------------------------------------------

    /**
     * Возвращает все зарегистрированные картинки. Используется рендерером.
     * Не изменяйте коллекцию напрямую — используйте методы менеджера.
     */
    public static Iterable<RenderedImage> getAll() {
        return IMAGES.values();
    }
}
