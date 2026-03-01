package dev.oblongboot.sxp.utils

import net.minecraft.network.chat.Component
import net.minecraft.client.Minecraft
import net.minecraft.world.scores.DisplaySlot
import java.util.regex.Pattern

object Scoreboard {
    private val bosses = listOf("Revenant", "Atoned", "Tarantula", "Sven", "Voidgloom", "Riftstalker", "Inferno")
    private val ROMAN_NUMERAL_REGEX = Pattern.compile("\\b(I{1,3}|IV|V|VI|VII|VIII|IX|X)\\b$")
    
    private val SLAYER_MAP = mapOf(
        "Coal Mine" to "Zombie",
        "Graveyard" to "Zombie",
        "Arachne's Burrow" to "Spider",
        "Arachne's Sanctuary" to "Spider",
        "Spider's Den" to "Spider",
        "Spider Mound" to "Spider",
        "Burning Desert" to "Spider",
        "Dragontail" to "Spider",
        "Crimson Isle" to "Spider",
        "Ruins" to "Sven",
        "Howling Cave" to "Sven",
        "The End" to "Enderman",
        "Void Sepulture" to "Enderman",
        "Zealot Bruiser Hideout" to "Enderman",
        "Dragons Nest" to "Enderman",
        "Smoldering Tomb" to "Blaze",
        "Stronghold" to "Blaze",
        "The Wasteland" to "Blaze",
        "Stillgore Château" to "Vampire"
    )
    
    private var slayerAreaMap: Map<String, String>? = null
    
    fun setSlayerAreaMap(map: Map<String, String>?) {
        slayerAreaMap = map
    }

    fun getSlayerType(): String {
        val zone = getArea() ?: return "Not in slayer area!"
        val cleanArea = zone
            .replace("§[0-9A-FK-OR]".toRegex(RegexOption.IGNORE_CASE), "")
            .replace("§.".toRegex(), "")
            .split("⏣")
            .getOrNull(1)
            ?.trim()
        val areaMap = slayerAreaMap ?: SLAYER_MAP
        return areaMap[cleanArea] ?: "Not in slayer area!"
    }
    
    private fun getArea(): String? {
        val scoreboardText = getScoreboardText()
        return scoreboardText.find { line ->
            line.contains("⏣") || line.contains("ф")
        }
    }
    
    fun getSlayerTier(): SlayerTierInfo? {
        val lines = getScoreboardText()
        if (lines.size < 2) return null
        for (line in lines) {
            val rawText = line.replace("§[0-9a-fk-or]".toRegex(RegexOption.IGNORE_CASE), "").trim()
            val matcher = ROMAN_NUMERAL_REGEX.matcher(rawText)
            if (!matcher.find()) continue
            val tier = matcher.group(0)
            val before = rawText.substring(0, matcher.start()).trim()
            for (boss in bosses) {
                if (before.contains(boss)) {
                    return SlayerTierInfo(boss, tier)
                }
            }
        }
        return null
    }
    
    fun getScoreboardText(): List<String> {
        val client = Minecraft.getInstance()
        val scoreboard = client.level?.scoreboard ?: return emptyList()
        val objective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR) ?: return emptyList()
        return try {
            scoreboard.trackedPlayers.asSequence()
                .filter { scoreHolder -> objective in scoreboard.listPlayerScores(scoreHolder) }
                .map { scoreHolder ->
                    val score = scoreboard.getOrCreatePlayerScore(scoreHolder, objective, true)
                    val displayText = score.display()?.string ?: scoreHolder.scoreboardName
                    val teamFormattedName = try {
                        val team = scoreboard.getPlayersTeam(scoreHolder.scoreboardName)
                        if (team != null) {
                            val textToDecorate = score.display() ?: Component.literal(scoreHolder.scoreboardName)
                            team.getFormattedName(textToDecorate).string
                        } else null
                    } catch (e: Exception) {
                        null
                    }
                    Pair(teamFormattedName ?: displayText, score.get())
                }
                .sortedWith(compareBy<Pair<String, Int>> { it.second }.reversed().thenBy { it.first.lowercase() })
                .map { (text, _) ->
                    text.replace("§[0-9a-fk-or]".toRegex(RegexOption.IGNORE_CASE), "")
                        .replace("[\u0000-\u001F\u007F-\u009F]".toRegex(), "")
                        .replace("\\s+".toRegex(), " ")//bleh
                        .trim()
                }
                .filter { it.isNotEmpty() && it.length > 1 }
                .toList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    data class SlayerTierInfo(
        val boss: String,
        val tier: String
    )
}