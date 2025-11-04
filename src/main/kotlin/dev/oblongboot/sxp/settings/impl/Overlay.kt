package dev.oblongboot.sxp.settings.impl

import dev.oblongboot.sxp.settings.Config
import dev.oblongboot.sxp.settings.Feature
import dev.oblongboot.sxp.utils.ChatUtils.modMessage

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
