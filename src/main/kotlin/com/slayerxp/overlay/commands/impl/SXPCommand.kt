package com.slayerxp.overlay.commands.impl

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import com.slayerxp.overlay.ui.OverlayManager
import com.slayerxp.overlay.utils.ChatUtils.modMessage
import com.mojang.brigadier.context.CommandContext
import com.slayerxp.overlay.ui.SettingsScreen.Companion.open as bleh
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

object SXPCommand {
    fun registerClient() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, registryAccess ->
            val aliases = listOf("sxp", "slayerxpoverlay", "sxpoverlay")
            aliases.forEach { alias ->
                dispatcher.register(
                    ClientCommandManager.literal(alias)
                        .executes { context -> executeClient(context) }
                )
            }
        }
    }
    
    private fun executeClient(context: CommandContext<FabricClientCommandSource>): Int {
        bleh()
        return 1
    }
}