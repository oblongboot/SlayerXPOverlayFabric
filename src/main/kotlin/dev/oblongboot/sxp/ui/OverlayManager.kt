package dev.oblongboot.sxp.ui

import dev.oblongboot.sxp.ui.Overlay
import dev.oblongboot.sxp.ui.KPHOverlay
import dev.oblongboot.sxp.ui.XPOverlay
import dev.oblongboot.sxp.utils.ChatUtils.modMessage
import dev.oblongboot.sxp.utils.Scheduler
import net.minecraft.client.Minecraft
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import net.minecraft.network.chat.Component
import org.lwjgl.glfw.GLFW
import java.awt.Color

class OverlayManager : Screen(Component.nullToEmpty("Overlay Manager")) {

    companion object {
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

    fun addOverlay(overlay: Overlay) {
        overlays.add(overlay)
    }

    override fun onClose() {
        super.onClose()
        if (dirty) {
            overlays.forEach { it.savePosition() }
        }
    }

    override fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        context.fill(0, 0, width, height, Color(0, 0, 0, 150).rgb)
        overlays.forEach { it.draw(context) }
        overlays.forEach { drawHitboxMarker(context, mouseX, mouseY, it) }

        super.render(context, mouseX, mouseY, delta)
    }

    private fun drawHitboxMarker(context: GuiGraphics, mouseX: Int, mouseY: Int, overlay: Overlay) {
        val x = overlay.x
        val y = overlay.y
        val w = overlay.width.toFloat()
        val h = overlay.height.toFloat()

        val isHovered = mouseX in x..(x + w.toInt()) && mouseY in y..(y + h.toInt())
        val isDragged = draggedOverlay == overlay

        val hitboxColor = when {
            isDragged -> Color(255, 255, 0, 150).rgb
            isHovered -> Color(0, 255, 0, 150).rgb
            else -> Color(255, 0, 0, 100).rgb
        }

        drawHollowRect(context, x, y, (x + w).toInt(), (y + h).toInt(), hitboxColor)

        val cornerSize = 5
        context.fill(x, y, x + cornerSize, y + cornerSize, Color(255, 255, 0, 200).rgb)
        context.fill((x + w - cornerSize).toInt(), y, (x + w).toInt(), y + cornerSize, Color(255, 255, 0, 200).rgb)
        context.fill(x, (y + h - cornerSize).toInt(), x + cornerSize, (y + h).toInt(), Color(255, 255, 0, 200).rgb)
        context.fill((x + w - cornerSize).toInt(), (y + h - cornerSize).toInt(), (x + w).toInt(), (y + h).toInt(), Color(255, 255, 0, 200).rgb)
    }

    private fun drawInfoPanel(context: GuiGraphics, mouseX: Int, mouseY: Int) {
        val posText = "Overlays: ${overlays.size} | Mouse: $mouseX, $mouseY | Dragging: $dragging"
        context.fill(10, 10, 10 + font.width(posText) + 10, 25, Color(0, 0, 0, 180).rgb)
        context.drawString(font, posText, 15, 15, Color.WHITE.rgb)
    }

    private fun drawHollowRect(context: GuiGraphics, x1: Int, y1: Int, x2: Int, y2: Int, color: Int) {
        context.fill(x1, y1, x2, y1 + 1, color)
        context.fill(x1, y2 - 1, x2, y2, color)
        context.fill(x1, y1, x1 + 1, y2, color)
        context.fill(x2 - 1, y1, x2, y2, color)
    }

    override fun mouseClicked(click: MouseButtonEvent, doubled: Boolean): Boolean {
        if (click.button() == 0) {
            val clickedOverlay = overlays.reversed().find { overlay ->
                click.x >= overlay.x && click.x <= overlay.x + overlay.width &&
                        click.y >= overlay.y && click.y <= overlay.y + overlay.height
            }

            if (clickedOverlay != null) {
                dragging = true
                draggedOverlay = clickedOverlay
                dragOffsetX = (click.x - clickedOverlay.x).toFloat()
                dragOffsetY = (click.y - clickedOverlay.y).toFloat()
                return true
            }
        }
        return super.mouseClicked(click, doubled)
    }

    override fun mouseDragged(click: MouseButtonEvent, deltaY: Double, offsetY: Double): Boolean {
        if (dragging && draggedOverlay != null) {
            val overlay = draggedOverlay!!
            val w = overlay.width
            val h = overlay.height

            var newX = (click.x - dragOffsetX).toFloat()
            var newY = (click.y - dragOffsetY).toFloat()
            newX = newX.coerceIn(0f, (width - w).toFloat())
            newY = newY.coerceIn(0f, (height - h).toFloat())

            overlay.x = newX.toInt()
            overlay.y = newY.toInt()

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
        when (input.input()) {
            GLFW.GLFW_KEY_ESCAPE -> {
                onClose()
                return true
            }
        }
        return super.keyPressed(input)
    }

    override fun isPauseScreen() = false
    override fun shouldCloseOnEsc() = true
    override fun renderBackground(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {}
}