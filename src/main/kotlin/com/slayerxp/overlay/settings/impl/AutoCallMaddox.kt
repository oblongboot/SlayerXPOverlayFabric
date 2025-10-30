package com.slayerxp.overlay.settings.impl

import com.slayerxp.overlay.settings.Config
import com.slayerxp.overlay.settings.Feature

object AutoCallMaddox : Feature {
    override val name = "AutoCallMaddox"
    override val description = "Automatically calls maddox when you fail a quest"
    override val category = "General QOL"
    override val default = false
    override var enabled: Boolean
        get() = Config.isToggled(name)
        set(value) {
            Config.setToggle(name, value)
        }

    override fun onToggle(newState: Boolean) { Config.setToggle(BossHighlight.name, newState)}
}
