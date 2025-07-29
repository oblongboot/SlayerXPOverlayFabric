package com.slayerxp.overlay.commands

import com.slayerxp.overlay.commands.impl.*

object CommandsManager {
    fun registerCommands() {
        SXPCommand.registerClient()
        SXPDevCommand.registerClient()
    }
}