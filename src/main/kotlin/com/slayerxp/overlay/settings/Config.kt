package com.slayerxp.overlay.settings

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File

object Config {
    private const val CONFIGFILE = "slayerxpoverlay.json"
    private const val CONFIGDIR = "config/SlayerXPOverlayFabric"
    private const val CONFIGPATH = "$CONFIGDIR/$CONFIGFILE"

    private val gson = Gson()
    private val configFile = File(CONFIGPATH)

    private var configData: JsonObject = JsonObject()

    init {
        val dir = File(CONFIGDIR)
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
        val newState = !isToggled(setting)
        configData.addProperty(setting, newState)
        saveConfig()
    }

    fun setToggle(setting: String, value: Boolean) {
        configData.addProperty(setting, value)
        saveConfig()
    }

    fun getLocationOfGUI(guiName: String): Pair<Int, Int>? {
        val hudObject = configData.getAsJsonObject("HUD") ?: return null
        val location = hudObject.getAsJsonObject(guiName) ?: return null

        val x = location.get("x")?.asInt ?: 5
        val y = location.get("y")?.asInt ?: 5

        return Pair(x, y)
    }

    fun setLocationOfGUI(guiName: String, x: Int, y: Int) {
        val hudObject = configData.getAsJsonObject("HUD") ?: JsonObject().also {
            configData.add("HUD", it)
        }

        val location = JsonObject().apply {
            addProperty("x", x)
            addProperty("y", y)
        }

        hudObject.add(guiName, location)
        saveConfig()
    }
    fun setDropdown(setting: String, index: Int) {
        configData.addProperty(setting, index)
        saveConfig()
    }

    fun getDropdown(setting: String, defaultIndex: Int = 0): Int {
        return configData.get(setting)?.asInt ?: defaultIndex
    }

    fun setMultiSelect(setting: String, indices: List<Int>) {
        val arr = gson.toJsonTree(indices).asJsonArray
        configData.add(setting, arr)
        saveConfig()
    }

    fun getMultiSelect(setting: String, default: Set<Int> = emptySet()): Set<Int> {
        return configData.getAsJsonArray(setting)?.map { it.asInt }?.toSet() ?: default
    }
}
