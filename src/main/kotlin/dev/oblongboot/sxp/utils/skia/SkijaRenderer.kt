package dev.oblongboot.sxp.utils.skia

import com.mojang.blaze3d.opengl.GlDevice
import com.mojang.blaze3d.opengl.GlStateManager
import com.mojang.blaze3d.opengl.GlTexture
import com.mojang.blaze3d.systems.RenderSystem
import dev.oblongboot.sxp.utils.skia.gl.State
import io.github.humbleui.skija.*
import io.github.humbleui.skija.Font as SkijaFont
import io.github.humbleui.types.IRect
import io.github.humbleui.types.Rect
import io.github.humbleui.types.RRect
import net.minecraft.client.Minecraft
import org.lwjgl.opengl.GL11C
import org.lwjgl.opengl.GL12C
import org.lwjgl.opengl.GL30C
import java.util.concurrent.CopyOnWriteArrayList
//CREDIT TO @altEpsilonPhoenix on discord (hes goated)
object SkijaRenderer {
    private const val GL_STATE_TEXTURE_UNITS = 12

    enum class GradientDirection {
        LEFT_TO_RIGHT,
        TOP_TO_BOTTOM,
        TOP_LEFT_TO_BOTTOM_RIGHT,
        BOTTOM_LEFT_TO_TOP_RIGHT
    }

    private val mc = Minecraft.getInstance()
    private val renderCallbacks = CopyOnWriteArrayList<Runnable>()
    private val topRenderCallbacks = CopyOnWriteArrayList<Runnable>()

    var context: DirectContext? = null
        internal set
    var surface: Surface? = null
        internal set
    var canvas: Canvas? = null
        internal set

    var isDrawing = false
        internal set

    private var hostGlState: State? = null
    private var scissorStackDepth = 0
    private var lastRTWidth = -1
    private var lastRTHeight = -1
    private var skipBlurFrames = 0

    fun bindEvent(event: dev.oblongboot.sxp.events.impl.SkiaDrawEvent) {
        this.context = event.context
        this.surface = event.surface
        this.canvas = event.canvas
        this.isDrawing = true
    }

    fun unbindEvent() {
        this.context = null
        this.surface = null
        this.canvas = null
        this.isDrawing = false
    }

    fun registerRender(runnable: Runnable) = renderCallbacks.add(runnable)
    fun unregisterRender(runnable: Runnable) = renderCallbacks.remove(runnable)
    fun registerTopRender(runnable: Runnable) = topRenderCallbacks.add(runnable)
    fun unregisterTopRender(runnable: Runnable) = topRenderCallbacks.remove(runnable)
    fun hasTopRenderCallbacks(): Boolean = topRenderCallbacks.isNotEmpty()

    fun runDrawables() {
        renderCallbacks.forEach {
            try {
                it.run()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun runTopDrawables() {
        topRenderCallbacks.forEach {
            try {
                it.run()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun renderTopCallbacks(width: Float, height: Float) {
        if (topRenderCallbacks.isEmpty()) return

        beginFrame(width, height)
        if (!isDrawing) return

        try {
            runTopDrawables()
        } finally {
            endFrame()
        }
    }

    fun beginFrame(width: Float, height: Float) {
        if (isDrawing) return
        if (width <= 0f || height <= 0f) return

        if (context == null) {
            context = DirectContext.makeGL()
        }
        val directContext = context ?: return

        val renderTarget = mc.mainRenderTarget
        val device = RenderSystem.getDevice() as? GlDevice ?: return
        val colorTexture = renderTarget.colorTexture as? GlTexture ?: return
        val glFramebuffer = colorTexture.getFbo(device.directStateAccess(), renderTarget.depthTexture)

        hostGlState = State(330).push()

        try {
            directContext.resetGLAll()

            GlStateManager._glBindFramebuffer(GL30C.GL_FRAMEBUFFER, glFramebuffer)
            GlStateManager._viewport(0, 0, renderTarget.width, renderTarget.height)
            GlStateManager._colorMask(true, true, true, true)
            GlStateManager._disableCull()
            GlStateManager._disableScissorTest()
            GlStateManager._disableDepthTest()
            GlStateManager._depthMask(false)
            GlStateManager._enableBlend()
            GlStateManager._blendFuncSeparate(GL11C.GL_SRC_ALPHA, GL11C.GL_ONE_MINUS_SRC_ALPHA, GL11C.GL_ONE, GL11C.GL_ONE_MINUS_SRC_ALPHA)

            GL11C.glPixelStorei(GL11C.GL_UNPACK_ALIGNMENT, 4)
            GL11C.glPixelStorei(GL11C.GL_PACK_ALIGNMENT, 4)
            GL11C.glPixelStorei(GL12C.GL_UNPACK_ROW_LENGTH, 0)
            GL11C.glPixelStorei(GL12C.GL_UNPACK_SKIP_PIXELS, 0)
            GL11C.glPixelStorei(GL12C.GL_UNPACK_SKIP_ROWS, 0)

            val backendRT = BackendRenderTarget.makeGL(
                renderTarget.width,
                renderTarget.height,
                0,
                8,
                glFramebuffer,
                FramebufferFormat.GR_GL_RGBA8
            )

            val wrappedSurface = Surface.wrapBackendRenderTarget(
                directContext,
                backendRT,
                SurfaceOrigin.BOTTOM_LEFT,
                ColorType.RGBA_8888,
                ColorSpace.getSRGB()
            )

            surface = wrappedSurface
            canvas = wrappedSurface.canvas
            isDrawing = true
            scissorStackDepth = 0

            val rtW = renderTarget.width
            val rtH = renderTarget.height
            if (rtW != lastRTWidth || rtH != lastRTHeight) {
                lastRTWidth = rtW
                lastRTHeight = rtH
                skipBlurFrames = 5
            } else if (skipBlurFrames > 0) {
                skipBlurFrames--
            }

            val guiScale = mc.window.guiScale.toFloat()
            canvas?.scale(guiScale, guiScale)
        } catch (_: Throwable) {
            surface?.close()
            surface = null
            canvas = null
            isDrawing = false
            scissorStackDepth = 0
            restoreHostGLState()
        }
    }

    fun endFrame() {
        if (!isDrawing) return

        try {
            while (scissorStackDepth > 0) {
                canvas?.restore()
                scissorStackDepth--
            }
            context?.flushAndSubmit(surface)
        } finally {
            surface?.close()
            context?.resetGLAll()
            restoreHostGLState()

            isDrawing = false
            canvas = null
            surface = null
            scissorStackDepth = 0
        }
    }

    fun save() = canvas?.save()
    fun restore() = canvas?.restore()
    fun translate(x: Float, y: Float) = canvas?.translate(x, y)
    fun rotate(angleDeg: Float) = canvas?.rotate(angleDeg)
    fun scale(x: Float, y: Float) = canvas?.scale(x, y)

    fun pushScissor(x: Float, y: Float, w: Float, h: Float) {
        if (w <= 0f || h <= 0f) return
        canvas?.save()
        canvas?.clipRect(Rect.makeXYWH(x, y, w, h))
        scissorStackDepth++
    }

    fun popScissor() {
        if (scissorStackDepth <= 0) return
        canvas?.restore()
        scissorStackDepth--
    }

    fun drawRoundedRect(x: Float, y: Float, w: Float, h: Float, radius: Float, colorARGB: Int) {
        if (w <= 0f || h <= 0f) return
        Paint().setColor(colorARGB).use { paint ->
            canvas?.drawRRect(RRect.makeXYWH(x, y, w, h, radius.coerceAtLeast(0f)), paint)
        }
    }

    fun drawRoundedRectVaried(
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        topLeftRadius: Float,
        topRightRadius: Float,
        bottomRightRadius: Float,
        bottomLeftRadius: Float,
        colorARGB: Int
    ) {
        if (w <= 0f || h <= 0f) return
        Paint().setColor(colorARGB).use { paint ->
            canvas?.drawRRect(
                RRect.makeComplexXYWH(
                    x,
                    y,
                    w,
                    h,
                    floatArrayOf(
                        topLeftRadius.coerceAtLeast(0f), topLeftRadius.coerceAtLeast(0f),
                        topRightRadius.coerceAtLeast(0f), topRightRadius.coerceAtLeast(0f),
                        bottomRightRadius.coerceAtLeast(0f), bottomRightRadius.coerceAtLeast(0f),
                        bottomLeftRadius.coerceAtLeast(0f), bottomLeftRadius.coerceAtLeast(0f)
                    )
                ),
                paint
            )
        }
    }

    fun drawRoundedRectBorder(
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        radius: Float,
        borderWidth: Float,
        borderColorARGB: Int
    ) {
        if (w <= 0f || h <= 0f || borderWidth <= 0f) return
        Paint()
            .setColor(borderColorARGB)
            .setMode(PaintMode.STROKE)
            .setStrokeWidth(borderWidth)
            .use { paint ->
                canvas?.drawRRect(RRect.makeXYWH(x, y, w, h, radius.coerceAtLeast(0f)), paint)
            }
    }

    fun drawRoundedRectVariedBorder(
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        topLeftRadius: Float,
        topRightRadius: Float,
        bottomRightRadius: Float,
        bottomLeftRadius: Float,
        borderWidth: Float,
        borderColorARGB: Int
    ) {
        if (w <= 0f || h <= 0f || borderWidth <= 0f) return
        Paint()
            .setColor(borderColorARGB)
            .setMode(PaintMode.STROKE)
            .setStrokeWidth(borderWidth)
            .use { paint ->
                canvas?.drawRRect(
                    RRect.makeComplexXYWH(
                        x,
                        y,
                        w,
                        h,
                        floatArrayOf(
                            topLeftRadius.coerceAtLeast(0f), topLeftRadius.coerceAtLeast(0f),
                            topRightRadius.coerceAtLeast(0f), topRightRadius.coerceAtLeast(0f),
                            bottomRightRadius.coerceAtLeast(0f), bottomRightRadius.coerceAtLeast(0f),
                            bottomLeftRadius.coerceAtLeast(0f), bottomLeftRadius.coerceAtLeast(0f)
                        )
                    ),
                    paint
                )
            }
    }

    fun drawRoundedGlow(
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        radius: Float,
        colorARGB: Int,
        blurSigma: Float,
        spread: Float = 0f
    ) {
        if (w <= 0f || h <= 0f || blurSigma <= 0f) return

        val activeCanvas = canvas ?: return
        val padding = blurSigma * 3f + spread
        val outerX = x - spread
        val outerY = y - spread
        val outerW = w + spread * 2f
        val outerH = h + spread * 2f
        val outerRadius = (radius + spread).coerceAtLeast(0f)
        val layerBounds = Rect.makeXYWH(
            x - padding,
            y - padding,
            w + padding * 2f,
            h + padding * 2f
        )

        Paint().use { layerPaint ->
            activeCanvas.saveLayer(layerBounds, layerPaint)
        }

        try {
            ImageFilter.makeBlur(blurSigma, blurSigma, FilterTileMode.DECAL).use { blurFilter ->
                Paint()
                    .setColor(colorARGB)
                    .setImageFilter(blurFilter)
                    .use { glowPaint ->
                        activeCanvas.drawRRect(
                            RRect.makeXYWH(outerX, outerY, outerW, outerH, outerRadius),
                            glowPaint
                        )
                    }
            }
        } finally {
            activeCanvas.restore()
        }
    }

    fun drawBackdropBlur(x: Float, y: Float, w: Float, h: Float, radius: Float, blurSigma: Float, alpha: Float = 1f) {
        if (w <= 0f || h <= 0f || blurSigma <= 0f) return
        if (skipBlurFrames > 0) return

        val activeSurface = surface ?: return
        val guiScale = mc.window.guiScale.toFloat().coerceAtLeast(1f)
        val blurPadding = blurSigma * 3f

        val expandedX = x - blurPadding
        val expandedY = y - blurPadding
        val expandedW = w + blurPadding * 2f
        val expandedH = h + blurPadding * 2f

        val snapshotX = kotlin.math.floor(expandedX * guiScale).toInt().coerceAtLeast(0)
        val snapshotY = kotlin.math.floor(expandedY * guiScale).toInt().coerceAtLeast(0)
        val snapshotRight = kotlin.math.ceil((expandedX + expandedW) * guiScale).toInt().coerceAtMost(activeSurface.width)
        val snapshotBottom = kotlin.math.ceil((expandedY + expandedH) * guiScale).toInt().coerceAtMost(activeSurface.height)
        val snapshotW = (snapshotRight - snapshotX).coerceAtLeast(1)
        val snapshotH = (snapshotBottom - snapshotY).coerceAtLeast(1)
        val skiaSnapshotY = activeSurface.height - snapshotY - snapshotH

        context?.flush()
        activeSurface.makeImageSnapshot(IRect.makeXYWH(snapshotX, skiaSnapshotY, snapshotW, snapshotH))?.use { snapshot ->
            val srcRect = Rect.makeWH(snapshot.width.toFloat(), snapshot.height.toFloat())
            val dstRect = Rect.makeXYWH(snapshotX / guiScale, snapshotY / guiScale, snapshotW / guiScale, snapshotH / guiScale)

            ImageFilter.makeBlur(blurSigma, blurSigma, FilterTileMode.DECAL).use { blurFilter ->
                Paint()
                    .setAlphaf(alpha.coerceIn(0f, 1f))
                    .setImageFilter(blurFilter)
                    .use { paint ->
                        canvas?.save()
                        canvas?.clipRRect(RRect.makeXYWH(x, y, w, h, radius.coerceAtLeast(0f)), true)
                        canvas?.drawImageRect(snapshot, srcRect, dstRect, paint, true)
                        canvas?.restore()
                    }
            }
        }
    }

    fun drawRoundedRectGradient(
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        radius: Float,
        colorStartARGB: Int,
        colorEndARGB: Int,
        direction: GradientDirection = GradientDirection.LEFT_TO_RIGHT
    ) {
        if (w <= 0f || h <= 0f) return

        val shader = createLinearGradientShader(x, y, w, h, colorStartARGB, colorEndARGB, direction)
        shader.use { linearGradient ->
            Paint().setShader(linearGradient).use { paint ->
                canvas?.drawRRect(RRect.makeXYWH(x, y, w, h, radius.coerceAtLeast(0f)), paint)
            }
        }
    }

    fun drawRoundedRectBorderGradient(
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        radius: Float,
        borderWidth: Float,
        colorStartARGB: Int,
        colorEndARGB: Int,
        direction: GradientDirection = GradientDirection.LEFT_TO_RIGHT
    ) {
        if (w <= 0f || h <= 0f || borderWidth <= 0f) return

        val shader = createLinearGradientShader(x, y, w, h, colorStartARGB, colorEndARGB, direction)
        shader.use { linearGradient ->
            Paint()
                .setShader(linearGradient)
                .setMode(PaintMode.STROKE)
                .setStrokeWidth(borderWidth)
                .use { paint ->
                    canvas?.drawRRect(RRect.makeXYWH(x, y, w, h, radius.coerceAtLeast(0f)), paint)
                }
        }
    }

    fun drawText(text: String, x: Float, y: Float, colorARGB: Int, font: SkijaFont) {
        TextLine.make(text, font).use { line ->
            val baseline = y - line.ascent
            Paint().setColor(colorARGB).use { paint ->
                canvas?.drawTextLine(line, x, baseline, paint)
            }
        }
    }

    fun getTextWidth(text: String, font: SkijaFont): Float {
        TextLine.make(text, font).use { line ->
            return line.width
        }
    }

    fun drawImage(skImage: Image, x: Float, y: Float, w: Float, h: Float, alpha: Float = 1f, radius: Float = 0f) {
        if (w <= 0f || h <= 0f) return

        val srcRect = Rect.makeWH(skImage.width.toFloat(), skImage.height.toFloat())
        drawImageInternal(skImage, srcRect, x, y, w, h, alpha, radius)
    }

    fun drawImageCropped(
        skImage: Image,
        srcX: Float,
        srcY: Float,
        srcW: Float,
        srcH: Float,
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        alpha: Float = 1f,
        radius: Float = 0f
    ) {
        if (w <= 0f || h <= 0f || srcW <= 0f || srcH <= 0f) return

        val imageW = skImage.width.toFloat()
        val imageH = skImage.height.toFloat()
        val clampedSrcX = srcX.coerceIn(0f, imageW)
        val clampedSrcY = srcY.coerceIn(0f, imageH)
        val clampedSrcW = srcW.coerceIn(0f, imageW - clampedSrcX)
        val clampedSrcH = srcH.coerceIn(0f, imageH - clampedSrcY)
        if (clampedSrcW <= 0f || clampedSrcH <= 0f) return

        val srcRect = Rect.makeXYWH(clampedSrcX, clampedSrcY, clampedSrcW, clampedSrcH)
        drawImageInternal(skImage, srcRect, x, y, w, h, alpha, radius)
    }

    fun destroy() {
        scissorStackDepth = 0
        context?.close()
        context = null
    }

    private fun restoreHostGLState() {
        val snapshot = hostGlState ?: return
        snapshot.pop()
        hostGlState = null
    }

    private fun gradientEndpoints(
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        direction: GradientDirection
    ): FloatArray {
        return when (direction) {
            GradientDirection.LEFT_TO_RIGHT -> floatArrayOf(x, y, x + w, y)
            GradientDirection.TOP_TO_BOTTOM -> floatArrayOf(x, y, x, y + h)
            GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT -> floatArrayOf(x, y, x + w, y + h)
            GradientDirection.BOTTOM_LEFT_TO_TOP_RIGHT -> floatArrayOf(x, y + h, x + w, y)
        }
    }

    private fun createLinearGradientShader(
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        colorStartARGB: Int,
        colorEndARGB: Int,
        direction: GradientDirection
    ): Shader {
        val endpoints = gradientEndpoints(x, y, w, h, direction)
        return Shader.makeLinearGradient(
            endpoints[0],
            endpoints[1],
            endpoints[2],
            endpoints[3],
            intArrayOf(colorStartARGB, colorEndARGB)
        )
    }

    private fun baselineForTopY(y: Float, font: SkijaFont): Float {
        return y - font.metrics.ascent
    }

    private fun drawImageInternal(
        image: Image,
        srcRect: Rect,
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        alpha: Float,
        radius: Float
    ) {
        val dstRect = Rect.makeXYWH(x, y, w, h)
        val clampedAlpha = alpha.coerceIn(0f, 1f)

        Paint().setAlphaf(clampedAlpha).use { paint ->
            if (radius > 0f) {
                canvas?.save()
                canvas?.clipRRect(RRect.makeXYWH(x, y, w, h, radius))
                canvas?.drawImageRect(image, srcRect, dstRect, SamplingMode.LINEAR, paint, true)
                canvas?.restore()
            } else {
                canvas?.drawImageRect(image, srcRect, dstRect, SamplingMode.LINEAR, paint, true)
            }
        }
    }



    fun argb(a: Int, r: Int, g: Int, b: Int): Int {
        return ((a and 255) shl 24) or ((r and 255) shl 16) or ((g and 255) shl 8) or (b and 255)
    }
}