package dev.oblongboot.sxp.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.oblongboot.sxp.events.Context;
import dev.oblongboot.sxp.events.WorldRenderEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.util.profiling.ProfilerFiller;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
* credit to quiteboring.dev (aka Nathan)
**/

@Mixin(LevelRenderer.class)
public class Mixin3DRendering {

  @Shadow
  @Final
  private RenderBuffers renderBuffers;

  @Unique
  private final Context ctx = new Context();

  @Inject(method = "renderLevel", at = @At("HEAD"))
  private void render(GraphicsResourceAllocator allocator, DeltaTracker tickCounter, boolean renderBlockOutline, Camera camera, Matrix4f positionMatrix, Matrix4f matrix4f, Matrix4f projectionMatrix, GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, CallbackInfo ci) {
    ctx.setConsumers(renderBuffers.bufferSource());
    ctx.setCamera(camera);
    new WorldRenderEvent.Start(ctx).post();
  }

  @Inject(method = "method_62214", at = @At("RETURN"))
  private void postRender(GpuBufferSlice gpuBufferSlice, LevelRenderState worldRenderState, ProfilerFiller profiler, Matrix4f matrix4f, ResourceHandle handle, ResourceHandle handle2, boolean bl, Frustum frustum, ResourceHandle handle3, ResourceHandle handle4, CallbackInfo ci) {
    new WorldRenderEvent.Last(ctx).post();
  }

  @ModifyExpressionValue(method = "method_62214", at = @At(value = "NEW", target = "()Lcom/mojang/blaze3d/vertex/PoseStack;"))
  private PoseStack setInternalStack(PoseStack original) {
    ctx.setMatrixStack(original);
    return original;
  }

}
