package dev.oblongboot.sxp.utils

import dev.oblongboot.sxp.settings.Config
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Style
import net.minecraft.text.Text

object ChatUtils {
    var prefix = "SlayerXPOverlay »"
    @JvmField val mc: MinecraftClient = MinecraftClient.getInstance()
    var isGradient = false

    fun modMessage(message: String?) {
        if (mc.player != null && message != null) {
            val finalMessage =
                    getColoredMessage(
                            message,
                            Config.getColor("MessageColorSelector1", java.awt.Color(33, 15, 235))
                                    .rgb,
                            Config.getColor("MessageColorSelector2", java.awt.Color(255, 87, 51))
                                    .rgb
                    )
            mc.execute {
                mc.player!!.sendMessage(finalMessage, false)
            }
        }
    }

    fun updatePrefix() {
        prefix =
                if (!Config.isToggled("ShortPrefix")) {
                    "SlayerXPOverlay »"
                } else {
                    "SXP »"
                }
    }

    @JvmStatic
    fun getGradientStyleMessage(message: String, startColor: Int, endColor: Int): Text {
        val msg = Text.empty()
        val len = message.length
        for (i in message.indices) {
            val c = message[i]
            val ratio = if (len > 1) i.toFloat() / (len - 1) else 0.0f
            val r =
                    ((1 - ratio) * (startColor shr 16 and 0xFF) +
                                    ratio * (endColor shr 16 and 0xFF))
                            .toInt()
            val g =
                    ((1 - ratio) * (startColor shr 8 and 0xFF) + ratio * (endColor shr 8 and 0xFF))
                            .toInt()
            val b = ((1 - ratio) * (startColor and 0xFF) + ratio * (endColor and 0xFF)).toInt()
            val color = (r shl 16) or (g shl 8) or b

            val charText = Text.literal(c.toString()).setStyle(Style.EMPTY.withColor(color))
            msg.append(charText)
        }
        return msg
    }

    fun getNormalStyleMessage(message: String, color1: Int, color2: Int): Text {
        val msg = Text.empty()
        msg.append(Text.literal("$prefix ").setStyle(Style.EMPTY.withColor(color1)))
        msg.append(Text.literal(message).setStyle(Style.EMPTY.withColor(color2)))
        return msg
    }

    fun getColoredMessage(message: String, color1: Int, color2: Int): Text {
        if (isGradient) {
            val temp = "$prefix $message"
            return getGradientStyleMessage(temp, color1, color2)
        }
        return getNormalStyleMessage(message, color1, color2)
    }
}
