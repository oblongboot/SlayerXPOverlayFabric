package com.slayerxp.overlay.settings.impl

import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket
import com.slayerxp.overlay.utils.ChatUtils.modMessage
import com.slayerxp.overlay.utils.APIUtils
import com.slayerxp.overlay.utils.Scoreboard
import com.slayerxp.overlay.events.onPacket
import com.slayerxp.overlay.ui.Overlay
import meteordevelopment.orbit.EventHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.text.DecimalFormat

class onMessage {

    companion object {
        private var questStartTime = 0L
        private var bossSpawnTime = 0L
        private var bossTimerStarted = false
        private var messageBool = false
        private var bonus = 1.0
        private var tier: String? = null
        private var correctXP = 0L

        private val numberFormatter = DecimalFormat("#,###")
        private var mapLoaded = false

        private val SLAYER_XP_VALUES = mapOf(
            "Zombie" to mapOf("I" to 5L, "II" to 25L, "III" to 100L, "IV" to 500L, "V" to 1500L),
            "Spider" to mapOf("I" to 5L, "II" to 25L, "III" to 100L, "IV" to 500L, "V" to 1500L),
            "Sven" to mapOf("I" to 5L, "II" to 25L, "III" to 100L, "IV" to 500L, "V" to 1500L),
            "Enderman" to mapOf("I" to 5L, "II" to 25L, "III" to 100L, "IV" to 500L, "V" to 1500L),
            "Blaze" to mapOf("I" to 5L, "II" to 25L, "III" to 100L, "IV" to 500L, "V" to 1500L),
            "Vampire" to mapOf("I" to 10L, "II" to 25L, "III" to 60L, "IV" to 120L, "V" to 150L)
        )

        fun initialize() {
            if (!mapLoaded) {
                CoroutineScope(Dispatchers.IO).launch {
                    loadSlayerMap()
                    loadMayorData()
                    mapLoaded = true
                }
            }
        }

        private suspend fun loadSlayerMap() {
            try {
                val slayerAreaMap = APIUtils.requestJson<Map<String, String>>(
                    "https://raw.githubusercontent.com/oblongboot/SlayerXPOverlayFabric/refs/heads/data/SlayerMap.json"
                )
                Scoreboard.setSlayerAreaMap(slayerAreaMap)
            } catch (e: Exception) {
                modMessage("Failed to load slayer area map from GitHub, using fallback")
                Scoreboard.setSlayerAreaMap(null)
            }
        }

        private suspend fun loadMayorData() {
            try {
                val mayorResponse = APIUtils.requestJson<Boolean>(
                    "https://slayerxpoverlay.hypickelapi.workers.dev/mayor"
                )
                bonus = if (mayorResponse == true) 1.25 else 1.0
            } catch (e: Exception) {
                bonus = 1.0
            }
        }


        fun handleSlayerQuestStart() {
            messageBool = true
            questStartTime = System.currentTimeMillis()
            bossTimerStarted = false
            bossSpawnTime = 0L
            val tierInfo = Scoreboard.getSlayerTier()
            tier = tierInfo?.tier
        }

        fun handleSlayerQuestComplete() {
            if (!messageBool) return
            messageBool = false

            val currentSlayerType = Scoreboard.getSlayerType()
            if (currentSlayerType == "Not in slayer area!") return
            val tierValue = tier ?: "I"
            correctXP = SLAYER_XP_VALUES[currentSlayerType]?.get(tierValue) ?: 5L
            if (currentSlayerType != "Vampire") {
                correctXP = (correctXP * bonus).toLong()
            }
            val apiData = APIUtils.getCachedXP()
            val currentXP = when (currentSlayerType) {
                "Zombie" -> apiData.zombie
                "Spider" -> apiData.spider
                "Sven" -> apiData.wolf
                "Enderman" -> apiData.enderman
                "Blaze" -> apiData.blaze
                "Vampire" -> apiData.vampire
                else -> 0L
            }

            val newXP = currentXP + correctXP

            val totalTime = System.currentTimeMillis() - questStartTime
            val bossTime = if (bossSpawnTime > 0) System.currentTimeMillis() - bossSpawnTime else 0L
            val spawnTime = totalTime - bossTime

            val timeStr = String.format("%.2f", totalTime / 1000.0)
            val bossTimeStr = String.format("%.2f", bossTime / 1000.0)
            val spawnTimeStr = String.format("%.2f", spawnTime / 1000.0)

            val parts = mutableListOf<String>()
            parts.add("Slayer XP: ${numberFormatter.format(newXP)}")
            parts.add("Time: ${timeStr}s, Boss: ${bossTimeStr}s, Spawn: ${spawnTimeStr}s")
            //parts.add(sessionInfo)

            modMessage(parts.joinToString(" | "))
            APIUtils.getXP()

            questStartTime = 0L
            bossSpawnTime = 0L
            bossTimerStarted = false
            tier = null
        }

        fun handleBossSpawn() {
            if (!bossTimerStarted && messageBool) { // not used yet i think
                bossTimerStarted = true
                bossSpawnTime = System.currentTimeMillis()
            }
        }

        fun updateOverlayDisplay() {
            val slayerType = Scoreboard.getSlayerType()
            if (slayerType == "Not in slayer area!") {
                Overlay.hide()
                return
            }

            val apiData = APIUtils.getCachedXP()
            val xp = when (slayerType) {
                "Zombie" -> apiData.zombie
                "Spider" -> apiData.spider
                "Sven" -> apiData.wolf
                "Enderman" -> apiData.enderman
                "Blaze" -> apiData.blaze
                "Vampire" -> apiData.vampire
                else -> 0L
            }

            Overlay.updateXP(slayerType, xp.toInt())
            Overlay.show()
        }
    }

    @EventHandler
    fun onPacketReceived(event: onPacket.Incoming) {
        val packet = event.packet
        if (packet !is GameMessageS2CPacket) return
        val message = packet.content().string.trim()

        when {
            
            message == "  SLAYER QUEST STARTED!" -> {
                handleSlayerQuestStart()
            }

            message == "  SLAYER QUEST COMPLETE!" || message == "  NICE! SLAYER BOSS SLAIN!" -> {
                handleSlayerQuestComplete()
            }
        }

        updateOverlayDisplay()
    }
}