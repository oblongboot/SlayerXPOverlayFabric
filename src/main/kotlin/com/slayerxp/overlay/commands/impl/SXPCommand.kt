package com.slayerxp.overlay.commands.impl

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

object SXPCommand {

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val aliases = listOf("sxp", "slayerxpoverlay", "sxpoverlay")

        aliases.forEach { alias ->
            dispatcher.register(
                CommandManager.literal(alias)
                    .executes { context -> execute(context) }
            )
        }
    }

    private fun execute(context: CommandContext<ServerCommandSource>): Int {
        context.source.sendFeedback({ Text.literal("Called.") }, false)
        return 1
    }
}
