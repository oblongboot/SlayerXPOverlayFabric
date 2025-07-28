package com.slayerxp.overlay.ui

import com.slayerxp.overlay.utils.ChatUtils.modMessage
import com.slayerxp.overlay.utils.Scheduler
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import java.awt.Color

class OverlayManager : Screen(Text.of("Overlay Manager")) {

    companion object {
        fun open() {
            Scheduler.scheduleTask(1) {
                MinecraftClient.getInstance().setScreen(OverlayManager())
                Overlay.hide()
            }
        }
    }

    private var dragging = false
    private var dragOffsetX = 0f
    private var dragOffsetY = 0f
    private var dirty = false

    override fun close() {
        super.close()
        Overlay.show()
        if (dirty) {
            Overlay.savePosition()
        }
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        context.fill(0, 0, width, height, Color(0, 0, 0, 150).rgb)
        Overlay.draw(context)
        drawHitboxMarker(context, mouseX, mouseY)
        super.render(context, mouseX, mouseY, delta)
    }

    private fun drawHitboxMarker(context: DrawContext, mouseX: Int, mouseY: Int) {
        val x = Overlay.x
        val y = Overlay.y
        val w = Overlay.width.toFloat()
        val h = Overlay.height.toFloat()
        val hitboxColor = if (mouseX in x..(x + w.toInt()) && mouseY in y..(y + h.toInt())) {
            Color(0, 255, 0, 150).rgb
        } else {
            Color(255, 0, 0, 100).rgb
        }
        drawHollowRect(context, x, y, (x + w).toInt(), (y + h).toInt(), hitboxColor)
        val cornerSize = 5
        context.fill(x, y, x + cornerSize, y + cornerSize, Color(255, 255, 0, 200).rgb)
        context.fill((x + w - cornerSize).toInt(), y, (x + w).toInt(), y + cornerSize, Color(255, 255, 0, 200).rgb)
        context.fill(x, (y + h - cornerSize).toInt(), x + cornerSize, (y + h).toInt(), Color(255, 255, 0, 200).rgb)
        context.fill((x + w - cornerSize).toInt(), (y + h - cornerSize).toInt(), (x + w).toInt(), (y + h).toInt(), Color(255, 255, 0, 200).rgb)
        val posText = "Pos: $x, $y | Size: ${w.toInt()}x${h.toInt()}"
        val mouseText = "Mouse: $mouseX, $mouseY | Dragging: $dragging"
        context.fill(10, 10, 10 + textRenderer.getWidth(posText) + 10, 35, Color(0, 0, 0, 180).rgb)
        context.drawTextWithShadow(textRenderer, posText, 15, 15, Color.WHITE.rgb)
        context.drawTextWithShadow(textRenderer, mouseText, 15, 25, Color.WHITE.rgb)
    }

    private fun drawHollowRect(context: DrawContext, x1: Int, y1: Int, x2: Int, y2: Int, color: Int) {
        context.fill(x1, y1, x2, y1 + 1, color)
        context.fill(x1, y2 - 1, x2, y2, color)
        context.fill(x1, y1, x1 + 1, y2, color)
        context.fill(x2 - 1, y1, x2, y2, color)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0) {
            val x = Overlay.x
            val y = Overlay.y
            val w = Overlay.width
            val h = Overlay.height

            if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h) {
                dragging = true
                dragOffsetX = (mouseX - x).toFloat()
                dragOffsetY = (mouseY - y).toFloat()
                return true
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        if (dragging) {
            val w = Overlay.width
            val h = Overlay.height

            var newX = (mouseX - dragOffsetX).toFloat()
            var newY = (mouseY - dragOffsetY).toFloat()
            newX = newX.coerceIn(0f, (width - w).toFloat())
            newY = newY.coerceIn(0f, (height - h).toFloat())

            Overlay.x = newX.toInt()
            Overlay.y = newY.toInt()

            dirty = true
            return true
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (dragging) {
            dragging = false
        }
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        when (keyCode) {
            GLFW.GLFW_KEY_ESCAPE -> {
                close()
                return true
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    private fun moveOverlay(dx: Int, dy: Int) {
        val x = Overlay.x
        val y = Overlay.y
        val w = Overlay.width
        val h = Overlay.height
        val newX = (x + dx).coerceIn(0, width - w)
        val newY = (y + dy).coerceIn(0, height - h)

        Overlay.x = newX
        Overlay.y = newY

        dirty = true
    }

    override fun shouldPause() = false
    override fun shouldCloseOnEsc() = true
    override fun renderBackground(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {}
}
