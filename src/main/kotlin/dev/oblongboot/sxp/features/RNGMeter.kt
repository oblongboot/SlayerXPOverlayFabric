package dev.oblongboot.sxp.features

import dev.oblongboot.sxp.SlayerConstants
import dev.oblongboot.sxp.utils.ChatUtils
import meteordevelopment.orbit.EventHandler
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket

class RNGMeter {
    @EventHandler
    fun onChatMsg(event: dev.oblongboot.sxp.events.OnPacket) {
        if (event.packet !is ClientboundSystemChatPacket) return;
        val message = event.packet.content().string
        println("b")
        if (message.trim().startsWith("RNG Meter -")) {
            println("a")
            val storedXP = Regex("""[0-9]{1,3}(,[0-9]{3})*|[0-9]+""").find(message)?.value?.replace(",", "")?.toIntOrNull()
            val item = SlayerConstants.mainTable["JUDGEMENT_CORE"] ?: return
            val requiredXP = item.requiredXp

            val percent = if (storedXP != null) {
                (storedXP.toDouble() / requiredXP.toDouble()) * 100.0
            } else 0.0
            println("c")
            val xpText = " - $storedXP/$requiredXP (${String.format("%.2f", percent)}%) for ${item.name}"
            ChatUtils.modMessage(xpText)
        }
    }
}