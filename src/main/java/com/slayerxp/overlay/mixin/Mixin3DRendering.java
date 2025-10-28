package com.slayerxp.overlay.mixin;

import com.slayerxp.overlay.events.EventManager;
import com.slayerxp.overlay.events.WorldRenderEvent;
import com.slayerxp.overlay.events.Context;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Unique;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.render.*;
import net.minecraft.client.render.state.WorldRenderState;
import net.minecraft.client.util.Handle;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
* credit to quiteboring.dev (aka Nathan)
**/

@Mixin(WorldRenderer.class)
public class Mixin3DRendering {

  @Shadow
  @Final
  private BufferBuilderStorage bufferBuilders;

  @Unique
  private final Context ctx = new Context();

  @Inject(method = "render", at = @At("HEAD"))
  private void render(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, Matrix4f positionMatrix, Matrix4f matrix4f, Matrix4f projectionMatrix, GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, CallbackInfo ci) {
    ctx.setConsumers(bufferBuilders.getEntityVertexConsumers());
    ctx.setCamera(camera);
    new WorldRenderEvent.Start(ctx).post();
  }

  @Inject(method = "method_62214", at = @At("RETURN"))
  private void postRender(GpuBufferSlice gpuBufferSlice, WorldRenderState worldRenderState, Profiler profiler, Matrix4f matrix4f, Handle handle, Handle handle2, boolean bl, Frustum frustum, Handle handle3, Handle handle4, CallbackInfo ci) {
    new WorldRenderEvent.Last(ctx).post();
  }

  @ModifyExpressionValue(method = "method_62214", at = @At(value = "NEW", target = "()Lnet/minecraft/client/util/math/MatrixStack;"))
  private MatrixStack setInternalStack(MatrixStack original) {
    ctx.setMatrixStack(original);
    return original;
  }

}
