package dev.oblongboot.sxp.core

import dev.oblongboot.sxp.utils.Render2D
import net.minecraft.client.gui.DrawContext
import java.awt.Color

class ButtonSetting(
    name: String,
    description: String = "",
    private val onClickAction: (() -> Unit)? = null 
) : Setting<Boolean>(name, description, false) { 
    override val width = 240
    override val height = 30

    override fun render(ctx: DrawContext) {
        val isHovered = isWithinBounds2(Render2D.Mouse.x.toInt(), Render2D.Mouse.y.toInt())
        val baseColor = Color(50, 90, 150, 180)
        val hoverColor = if (isHovered) baseColor.brighter() else baseColor
        Render2D.drawWhateverTheFuckThisIs(ctx, x, y, width, height, 6, hoverColor)
        Render2D.drawOutline(ctx, x, y, width, height, Color(0, 180, 255))
        val textY = y + (height - Render2D.textRenderer.fontHeight) / 2
        val textX = x + (width - Render2D.textRenderer.getWidth(name)) / 2

        Render2D.drawString(ctx, name, textX, textY, 1f, true)
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

    override fun onValueChanged(oldValue: Boolean, newValue: Boolean) {} // why am i even overrideing this

    private fun isWithinBounds2(mouseX: Int, mouseY: Int): Boolean {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height
    }
}
