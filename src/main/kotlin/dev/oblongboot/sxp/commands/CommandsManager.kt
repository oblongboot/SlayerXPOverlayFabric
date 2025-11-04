package dev.oblongboot.sxp.commands

import dev.oblongboot.sxp.commands.impl.*

object CommandsManager {
    fun registerCommands() {
        SXPCommand.registerClient()
        SXPDevCommand.registerClient()
    }
}