package dev.oblongboot.sxp.features

import dev.oblongboot.sxp.events.OnPacket
import dev.oblongboot.sxp.settings.Config
import dev.oblongboot.sxp.utils.Scheduler
import meteordevelopment.orbit.EventHandler
import net.minecraft.client.MinecraftClient
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket

class MiniBossAlert {
    @EventHandler
    fun onChatMessage(event: OnPacket.Incoming) {
        val packet = event.packet
        if (packet !is GameMessageS2CPacket) return
        if (!Config.isToggled("MiniBossAlert")) return
        val msg = packet.content().string.trim()

        val regex = Regex("SLAYER MINI-BOSS (.+) has spawned!")
        val match = regex.find(msg) ?: return
        val mini = match.groupValues[1]

        val client = MinecraftClient.getInstance()
        val player = client.player ?: return

        client.execute {
            client.inGameHud.setTitle(
                Text.literal(mini).formatted(Formatting.DARK_RED, Formatting.BOLD)
            )

            client.inGameHud.setSubtitle(Text.empty())
            client.inGameHud.setTitleTicks(0, 25, 0)

            client.player?.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value())
        }

    }
}