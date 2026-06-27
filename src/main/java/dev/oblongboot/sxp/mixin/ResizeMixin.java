package dev.oblongboot.sxp.mixin;

import net.minecraft.client.Minecraft;
import dev.oblongboot.sxp.utils.skia.SkiaContext;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class ResizeMixin {

  @Inject(method = "resizeGui", at = @At("RETURN"))
  private void onResizeGui(CallbackInfo ci) {
    long windowHandle = ((Minecraft)(Object)this).getWindow().handle();
    int[] width = new int[1];
    int[] height = new int[1];
    GLFW.glfwGetFramebufferSize(windowHandle, width, height);
    int fbId = GL30.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
    int finalWidth = Math.max(width[0], 1);
    int finalHeight = Math.max(height[0], 1);
    SkiaContext.INSTANCE.initSkia(finalWidth, finalHeight, fbId);
  }
}
