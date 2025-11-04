package dev.oblongboot.sxp.settings.impl

import dev.oblongboot.sxp.settings.Config
import dev.oblongboot.sxp.settings.Feature

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
