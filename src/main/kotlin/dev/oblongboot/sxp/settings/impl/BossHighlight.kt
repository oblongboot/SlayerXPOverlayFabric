package dev.oblongboot.sxp.settings.impl

import dev.oblongboot.sxp.settings.Config
import dev.oblongboot.sxp.settings.Feature

object BossHighlight : Feature {
    override val name = "BossHighlight"
    override val description = "Highlights the boss in a color of your choice"
    override val default = false
    override val category = "Highlights"
    override var enabled: Boolean
        get() = Config.isToggled(name)
        set(value) {
            Config.setToggle(name, value)
        }
    
    override fun onToggle(newState: Boolean) { Config.setToggle(name, newState)}
}
