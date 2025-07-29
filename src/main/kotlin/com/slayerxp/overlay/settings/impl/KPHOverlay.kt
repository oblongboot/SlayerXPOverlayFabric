package com.slayerxp.overlay.settings.impl

import com.slayerxp.overlay.events.onPacket
import meteordevelopment.orbit.EventHandler
import com.slayerxp.overlay.settings.config
import com.slayerxp.overlay.settings.Feature
import com.slayerxp.overlay.utils.ChatUtils.modMessage

object KPHOverlay : Feature {
    override val name = "KPHOverlay"
    override val description = "Shows slayer kills per hour in a movable overlay"
    override val default = false
    override val category = "Overlays"
    
    override var enabled: Boolean
        get() = config.isToggled(name)
        set(value) {
            config.setToggle(name, value)
        }
    
    override fun onToggle(newState: Boolean) {}
}