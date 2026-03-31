package dev.oblongboot.sxp.ui

import dev.oblongboot.sxp.core.Element
import dev.oblongboot.sxp.core.SwitchConfig
import dev.oblongboot.sxp.core.DropdownSetting
import dev.oblongboot.sxp.core.ButtonSetting
import dev.oblongboot.sxp.core.CheckboxSetting
//import dev.oblongboot.sxp.features.AutoCallMaddoxFeat
import dev.oblongboot.sxp.settings.FeatureManager
import dev.oblongboot.sxp.settings.Config
import dev.oblongboot.sxp.core.ColorboxSetting
import dev.oblongboot.sxp.events.impl.SkiaDrawEvent
import dev.oblongboot.sxp.utils.Scheduler
import dev.oblongboot.sxp.utils.ChatUtils.updatePrefix
import dev.oblongboot.sxp.utils.ChatUtils.isGradient
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import org.lwjgl.glfw.GLFW
import dev.oblongboot.sxp.utils.Render2D
import dev.oblongboot.sxp.utils.skia.SkijaRenderer
import meteordevelopment.orbit.EventHandler
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.input.KeyEvent
import java.awt.Color
import kotlin.math.sin
import kotlin.math.cos
import kotlin.random.Random

class SettingsScreen : Screen(Component.nullToEmpty("SlayerXPOverlay Config")) {

    companion object {
        val titleFont by lazy { io.github.humbleui.skija.Font(io.github.humbleui.skija.FontMgr.getDefault().matchFamilyStyle(null, io.github.humbleui.skija.FontStyle.NORMAL), 28f) }
        val catFont by lazy { io.github.humbleui.skija.Font(io.github.humbleui.skija.FontMgr.getDefault().matchFamilyStyle(null, io.github.humbleui.skija.FontStyle.NORMAL), 16f) }
        val elementFont by lazy { io.github.humbleui.skija.Font(io.github.humbleui.skija.FontMgr.getDefault().matchFamilyStyle(null, io.github.humbleui.skija.FontStyle.NORMAL), 14f) }
        val smallFont by lazy { io.github.humbleui.skija.Font(io.github.humbleui.skija.FontMgr.getDefault().matchFamilyStyle(null, io.github.humbleui.skija.FontStyle.NORMAL), 11f) }

        fun open() {
            Scheduler.scheduleTask(1) {
                Minecraft.getInstance().setScreen(SettingsScreen())
            }
        }
    }

    private val elements = mutableListOf<Element>()
    private val elementHeight = 25
    private val elementSpacing = 10
    private val sidebarWidth = 200
    private val categories = mutableListOf<Category>()
    private var selectedCategory: Category? = null
    private val startTime = System.currentTimeMillis()

    private val particles = List(50) { _ ->
        Particle(
            Random.nextDouble(0.1, 0.3),
            Random.nextFloat() * 3 + 1f,
            Random.nextInt(150, 255),
            Random.nextDouble(0.0, Math.PI * 2)
        )
    }

    private val animationTime: Double
        get() = (System.currentTimeMillis() - startTime).toDouble()

    private var dialogX = 0f
    private var dialogY = 0f
    private var dialogW = 0f
    private var dialogH = 0f
    private val sidebarW = 160f
    private val animation = UIBounceAnimation(400)

    private val DESIGN_WIDTH = 960f
    private val DESIGN_HEIGHT = 540f

    override fun init() {
        animation.start()
        super.init()

        dialogW = 800f
        dialogH = 500f
        dialogX = (DESIGN_WIDTH - dialogW) / 2f
        dialogY = (DESIGN_HEIGHT - dialogH) / 2f
        
        setupCategories()
        val targetCatName = selectedCategory?.name ?: categories.firstOrNull()?.name
        targetCatName?.let { updateElementsForCategory(it) }
    }


    private fun updateElementsForCategory(name: String) {
        elements.clear()
        
        val contentX = dialogX + sidebarW + 15f
        var currentY = dialogY + 80f

        when (name) {
            "General" -> {
                val overlaySwitch = SwitchConfig(
                    name = "Overlay",
                    default = false,
                    description = "Shows Slayer XP in a movable overlay"
                ).apply {
                    x = contentX.toInt()
                    y = currentY.toInt()
                }
                elements.add(overlaySwitch)
                currentY += elementHeight + elementSpacing

                val kphSwitch = SwitchConfig(
                    name = "KPHOverlay",
                    default = false,
                    description = "Shows slayer kills per hour in a movable overlay"
                ).apply {
                    x = contentX.toInt()
                    y = currentY.toInt()
                }
                elements.add(kphSwitch)
                currentY += elementHeight + elementSpacing

                val openOtherGUI = ButtonSetting(
                    name = "Open Overlay Manager",
                    description = "Opens the overlay manager",
                    onClickAction = { OverlayManager.open() }
                ).apply {
                    x = contentX.toInt()
                    y = currentY.toInt()
                }
                elements.add(openOtherGUI)
                currentY += elementHeight + elementSpacing
                
                val shortPrefix = SwitchConfig(
                    name = "ShortPrefix",
                    default = false,
                    description = "Changes the prefix from SlayerXPOverlay to SXP",
                    onValueChangeAction = { updatePrefix() }
                ).apply {
                    x = contentX.toInt()
                    y = currentY.toInt()
                }
                elements.add(shortPrefix)
                currentY += elementHeight + elementSpacing

                val bossInfoCheckbox = CheckboxSetting(
                    name = "BossInfoCheckbox",
                    options = listOf("XP", "Kills", "Time", "KPH"),
                    defaultSelected = setOf(0, 2)
                ).apply {
                    x = contentX.toInt()
                    y = currentY.toInt()
                }
                elements.add(bossInfoCheckbox)
                currentY += elementHeight + elementSpacing
            }
            "General QOL" -> {
                val autoCallMaddox = SwitchConfig(
                    name = "AutoCallMaddox",
                    default = false
                ).apply {
                    x = contentX.toInt()
                    y = currentY.toInt()
                }
                elements.add(autoCallMaddox)
            }


//            "Blaze" -> {
//                val BVDamage = SwitchConfig(
//                    name = "BurningVengeanceDamage",
//                    default = false,
//                    description = "Says the Damage of your first Burning Vengeance Activation in chat"
//                ).apply {
//                    x = sidebarWidth + 20
//                    y = yPos
//                }
//                elements.add(BVDamage)
//                yPos += elementHeight + elementSpacing + 60
//
//                val BVTimer = SwitchConfig(
//                    name = "BurningVengeanceTimer",
//                    default = false,
//                    description = "Counts down the time until Burning Vengeance Activates"
//                ).apply {
//                    x = sidebarWidth + 20
//                    y = yPos
//                }
//                elements.add(BVTimer)
//            }

            "Colors" -> {
                val messageColorSelector1 = ColorboxSetting(
                    name = "MessageColorSelector1",
                    defaultColor = Color(0, 255, 255),
                    description = "Start Color for chat msgs"
                ).apply {
                    x = contentX.toInt()
                    y = currentY.toInt()
                }
                elements.add(messageColorSelector1)

                val messageColorSelector2 = ColorboxSetting(
                    name = "MessageColorSelector2",
                    defaultColor = Color(0, 0, 255),
                    description = "End Color for chat msgs"
                ).apply {
                    x = (contentX + 250f).toInt()
                    y = currentY.toInt() 
                }
                elements.add(messageColorSelector2)
                currentY += elementHeight + elementSpacing + 150f

                val gradientSwitch = SwitchConfig(
                    name = "IsGradient",
                    default = true,
                    description = "Sends the message in a gradient",
                    onValueChangeAction = { isGradient = Config.isToggled("IsGradient") }
                ).apply {
                    x = contentX.toInt()
                    y = currentY.toInt()
                }
                elements.add(gradientSwitch)
                currentY += elementHeight + elementSpacing
            }
        }

        val configStates = FeatureManager.getAllConfigStates()
        elements.forEach { element ->
            when (element) {
                is SwitchConfig -> {
                    val currentState = configStates[element.name] as? Boolean ?: element.default
                    element.setValueSilently(currentState)
                }
                is DropdownSetting -> {
                    val currentState = configStates[element.name] as? Int ?: element.default
                    element.value = currentState
                }
                is ColorboxSetting -> {
                    val currentColor = Config.getColor(element.name, element.default)
                    element.svs(currentColor)
                }
            }
        }
    }

    private fun setupCategories() {
        val previousSelectedCategory = selectedCategory?.name ?: categories.firstOrNull()?.name
        categories.clear()
        
        var currentY = dialogY + 80f
        val catNames = listOf("General", "General QOL", "Colors")

        catNames.forEachIndexed { index, name ->
            categories.add(
                Category(
                    name = name,
                    x = (dialogX + 10f).toInt(),
                    y = (currentY - 10f).toInt(),
                    width = sidebarW.toInt() - 20,
                    height = 32,
                    selected = (name == previousSelectedCategory || (previousSelectedCategory == null && index == 0))
                )
            )
            currentY += 40f
        }
        
        selectedCategory = categories.find { it.selected } ?: categories.firstOrNull()
        selectedCategory?.selected = true
    }

    @EventHandler
    fun onSkijaRender(event: SkiaDrawEvent) {
        if (minecraft.screen != this) {
            return
        }

        val window = Minecraft.getInstance().window
        val sw = window.guiScaledWidth.toFloat()
        val sh = window.guiScaledHeight.toFloat()
        val scaleX = sw / DESIGN_WIDTH
        val scaleY = sh / DESIGN_HEIGHT
        val finalUIScale = kotlin.math.min(scaleX, scaleY)
        val offsetX = (sw - DESIGN_WIDTH * finalUIScale) / 2f
        val offsetY = (sh - DESIGN_HEIGHT * finalUIScale) / 2f
        val designMouseX = ((Render2D.Mouse.x - offsetX) / finalUIScale).toInt()
        val designMouseY = ((Render2D.Mouse.y - offsetY) / finalUIScale).toInt()

        SkijaRenderer.beginFrame(sw, sh)
        if (!SkijaRenderer.isDrawing) return

        try {
            SkijaRenderer.drawBackdropBlur(0f, 0f, sw, sh, 0f, 20f, 0.5f)
            SkijaRenderer.drawRoundedRect(0f, 0f, sw, sh, 0f, SkijaRenderer.argb(160, 5, 10, 15))

            val bounceScale = animation.get()

            val centerX = dialogX + dialogW / 2f
            val centerY = dialogY + dialogH / 2f

            SkijaRenderer.save()
            SkijaRenderer.translate(offsetX, offsetY)
            SkijaRenderer.scale(finalUIScale, finalUIScale)
            SkijaRenderer.translate(centerX, centerY)
            SkijaRenderer.scale(bounceScale, bounceScale)
            SkijaRenderer.translate(-centerX, -centerY)
            
            SkijaRenderer.drawRoundedRect(dialogX, dialogY, dialogW, dialogH, 10f, SkijaRenderer.argb(230, 15, 20, 30))

            SkijaRenderer.drawRoundedRectBorderGradient(dialogX, dialogY, dialogW, dialogH, 10f, 1f, SkijaRenderer.argb(120, 0, 100, 200), SkijaRenderer.argb(80, 0, 50, 120), SkijaRenderer.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT)

            val sbX = dialogX
            SkijaRenderer.drawRoundedRect(sbX, dialogY, sidebarW, dialogH, 10f, SkijaRenderer.argb(60, 10, 15, 25))

            val title = "SXP"
            val titleW = SkijaRenderer.getTextWidth(title, titleFont)
            SkijaRenderer.drawText(title, sbX + (sidebarW - titleW) / 2f, dialogY + 30f, SkijaRenderer.argb(255, 240, 245, 255), titleFont)


            categories.forEach { cat ->
                val cx = cat.x.toFloat()
                val cy = cat.y.toFloat()
                val cw = cat.width.toFloat()
                val ch = cat.height.toFloat()

                if (cat.selected) {
                    SkijaRenderer.drawRoundedGlow(cx, cy, cw, ch, 6f, SkijaRenderer.argb(50, 0, 120, 255), 10f)
                    SkijaRenderer.drawRoundedRectGradient(cx, cy, cw, ch, 6f, SkijaRenderer.argb(180, 0, 90, 200), SkijaRenderer.argb(180, 0, 60, 150))
                } else {
                    val isHovered = cat.contains(designMouseX, designMouseY)
                    val bgColor = if (isHovered) SkijaRenderer.argb(100, 30, 60, 100) else SkijaRenderer.argb(40, 20, 30, 50)
                    SkijaRenderer.drawRoundedRect(cx, cy, cw, ch, 6f, bgColor)
                }

                val tw = SkijaRenderer.getTextWidth(cat.name, catFont)
                val tx = cx + (cw - tw) / 2f
                val ty = cy + ch / 2f - 7f
                val tc = if (cat.selected) SkijaRenderer.argb(255, 255, 255, 255) else SkijaRenderer.argb(200, 200, 200, 220)
                SkijaRenderer.drawText(cat.name, tx, ty, tc, catFont)
            }

            elements.forEach { it.render(designMouseX, designMouseY) }

//            var iy = dialogY + dialogH - 45f
//            listOf(
//                "Click toggles to enable/disable features",
//                "Press 'O' to open overlay manager",
//                "Press ESC to close"
//            ).forEach {
//                SkijaRenderer.drawText(it, dialogX + 15f, iy, SkijaRenderer.argb(150, 200, 200, 200), smallFont)
//                iy += 15f
//            }

            SkijaRenderer.restore()

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            SkijaRenderer.endFrame()
        }
    }

    override fun mouseClicked(click: MouseButtonEvent, doubled: Boolean): Boolean {
        val window = Minecraft.getInstance().window
        val sw = window.guiScaledWidth.toFloat()
        val sh = window.guiScaledHeight.toFloat()
        val finalUIScale = kotlin.math.min(sw / DESIGN_WIDTH, sh / DESIGN_HEIGHT)
        val offsetX = (sw - DESIGN_WIDTH * finalUIScale) / 2f
        val offsetY = (sh - DESIGN_HEIGHT * finalUIScale) / 2f

        val designMouseX = ((click.x - offsetX) / finalUIScale).toInt()
        val designMouseY = ((click.y - offsetY) / finalUIScale).toInt()

        if (click.button() == 0) {
            categories.forEach { cat ->
                if (cat.contains(designMouseX, designMouseY)) {
                    categories.forEach { it.selected = false }
                    cat.selected = true
                    selectedCategory = cat
                    updateElementsForCategory(cat.name)
                    return true
                }
            }

            elements.forEach { element ->
                if (element.onClick(designMouseX, designMouseY)) return true
            }
        }
        return super.mouseClicked(click, doubled)
    }

    override fun keyPressed(input: KeyEvent): Boolean {
        when (input.input()) {
            GLFW.GLFW_KEY_ESCAPE -> {
                onClose()
                return true
            }
            GLFW.GLFW_KEY_O -> {
                OverlayManager.open()
                return true
            }
        }
        return super.keyPressed(input)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean {
        val window = Minecraft.getInstance().window
        val sw = window.guiScaledWidth.toFloat()
        val sh = window.guiScaledHeight.toFloat()
        val finalUIScale = kotlin.math.min(sw / DESIGN_WIDTH, sh / DESIGN_HEIGHT)
        val offsetX = (sw - DESIGN_WIDTH * finalUIScale) / 2f
        val offsetY = (sh - DESIGN_HEIGHT * finalUIScale) / 2f

        val designMouseX = ((mouseX - offsetX) / finalUIScale).toInt()
        val designMouseY = ((mouseY - offsetY) / finalUIScale).toInt()

        elements.forEach { element ->
            if (element is DropdownSetting && element.onScroll(designMouseX, designMouseY, verticalAmount)) {
                return true
            }
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
    }

    override fun isPauseScreen() = false
    override fun shouldCloseOnEsc() = true
   // override fun renderBackground(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {}

    private data class Particle(
        val speed: Double,
        val size: Float,
        val blueIntensity: Int,
        val phase: Double
    )
}

data class Category(
    val name: String,
    var x: Int,
    var y: Int,
    var width: Int,
    var height: Int,
    var selected: Boolean = false
) {
    fun contains(mouseX: Int, mouseY: Int) = mouseX in x until (x + width) && mouseY in y until (y + height)
}
