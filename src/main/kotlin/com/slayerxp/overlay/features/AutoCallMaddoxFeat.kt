package com.slayerxp.overlay.features

import com.slayerxp.overlay.events.OnPacket
import com.slayerxp.overlay.settings.Config
import com.slayerxp.overlay.utils.Scheduler
import meteordevelopment.orbit.EventHandler
import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket

object AutoCallMaddoxFeat {
    @EventHandler
    fun onChatMessage(event: OnPacket.Incoming) {
        val packet = event.packet
        if (packet !is GameMessageS2CPacket) return
        if (!Config.isToggled("AutoCallMaddox")) return
        val msg = packet.content().toString()
        if (msg !== "  SLAYER QUEST FAILED!") return
        Scheduler.scheduleTask(20) { MinecraftClient.getInstance().networkHandler?.sendChatMessage("/call Maddox") }
    }
}