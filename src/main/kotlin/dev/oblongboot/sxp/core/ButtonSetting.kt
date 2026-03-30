package dev.oblongboot.sxp.core

import dev.oblongboot.sxp.utils.Render2D
import net.minecraft.client.gui.GuiGraphics
import java.awt.Color

class ButtonSetting(
    name: String,
    description: String = "",
    private val onClickAction: (() -> Unit)? = null 
) : Setting<Boolean>(name, description, false) { 
    override var x = 0
    override var y = 0
    override val width = 240
    override val height = 30

    override fun render(mouseX: Int, mouseY: Int) {
        val skija = dev.oblongboot.sxp.utils.skia.SkijaRenderer
        val isHovered = isWithinBounds2(mouseX, mouseY)
        val baseColor = if (isHovered) skija.argb(160, 40, 80, 140) else skija.argb(100, 20, 40, 70)
        
        skija.drawRoundedRect(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat(), 4f, baseColor)
        
        if (isHovered) {
             skija.drawRoundedGlow(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat(), 4f, skija.argb(60, 0, 120, 255), 10f, 1f)
        }
        
        val borderColor = if (isHovered) skija.argb(200, 0, 150, 255) else skija.argb(100, 0, 100, 200)
        skija.drawRoundedRectBorderGradient(
            x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat(), 4f, 1f,
            borderColor, borderColor,
            dev.oblongboot.sxp.utils.skia.SkijaRenderer.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT
        )

        val font = dev.oblongboot.sxp.ui.SettingsScreen.elementFont
        val textWidth = skija.getTextWidth(name, font)
        val textY = y + height / 3.8f// - 5f
        val textX = x + (width - textWidth) / 2f

        val textColor = if (isHovered) skija.argb(255, 255, 255, 255) else skija.argb(255, 220, 230, 255)
        skija.drawText(name, textX, textY, textColor, font)
    }

    override fun onClick(mouseX: Int, mouseY: Int): Boolean {
        if (isWithinBounds2(mouseX, mouseY)) {
            val oldValue = value
            value = !value
            onClickAction?.invoke()

            onValueChanged(oldValue, value)
            return true
        }
        return false
    }

    override fun onHover(mouseX: Int, mouseY: Int): Boolean {
        return isWithinBounds2(mouseX, mouseY)
    }

    override fun reset() {
        value = default
    }

    override fun onValueChanged(oldValue: Boolean, newValue: Boolean) {}

    private fun isWithinBounds2(mouseX: Int, mouseY: Int): Boolean {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height
    }
}
