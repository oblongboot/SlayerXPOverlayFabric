package dev.oblongboot.sxp.commands.impl

import dev.oblongboot.sxp.utils.APIUtils
import dev.oblongboot.sxp.settings.Config
import dev.oblongboot.sxp.utils.Scoreboard
import dev.oblongboot.sxp.utils.Scheduler
import dev.oblongboot.sxp.ui.SettingsScreen.Companion.open as bleh
import dev.oblongboot.sxp.settings.FeatureManager
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Style
import dev.oblongboot.sxp.utils.ChatUtils.getGradientStyleMessage
import dev.oblongboot.sxp.settings.impl.onMessage
import dev.oblongboot.sxp.utils.ChatUtils.modMessage
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import com.mojang.brigadier.arguments.StringArgumentType  
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

// Temp remove later
import dev.oblongboot.sxp.utils.getArmorStands

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

        if (devSetting == "simslayer") {
            onMessage.handleSlayerQuestStart(true)
            modMessage("starting sim, ends in 50 ticks")
            Scheduler.scheduleTask(50) {modMessage("completing"); onMessage.handleSlayerQuestComplete(true)}
            //onMessage.handleSlayerQuestComplete()
        }
        if (devSetting == "getXP") {
            APIUtils.getXP()
            modMessage("getting xp")
        }
        return 1
    }
}