package dev.oblongboot.sxp.mixin;

import com.mojang.blaze3d.platform.Window;
import dev.oblongboot.sxp.utils.skia.SkiaContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.TracyFrameCapture;

@Mixin(Window.class)
public class WindowMixin {

  @Inject(method = "onFramebufferResize", at = @At("RETURN"))
  private void onFramebufferResize(long window, int width, int height, CallbackInfo ci) {
    int finalWidth = Math.max(width, 1);
    int finalHeight = Math.max(height, 1);
    System.out.println("Window resized to " + finalWidth + "x" + finalHeight);

    SkiaContext.INSTANCE.initSkia(finalWidth, finalHeight);
  }

  @Inject(method = "updateDisplay", at = @At("HEAD"))
  private void onUpdateDisplay(TracyFrameCapture capturer, CallbackInfo ci) {
    SkiaContext.INSTANCE.draw();
  }
}
