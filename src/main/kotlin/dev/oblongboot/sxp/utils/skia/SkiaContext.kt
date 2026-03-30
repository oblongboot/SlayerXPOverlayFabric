package dev.oblongboot.sxp.utils.skia

import io.github.humbleui.skija.*
import dev.oblongboot.sxp.events.EventManager
import dev.oblongboot.sxp.events.impl.SkiaDrawEvent
import dev.oblongboot.sxp.utils.skia.gl.States
import org.lwjgl.opengl.GL11

internal object SkiaContext {

  private val states = arrayOf(
    BackendState.GL_BLEND,
    BackendState.GL_VERTEX,
    BackendState.GL_PIXEL_STORE,
    BackendState.GL_TEXTURE_BINDING,
    BackendState.GL_MISC
  )

  private var context: DirectContext? = null
  private var renderTarget: WrappedBackendRenderTarget? = null
  private var surface: Surface? = null

  var canvas: Canvas? = null
    private set

  fun initSkia(width: Int, height: Int) {
    if (context == null) {
      context = DirectContext.makeGL()
    }

    surface?.close()
    renderTarget?.close()

    renderTarget = WrappedBackendRenderTarget.makeGL(width, height, 0, 8, 0, FramebufferFormat.GR_GL_RGBA8)
    surface = Surface.wrapBackendRenderTarget(
      requireNotNull(context),
      requireNotNull(renderTarget),
      SurfaceOrigin.BOTTOM_LEFT,
      ColorType.RGBA_8888,
      ColorSpace.getSRGB()
    )

    canvas = surface?.canvas
  }

  fun draw() {
    if (context == null || surface == null) return

    States.push()
    GL11.glDisable(GL11.GL_CULL_FACE)
    GL11.glClearColor(0f, 0f, 0f, 0f)

    context?.reset(*states)

    canvas?.let { canvas ->
      context?.let { context ->
        renderTarget?.let { renderTarget ->
          surface?.let { surface ->
             EventManager.post(SkiaDrawEvent(context, renderTarget, surface, canvas))
          }
        }
      }
    }

    context?.flushAndSubmit(surface)

    States.pop()
  }

}
