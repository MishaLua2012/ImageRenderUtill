package dev.wantop;

import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Общие константы и утилитарные методы, доступные и на клиенте.
 * Основная логика мода (весь публичный API) находится в клиентском пакете
 * {@code dev.wantop.client}, так как эта утилита предназначена только для клиента.
 */
public final class ImageRenderUtill {
    public static final String MOD_ID = "imagerenderutill";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private ImageRenderUtill() {
    }

    /**
     * Собирает ResourceLocation с namespace этого мода, например:
     * {@code ImageRenderUtill.id("textures/gui/my_image.png")}.
     */
    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
