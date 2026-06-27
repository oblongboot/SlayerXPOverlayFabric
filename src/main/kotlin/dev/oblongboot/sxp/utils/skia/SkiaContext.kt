package dev.oblongboot.sxp.utils.skia

import io.github.humbleui.skija.*
import dev.oblongboot.sxp.events.EventManager
import dev.oblongboot.sxp.events.impl.SkiaDrawEvent
import dev.oblongboot.sxp.utils.skia.gl.States
import net.minecraft.client.Minecraft
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30

/*
 * This file is part of https://github.com/Lyzev/Skija.
 *
 * Copyright (c) 2025. Lyzev
 *
 * Skija is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, version 3 of the License, or
 * (at your option) any later version.
 *
 * Skija is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Skija. If not, see <https://www.gnu.org/licenses/>.
 */
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
  private var lastWidth = -1
  private var lastHeight = -1
  private var lastFbId = -1

  var canvas: Canvas? = null
    private set

  fun initSkia(width: Int, height: Int, fbId: Int) {
    val finalWidth = width.coerceAtLeast(1)
    val finalHeight = height.coerceAtLeast(1)

    if (context == null) {
      context = DirectContext.makeGL()
    }

    surface?.close()
    renderTarget?.close()

    renderTarget = WrappedBackendRenderTarget.makeGL(finalWidth, finalHeight, 0, 8, fbId, FramebufferFormat.GR_GL_RGBA8)
    surface = Surface.wrapBackendRenderTarget(
      requireNotNull(context),
      requireNotNull(renderTarget),
      SurfaceOrigin.BOTTOM_LEFT,
      ColorType.RGBA_8888,
      ColorSpace.getSRGB()
    )

    canvas = surface?.canvas
    lastWidth = finalWidth
    lastHeight = finalHeight
    lastFbId = fbId
  }

  fun ensureSkia(fbId: Int) {
    val window = Minecraft.getInstance().window
    val width = IntArray(1)
    val height = IntArray(1)
    GLFW.glfwGetFramebufferSize(window.handle(), width, height)
    if (context == null || surface == null || width[0] != lastWidth || height[0] != lastHeight || fbId != lastFbId) {
      initSkia(width[0], height[0], fbId)
    }
  }

  fun draw() {
    val fbId = GL30.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING)
    ensureSkia(fbId)
    if (context == null || surface == null) return

    States.push()
    GL11.glDisable(GL11.GL_CULL_FACE)

    context?.reset(*states)

    val activeCanvas = canvas
    val activeContext = context
    val activeRenderTarget = renderTarget
    val activeSurface = surface

    if (activeCanvas != null && activeContext != null && activeRenderTarget != null && activeSurface != null) {
      val guiScale = Minecraft.getInstance().window.guiScale.toFloat().coerceAtLeast(1f)
      activeCanvas.save()
      activeCanvas.scale(guiScale, guiScale)
      EventManager.post(SkiaDrawEvent(activeContext, activeRenderTarget, activeSurface, activeCanvas))
      activeCanvas.restore()
      activeContext.flushAndSubmit(activeSurface)
    }

    States.pop()
  }

}
