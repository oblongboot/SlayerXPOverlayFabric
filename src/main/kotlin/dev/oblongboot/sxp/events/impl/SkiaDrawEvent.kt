package dev.oblongboot.sxp.events.impl

import io.github.humbleui.skija.Canvas
import io.github.humbleui.skija.DirectContext
import dev.oblongboot.sxp.utils.skia.WrappedBackendRenderTarget

class SkiaDrawEvent(
  val context: DirectContext,
  val renderTarget: WrappedBackendRenderTarget,
  val surface: io.github.humbleui.skija.Surface,
  val canvas: Canvas
)
