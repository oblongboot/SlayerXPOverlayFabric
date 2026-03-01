package dev.oblongboot.sxp.features

import dev.oblongboot.sxp.events.OnPacket
import dev.oblongboot.sxp.settings.Config
import dev.oblongboot.sxp.utils.Scheduler
import meteordevelopment.orbit.EventHandler
import net.minecraft.client.Minecraft
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket

class AutoCallMaddoxFeat {
    @EventHandler
    fun onChatMessage(event: OnPacket.Incoming) {
        val packet = event.packet
        if (packet !is ClientboundSystemChatPacket) return
        if (!Config.isToggled("AutoCallMaddox")) return
        val msg = packet.content().string.trim()
        if (msg != "SLAYER QUEST FAILED!") return
        Scheduler.scheduleTask(5) { Minecraft.getInstance().connection?.sendChat("/call Maddox") }
    }
}
