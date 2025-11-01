package com.slayerxp.overlay.utils

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.util.Formatting
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
    val minecraft = MinecraftClient.getInstance()
    val textRenderer = minecraft.textRenderer
    val window get() = minecraft.window
    val mouse = minecraft.mouse
    val screenWidth get() = window.width
    val screenHeight get() = window.height
    val scaledWidth get() = window.scaledWidth
    val scaledHeight get() = window.scaledHeight

    @JvmOverloads
    fun drawString(ctx: DrawContext, str: String, x: Int, y: Int, scale: Float = 1f, shadow: Boolean = true) {
        val matrices = ctx.matrices
        if (scale != 1f) {
            matrices.pushMatrix()
            matrices.scale(scale, scale)
        }

        ctx.drawText(
            textRenderer,
            str.replace(formattingRegex, "${Formatting.FORMATTING_CODE_PREFIX}"),
            x,
            y,
            -1,
            shadow
        )

        if (scale != 1f) matrices.popMatrix()
    }
    @JvmOverloads
    fun drawStringNW(ctx: DrawContext, str: String, x: Int, y: Int, scale: Float = 1f, shadow: Boolean = true) {
        var yy = y
        str.split("\n").forEach {
            drawString(ctx, it, x, yy, scale, shadow)
            yy += 10
        }
    }

    @JvmOverloads
    fun drawRect(ctx: DrawContext, x: Int, y: Int, width: Int, height: Int, color: Color = Color.WHITE) {
        ctx.fill(x, y, x + width, y + height, color.rgb)
    }

    fun String.width(): Int {
        val newlines = this.split("\n")
        if (newlines.size <= 1) return textRenderer.getWidth(this.clearCodes())

        var maxWidth = 0

        for (line in newlines)
            maxWidth = max(maxWidth, textRenderer.getWidth(line.clearCodes()))

        return maxWidth
    }

    fun String.height(): Int {
        val newlines = this.split("\n")
        if (newlines.size <= 1) return textRenderer.fontHeight

        return textRenderer.fontHeight * (newlines.size + 1)
    }

    @JvmOverloads
    fun drawOutline(ctx: DrawContext, x: Int, y: Int, width: Int, height: Int, color: Color = Color.WHITE) {
        ctx.fill(x, y, x + width, y + 1, color.rgb) // Top!
        ctx.fill(x, y + height - 1, x + width, y + height, color.rgb) // Bottom maybe
        ctx.fill(x, y + 1, x + 1, y + height - 1, color.rgb) // Left?
        ctx.fill(x + width - 1, y + 1, x + width, y + height - 1, color.rgb) // Right?
    }

    fun drawWhateverTheFuckThisIs(ctx: DrawContext, x: Int, y: Int, width: Int, height: Int, radius: Int, color: Color) {
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
        val x get() = mouse.x * scaledWidth / max(1, screenWidth)
        val y get() = mouse.y * scaledHeight / max(1, screenHeight)

        fun isDown(button: Int): Boolean {
            return when(button) {
                0 -> GLFW.glfwGetMouseButton(MinecraftClient.getInstance().window.handle, GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS
                1 -> GLFW.glfwGetMouseButton(MinecraftClient.getInstance().window.handle, GLFW.GLFW_MOUSE_BUTTON_2) == GLFW.GLFW_PRESS
                2 -> GLFW.glfwGetMouseButton(MinecraftClient.getInstance().window.handle, GLFW.GLFW_MOUSE_BUTTON_3) == GLFW.GLFW_PRESS
                else -> false
            }
        }
    }
}