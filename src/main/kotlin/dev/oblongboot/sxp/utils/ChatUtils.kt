package dev.oblongboot.sxp.utils

import dev.oblongboot.sxp.settings.Config
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.Component

object ChatUtils {
    var prefix = "SlayerXPOverlay »"
    @JvmField val mc: Minecraft = Minecraft.getInstance()
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
                mc.player!!.sendSystemMessage(finalMessage)
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
    fun getGradientStyleMessage(message: String, startColor: Int, endColor: Int): Component {
        val msg = Component.empty()
        val len = message.length
        for (i in message.indices) {
            val c = message[i]
            val ratio = i.toFloat() / (len - 1)
            val r =
                    ((1 - ratio) * (startColor shr 16 and 0xFF) +
                                    ratio * (endColor shr 16 and 0xFF))
                            .toInt()
            val g =
                    ((1 - ratio) * (startColor shr 8 and 0xFF) + ratio * (endColor shr 8 and 0xFF))
                            .toInt()
            val b = ((1 - ratio) * (startColor and 0xFF) + ratio * (endColor and 0xFF)).toInt()
            val color = (r shl 16) or (g shl 8) or b

            val charText = Component.literal(c.toString()).setStyle(Style.EMPTY.withColor(color))
            msg.append(charText)
        }
        return msg
    }

    fun getNormalStyleMessage(message: String, color1: Int, color2: Int): Component {
        val msg = Component.empty()
        msg.append(Component.literal("$prefix ").setStyle(Style.EMPTY.withColor(color1)))
        msg.append(Component.literal(message).setStyle(Style.EMPTY.withColor(color2)))
        return msg
    }

    fun getColoredMessage(message: String, color1: Int, color2: Int): Component {
        if (isGradient) {
            val temp = "$prefix $message"
            return getGradientStyleMessage(temp, color1, color2)
        }
        return getNormalStyleMessage(message, color1, color2)
    }
}
