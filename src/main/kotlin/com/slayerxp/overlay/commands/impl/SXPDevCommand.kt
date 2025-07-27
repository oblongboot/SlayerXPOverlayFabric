package com.slayerxp.overlay.commands.impl

import com.slayerxp.overlay.utils.APIUtils
import com.slayerxp.overlay.utils.ChatUtils.modMessage
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType  
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

object SXPDevCommand {

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val aliases = listOf("sxpdev", "slayerxpoverlaydev", "sxpoverlaydev")

        aliases.forEach { alias ->
            dispatcher.register(
                CommandManager.literal(alias)
                    .then(
                        CommandManager.argument("devSetting", StringArgumentType.word())
                            .then(
                                CommandManager.argument("debug", StringArgumentType.word())
                                    .executes { context -> execute(context) }
                            )
                    )
            )
        }
    }

    private fun execute(context: CommandContext<ServerCommandSource>): Int {
        val devSetting = StringArgumentType.getString(context, "devSetting")
        val debug = StringArgumentType.getString(context, "debug")

        if (devSetting == "getXP") { 
            APIUtils.getXP()
            modMessage("getting xp")
        }
        modMessage("/sxpdev devsetting:$devSetting debug:$debug")
        return 1
    }
}
