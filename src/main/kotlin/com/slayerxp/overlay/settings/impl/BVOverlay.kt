package com.slayerxp.overlay.settings.impl

import com.slayerxp.overlay.settings.Config
import com.slayerxp.overlay.settings.Feature

object BVOverlay : Feature {
    override val name = "BurningVengeanceOverlay";
    override val description = "Counts down until your burning vengeance activates";
    override val default = false;
    override val category = "Blaze";

    override var enabled: Boolean
        get() = Config.isToggled(name)
        set(value) {
            Config.setToggle(name, value)
        }

    override fun onToggle(newState: Boolean) {}
}