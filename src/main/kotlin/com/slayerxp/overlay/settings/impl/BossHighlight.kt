package com.slayerxp.overlay.settings.impl

import com.slayerxp.overlay.settings.Config
import com.slayerxp.overlay.settings.Feature

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
