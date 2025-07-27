package com.slayerxp.overlay.settings

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File

object config {
    private const val cfgFile = "slayerxpoverlay.json"
    private const val cfgDir = "config/SlayerXPOverlayFabric"
    private const val cfgPath = "$cfgDir/$cfgFile"

    private val gson = Gson()
    private val configFile = File(cfgPath)

    private var configData: JsonObject = JsonObject()

    init {
        val dir = File(cfgDir)
        if (!dir.exists()) dir.mkdirs()

        if (!configFile.exists()) {
            configFile.writeText("{}")
        }

        loadConfig()
    }

    private fun loadConfig() {
        val text = configFile.readText()
        configData = JsonParser.parseString(text).asJsonObject
    }

    private fun saveConfig() {
        configFile.writeText(gson.toJson(configData))
    }

    fun isToggled(setting: String): Boolean {
        return configData.get(setting)?.asBoolean ?: false
    }

    fun toggle(setting: String) {
        val oldState = isToggled(setting)
        val newState = !oldState
        configData.addProperty(setting, newState)
        saveConfig()
    }
    fun setToggle(setting: String, value: Boolean) {
        configData.addProperty(setting, value)
        saveConfig()
    }

    fun getLocationOfGUI(guiName: String): Triple<Int, Int, Int>? {
        return configData.get(guiName)?.asJsonObject?.let { location ->
            val x = location.get("x")?.asInt ?: 0
            val y = location.get("y")?.asInt ?: 0
            val z = location.get("z")?.asInt ?: 0
            Triple(x, y, z)
        } ?: run {
            println("wtflip $guiName no work")
            null
        }
    }
    fun setLocationOfGUI(guiName: String, x: Int, y: Int, z: Int) {
        val location = JsonObject()
        location.addProperty("x", x)
        location.addProperty("y", y)
        location.addProperty("z", z)
        configData.add(guiName, location)
        saveConfig()
    }
}
