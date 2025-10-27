package com.slayerxp.overlay.ui

import com.slayerxp.overlay.utils.ChatUtils.prefix
import com.slayerxp.overlay.ui.Overlay as OverlayShitAHHHHH
import com.slayerxp.overlay.utils.Render2D
import com.slayerxp.overlay.settings.Config
import net.minecraft.client.gui.DrawContext

object XPOverlay: OverlayShitAHHHHH {
    override var shouldShow = true
    private var label = "$prefix Loading!"
    override var x = 100
    override var y = 100
    override val width = 150
    override val height = 50
    private var dragging = false
    private var dragOffsetX = 0
    private var dragOffsetY = 0

    init {
        loadPosition()
    }

    override fun show() { shouldShow = true }
    override fun hide() { shouldShow = false }

    fun updateXP(slayer: String, xp: Int) {
        label = "$prefix $slayer XP: $xp"
    }

    override fun draw(ctx: DrawContext) {
        if (!shouldShow) return

        if (dragging) {
            x = (Render2D.Mouse.x - dragOffsetX).toInt()
            y = (Render2D.Mouse.y - dragOffsetY).toInt()
        }
        Render2D.drawString(ctx, label, x + 10, y + 15)
    }

    override fun savePosition() {
        Config.setLocationOfGUI("overlayWindow", x, y)
    }

    private fun loadPosition() {
        val pos = Config.getLocationOfGUI("overlayWindow")
        if (pos != null) {
            x = pos.first
            y = pos.second
        }
    }
}