package dev.oblongboot.sxp

import net.fabricmc.api.ModInitializer
import dev.oblongboot.sxp.events.EventManager.EVENT_BUS
import dev.oblongboot.sxp.settings.impl.onMessage.Companion as MessageCompanion
import dev.oblongboot.sxp.utils.APIUtils
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.DeltaTracker
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
import dev.oblongboot.sxp.utils.skia.SkijaRenderer
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.minecraft.resources.Identifier

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
        EVENT_BUS.subscribe(dev.oblongboot.sxp.ui.SettingsScreen())
        
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
                            val updateAvailable = dev.oblongboot.sxp.utils.UpdateChecker.isUpdateAvailable("1.2.2")
                            if (updateAvailable) {
                                Minecraft.getInstance().execute {
                                    modMessage(
                                        "A new version of SlayerXPOverlayFabric is available! " +
                                        "You are running version v1.2.2. " +
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
        HudElementRegistry.attachElementBefore(
            VanillaHudElements.CHAT,
            Identifier.fromNamespaceAndPath("sxp", "huds")
        ) { _, _ ->
            val mc = Minecraft.getInstance()
            val sw = mc.window.guiScaledWidth.toFloat()
            val sh = mc.window.guiScaledHeight.toFloat()

            SkijaRenderer.beginFrame(sw, sh)
            if (SkijaRenderer.isDrawing) {
                try {
                    if (OverlayModule.enabled)    XPOverlay.draw()
                    if (KPHModule.enabled)        KPHOverlay.draw()
                    if (BVOverlayModule.enabled)  BVOverlay.draw()
                } finally {
                    SkijaRenderer.endFrame()
                }
            }
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
        Minecraft.getInstance().execute {
            modMessage(border)
            modMessage("Thank you for installing SlayerXPOverlayFabric!")
            modMessage("Credits: oblongboot (and Februari10 for the help!)")
            modMessage("GitHub: https://github.com/oblongboot/SlayerXPOverlayFabric")
            modMessage(border)
        }
    }
}
