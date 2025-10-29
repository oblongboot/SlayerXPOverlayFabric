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
            val num = Config.getDropdown("MessageColor");
            val temp = String.format("§%sSlayerXPOverlay » §%s%s", colors[num][0], colors[num][1], message)
            val finalMessage: Text = Text.literal(temp)
            mc.player!!.sendMessage(finalMessage, false)
        }
    }
}