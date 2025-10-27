package com.slayerxp.overlay.settings.impl

import com.slayerxp.overlay.settings.Config
import com.slayerxp.overlay.settings.Feature

object Test2 : Feature {
    override val name = "test2"
    override val description = "hi vro"
    override val default = false
    override val category = "Overlay"
    
    override var enabled: Boolean
        get() = Config.isToggled(name)
        set(value) {
            Config.setToggle(name, value)
        }
    
    override fun onToggle(newState: Boolean) {}
}
