package com.slayerxp.overlay.utils

import com.slayerxp.overlay.utils.APIUtils
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.booleanOrNull

object UpdateChecker {
    private const val VERSION_URL = "https://slayerxpoverlay.hypickelapi.workers.dev/lv?current="

    suspend fun isUpdateAvailable(currentVersion: String): Boolean {
        println("requesting $VERSION_URL$currentVersion")
        val response = APIUtils.requestJson<JsonObject>("$VERSION_URL$currentVersion")
        val isLatest = (response?.jsonObject?.get("isLatest") as? JsonPrimitive)?.booleanOrNull ?: return false
        return !isLatest
    }
}
