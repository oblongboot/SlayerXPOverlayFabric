package com.slayerxp.overlay.settings

import com.slayerxp.overlay.settings.config
import com.slayerxp.overlay.utils.ChatUtils.modMessage
import com.slayerxp.overlay.settings.impl.*

object FeatureManager {
    private val listOfFeatures = listOf(
        Overlay, KPHOverlay
    )
    fun getAllConfigStates(): Map<String, Boolean> {
        return listOfFeatures.associate { feature ->
            feature.name to config.isToggled(feature.name)
        }
    }

    fun loadAllFeatureStates() {
        listOfFeatures.forEach { feature ->
            val state = config.isToggled(feature.name)
            feature.enabled = state 
        }
    }
}
