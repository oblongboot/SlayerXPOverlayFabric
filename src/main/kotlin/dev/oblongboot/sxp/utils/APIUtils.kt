package dev.oblongboot.sxp.utils

import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

object APIUtils {
    var BlazeXP: Long = 0
    var EmanXP: Long = 0
    var SpiderXP: Long = 0
    var ZombieXP: Long = 0
    var WolfXP: Long = 0
    var VampireXP: Long = 0

    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    val json = Json { ignoreUnknownKeys = true }
    private var cachedContributors: List<Contributor> = emptyList()

    /** Generic request function to return parsed JSON from a URL. */
    suspend inline fun <reified T> requestJson(url: String): T? {
        return try {
            val connection =
                    withContext(Dispatchers.IO) {
                        (URL(url).openConnection() as HttpURLConnection).apply {
                            requestMethod = "GET"
                            setRequestProperty("Accept", "application/json")
                            setRequestProperty("User-Agent", "Mozilla/5.0")
                            connectTimeout = 5000
                            readTimeout = 5000
                            doInput = true
                            connect()
                        }
                    }

            if (connection.responseCode == 200) {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                connection.disconnect()
                json.decodeFromString<T>(responseText)
            } else {
                println("HTTP error: ${connection.responseCode}")
                connection.disconnect()
                null
            }
        } catch (e: Exception) {
            println("Request failed: ${e.message}")
            null
        }
    }

    @Serializable
    data class Contributor(
            val name: String,
            val colorOne: String = "#FFD700", // default to gold!
            val colorTwo: String = "#FFD700"
    )
    fun getContributors(): List<Contributor> = cachedContributors
    private suspend fun xp() {
        val ign = ChatUtils.mc.player?.gameProfile?.name ?: return
        // ChatUtils.modMessage(ign)

        val url = "https://slayerxpoverlay.hypickelapi.workers.dev/slayer?username=$ign"
        val response = requestJson<SlayerXPResponse>(url)

        if (response != null) {
            BlazeXP = parseXP(response.blazeXP)
            EmanXP = parseXP(response.endermanXP)
            SpiderXP = parseXP(response.spiderXP)
            ZombieXP = parseXP(response.zombieXP)
            WolfXP = parseXP(response.wolfXP)
            VampireXP = parseXP(response.vampireXP)
        }
    }

    suspend fun fetchContributors() {
        val result =
                requestJson<List<Contributor>>(
                        "https://oblongboot.dev/slayerxpoverlay/ContributersGradient.json"
                )
        if (result != null) {
            cachedContributors = result
        }
    }

    fun getXP() {
        scope.launch { xp() }
    }

    fun startAutoXPUpdates() {
        scope.launch {
            while (isActive) {
                getXP()
                delay(5 * 60 * 1000L)
            }
        }
    }

    fun stop() {
        scope.cancel()
    }

    fun getCachedXP(): SlayerXP {
        return SlayerXP(BlazeXP, EmanXP, SpiderXP, ZombieXP, WolfXP, VampireXP)
    }

    private fun parseXP(xpString: String): Long = xpString.replace(",", "").toLongOrNull() ?: 0L
}

@Serializable
data class SlayerXPResponse(
        @SerialName("blaze_xp") val blazeXP: String,
        @SerialName("enderman_xp") val endermanXP: String,
        @SerialName("spider_xp") val spiderXP: String,
        @SerialName("zombie_xp") val zombieXP: String,
        @SerialName("wolf_xp") val wolfXP: String,
        @SerialName("vampire_xp") val vampireXP: String,
)

data class SlayerXP(
        val blaze: Long,
        val enderman: Long,
        val spider: Long,
        val zombie: Long,
        val wolf: Long,
        val vampire: Long,
)
