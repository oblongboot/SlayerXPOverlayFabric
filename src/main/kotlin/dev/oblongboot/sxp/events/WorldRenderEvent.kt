package dev.oblongboot.sxp.events

import dev.oblongboot.sxp.events.EventManager
import net.minecraft.client.Camera
import net.minecraft.client.renderer.MultiBufferSource
import com.mojang.blaze3d.vertex.PoseStack


abstract class WorldRenderEvent(val context: Context) {
  class Start(context: Context) : WorldRenderEvent(context)
  class Last(context: Context) : WorldRenderEvent(context)

  fun post() {
    EventManager.post(this)
  }
}

class Context {
  var matrixStack: PoseStack? = null
  lateinit var consumers: MultiBufferSource
  lateinit var camera: Camera
}