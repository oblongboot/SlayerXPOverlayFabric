package com.slayerxp.overlay.events

import com.slayerxp.overlay.events.EventManager
import net.minecraft.client.render.Camera
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack


abstract class WorldRenderEvent(val context: Context) {
  class Start(context: Context) : WorldRenderEvent(context)
  class Last(context: Context) : WorldRenderEvent(context)

  fun post() {
    EventManager.post(this)
  }
}

class Context {
  var matrixStack: MatrixStack? = null
  lateinit var consumers: VertexConsumerProvider
  lateinit var camera: Camera
}