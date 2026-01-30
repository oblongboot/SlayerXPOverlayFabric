package dev.oblongboot.sxp.commands.impl

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import com.mojang.brigadier.context.CommandContext
import dev.oblongboot.sxp.settings.impl.onMessage
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

object SXPResetKPHCommand {
    fun registerClient() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            val aliases = listOf("sxpresetkph", "slayerxpoverlayresetkph")
            aliases.forEach { alias ->
                dispatcher.register(
                    ClientCommandManager.literal(alias)
                        .executes { context -> executeClient(context) }
                )
            }
        }
    }

    private fun executeClient(context: CommandContext<FabricClientCommandSource>): Int {
        onMessage.resetKPH()
        return 1
    }
}