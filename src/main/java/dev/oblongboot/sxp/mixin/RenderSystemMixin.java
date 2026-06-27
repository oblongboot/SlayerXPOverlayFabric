package dev.oblongboot.sxp.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.TracyFrameCapture;
import dev.oblongboot.sxp.utils.skia.SkiaContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderSystem.class, remap = false)
public class RenderSystemMixin {

  @Inject(method = "flipFrame", at = @At("HEAD"))
  private static void onFlipFrame(TracyFrameCapture capturer, CallbackInfo ci) {
    SkiaContext.INSTANCE.draw();
  }
}
