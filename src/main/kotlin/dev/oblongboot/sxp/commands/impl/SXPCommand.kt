package dev.oblongboot.sxp.commands.impl

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import com.mojang.brigadier.context.CommandContext
import dev.oblongboot.sxp.ui.SettingsScreen.Companion.open as bleh
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

object SXPCommand {
    fun registerClient() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
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