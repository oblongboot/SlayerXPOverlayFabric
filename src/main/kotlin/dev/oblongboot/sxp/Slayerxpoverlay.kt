package dev.oblongboot.sxp

import net.fabricmc.api.ModInitializer
import dev.oblongboot.sxp.events.EventManager.EVENT_BUS
import dev.oblongboot.sxp.settings.impl.onMessage.Companion as MessageCompanion
import dev.oblongboot.sxp.utils.APIUtils
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles
import dev.oblongboot.sxp.settings.impl.onMessage
import dev.oblongboot.sxp.commands.CommandsManager
import dev.oblongboot.sxp.ui.XPOverlay
import dev.oblongboot.sxp.ui.KPHOverlay
import dev.oblongboot.sxp.ui.BVOverlay
import dev.oblongboot.sxp.settings.impl.KPHOverlay as KPHModule
import dev.oblongboot.sxp.settings.impl.Overlay as OverlayModule
import dev.oblongboot.sxp.settings.impl.BVOverlay as BVOverlayModule
import dev.oblongboot.sxp.settings.Config
import dev.oblongboot.sxp.settings.FeatureManager
import dev.oblongboot.sxp.utils.ChatUtils.modMessage
import kotlinx.coroutines.launch
import dev.oblongboot.sxp.utils.APIUtils.getXP
import dev.oblongboot.sxp.utils.ChatUtils.isGradient
import dev.oblongboot.sxp.utils.ChatUtils.updatePrefix

object Slayerxpoverlay : ModInitializer {
    private val logger = LoggerFactory.getLogger("slayerxpoverlay")
    const val VERSION = "@@VERSION@@"
    var shouldCheck = true

    override fun onInitialize() {
        EVENT_BUS.registerLambdaFactory("dev.oblongboot.sxp") { lookupInMethod, klass ->
            lookupInMethod.invoke(null, klass, MethodHandles.lookup()) as MethodHandles.Lookup
        }

        FeatureManager.registerFeature(dev.oblongboot.sxp.settings.impl.BossHighlight)
        FeatureManager.registerFeature(dev.oblongboot.sxp.settings.impl.Overlay)
        FeatureManager.registerFeature(dev.oblongboot.sxp.settings.impl.KPHOverlay)
        FeatureManager.registerFeature(dev.oblongboot.sxp.settings.impl.BVOverlay)
        FeatureManager.registerFeature(dev.oblongboot.sxp.settings.impl.Test2)
        FeatureManager.registerFeature(dev.oblongboot.sxp.settings.impl.AutoCallMaddox)

        EVENT_BUS.subscribe(onMessage())
        EVENT_BUS.subscribe(dev.oblongboot.sxp.features.BossHighlightFeat())
        EVENT_BUS.subscribe(dev.oblongboot.sxp.features.AutoCallMaddoxFeat())
        EVENT_BUS.subscribe(dev.oblongboot.sxp.features.MiniBossAlert())
        
        APIUtils.getXP()
        APIUtils.startAutoXPUpdates()
        CommandsManager.registerCommands()
        MessageCompanion.initialize()
        ClientPlayConnectionEvents.JOIN.register { _, _, _ ->
            APIUtils.scope.launch {
                try {
                    if (Config.isToggled("firstTimeInstall")) {
                        logger.debug("First time install flag already set, skipping welcome message.")
                        APIUtils.getXP()
                    } else {
                        sendWelcomeMessages()
                        Config.setToggle("firstTimeInstall", true)
                        logger.info("Welcome message sent and firstTimeInstall toggled to true.")
                    }
                    APIUtils.scope.launch {
                        try {
                            if (!shouldCheck) return@launch
                            shouldCheck = false
                            val updateAvailable = dev.oblongboot.sxp.utils.UpdateChecker.isUpdateAvailable("1.0.0")
                            if (updateAvailable) {
                                MinecraftClient.getInstance().execute {
                                    modMessage(
                                        "A new version of SlayerXPOverlayFabric is available! " +
                                        "You are running version v1.0.0. " +
                                        "Please check the GitHub page for the latest version."
                                    )
                                }
                            }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }

                } catch (ex: Exception) {
                    logger.error("Error during SlayerXPOverlayFabric first time install check: ", ex)
                }
            }
        }
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
//            if (BVOverlayModule.enabled) {
//                BVOverlay.draw(drawContext)
//            } else {
//                //empty
//            }
            BVOverlay.draw(drawContext)
        }

        // Random Prefix Color Shit
        // Keep this at the bottom because it isn't very important
        // Add important stuff above this if needed
        updatePrefix()
        isGradient = Config.isToggled("IsGradient")

    APIUtils.scope.launch {
        APIUtils.fetchContributors()
    }

    }
    private fun sendWelcomeMessages() {
        val border = "-".repeat(53)
        MinecraftClient.getInstance().execute {
            modMessage(border)
            modMessage("Thank you for installing SlayerXPOverlayFabric!")
            modMessage("Credits: oblongboot (and Februari10 for the help!)")
            modMessage("GitHub: https://github.com/oblongboot/SlayerXPOverlayFabric")
            modMessage(border)
        }
    }
}
