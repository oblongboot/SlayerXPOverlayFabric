package com.slayerxp.overlay.core

import com.slayerxp.overlay.utils.Render2D
import com.slayerxp.overlay.settings.config
import com.slayerxp.overlay.settings.FeatureManager
import net.minecraft.client.gui.DrawContext
import java.awt.Color

class SwitchConfig(
    name: String,
    default: Boolean = false,
    description: String = ""
) : Setting<Boolean>(name, description, default) {

    override val width = 240 
    override val height = 30  

    override fun render(ctx: DrawContext) {
        val toggleText = if (value) "ON" else "OFF"
        val baseColor = if (value) Color(0, 120, 220, 200) else Color(50, 60, 90, 180)
        val isHovered = isWithinBounds(Render2D.Mouse.x.toInt(), Render2D.Mouse.y.toInt())
        val hoverColor = if (isHovered) baseColor.brighter() else baseColor
        Render2D.drawWhateverTheFuckThisIs(ctx, x, y, width, height, 6, hoverColor)
        Render2D.drawOutline(ctx, x, y, width, height, Color(0, 180, 255))
        val textY = y + (height - Render2D.textRenderer.fontHeight) / 2
        Render2D.drawString(ctx, name, x + 10, textY, 1f, true)
        val toggleColor = if (value) Color(200, 240, 255).rgb else Color(170, 170, 190).rgb
        Render2D.drawString(ctx, toggleText, x + width - 40, textY, 1f, true)
    }


    override fun onClick(mouseX: Int, mouseY: Int): Boolean {
        if (isWithinBounds(mouseX, mouseY)) {
            value = !value
            return true
        }
        return false
    }

    override fun onHover(mouseX: Int, mouseY: Int): Boolean {
        return isWithinBounds(mouseX, mouseY)
    }

    override fun reset() {
        value = default
    }

    override fun onValueChanged(oldValue: Boolean, newValue: Boolean) {
        config.setToggle(name, newValue)
        FeatureManager.loadAllFeatureStates()
        println("$name toggled: $oldValue -> $newValue")
    }
}
