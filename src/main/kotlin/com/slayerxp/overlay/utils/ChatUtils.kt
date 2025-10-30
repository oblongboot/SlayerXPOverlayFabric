package com.slayerxp.overlay.utils

import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import com.slayerxp.overlay.settings.Config

object ChatUtils {
    var prefix = "§bSlayerXPOverlay »§3"
    @JvmField
    val mc: MinecraftClient = MinecraftClient.getInstance()
    val colors: Array<Array<String>> = arrayOf(
        arrayOf("b", "3"),
        arrayOf("4", "c"),
        arrayOf("5", "d"),
        arrayOf("2", "a"),
        arrayOf("6", "e")
    )

    fun modMessage(message: String?) {
        if (mc.player != null) {
            val finalMessage: Text = Text.literal("$prefix $message")
            mc.player!!.sendMessage(finalMessage, false)
        }
    }

    fun updatePrefix() {
        val num = Config.getDropdown("MessageColor")
        prefix = if (!Config.isToggled("ShortPrefix")) {
            String.format("§%sSlayerXPOverlay »§%s", colors[num][0], colors[num][1])
        } else {
            String.format("§%sSXP »§%s", colors[num][0], colors[num][1])
        }
    }

    fun getGradientStyleMessage(message: String, startColor: Int, endColor: Int): Text {
        val msg = net.minecraft.text.Text.empty()
        val len = message.length
        for (i in message.indices) {
            val c = message[i]
            val ratio = i.toFloat() / (len - 1)
            val r = ((1 - ratio) * (startColor shr 16 and 0xFF) + ratio * (endColor shr 16 and 0xFF)).toInt()
            val g = ((1 - ratio) * (startColor shr 8 and 0xFF) + ratio * (endColor shr 8 and 0xFF)).toInt()
            val b = ((1 - ratio) * (startColor and 0xFF) + ratio * (endColor and 0xFF)).toInt()
            val color = (r shl 16) or (g shl 8) or b
                
            val charText = net.minecraft.text.Text.literal(c.toString())
                .setStyle(net.minecraft.text.Style.EMPTY.withColor(color))
            msg.append(charText)
        }
        return msg
    }
}