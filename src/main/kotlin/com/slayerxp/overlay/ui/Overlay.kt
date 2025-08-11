package com.slayerxp.overlay.ui

import com.slayerxp.overlay.utils.ChatUtils.prefix
import com.slayerxp.overlay.settings.impl.Overlay as OverlayModule
import com.slayerxp.overlay.settings.impl.onMessage
import com.slayerxp.overlay.utils.Scoreboard
import com.slayerxp.overlay.utils.Render2D
import com.slayerxp.overlay.settings.config
import net.minecraft.client.gui.DrawContext
import java.text.DecimalFormat

object Overlay {
    var shouldShow = true
    private var label = "$prefix Loading!"
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

    fun updateXP(slayer: String, xp: Int) {
        label = "$prefix $slayer XP: $xp"
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