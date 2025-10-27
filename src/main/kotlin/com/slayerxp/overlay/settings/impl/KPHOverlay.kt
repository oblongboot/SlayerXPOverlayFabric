package com.slayerxp.overlay.settings.impl

import com.slayerxp.overlay.settings.Config
import com.slayerxp.overlay.settings.Feature

object KPHOverlay : Feature {
    override val name = "KPHOverlay"
    override val description = "Shows slayer kills per hour in a movable overlay"
    override val default = false
    override val category = "Overlays"
    
    override var enabled: Boolean
        get() = Config.isToggled(name)
        set(value) {
            Config.setToggle(name, value)
        }
    
    override fun onToggle(newState: Boolean) {}
}