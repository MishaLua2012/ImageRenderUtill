package dev.wantop.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.wantop.ImageRenderUtill;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Простое JSON-хранилище позиций/размеров картинок, помеченных как "сохраняемые"
 * (обычно это перетаскиваемые картинки). Файл лежит в config/imagerenderutill.json.
 * <p>
 * Хранилище общее для всех модов, использующих эту утилиту в рамках одной сборки:
 * записи разделяются по ключу {@code id}, который передаёт вызывающий код,
 * поэтому рекомендуется использовать префикс с modid, например "mymod:companion".
 */
public final class ImageUtillConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type MAP_TYPE = new TypeToken<Map<String, Entry>>() {}.getType();

    private static final Path CONFIG_PATH =
            FabricLoader.getInstance().getConfigDir().resolve("imagerenderutill.json");

    private static Map<String, Entry> cache;

    private ImageUtillConfig() {
    }

    /**
     * Позиция и размер, которые сохраняются на диск.
     */
    public static final class Entry {
        public int x;
        public int y;
        public int width;
        public int height;

        public Entry() {
        }

        public Entry(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    private static synchronized Map<String, Entry> getCache() {
        if (cache == null) {
            cache = load();
        }
        return cache;
    }

    private static Map<String, Entry> load() {
        if (!Files.exists(CONFIG_PATH)) {
            return new HashMap<>();
        }
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8)) {
            Map<String, Entry> data = GSON.fromJson(reader, MAP_TYPE);
            return data != null ? data : new HashMap<>();
        } catch (IOException | com.google.gson.JsonSyntaxException e) {
            ImageRenderUtill.LOGGER.warn("[ImageRenderUtill] Не удалось прочитать конфиг {}: {}", CONFIG_PATH, e.getMessage());
            return new HashMap<>();
        }
    }

    private static synchronized void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH, StandardCharsets.UTF_8)) {
                GSON.toJson(cache, MAP_TYPE, writer);
            }
        } catch (IOException e) {
            ImageRenderUtill.LOGGER.warn("[ImageRenderUtill] Не удалось сохранить конфиг {}: {}", CONFIG_PATH, e.getMessage());
        }
    }

    /**
     * Возвращает сохранённую позицию/размер для данного id, если она есть.
     */
    public static Entry get(String id) {
        return getCache().get(id);
    }

    /**
     * Сохраняет позицию/размер для данного id и сразу пишет файл на диск.
     */
    public static void put(String id, int x, int y, int width, int height) {
        getCache().put(id, new Entry(x, y, width, height));
        save();
    }

    /**
     * Удаляет сохранённую запись (например, если картинка больше не используется).
     */
    public static void remove(String id) {
        if (getCache().remove(id) != null) {
            save();
        }
    }
}
