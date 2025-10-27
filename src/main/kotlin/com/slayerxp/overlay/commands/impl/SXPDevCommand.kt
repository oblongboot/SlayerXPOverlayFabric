package com.slayerxp.overlay.commands.impl

import com.slayerxp.overlay.utils.APIUtils
import com.slayerxp.overlay.settings.Config
import com.slayerxp.overlay.utils.Scoreboard
import com.slayerxp.overlay.ui.SettingsScreen.Companion.open as bleh
import com.slayerxp.overlay.settings.FeatureManager
import com.slayerxp.overlay.utils.ChatUtils.modMessage
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import com.mojang.brigadier.arguments.StringArgumentType  
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

// Temp remove later
import com.slayerxp.overlay.utils.getArmorStands

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
        if (devSetting == "test2") { Config.toggle(debug) }
        if (devSetting == "scoreboarddebug") { val e = Scoreboard.getSlayerType(); modMessage("Scoreboard area: $e") }
        if (devSetting == "gui") {bleh()}
        if (devSetting == "armorstand") {
            val armorStands = getArmorStands()
            for (stand in armorStands) {
                println(stand.toString());
            }
        }
        
        modMessage("/sxpdev devsetting:$devSetting debug:$debug")
        return 1
    }
}