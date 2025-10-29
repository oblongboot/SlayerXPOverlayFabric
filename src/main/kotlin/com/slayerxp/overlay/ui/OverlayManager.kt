package com.slayerxp.overlay.ui

import com.slayerxp.overlay.ui.Overlay
import com.slayerxp.overlay.ui.KPHOverlay
import com.slayerxp.overlay.ui.XPOverlay
import com.slayerxp.overlay.utils.ChatUtils.modMessage
import com.slayerxp.overlay.utils.Scheduler
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Click
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.input.KeyInput
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import java.awt.Color

class OverlayManager : Screen(Text.of("Overlay Manager")) {

    companion object {
        fun open() {
            Scheduler.scheduleTask(1) {
                val manager = OverlayManager()
                manager.addOverlay(KPHOverlay)
                manager.addOverlay(XPOverlay)
                manager.addOverlay(BVOverlay)
                MinecraftClient.getInstance().setScreen(manager)
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

    override fun close() {
        super.close()
        if (dirty) {
            overlays.forEach { it.savePosition() }
        }
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        context.fill(0, 0, width, height, Color(0, 0, 0, 150).rgb)
        overlays.forEach { it.draw(context) }
        overlays.forEach { drawHitboxMarker(context, mouseX, mouseY, it) }

        super.render(context, mouseX, mouseY, delta)
    }

    private fun drawHitboxMarker(context: DrawContext, mouseX: Int, mouseY: Int, overlay: Overlay) {
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

    private fun drawInfoPanel(context: DrawContext, mouseX: Int, mouseY: Int) {
        val posText = "Overlays: ${overlays.size} | Mouse: $mouseX, $mouseY | Dragging: $dragging"
        context.fill(10, 10, 10 + textRenderer.getWidth(posText) + 10, 25, Color(0, 0, 0, 180).rgb)
        context.drawTextWithShadow(textRenderer, posText, 15, 15, Color.WHITE.rgb)
    }

    private fun drawHollowRect(context: DrawContext, x1: Int, y1: Int, x2: Int, y2: Int, color: Int) {
        context.fill(x1, y1, x2, y1 + 1, color)
        context.fill(x1, y2 - 1, x2, y2, color)
        context.fill(x1, y1, x1 + 1, y2, color)
        context.fill(x2 - 1, y1, x2, y2, color)
    }

    override fun mouseClicked(click: Click, doubled: Boolean): Boolean {
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

    override fun mouseDragged(click: Click, deltaY: Double, offsetY: Double): Boolean {
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

    override fun mouseReleased(click: Click): Boolean {
        if (dragging) {
            dragging = false
            draggedOverlay = null
        }
        return super.mouseReleased(click)
    }

    override fun keyPressed(input: KeyInput): Boolean {
        when (input.keycode) {
            GLFW.GLFW_KEY_ESCAPE -> {
                close()
                return true
            }
        }
        return super.keyPressed(input)
    }

    override fun shouldPause() = false
    override fun shouldCloseOnEsc() = true
    override fun renderBackground(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {}
}