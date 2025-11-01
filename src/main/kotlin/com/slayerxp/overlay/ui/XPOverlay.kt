package com.slayerxp.overlay.ui
import com.slayerxp.overlay.utils.ChatUtils.prefix
import com.slayerxp.overlay.ui.Overlay as OverlayShitAHHHHH
import com.slayerxp.overlay.utils.Render2D
import com.slayerxp.overlay.settings.Config
import com.slayerxp.overlay.utils.ChatUtils.getGradientStyleMessage
import net.minecraft.client.gui.DrawContext
import java.text.DecimalFormat

object XPOverlay: OverlayShitAHHHHH {
    override var shouldShow = true
    private var label: net.minecraft.text.Text = net.minecraft.text.Text.literal("$prefix Loading!")
    override var x = 100
    override var y = 100
    override val width = 150
    override val height = 50
    private var dragging = false
    private var dragOffsetX = 0
    private var dragOffsetY = 0
    
    private val DEFAULT_COLOR_1 = java.awt.Color(33, 15, 235)  // #210FEB
    private val DEFAULT_COLOR_2 = java.awt.Color(255, 87, 51)  // #FF5733
    
    init {
        loadPosition()
        label = getGradientStyleMessage(
            "$prefix Loading!", 
            Config.getColor("MessageColorSelector1", DEFAULT_COLOR_1).rgb,
            Config.getColor("MessageColorSelector2", DEFAULT_COLOR_2).rgb
        )
    }
    
    override fun show() { shouldShow = true }
    override fun hide() { shouldShow = false }
    
    fun updateXP(slayer: String, xp: Int) {
        // I got annoyed at the overlay not being comma seperated
        val temp = DecimalFormat("#,###").format(xp)
        label = getGradientStyleMessage(
            "$prefix $slayer XP: $temp",
            Config.getColor("MessageColorSelector1", DEFAULT_COLOR_1).rgb,
            Config.getColor("MessageColorSelector2", DEFAULT_COLOR_2).rgb
        )
    }
    
    override fun draw(ctx: DrawContext) {
        if (!shouldShow) return
        if (dragging) {
            x = (Render2D.Mouse.x - dragOffsetX).toInt()
            y = (Render2D.Mouse.y - dragOffsetY).toInt()
        }
        Render2D.drawStringTextFormat(ctx, label, x + 10, y + 15)
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