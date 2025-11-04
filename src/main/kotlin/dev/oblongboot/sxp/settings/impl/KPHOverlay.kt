package dev.oblongboot.sxp.settings.impl

import dev.oblongboot.sxp.settings.Config
import dev.oblongboot.sxp.settings.Feature

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