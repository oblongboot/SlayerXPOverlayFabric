package dev.oblongboot.sxp.mixin;

import dev.oblongboot.sxp.utils.skia.SkiaContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void registerSkia(GameConfig gameConfig, CallbackInfo ci) {
        int[] width = new int[1];
        int[] height = new int[1];

        long windowHandle = Minecraft.getInstance().getWindow().handle();
        GLFW.glfwGetFramebufferSize(windowHandle, width, height);

        int finalWidth = Math.max(width[0], 1);
        int finalHeight = Math.max(height[0], 1);

        SkiaContext.INSTANCE.initSkia(finalWidth, finalHeight);
    }

    @Inject(
        method = "renderFrame",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/RenderSystem;flipFrame(Lcom/mojang/blaze3d/TracyFrameCapture;)V"
        )
    )
    private void onBeforeFlipFrame(boolean advanceGameTime, CallbackInfo ci) {
        SkiaContext.INSTANCE.draw();
    }

}
