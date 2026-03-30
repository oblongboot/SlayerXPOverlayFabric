package dev.oblongboot.sxp.features

import dev.oblongboot.sxp.events.OnPacket
import dev.oblongboot.sxp.settings.Config
import dev.oblongboot.sxp.utils.Scheduler
import meteordevelopment.orbit.EventHandler
import net.minecraft.client.Minecraft
import net.minecraft.sounds.SoundSource
import net.minecraft.sounds.SoundEvents
import net.minecraft.network.chat.Component
import net.minecraft.ChatFormatting
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket

class MiniBossAlert {
    @EventHandler
    fun onChatMessage(event: OnPacket.Incoming) {
        val packet = event.packet
        if (packet !is ClientboundSystemChatPacket) return
        if (!Config.isToggled("MiniBossAlert")) return
        val msg = packet.content().string.trim()

        val regex = Regex("^SLAYER MINI-BOSS (.+) has spawned!")
        val match = regex.find(msg) ?: return
        val mini = match.groupValues[1]

        val client = Minecraft.getInstance()
        val player = client.player ?: return

        client.execute {
            client.gui.setTitle(
                Component.literal(mini).withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD)
            )

            client.gui.setSubtitle(Component.empty())
            client.gui.setTimes(0, 25, 0)

            client.player?.makeSound(SoundEvents.NOTE_BLOCK_PLING.value())
        }

    }
}