package com.slayerxp.overlay.settings

import com.slayerxp.overlay.settings.impl.*

object FeatureManager {
    private val listOfFeatures = listOf(
        Overlay, KPHOverlay, Test2
    )
    
    fun getAllConfigStates(): Map<String, Any> {
        val states = mutableMapOf<String, Any>()
        
        listOfFeatures.forEach { feature ->
            states[feature.name] = Config.isToggled(feature.name)
        }
        
        val knownDropdowns = listOf("BossInfoDropdown")
        knownDropdowns.forEach { dropdownName -> // this is COOKED
            states[dropdownName] = Config.getDropdown(dropdownName, 0)
        }

        val knownCheckboxes = listOf("MultiSelectTest")
        knownCheckboxes.forEach { checkboxName ->
            states[checkboxName] = Config.getMultiSelect(checkboxName, setOf(0))
        }
        
        return states
    }
    
    fun loadAllFeatureStates() {
        listOfFeatures.forEach { feature ->
            val state = Config.isToggled(feature.name)
            feature.enabled = state
        }
    }
}