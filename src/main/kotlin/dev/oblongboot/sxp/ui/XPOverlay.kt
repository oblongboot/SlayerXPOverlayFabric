package dev.oblongboot.sxp.ui
import dev.oblongboot.sxp.utils.ChatUtils.prefix
import dev.oblongboot.sxp.ui.Overlay as OverlayShitAHHHHH
import dev.oblongboot.sxp.utils.Render2D
import dev.oblongboot.sxp.settings.Config
import dev.oblongboot.sxp.utils.ChatUtils.getColoredMessage
import dev.oblongboot.sxp.utils.ChatUtils.mc
import dev.oblongboot.sxp.utils.skia.SkijaRenderer
import net.minecraft.client.gui.GuiGraphicsExtractor
import java.text.DecimalFormat

object XPOverlay: OverlayShitAHHHHH {
    override var shouldShow = true
    private var label: net.minecraft.network.chat.Component = net.minecraft.network.chat.Component.literal("$prefix Loading!")
    override var x = 100
    override var y = 100
    override val width = 150
    override val height = 50
    private var dragging = false
    private var dragOffsetX = 0
    private var dragOffsetY = 0
    
    init {
        loadPosition()
        label = getColoredMessage(
            "Loading!",
            Config.getColor("MessageColorSelector1", java.awt.Color(33, 15, 235)).rgb,
            Config.getColor("MessageColorSelector2", java.awt.Color(255, 87, 51)).rgb
        )
    }
    
    override fun show() { shouldShow = true }
    override fun hide() { shouldShow = false }
    
    fun updateXP(slayer: String, xp: Int) {
        // I got annoyed at the overlay not being comma seperated
        val temp = DecimalFormat("#,###").format(xp)
        label = getColoredMessage(
            "$slayer XP: $temp",
            Config.getColor("MessageColorSelector1", java.awt.Color(33, 15, 235)).rgb,
            Config.getColor("MessageColorSelector2", java.awt.Color(255, 87, 51)).rgb
        )
    }
    
    override fun draw() { //?????
        if (!shouldShow) return
        if (dragging) {
            x = (Render2D.Mouse.x - dragOffsetX).toInt()
            y = (Render2D.Mouse.y - dragOffsetY).toInt()
        }
        SkijaRenderer.drawMCText(label, (x + 10).toFloat(), (y + 15).toFloat(), SkijaRenderer.argb(255, 240, 245, 255), SettingsScreen.elementFont)
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