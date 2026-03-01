package dev.oblongboot.sxp.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import net.minecraft.ChatFormatting
import org.lwjgl.glfw.GLFW
import java.awt.Color
import kotlin.math.max

/*
* Code by Synnerz, Devonian 
* Modified Under GNU General Public License v3.0
* https://github.com/Synnerz/devonian/blob/main/src/main/kotlin/com/github/synnerz/devonian/utils/render/Render2D.kt
*/

object Render2D {
    private val removeCodesRegex = "[\\u00a7&][0-9a-fk-or]".toRegex()
    fun String.clearCodes(): String = this.replace(removeCodesRegex, "")
    private val formattingRegex = "(?<!\\\\\\\\)&(?=[0-9a-fk-or])".toRegex()
    val minecraft = Minecraft.getInstance()
    val textRenderer = minecraft.font
    val window get() = minecraft.window
    val mouse = minecraft.mouseHandler
    val screenWidth get() = window.screenWidth
    val screenHeight get() = window.screenHeight
    val scaledWidth get() = window.guiScaledWidth
    val scaledHeight get() = window.guiScaledHeight

    @JvmOverloads
    fun drawString(ctx: GuiGraphics, str: String, x: Int, y: Int, scale: Float = 1f, shadow: Boolean = true) {
        val matrices = ctx.pose()
        if (scale != 1f) {
            matrices.pushMatrix()
            matrices.scale(scale, scale)
        }

        ctx.drawString(
            textRenderer,
            str.replace(formattingRegex, "${ChatFormatting.PREFIX_CODE}"),
            x,
            y,
            -1,
            shadow
        )

        if (scale != 1f) matrices.popMatrix()
    }

    @JvmOverloads
    fun drawStringTextFormat(
        ctx: GuiGraphics,
        text: net.minecraft.network.chat.Component,
        x: Int,
        y: Int,
        scale: Float = 1f,
        shadow: Boolean = true
    ) {
        val matrices = ctx.pose()
        if (scale != 1f) {
            matrices.pushMatrix()
            matrices.scale(scale, scale)
        }

        ctx.drawString(textRenderer, text, x, y, -1, shadow)

        if (scale != 1f) matrices.popMatrix()
    }
    @JvmOverloads
    fun drawStringNW(ctx: GuiGraphics, str: String, x: Int, y: Int, scale: Float = 1f, shadow: Boolean = true) {
        var yy = y
        str.split("\n").forEach {
            drawString(ctx, it, x, yy, scale, shadow)
            yy += 10
        }
    }

    @JvmOverloads
    fun drawRect(ctx: GuiGraphics, x: Int, y: Int, width: Int, height: Int, color: Color = Color.WHITE) {
        ctx.fill(x, y, x + width, y + height, color.rgb)
    }

    fun String.width(): Int {
        val newlines = this.split("\n")
        if (newlines.size <= 1) return textRenderer.width(this.clearCodes())

        var maxWidth = 0

        for (line in newlines)
            maxWidth = max(maxWidth, textRenderer.width(line.clearCodes()))

        return maxWidth
    }

    fun String.height(): Int {
        val newlines = this.split("\n")
        if (newlines.size <= 1) return textRenderer.lineHeight

        return textRenderer.lineHeight * (newlines.size + 1)
    }

    @JvmOverloads
    fun drawOutline(ctx: GuiGraphics, x: Int, y: Int, width: Int, height: Int, color: Color = Color.WHITE) {
        ctx.fill(x, y, x + width, y + 1, color.rgb) // Top!
        ctx.fill(x, y + height - 1, x + width, y + height, color.rgb) // Bottom maybe
        ctx.fill(x, y + 1, x + 1, y + height - 1, color.rgb) // Left?
        ctx.fill(x + width - 1, y + 1, x + width, y + height - 1, color.rgb) // Right?
    }

    fun drawWhateverTheFuckThisIs(ctx: GuiGraphics, x: Int, y: Int, width: Int, height: Int, radius: Int, color: Color) {
        val argb = color.rgb or (color.alpha shl 24)
        ctx.fill(x + radius, y, x + width - radius, y + height, argb)
        ctx.fill(x, y + radius, x + width, y + height - radius, argb)
        for (i in 0 until radius) {
            val dy = Math.sqrt((radius * radius - i * i).toDouble()).toInt()
            ctx.fill(x + radius - i - 1, y + radius - dy, x + radius - i, y + radius, argb)
            ctx.fill(x + width - radius + i, y + radius - dy, x + width - radius + i + 1, y + radius, argb)
            ctx.fill(x + radius - i - 1, y + height - radius, x + radius - i, y + height - radius + dy, argb)
            ctx.fill(x + width - radius + i, y + height - radius, x + width - radius + i + 1, y + height - radius + dy, argb)
        }
    }


    object Mouse {
        val x get() = mouse.xpos() * scaledWidth / max(1, screenWidth)
        val y get() = mouse.ypos() * scaledHeight / max(1, screenHeight)

        fun isDown(button: Int): Boolean {
            return when(button) {
                0 -> GLFW.glfwGetMouseButton(Minecraft.getInstance().window.handle(), GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS
                1 -> GLFW.glfwGetMouseButton(Minecraft.getInstance().window.handle(), GLFW.GLFW_MOUSE_BUTTON_2) == GLFW.GLFW_PRESS
                2 -> GLFW.glfwGetMouseButton(Minecraft.getInstance().window.handle(), GLFW.GLFW_MOUSE_BUTTON_3) == GLFW.GLFW_PRESS
                else -> false
            }
        }
    }
}
