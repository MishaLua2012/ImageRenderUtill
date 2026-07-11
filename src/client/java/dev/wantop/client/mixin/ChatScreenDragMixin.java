package dev.wantop.client.mixin;

import dev.wantop.client.image.ImageDragHandler;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Позволяет перетаскивать картинки, помеченные как draggable, зажав ЛКМ,
 * пока открыт экран чата. Мы намеренно не трогаем обычный игровой экран (без GUI),
 * чтобы не мешать управлению персонажем/камерой мышью.
 */
@Mixin(ChatScreen.class)
public abstract class ChatScreenDragMixin {

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void imageRenderUtill$onMouseClicked(double mouseX, double mouseY, int button,
                                                  CallbackInfoReturnable<Boolean> cir) {
        if (ImageDragHandler.onMouseClicked(mouseX, mouseY, button)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void imageRenderUtill$onMouseDragged(double mouseX, double mouseY, int button,
                                                  double dragX, double dragY,
                                                  CallbackInfoReturnable<Boolean> cir) {
        if (ImageDragHandler.onMouseDragged(mouseX, mouseY, button)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void imageRenderUtill$onMouseReleased(double mouseX, double mouseY, int button,
                                                   CallbackInfoReturnable<Boolean> cir) {
        if (ImageDragHandler.onMouseReleased(mouseX, mouseY, button)) {
            cir.setReturnValue(true);
        }
    }
}
