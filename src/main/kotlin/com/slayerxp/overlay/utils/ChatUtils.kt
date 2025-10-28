package com.slayerxp.overlay.utils

import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text

object ChatUtils {
    val prefix = "§bSlayerXPOverlay »§3"
    @JvmField
    val mc: MinecraftClient = MinecraftClient.getInstance() 

    fun modMessage(message: String?) {
        if (mc.player != null) {
            val finalMessage: Text = Text.literal("§bSlayerXPOverlay » §3$message")
            mc.player!!.sendMessage(finalMessage, false)
        }
    }
}