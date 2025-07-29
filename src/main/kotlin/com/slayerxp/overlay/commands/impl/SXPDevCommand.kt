package com.slayerxp.overlay.commands.impl

import com.slayerxp.overlay.utils.APIUtils
import com.slayerxp.overlay.settings.config
import com.slayerxp.overlay.settings.impl.onMessage.Companion.getSlayer
import com.slayerxp.overlay.settings.FeatureManager
import com.slayerxp.overlay.utils.ChatUtils.modMessage
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import com.mojang.brigadier.arguments.StringArgumentType  
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

object SXPDevCommand {
    fun registerClient() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, registryAccess ->
            val aliases = listOf("sxpdev", "slayerxpoverlaydev", "sxpoverlaydev")
            aliases.forEach { alias ->
                dispatcher.register(
                    ClientCommandManager.literal(alias)
                        .then(
                            ClientCommandManager.argument("devSetting", StringArgumentType.word())
                                .then(
                                    ClientCommandManager.argument("debug", StringArgumentType.word())
                                        .executes { context -> executeClient(context) }
                                )
                        )
                )
            }
        }
    }
    
    private fun executeClient(context: CommandContext<FabricClientCommandSource>): Int {
        val devSetting = StringArgumentType.getString(context, "devSetting")
        val debug = StringArgumentType.getString(context, "debug")
        
        if (devSetting == "getXP") {
            APIUtils.getXP()
            modMessage("getting xp")
        }
        if (devSetting == "test1") {
            FeatureManager.loadAllFeatureStates()
        }
        if (devSetting == "test2") { config.toggle(debug) }
        if (devSetting == "getslayer") { getSlayer() }
        
        modMessage("/sxpdev devsetting:$devSetting debug:$debug")
        return 1
    }
}