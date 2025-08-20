package com.slayerxp.overlay.settings.impl

import com.slayerxp.overlay.settings.config
import com.slayerxp.overlay.settings.Feature
import com.slayerxp.overlay.utils.ChatUtils.modMessage

object test2 : Feature {
    override val name = "test2"
    override val description = "hi vro"
    override val default = false
    override val category = "Overlay"
    
    override var enabled: Boolean
        get() = config.isToggled(name)
        set(value) {
            config.setToggle(name, value)
        }
    
    override fun onToggle(newState: Boolean) {}
}
