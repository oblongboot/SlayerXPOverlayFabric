package dev.oblongboot.sxp.core

import dev.oblongboot.sxp.utils.Render2D
import dev.oblongboot.sxp.settings.Config
import dev.oblongboot.sxp.settings.FeatureManager
import net.minecraft.client.gui.GuiGraphicsExtractor
import java.awt.Color

class SwitchConfig(
    name: String,
    default: Boolean = false,
    description: String = "",
    private val onValueChangeAction: (() -> Unit)? = null
) : Setting<Boolean>(name, description, default) {
    override val width = 240
    override val height = 30
    
    private var isInitializing = false

    override fun render(mouseX: Int, mouseY: Int) {
        val skija = dev.oblongboot.sxp.utils.skia.SkijaRenderer
        val toggleText = if (value) "ON" else "OFF"
        val isHovered = isWithinBounds2(mouseX, mouseY)
        val baseColor = if (value) skija.argb(150, 0, 100, 200) else skija.argb(100, 20, 30, 50)
        
        if (value) {
            skija.drawRoundedRectGradient(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat(), 4f, 
                skija.argb(160, 0, 120, 240), skija.argb(160, 0, 80, 180), 
                dev.oblongboot.sxp.utils.skia.SkijaRenderer.GradientDirection.LEFT_TO_RIGHT)
        } else {
            skija.drawRoundedRect(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat(), 4f, baseColor)
        }
        
        if (isHovered) {
             skija.drawRoundedGlow(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat(), 4f, skija.argb(50, 0, 150, 255), 10f, 1f)
        }
        
        val alphaBorder = if (value) 200 else 80
        skija.drawRoundedRectBorderGradient(
            x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat(), 4f, 1f,
            skija.argb(alphaBorder, 0, 140, 255), skija.argb(alphaBorder, 0, 90, 200),
            dev.oblongboot.sxp.utils.skia.SkijaRenderer.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT
        )

        val font = dev.oblongboot.sxp.ui.SettingsScreen.elementFont
        val textY = y + height / 3.8f// - 5f
        
        val nameColor = if (value || isHovered) skija.argb(255, 255, 255, 255) else skija.argb(255, 200, 210, 225)
        skija.drawText(name, x + 10f, textY, nameColor, font)
        
        val toggleColor = if (value) skija.argb(255, 220, 240, 255) else skija.argb(255, 140, 150, 170)
        val tw = skija.getTextWidth(toggleText, font)
        skija.drawText(toggleText, x + width - tw - 10f, textY, toggleColor, font)
    }

    override fun onClick(mouseX: Int, mouseY: Int): Boolean {
        if (isWithinBounds2(mouseX, mouseY)) {
            val oldValue = value
            val newValue = !value
            value = newValue
            onValueChanged(oldValue, newValue)
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
    
    fun setValueSilently(newValue: Boolean) {
        isInitializing = true
        value = newValue
        isInitializing = false
    }

    override fun onValueChanged(oldValue: Boolean, newValue: Boolean) {
        if (isInitializing || oldValue == newValue) return

        Config.setToggle(name, newValue)
        FeatureManager.loadAllFeatureStates()
        FeatureManager.notifyToggleChanged(name, newValue)
        onValueChangeAction?.invoke()

        println("$name toggled: $oldValue -> $newValue")
    }


    private fun isWithinBounds2(mouseX: Int, mouseY: Int): Boolean {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height
    }
}