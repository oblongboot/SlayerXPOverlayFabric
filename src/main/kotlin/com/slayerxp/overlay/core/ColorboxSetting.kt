package com.slayerxp.overlay.core

import com.slayerxp.overlay.utils.Render2D
import com.slayerxp.overlay.settings.Config
import net.minecraft.client.gui.DrawContext
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

    override fun render(ctx: DrawContext) {
        val baseColor = Color(50, 60, 90, 180)
        val isHovered = isWithinBounds(Render2D.Mouse.x.toInt(), Render2D.Mouse.y.toInt())
        val hoverColor = if (isHovered) baseColor.brighter() else baseColor

        val targetHeight = if (expanded) pickerHeight + 60 else 0
        animHeight += (targetHeight - animHeight) * animSpeed

        val mouseX = Render2D.Mouse.x.toInt()
        val mouseY = Render2D.Mouse.y.toInt()
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

        Render2D.drawWhateverTheFuckThisIs(ctx, x, y, width, height, 6, hoverColor)
        Render2D.drawOutline(ctx, x, y, width, height, Color(0, 180, 255))
        val textY = y + (height - Render2D.textRenderer.fontHeight) / 2
        Render2D.drawString(ctx, name, x + 10, textY, 1f, true)

        val previewX = x + width - previewSize - 10
        val previewY = y + (height - previewSize) / 2
        Render2D.drawWhateverTheFuckThisIs(ctx, previewX, previewY, previewSize, previewSize, 3, value)
        Render2D.drawOutline(ctx, previewX, previewY, previewSize, previewSize, Color.WHITE)

        if (animHeight > 0.5f) {
            Render2D.drawWhateverTheFuckThisIs(ctx, x, pickerY - 3, width, (animHeight + 6).toInt(), 6, Color(30, 40, 60, 220))
            drawSVPicker(ctx, pickerX, pickerY, svWidth, pickerHeight)
            drawHueBar(ctx, hueX, pickerY, hueBarWidth, hueBarHeight)
            drawAlphaSlider(ctx, pickerX, alphaY, pickerWidth, 15)
            val finalPreviewX = pickerX + pickerWidth - 60
            val finalPreviewY = alphaY + 20
            Render2D.drawWhateverTheFuckThisIs(ctx, finalPreviewX, finalPreviewY, 50, 25, 3, value)
            Render2D.drawOutline(ctx, finalPreviewX, finalPreviewY, 50, 25, Color.WHITE)
            val hexColor = String.format("#%02X%02X%02X%02X", value.red, value.green, value.blue, value.alpha)
            Render2D.drawString(ctx, hexColor, pickerX, finalPreviewY + 7, 0.9f, true)
        }
    }


    private fun drawSVPicker(ctx: DrawContext, px: Int, py: Int, w: Int, h: Int) {
        val stripWidth = 4
        val stripHeight = 8

        for (i in 0 until w step stripWidth) {
            for (j in 0 until h step stripHeight) {
                val s = i.toFloat() / w
                val v = 1f - (j.toFloat() / h)
                val color = Color.getHSBColor(hue, s, v)
                Render2D.drawWhateverTheFuckThisIs(ctx, px + i, py + j, stripWidth, stripHeight, 0, color)
            }
        }

        val cursorX = px + (saturation * w).toInt()
        val cursorY = py + ((1f - brightness) * h).toInt()
        Render2D.drawOutline(ctx, cursorX - 4, cursorY - 4, 8, 8, Color.WHITE)
        Render2D.drawOutline(ctx, cursorX - 3, cursorY - 3, 6, 6, Color.BLACK)
    }

    private fun drawHueBar(ctx: DrawContext, hx: Int, hy: Int, w: Int, h: Int) {
        val stripHeight = 4
        for (i in 0 until h step stripHeight) {
            val hueVal = i.toFloat() / h
            val color = Color.getHSBColor(hueVal, 1f, 1f)
            Render2D.drawWhateverTheFuckThisIs(ctx, hx, hy + i, w, stripHeight, 0, color)
        }

        Render2D.drawOutline(ctx, hx, hy, w, h, Color.WHITE)

        val cursorY = hy + (hue * h).toInt()
        Render2D.drawWhateverTheFuckThisIs(ctx, hx - 2, cursorY - 2, w + 4, 4, 0, Color.WHITE)
    }

    private fun drawAlphaSlider(ctx: DrawContext, ax: Int, ay: Int, w: Int, h: Int) {
        val checkSize = 8
        for (i in 0 until w step checkSize) {
            for (j in 0 until h step checkSize) {
                val isLight = ((i / checkSize) + (j / checkSize)) % 2 == 0
                val color = if (isLight) Color(200, 200, 200) else Color(100, 100, 100)
                Render2D.drawWhateverTheFuckThisIs(ctx, ax + i, ay + j, checkSize, checkSize, 0, color)
            }
        }

        val baseColor = Color(value.red, value.green, value.blue)
        val stripWidth = 4
        for (i in 0 until w step stripWidth) {
            val a = (i.toFloat() / w * 255).toInt()
            val color = Color(baseColor.red, baseColor.green, baseColor.blue, a)
            Render2D.drawWhateverTheFuckThisIs(ctx, ax + i, ay, stripWidth, h, 0, color)
        }

        Render2D.drawOutline(ctx, ax, ay, w, h, Color.WHITE)

        val cursorX = ax + (alpha.toFloat() / 255f * w).toInt()
        Render2D.drawWhateverTheFuckThisIs(ctx, cursorX - 2, ay - 2, 4, h + 4, 0, Color.WHITE)
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

    override fun onValueChanged(oldValue: Color, newValue: Color) {
        Config.setColor(name, newValue)
        onValueChangeAction?.invoke(newValue)
    }

    private fun isWithinBounds(
        mouseX: Int, mouseY: Int,
        bx: Int = x, by: Int = y,
        bw: Int = width, bh: Int = height
    ): Boolean {
        return mouseX >= bx && mouseX <= bx + bw && mouseY >= by && mouseY <= by + bh
    }
}