package com.slayerxp.overlay.ui

import com.slayerxp.overlay.core.Element
import com.slayerxp.overlay.core.SwitchConfig
import com.slayerxp.overlay.core.DropdownSetting
import com.slayerxp.overlay.core.ButtonSetting
import com.slayerxp.overlay.core.CheckboxSetting
//import com.slayerxp.overlay.features.AutoCallMaddoxFeat
import com.slayerxp.overlay.settings.FeatureManager
import com.slayerxp.overlay.settings.Config
import com.slayerxp.overlay.core.ColorboxSetting
import com.slayerxp.overlay.utils.Scheduler
import com.slayerxp.overlay.utils.ChatUtils.updatePrefix
import com.slayerxp.overlay.utils.ChatUtils.isGradient
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import com.slayerxp.overlay.utils.Render2D
import net.minecraft.client.gui.Click
import net.minecraft.client.input.KeyInput
import java.awt.Color
import kotlin.math.sin
import kotlin.math.cos
import kotlin.random.Random

class SettingsScreen : Screen(Text.of("SlayerXPOverlay Config")) {

    companion object {
        fun open() {
            Scheduler.scheduleTask(1) {
                MinecraftClient.getInstance().setScreen(SettingsScreen())
            }
        }
    }

    private val elements = mutableListOf<Element>()
    private val elementHeight = 25
    private val elementSpacing = 5
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

    init {
        setupCategories()
        selectedCategory = categories.firstOrNull()
        selectedCategory?.let { updateElementsForCategory(it.name) }
    }

    private fun renderParticles(ctx: DrawContext) {
        val width = Render2D.scaledWidth
        val height = Render2D.scaledHeight

        for ((index, particle) in particles.withIndex()) {
            val x = ((sin(animationTime * 0.0003 * particle.speed + index + particle.phase) * 0.5 + 0.5) * width).toInt()
            val y = ((cos(animationTime * 0.0004 * particle.speed + index * 0.5 + particle.phase) * 0.5 + 0.5) * height).toInt()
            val alpha = ((sin(animationTime * 0.0008 + index) * 0.5 + 0.5) * 255).toInt().coerceIn(50, 255)
            val color = Color(0, 100, particle.blueIntensity, alpha)
            Render2D.drawRect(ctx, x, y, particle.size.toInt(), particle.size.toInt(), color)
        }
    }

    private fun updateElementsForCategory(name: String) {
        elements.clear()
        var yPos = 100

        when (name) {
            "General" -> {
                val overlaySwitch = SwitchConfig(
                    name = "Overlay",
                    default = false,
                    description = "Shows Slayer XP in a movable overlay"
                ).apply {
                    x = sidebarWidth + 20
                    y = yPos
                }
                elements.add(overlaySwitch)
                yPos += elementHeight + elementSpacing

                val kphSwitch = SwitchConfig(
                    name = "KPHOverlay",
                    default = false,
                    description = "Shows slayer kills per hour in a movable overlay"
                ).apply {
                    x = sidebarWidth + 20
                    y = yPos
                }
                elements.add(kphSwitch)
                yPos += elementHeight + elementSpacing

                val openOtherGUI = ButtonSetting(
                    name = "Open Overlay Manager",
                    description = "Opens the overlay manager",
                    onClickAction = {
                        OverlayManager.open()
                    }
                ).apply {
                    x = sidebarWidth + 20
                    y = yPos
                }
                elements.add(openOtherGUI)
                yPos += elementHeight + elementSpacing
                

                val bossInfoCheckbox = CheckboxSetting(
                    name = "BossInfoCheckbox",
                    options = listOf(
                        "XP",
                        "Kills",
                        "Time",
                        "KPH"
                    ),
                    defaultSelected = setOf(0, 2)
                ).apply {
                    x = sidebarWidth + 20
                    y = yPos
                }
                elements.add(bossInfoCheckbox)
                yPos += elementHeight + elementSpacing + 20


                val shortPrefix = SwitchConfig(
                    name = "ShortPrefix",
                    default = false,
                    description = "Changes the prefix from SlayerXPOverlay to SXP",
                    onValueChangeAction = {
                        updatePrefix()
                    }
                ).apply {
                    x = sidebarWidth + 20
                    y = yPos
                }
                elements.add(shortPrefix)
                yPos += elementHeight + elementSpacing + 60
            }
            "General QOL" -> {
                val autoCallMaddox = SwitchConfig(
                    name = "AutoCallMaddox",
                    default = false
                ).apply {
                    x = sidebarWidth +20
                    y = yPos
                }
                elements.add(autoCallMaddox)
                
                val HighlightsToggle = SwitchConfig(
                    name = "BossHighlight",
                    default = false,
                    description = "Highlight bosses!!!"
                ).apply {
                    x = sidebarWidth + 20
                    y = yPos
                }
                elements.add(HighlightsToggle)
                yPos += elementHeight + elementSpacing
            }


            "KPH" -> {
                val detailedSwitch = SwitchConfig(
                    name = "testSwitch",
                    default = false,
                    description = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaneeedmakelongsoicantestifthetextgoesweirdaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                ).apply {
                    x = sidebarWidth + 20
                    y = yPos
                }
                elements.add(detailedSwitch)
            }

            "Blaze" -> {
                val BVDamage = SwitchConfig(
                    name = "BurningVengeanceDamage",
                    default = false,
                    description = "Says the Damage of your first Burning Vengeance Activation in chat"
                ).apply {
                    x = sidebarWidth + 20
                    y = yPos
                }
                elements.add(BVDamage)
                yPos += elementHeight + elementSpacing + 60

                val BVTimer = SwitchConfig(
                    name = "BurningVengeanceTimer",
                    default = false,
                    description = "Counts down the time until Burning Vengeance Activates"
                ).apply {
                    x = sidebarWidth + 20
                    y = yPos
                }
                elements.add(BVTimer)
            }

            "Colors" -> {
                val messageColorSelector1 = ColorboxSetting(
                    name = "MessageColorSelector1",
                    defaultColor = Color(0, 255, 255),
                    description = "Start Color for the chat message gradient"
                ).apply {
                    x = sidebarWidth + 20
                    y = yPos
                }
                elements.add(messageColorSelector1)
                yPos += elementHeight + elementSpacing

                val messageColorSelector2 = ColorboxSetting(
                    name = "MessageColorSelector2",
                    defaultColor = Color(0, 0, 255),
                    description = "End Color for the chat message gradient"
                ).apply {
                    x = sidebarWidth + 270
                    y = yPos - 30
                }
                elements.add(messageColorSelector2)
                yPos += elementHeight + elementSpacing

                val gradientSwitch = SwitchConfig(
                    name = "IsGradient",
                    default = false,
                    description = "Sends the message in a gradient",
                    onValueChangeAction = {
                        isGradient = Config.isToggled("IsGradient");
                    }
                ).apply {
                    x = sidebarWidth + 20;
                    y = yPos
                }
                elements.add(gradientSwitch)
                yPos += elementHeight + elementSpacing
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
        categories.clear()
        var yPos = 40
        val catNames = listOf("General", "General QOL", "KPH", "Blaze", "Colors")

        catNames.forEachIndexed { index, name ->
            categories.add(
                Category(
                    name = name,
                    x = 7,
                    y = yPos,
                    width = sidebarWidth - 14,
                    height = 32,
                    selected = index == 0
                )
            )
            yPos += 37
        }
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        context.fill(0, 0, sidebarWidth, height, Color(0, 0, 0, 128).rgb)
        context.fill(sidebarWidth - 1, 0, sidebarWidth, height, Color(40, 40, 50, 255).rgb)
        context.fill(sidebarWidth, 0, width, height, Color(40, 60, 120, 150).rgb)

        val title = "§bSlayerXPOverlay §3Config"
        val titleWidth = textRenderer.getWidth(title)
        context.drawTextWithShadow(textRenderer, title, (sidebarWidth - titleWidth) / 2, 20, Color.WHITE.rgb)

        categories.forEach { cat ->
            val bgColor = if (cat.selected) Color(0, 120, 255, 255).rgb else Color(30, 60, 120, 200).rgb
            context.fill(cat.x, cat.y, cat.x + cat.width, cat.y + cat.height, bgColor)

            val textX = cat.x + (cat.width - textRenderer.getWidth(cat.name)) / 2
            val textY = cat.y + (cat.height - textRenderer.fontHeight) / 2
            context.drawTextWithShadow(textRenderer, cat.name, textX, textY, Color.WHITE.rgb)
        }

        renderParticles(context)

        elements.forEach { element ->
            element.render(context)
        }

        var instructionY = height - 80
        listOf(
            "Click toggles to enable/disable features",
            "Press 'O' to open overlay manager",
            "Press ESC to close this menu"
        ).forEach {
            context.drawTextWithShadow(textRenderer, it, 15, instructionY, Color.GRAY.rgb)
            instructionY += 15
        }

        super.render(context, mouseX, mouseY, delta)
    }

    override fun mouseClicked(click: Click, doubled: Boolean): Boolean {
        if (click.button() == 0) {
            categories.forEach { cat ->
                if (cat.contains(click.x.toInt(), click.y.toInt())) {
                    categories.forEach { it.selected = false }
                    cat.selected = true
                    selectedCategory = cat
                    updateElementsForCategory(cat.name)
                    return true
                }
            }

            elements.forEach { element ->
                if (element.onClick(click.x.toInt(), click.y.toInt())) return true
            }
        }
        return super.mouseClicked(click, doubled)
    }

    override fun keyPressed(input: KeyInput): Boolean {
        when (input.keycode) {
            GLFW.GLFW_KEY_ESCAPE -> {
                close()
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
        elements.forEach { element ->
            if (element is DropdownSetting && element.onScroll(mouseX.toInt(), mouseY.toInt(), verticalAmount)) {
                return true
            }
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
    }

    override fun shouldPause() = false
    override fun shouldCloseOnEsc() = true
    override fun renderBackground(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {}

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
