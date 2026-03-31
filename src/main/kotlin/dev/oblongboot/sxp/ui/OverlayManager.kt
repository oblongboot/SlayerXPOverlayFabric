package dev.oblongboot.sxp.ui

import dev.oblongboot.sxp.events.impl.SkiaDrawEvent
import dev.oblongboot.sxp.utils.Render2D
import dev.oblongboot.sxp.utils.Scheduler
import dev.oblongboot.sxp.utils.skia.SkijaRenderer
import meteordevelopment.orbit.EventHandler
import net.minecraft.client.Minecraft
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import net.minecraft.network.chat.Component
import org.lwjgl.glfw.GLFW

class OverlayManager : Screen(Component.nullToEmpty("Overlay Manager")) {

    companion object {
        private val COLOR_BG = SkijaRenderer.argb(150, 0, 0, 0)
        private val COLOR_BORDER_IDLE = SkijaRenderer.argb(100, 255, 0, 0)
        private val COLOR_BORDER_HOVER = SkijaRenderer.argb(150, 0, 255, 0)
        private val COLOR_BORDER_DRAG = SkijaRenderer.argb(150, 255, 255, 0)
        private val COLOR_CORNER = SkijaRenderer.argb(200, 255, 255, 0)

        fun open() {
            Scheduler.scheduleTask(1) {
                val manager = OverlayManager()
                manager.addOverlay(KPHOverlay)
                manager.addOverlay(XPOverlay)
                manager.addOverlay(BVOverlay)
                Minecraft.getInstance().setScreen(manager)
            }
        }
    }

    private val overlays = mutableListOf<Overlay>()

    private var dragging = false
    private var dragOffsetX = 0f
    private var dragOffsetY = 0f
    private var dirty = false
    private var draggedOverlay: Overlay? = null

    fun addOverlay(overlay: Overlay) = overlays.add(overlay)

    override fun onClose() {
        super.onClose()
        if (dirty) overlays.forEach { it.savePosition() }
    }

    @EventHandler
    fun onSkija(event: SkiaDrawEvent) {
        SkijaRenderer.drawRoundedRect(0f, 0f, width.toFloat(), height.toFloat(), 0f, COLOR_BG)

        val mx = Render2D.Mouse.x.toInt()
        val my = Render2D.Mouse.y.toInt()

        overlays.forEach { it.draw() }
        overlays.forEach { drawHitboxMarker(mx, my, it) }
    }

    private fun drawHitboxMarker(mouseX: Int, mouseY: Int, overlay: Overlay) {
        val x = overlay.x.toFloat()
        val y = overlay.y.toFloat()
        val w = overlay.width.toFloat()
        val h = overlay.height.toFloat()

        val isHovered = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h
        val isDragged = draggedOverlay == overlay

        val borderColor = when {
            isDragged -> COLOR_BORDER_DRAG
            isHovered -> COLOR_BORDER_HOVER
            else      -> COLOR_BORDER_IDLE
        }

        SkijaRenderer.drawRoundedRectBorder(x, y, w, h, 4f, 1.5f, borderColor)

        val cs = 5f
        SkijaRenderer.drawRoundedRect(x,         y,         cs, cs, 0f, COLOR_CORNER)
        SkijaRenderer.drawRoundedRect(x + w - cs, y,         cs, cs, 0f, COLOR_CORNER)
        SkijaRenderer.drawRoundedRect(x,         y + h - cs, cs, cs, 0f, COLOR_CORNER)
        SkijaRenderer.drawRoundedRect(x + w - cs, y + h - cs, cs, cs, 0f, COLOR_CORNER)
    }

    override fun mouseClicked(click: MouseButtonEvent, doubled: Boolean): Boolean {
        if (click.button() == 0) {
            val clicked = overlays.reversed().find { o ->
                click.x >= o.x && click.x <= o.x + o.width &&
                        click.y >= o.y && click.y <= o.y + o.height
            }
            if (clicked != null) {
                dragging = true
                draggedOverlay = clicked
                dragOffsetX = (click.x - clicked.x).toFloat()
                dragOffsetY = (click.y - clicked.y).toFloat()
                return true
            }
        }
        return super.mouseClicked(click, doubled)
    }

    override fun mouseDragged(click: MouseButtonEvent, deltaY: Double, offsetY: Double): Boolean {
        if (dragging && draggedOverlay != null) {
            val o = draggedOverlay!!
            var newX = (click.x - dragOffsetX).coerceIn(0.0, (width  - o.width ).toDouble())
            var newY = (click.y - dragOffsetY).coerceIn(0.0, (height - o.height).toDouble())
            o.x = newX.toInt()
            o.y = newY.toInt()
            dirty = true
            return true
        }
        return super.mouseDragged(click, deltaY, offsetY)
    }

    override fun mouseReleased(click: MouseButtonEvent): Boolean {
        if (dragging) {
            dragging = false
            draggedOverlay = null
        }
        return super.mouseReleased(click)
    }

    override fun keyPressed(input: KeyEvent): Boolean {
        if (input.input() == GLFW.GLFW_KEY_ESCAPE) {
            onClose()
            return true
        }
        return super.keyPressed(input)
    }

    override fun isPauseScreen() = false
    override fun shouldCloseOnEsc() = true
}