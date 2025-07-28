package com.slayerxp.overlay

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.slayerxp.overlay.commands.CommandsManager
import com.slayerxp.overlay.ui.Overlay
import com.slayerxp.overlay.settings.impl.Overlay as OverlayModule
import com.slayerxp.overlay.settings.config
import com.slayerxp.overlay.utils.ChatUtils.modMessage
import com.slayerxp.overlay.utils.APIUtils.getXP

object Slayerxpoverlay : ModInitializer {
    private val logger = LoggerFactory.getLogger("slayerxpoverlay")

    override fun onInitialize() {
        CommandsManager.registerCommands()
        ClientPlayConnectionEvents.JOIN.register(ClientPlayConnectionEvents.Join { handler, sender, client ->
            try {
                if (config.isToggled("firstTimeInstall")) {
                    logger.debug("First time install flag already set, skipping welcome message.")
                    getXP()
                    return@Join
                }
                sendWelcomeMessages()
                config.setToggle("firstTimeInstall", true)
                logger.info("Welcome message sent and firstTimeInstall toggled to true.")

            } catch (ex: Exception) {
                logger.error("Error during SlayerXPOverlayFabric first time install check: ", ex)
            }
        })
        HudRenderCallback.EVENT.register { drawContext: DrawContext, tickCounter: RenderTickCounter ->
            if (OverlayModule.enabled) {
                Overlay.draw(drawContext)
            } else {
                //empty
            }
        }
    }
    fun sendWelcomeMessages() {
        val border = "-".repeat(53)
        modMessage(border)
        modMessage("Thank you for installing SlayerXPOverlayFabric!")
        modMessage("Credits: oblongboot (and Februari10 for the help!)")
        modMessage("GitHub: https://github.com/oblongboot/SlayerXPOverlayFabric")
        modMessage(border)
    }
}