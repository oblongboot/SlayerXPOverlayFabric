package dev.oblongboot.sxp.ui

import dev.oblongboot.sxp.utils.Render2D
import dev.oblongboot.sxp.settings.Config
import dev.oblongboot.sxp.utils.skia.SkijaRenderer

object BVOverlay: Overlay {
    override var shouldShow = true
    var label = ""
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

    override fun show() {shouldShow = true }
    override fun hide() {shouldShow = false }

    override fun draw() {
        if (!shouldShow) return

        if (dragging) {
            x = (Render2D.Mouse.x - dragOffsetX).toInt()
            y = (Render2D.Mouse.y - dragOffsetY).toInt()
        }
        SkijaRenderer.drawText(label, (x + 10).toFloat(), (y + 15).toFloat(), SkijaRenderer.argb(255, 240, 245, 255), SettingsScreen.elementFont)
    }

    override fun savePosition() {
        Config.setLocationOfGUI("bvOverlay", x, y)
    }

    private fun loadPosition() {
        val pos = Config.getLocationOfGUI("bvOverlay")
        if (pos != null) {
            x = pos.first
            y = pos.second
        }
    }
}