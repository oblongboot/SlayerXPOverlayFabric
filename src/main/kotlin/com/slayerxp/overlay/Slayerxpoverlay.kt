package com.slayerxp.overlay

import net.fabricmc.api.ModInitializer
import com.slayerxp.overlay.events.EventManager.EVENT_BUS
import com.slayerxp.overlay.settings.impl.onMessage.Companion as MessageCompanion
import com.slayerxp.overlay.utils.APIUtils
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles
import com.slayerxp.overlay.settings.impl.onMessage
import com.slayerxp.overlay.commands.CommandsManager
import com.slayerxp.overlay.ui.XPOverlay
import com.slayerxp.overlay.ui.KPHOverlay
import com.slayerxp.overlay.settings.impl.KPHOverlay as KPHModule
import com.slayerxp.overlay.settings.impl.Overlay as OverlayModule
import com.slayerxp.overlay.settings.Config
import com.slayerxp.overlay.utils.ChatUtils.modMessage
import com.slayerxp.overlay.utils.APIUtils.getXP

object Slayerxpoverlay : ModInitializer {
    private val logger = LoggerFactory.getLogger("slayerxpoverlay")
    const val VERSION = "@@VERSION@@"

    override fun onInitialize() {
        EVENT_BUS.registerLambdaFactory("com.slayerxp.overlay") { lookupInMethod, klass ->
            lookupInMethod.invoke(null, klass, MethodHandles.lookup()) as MethodHandles.Lookup
        }

        com.slayerxp.overlay.settings.FeatureManager.registerFeature(com.slayerxp.overlay.settings.impl.BossHighlight)
        com.slayerxp.overlay.settings.FeatureManager.registerFeature(com.slayerxp.overlay.settings.impl.Overlay)
        com.slayerxp.overlay.settings.FeatureManager.registerFeature(com.slayerxp.overlay.settings.impl.KPHOverlay)
        com.slayerxp.overlay.settings.FeatureManager.registerFeature(com.slayerxp.overlay.settings.impl.Test2)

        EVENT_BUS.subscribe(onMessage())
        APIUtils.getXP()
        APIUtils.startAutoXPUpdates()
        CommandsManager.registerCommands()
        MessageCompanion.initialize()
        ClientPlayConnectionEvents.JOIN.register(ClientPlayConnectionEvents.Join { _, _, _ ->
            try {
                if (Config.isToggled("firstTimeInstall")) {
                    logger.debug("First time install flag already set, skipping welcome message.")
                    getXP()
                    return@Join
                }
                sendWelcomeMessages()
                Config.setToggle("firstTimeInstall", true)
                logger.info("Welcome message sent and firstTimeInstall toggled to true.")

            } catch (ex: Exception) {
                logger.error("Error during SlayerXPOverlayFabric first time install check: ", ex)
            }
        })
        HudRenderCallback.EVENT.register { drawContext: DrawContext, _: RenderTickCounter ->
            if (OverlayModule.enabled) {
                XPOverlay.draw(drawContext)
            } else {
                //empty
            }
            if (KPHModule.enabled) {
                KPHOverlay.draw(drawContext)
            } else {
                //empty
            }
        }
    }
    ///////////////////val test = APIUtils.requestJson("")//WHY IS HTIS HGERE
    private fun sendWelcomeMessages() {
        val border = "-".repeat(53)
        modMessage(border)
        modMessage("Thank you for installing SlayerXPOverlayFabric!")
        modMessage("Credits: oblongboot (and Februari10 for the help!)")
        modMessage("GitHub: https://github.com/oblongboot/SlayerXPOverlayFabric")
        modMessage(border)
    }
}