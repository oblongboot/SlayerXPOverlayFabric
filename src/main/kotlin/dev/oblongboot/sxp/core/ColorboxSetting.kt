package dev.oblongboot.sxp.core

import dev.oblongboot.sxp.utils.Render2D
import dev.oblongboot.sxp.settings.Config
import net.minecraft.client.gui.GuiGraphics
import java.awt.Color
import kotlin.math.max
import kotlin.math.min

class ColorboxSetting(
    name: String,
    defaultColor: Color = Color.WHITE,
    description: String = "",
    private val onValueChangeAction: ((Color) -> Unit)? = null
) : Setting<Color>(name, description, defaultColor) {

    override val width = 240
    override val height = 30
    private var expanded = false
    private var animHeight = 0f
    private val animSpeed = 0.25f
    private val pickerWidth = 220
    private val pickerHeight = 150
    private val hueBarWidth = 20
    private val hueBarHeight = pickerHeight
    private val previewSize = 30
    private var hue = 0f
    private var saturation = 1f
    private var brightness = 1f
    private var alpha = 255
    private var draggingSV = false
    private var draggingHue = false
    private var draggingAlpha = false
    private var aaa = false
    private var cachedHue = -1f
    private var needsGradientUpdate = true

    init {
        updateHSVFromColor(value)
    }

    override fun render(mouseX: Int, mouseY: Int) {
        val skija = dev.oblongboot.sxp.utils.skia.SkijaRenderer
        val baseColor = skija.argb(100, 20, 40, 70)
        val isHovered = isWithinBounds(mouseX, mouseY)
        val hoverColor = if (isHovered) skija.argb(160, 40, 80, 140) else baseColor

        val targetHeight = if (expanded) pickerHeight + 60 else 0
        animHeight += (targetHeight - animHeight) * animSpeed

        val mouseXFloat = mouseX.toFloat()
        val mouseYFloat = mouseY.toFloat()
        val mouseDown = Render2D.Mouse.isDown(0)

        val pickerX = x + 10
        val pickerY = y + height + 5
        val svWidth = pickerWidth - hueBarWidth - 15
        val hueX = pickerX + svWidth + 10
        val alphaY = pickerY + pickerHeight + 10

        if (expanded) {
            if (draggingSV) updateSV(mouseX, mouseY, pickerX, pickerY, svWidth, pickerHeight)
            if (draggingHue) updateHue(mouseY, pickerY, hueBarHeight)
            if (draggingAlpha) updateAlpha(mouseX, pickerX, pickerWidth)
        }

        if (!mouseDown && aaa) {
            draggingSV = false
            draggingHue = false
            draggingAlpha = false
        }
        aaa = mouseDown
        
        skija.drawRoundedRect(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat(), 4f, hoverColor)

        if (isHovered) {
             skija.drawRoundedGlow(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat(), 4f, skija.argb(60, 0, 120, 255), 10f, 1f)
        }
        
        val borderColor = if (expanded || isHovered) skija.argb(255, 0, 150, 255) else skija.argb(150, 0, 100, 200)
        skija.drawRoundedRectBorderGradient(
            x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat(), 4f, 1f,
            borderColor, borderColor,
            dev.oblongboot.sxp.utils.skia.SkijaRenderer.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT
        )

        val font = dev.oblongboot.sxp.ui.SettingsScreen.elementFont
        val textY = y + height / 3.8f// - 5f
        skija.drawText(name, x + 10f, textY, skija.argb(255, 255, 255, 255), font)

        val previewX = x + width - previewSize - 10f
        val previewY = y + (height - previewSize) / 2f
        val previewArgb = skija.argb(value.alpha, value.red, value.green, value.blue)
        skija.drawRoundedRect(previewX, previewY, previewSize.toFloat(), previewSize.toFloat(), 3f, previewArgb)
        skija.drawRoundedRectBorderGradient(previewX, previewY, previewSize.toFloat(), previewSize.toFloat(), 3f, 1f, skija.argb(150, 255, 255, 255), skija.argb(50, 255, 255, 255), dev.oblongboot.sxp.utils.skia.SkijaRenderer.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT)

        if (animHeight > 0.5f) {
            skija.drawRoundedRect(x.toFloat(), pickerY - 3f, width.toFloat(), animHeight + 6f, 4f, skija.argb(220, 15, 25, 40))
            skija.drawRoundedRectBorderGradient(x.toFloat(), pickerY - 3f, width.toFloat(), animHeight + 6f, 4f, 1f, skija.argb(100, 0, 100, 200), skija.argb(100, 0, 100, 200), dev.oblongboot.sxp.utils.skia.SkijaRenderer.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT)

            drawSVPicker(pickerX, pickerY, svWidth, pickerHeight)
            drawHueBar(hueX, pickerY, hueBarWidth, hueBarHeight)
            drawAlphaSlider(pickerX, alphaY, pickerWidth, 15)
            
            val finalPreviewX = pickerX + pickerWidth - 60f
            val finalPreviewY = alphaY + 20f
            skija.drawRoundedRect(finalPreviewX, finalPreviewY, 50f, 25f, 3f, previewArgb)
            skija.drawRoundedRectBorderGradient(finalPreviewX, finalPreviewY, 50f, 25f, 3f, 1f, skija.argb(150, 255, 255, 255), skija.argb(50, 255, 255, 255), dev.oblongboot.sxp.utils.skia.SkijaRenderer.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT)
            
            val hexColor = String.format("#%02X%02X%02X%02X", value.red, value.green, value.blue, value.alpha)
            val smallFont = dev.oblongboot.sxp.ui.SettingsScreen.smallFont
            skija.drawText(hexColor, pickerX.toFloat(), finalPreviewY + 15f, skija.argb(255, 180, 200, 230), smallFont)
        }
    }


    private fun drawSVPicker(px: Int, py: Int, w: Int, h: Int) {
        val skija = dev.oblongboot.sxp.utils.skia.SkijaRenderer
        val stripWidth = 4f
        val stripHeight = 8f

        for (i in 0 until w step stripWidth.toInt()) {
            for (j in 0 until h step stripHeight.toInt()) {
                val s = i.toFloat() / w
                val v = 1f - (j.toFloat() / h)
                val color = Color.getHSBColor(hue, s, v)
                skija.drawRoundedRect(px + i.toFloat(), py + j.toFloat(), stripWidth, stripHeight, 0f, skija.argb(255, color.red, color.green, color.blue))
            }
        }

        val cursorX = px + (saturation * w).toFloat()
        val cursorY = py + ((1f - brightness) * h).toFloat()
        skija.drawRoundedRectBorderGradient(cursorX - 4f, cursorY - 4f, 8f, 8f, 4f, 1f, skija.argb(255, 255, 255, 255), skija.argb(255, 255, 255, 255), dev.oblongboot.sxp.utils.skia.SkijaRenderer.GradientDirection.LEFT_TO_RIGHT)
        skija.drawRoundedRectBorderGradient(cursorX - 3f, cursorY - 3f, 6f, 6f, 3f, 1f, skija.argb(255, 0, 0, 0), skija.argb(255, 0, 0, 0), dev.oblongboot.sxp.utils.skia.SkijaRenderer.GradientDirection.LEFT_TO_RIGHT)
    }

    private fun drawHueBar(hx: Int, hy: Int, w: Int, h: Int) {
        val skija = dev.oblongboot.sxp.utils.skia.SkijaRenderer
        val stripHeight = 4f
        for (i in 0 until h step stripHeight.toInt()) {
            val hueVal = i.toFloat() / h
            val color = Color.getHSBColor(hueVal, 1f, 1f)
            skija.drawRoundedRect(hx.toFloat(), hy + i.toFloat(), w.toFloat(), stripHeight, 0f, skija.argb(255, color.red, color.green, color.blue))
        }

        skija.drawRoundedRectBorderGradient(hx.toFloat(), hy.toFloat(), w.toFloat(), h.toFloat(), 0f, 1f, skija.argb(255, 255, 255, 255), skija.argb(255, 255, 255, 255), dev.oblongboot.sxp.utils.skia.SkijaRenderer.GradientDirection.LEFT_TO_RIGHT)

        val cursorY = hy + (hue * h).toFloat()
        skija.drawRoundedRect(hx - 2f, cursorY - 2f, w + 4f, 4f, 2f, skija.argb(255, 255, 255, 255))
    }

    private fun drawAlphaSlider(ax: Int, ay: Int, w: Int, h: Int) {
        val skija = dev.oblongboot.sxp.utils.skia.SkijaRenderer
        val checkSize = 8f
        for (i in 0 until w step checkSize.toInt()) {
            for (j in 0 until h step checkSize.toInt()) {
                val isLight = ((i / checkSize.toInt()) + (j / checkSize.toInt())) % 2 == 0
                val color = if (isLight) skija.argb(255, 200, 200, 200) else skija.argb(255, 100, 100, 100)
                skija.drawRoundedRect(ax + i.toFloat(), ay + j.toFloat(), checkSize, checkSize, 0f, color)
            }
        }

        val baseColor = Color(value.red, value.green, value.blue)
        val stripWidth = 4f
        for (i in 0 until w step stripWidth.toInt()) {
            val a = (i.toFloat() / w * 255).toInt()
            skija.drawRoundedRect(ax + i.toFloat(), ay.toFloat(), stripWidth, h.toFloat(), 0f, skija.argb(a, baseColor.red, baseColor.green, baseColor.blue))
        }

        skija.drawRoundedRectBorderGradient(ax.toFloat(), ay.toFloat(), w.toFloat(), h.toFloat(), 0f, 1f, skija.argb(255, 255, 255, 255), skija.argb(255, 255, 255, 255), dev.oblongboot.sxp.utils.skia.SkijaRenderer.GradientDirection.LEFT_TO_RIGHT)

        val cursorX = ax + (alpha.toFloat() / 255f * w).toFloat()
        skija.drawRoundedRect(cursorX - 2f, ay - 2f, 4f, h + 4f, 2f, skija.argb(255, 255, 255, 255))
    }

    override fun onClick(mouseX: Int, mouseY: Int): Boolean {
        if (isWithinBounds(mouseX, mouseY)) {
            expanded = !expanded
            return true
        }

        if (expanded) {
            val pickerX = x + 10
            val pickerY = y + height + 5
            val svWidth = pickerWidth - hueBarWidth - 15
            val hueX = pickerX + svWidth + 10
            val alphaY = pickerY + pickerHeight + 10

            if (isWithinBounds(mouseX, mouseY, pickerX, pickerY, svWidth, pickerHeight)) {
                draggingSV = true
                updateSV(mouseX, mouseY, pickerX, pickerY, svWidth, pickerHeight)
                return true
            }

            if (isWithinBounds(mouseX, mouseY, hueX, pickerY, hueBarWidth, hueBarHeight)) {
                draggingHue = true
                updateHue(mouseY, pickerY, hueBarHeight)
                return true
            }

            if (isWithinBounds(mouseX, mouseY, pickerX, alphaY, pickerWidth, 15)) {
                draggingAlpha = true
                updateAlpha(mouseX, pickerX, pickerWidth)
                return true
            }
        }

        return false
    }

    fun onMousePressed(mouseX: Int, mouseY: Int): Boolean {
        return onClick(mouseX, mouseY)
    }

    fun onMouseDrag(mouseX: Int, mouseY: Int): Boolean {
        val pickerX = x + 10
        val pickerY = y + height + 5
        val svWidth = pickerWidth - hueBarWidth - 15
        val hueX = pickerX + svWidth + 10
        val alphaY = pickerY + pickerHeight + 10

        if (draggingSV) {
            updateSV(mouseX, mouseY, pickerX, pickerY, svWidth, pickerHeight)
            return true
        }

        if (draggingHue) {
            updateHue(mouseY, pickerY, hueBarHeight)
            return true
        }

        if (draggingAlpha) {
            updateAlpha(mouseX, pickerX, pickerWidth)
            return true
        }

        return false
    }

    fun onMouseRelease(): Boolean {
        val wasGragging = draggingSV || draggingHue || draggingAlpha
        draggingSV = false
        draggingHue = false
        draggingAlpha = false
        return wasGragging
    }

    private fun updateSV(mouseX: Int, mouseY: Int, px: Int, py: Int, w: Int, h: Int) {
        saturation = ((mouseX - px).toFloat() / w).coerceIn(0f, 1f)
        brightness = (1f - (mouseY - py).toFloat() / h).coerceIn(0f, 1f)
        updateColorFromHSV()
    }

    private fun updateHue(mouseY: Int, py: Int, h: Int) {
        hue = ((mouseY - py).toFloat() / h).coerceIn(0f, 1f)
        updateColorFromHSV()
    }

    private fun updateAlpha(mouseX: Int, ax: Int, w: Int) {
        alpha = ((mouseX - ax).toFloat() / w * 255).toInt().coerceIn(0, 255)
        updateColorFromHSV()
    }

    private fun updateColorFromHSV() {
        val rgb = Color.getHSBColor(hue, saturation, brightness)
        val oldValue = value
        value = Color(rgb.red, rgb.green, rgb.blue, alpha)
        onValueChanged(oldValue, value)
    }

    private fun updateHSVFromColor(color: Color) {
        val hsb = Color.RGBtoHSB(color.red, color.green, color.blue, null)
        hue = hsb[0]
        saturation = hsb[1]
        brightness = hsb[2]
        alpha = color.alpha
    }

    override fun onHover(mouseX: Int, mouseY: Int): Boolean {
        if (isWithinBounds(mouseX, mouseY)) return true

        if (expanded) {
            val pickerX = x + 10
            val pickerY = y + height + 5
            val svWidth = pickerWidth - hueBarWidth - 15
            val totalHeight = pickerHeight + 50
        //    isDragging = true

            if (isWithinBounds(mouseX, mouseY, x, pickerY - 3, width, totalHeight)) {
                return true
            }
        }

        return false
    }

    override fun reset() {
        value = default
        updateHSVFromColor(value)
    }

    override public fun onValueChanged(oldValue: Color, newValue: Color) {
        Config.setColor(name, newValue)
        onValueChangeAction?.invoke(newValue)
    }

    fun svs(color: Color) {
        value = color
        updateHSVFromColor(color)
    }

    private fun isWithinBounds(
        mouseX: Int, mouseY: Int,
        bx: Int = x, by: Int = y,
        bw: Int = width, bh: Int = height
    ): Boolean {
        return mouseX >= bx && mouseX <= bx + bw && mouseY >= by && mouseY <= by + bh
    }
}