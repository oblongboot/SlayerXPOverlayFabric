package com.slayerxp.overlay.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.slayerxp.overlay.commands.impl.*
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

object CommandsManager {

    fun registerCommands() {
        CommandRegistrationCallback.EVENT.register { dispatcher: CommandDispatcher<ServerCommandSource>,
                                                   registryAccess,
                                                   environment ->

            SXPCommand.register(dispatcher)
            SXPDevCommand.register(dispatcher)
        }
    }
}
