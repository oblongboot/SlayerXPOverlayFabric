package com.slayerxp.overlay.ui

import com.slayerxp.overlay.core.Element
import com.slayerxp.overlay.core.SwitchConfig
import com.slayerxp.overlay.core.DropdownSetting
import com.slayerxp.overlay.core.ButtonSetting
import com.slayerxp.overlay.settings.config
import com.slayerxp.overlay.utils.ChatUtils.modMessage
import com.slayerxp.overlay.settings.FeatureManager
import com.slayerxp.overlay.utils.Scheduler
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import com.slayerxp.overlay.utils.Render2D
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

    private val particles = List(50) { index ->
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

                val buttonthing = ButtonSetting(
                    name = "Test Button",
                    description = "This is a test button !!!",
                    onClickAction = {
                        modMessage("Button Clicked!!!!")
                    }
                ).apply {
                    x = sidebarWidth + 20
                    y = yPos
                }
                elements.add(buttonthing)
                yPos += elementHeight + elementSpacing

                val dropDownSettingTest = DropdownSetting(
                    name = "BossInfoDropdown",
                    options = listOf("XP", "Kills", "Time", "KPH", "XP + Kills", "XP + Time", "XP + KPH", "Kills + Time", "Kills + KPH", "Time + KPH", "XP + Kills + Time", "XP + Kills + KPH", "XP + Time + KPH", "Kills + Time + KPH", "XP + Kills + Time + KPH"),
                    defaultIndex = 14,
                    description = "Information to show in the chat on boss kill."
                ).apply {
                    x = sidebarWidth + 20
                    y = yPos
                }
                elements.add(dropDownSettingTest)
                yPos += elementHeight + elementSpacing + 60
            }

            "Overlay" -> {
                val colorSwitch = SwitchConfig(
                    name = "test2",
                    default = true,
                    description = "hi vro"
                ).apply {
                    x = sidebarWidth + 20
                    y = yPos
                }
                elements.add(colorSwitch)
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

            "Other" -> {
                val debugSwitch = SwitchConfig(
                    name = "DebugMode",
                    default = false,
                    description = "Enable debug shi"
                ).apply {
                    x = sidebarWidth + 20
                    y = yPos
                }
                elements.add(debugSwitch)
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
            }
        }
    }

    private fun setupCategories() {
        categories.clear()
        var yPos = 40
        val catNames = listOf("General", "Overlay", "KPH", "Other")

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

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0) {
            categories.forEach { cat ->
                if (cat.contains(mouseX.toInt(), mouseY.toInt())) {
                    categories.forEach { it.selected = false }
                    cat.selected = true
                    selectedCategory = cat
                    updateElementsForCategory(cat.name)
                    return true
                }
            }

            elements.forEach { element ->
                if (element.onClick(mouseX.toInt(), mouseY.toInt())) return true
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        when (keyCode) {
            GLFW.GLFW_KEY_ESCAPE -> {
                close()
                return true
            }
            GLFW.GLFW_KEY_O -> {
                OverlayManager.open()
                return true
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
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
    fun contains(mouseX: Int, mouseY: Int) =
        mouseX in x until (x + width) && mouseY in y until (y + height)
}