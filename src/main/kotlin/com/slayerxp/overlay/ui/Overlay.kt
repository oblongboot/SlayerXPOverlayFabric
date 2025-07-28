package com.slayerxp.overlay.ui

import com.slayerxp.overlay.utils.Render2D
import com.slayerxp.overlay.settings.config
import net.minecraft.client.gui.DrawContext
import java.awt.Color

object Overlay {
    var shouldShow = true
    private var label = "overlayyyyyyyyyyyyyyyyy"

    var x = 100
    var y = 100
    val width = 150
    val height = 50
    private var dragging = false
    private var dragOffsetX = 0
    private var dragOffsetY = 0

    init {
        loadPosition()
    }

    fun show() { shouldShow = true }
    fun hide() { shouldShow = false }

    fun updateXP(xp: Int) {
        label = "aaaaaaaaaaaaaaaaaaaaaaaa"
    }

    fun draw(ctx: DrawContext) {
        if (!shouldShow) return

        if (dragging) {
            x = (Render2D.Mouse.x - dragOffsetX).toInt()
            y = (Render2D.Mouse.y - dragOffsetY).toInt()
        }
        Render2D.drawString(ctx, label, x + 10, y + 15)
    }

    fun savePosition() {
        config.setLocationOfGUI("overlayWindow", x, y)
    }

    private fun loadPosition() {
        val pos = config.getLocationOfGUI("overlayWindow")
        if (pos != null) {
            x = pos.first
            y = pos.second
        }
    }
}
