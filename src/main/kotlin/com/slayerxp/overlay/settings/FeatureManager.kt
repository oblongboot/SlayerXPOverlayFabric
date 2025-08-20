package com.slayerxp.overlay.settings

import com.slayerxp.overlay.settings.config
import com.slayerxp.overlay.utils.ChatUtils.modMessage
import com.slayerxp.overlay.settings.impl.*

object FeatureManager {
    private val listOfFeatures = listOf(
        Overlay, KPHOverlay, test2
    )
    
    fun getAllConfigStates(): Map<String, Any> {
        val states = mutableMapOf<String, Any>()
        
        listOfFeatures.forEach { feature ->
            states[feature.name] = config.isToggled(feature.name)
        }
        
        val knownDropdowns = listOf("BossInfoDropdown")
        knownDropdowns.forEach { dropdownName -> // this is COOKED
            states[dropdownName] = config.getDropdown(dropdownName, 0)
        }
        
        return states
    }
    
    fun loadAllFeatureStates() {
        listOfFeatures.forEach { feature ->
            val state = config.isToggled(feature.name)
            feature.enabled = state
        }
    }
}