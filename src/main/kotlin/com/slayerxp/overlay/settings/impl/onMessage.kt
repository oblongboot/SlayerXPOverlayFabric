package com.slayerxp.overlay.settings.impl

import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket
import com.slayerxp.overlay.utils.ChatUtils.modMessage
import com.slayerxp.overlay.events.onPacket
import meteordevelopment.orbit.EventHandler
import net.minecraft.text.Text
import net.minecraft.client.MinecraftClient
import net.minecraft.scoreboard.ScoreboardDisplaySlot

class onMessage {

    @EventHandler
    fun onPacketRecived(event: onPacket.Incoming) {
        val packet = event.packet
        if (packet !is GameMessageS2CPacket) return
        val messagee = packet.content().string
        if (messagee == "  SLAYER QUEST COMPLETE!") {
            getSlayer()
        }
    }
   
    companion object {
        fun getSlayer() {
            val client = MinecraftClient.getInstance()
            val scoreboard = client.world?.scoreboard ?: return
            val objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR) ?: return
            val scoreboardText = getScoreboardText()
            
            val overworldLine = scoreboardText.filter { it.contains("⏣", ignoreCase = true) }
            val riftLine = scoreboardText.filter { it.contains("ф", ignoreCase = true) }
            
            if (overworldLine.isNotEmpty()) {
                modMessage("overworld line! $overworldLine")
            } else if (riftLine.isNotEmpty()) {
                modMessage("rift line! $riftLine")
            }    
        }
        
        fun getScoreboardText(): List<String> {
            val client = MinecraftClient.getInstance()
            val scoreboard = client.world?.scoreboard ?: return emptyList()
           
            val objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR) ?: return emptyList()
           
            return try {
                scoreboard.knownScoreHolders.asSequence().filter { scoreHolder ->
                    objective in scoreboard.getScoreHolderObjectives(scoreHolder)
                }.map { scoreHolder ->
                    val score = scoreboard.getOrCreateScore(scoreHolder, objective, true)
                   
                    val displayText = score.displayText?.string
                        ?: scoreHolder.nameForScoreboard
                   
                    val teamFormattedName = try {
                        val team = scoreboard.getScoreHolderTeam(scoreHolder.nameForScoreboard)
                        if (team != null) {
                            val textToDecorate = score.displayText ?: Text.literal(scoreHolder.nameForScoreboard)
                            team.decorateName(textToDecorate).string
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }
                   
                    Pair(teamFormattedName ?: displayText, score.score)
                }.sortedWith(
                    compareBy<Pair<String, Int>> { it.second }.reversed()
                        .thenBy { it.first.lowercase() }
                ).map { (text, _) ->
                    text.replace("§[0-9a-fk-or]".toRegex(), "") 
                        .replace(Regex("[\u0000-\u001F\u007F-\u009F]"), "") 
                        .replace(Regex("\\s+"), " ") 
                        .trim()
                }.filter { it.isNotEmpty() && it.length > 1 }.toList() 
               
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}