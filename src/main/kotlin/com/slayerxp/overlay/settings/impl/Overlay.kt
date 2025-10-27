package com.slayerxp.overlay.settings.impl

import com.slayerxp.overlay.settings.Config
import com.slayerxp.overlay.settings.Feature
import com.slayerxp.overlay.utils.ChatUtils.modMessage

object Overlay : Feature {
    override val name = "Overlay"
    override val description = "Shows Slayer XP in a movable overlay"
    override val default = false
    override val category = "Overlays"
    
    override var enabled: Boolean
        get() = Config.isToggled(name)
        set(value) {
            Config.setToggle(name, value)
        }
    
    override fun onToggle(newState: Boolean) {
        modMessage("$name = $newState") // not working and idk why so bleh
    }
}
