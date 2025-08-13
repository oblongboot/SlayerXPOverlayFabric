package com.slayerxp.overlay.core

import com.slayerxp.overlay.utils.Render2D
import com.slayerxp.overlay.settings.config
import com.slayerxp.overlay.settings.FeatureManager
import net.minecraft.client.gui.DrawContext

class SwitchConfig(
    name: String,
    default: Boolean = false,
    description: String = ""
) : Setting<Boolean>(name, description, default) {
    
    override val width = 200
    override val height = 20
    
    override fun render(ctx: DrawContext) {
        val toggleText = if (value) "ON" else "OFF"
        val color = if (value) 0x00FF00 else 0xFF0000
        Render2D.drawRect(ctx, x, y, width, height, java.awt.Color(0, 0, 0, 128))
        Render2D.drawString(ctx, name, x + 5, y + 5)
        Render2D.drawString(ctx, toggleText, x + width - 30, y + 5)
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
