package com.slayerxp.overlay.ui

import com.slayerxp.overlay.core.Element
import com.slayerxp.overlay.core.SwitchConfig
import com.slayerxp.overlay.settings.FeatureManager
import com.slayerxp.overlay.utils.Render2D
import com.slayerxp.overlay.utils.Scheduler
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import java.awt.Color

class SettingsScreen : Screen(Text.of("SlayerXPOverlay Config")) {
    
    companion object {
        fun open() {
            Scheduler.scheduleTask(1) {
                MinecraftClient.getInstance().setScreen(SettingsScreen())
            }
        }
    }
    
    private val elements = mutableListOf<Element>()
    private val scrollOffset = 0
    private val elementHeight = 25
    private val elementSpacing = 5
    private val sidebarWidth = 200
    private val categories = mutableListOf<Category>()
    private var selectedCategory: Category? = null
    
    init {
        setupElements()
        setupCategories()
    }

    private fun updateElementsForCategory(name: String) {
        elements.clear()
        when (name) {
            "General" -> { /* add general config elements */ }
            "Overlay" -> { /* add overlay config elements */ }
            "KPH"     -> { /* add kph config elements */ }
            "Other"   -> { /* add other config elements */ }
        }
    }


    private fun setupCategories() {
        categories.clear()
        var yPos = 40
        val catNames = listOf("General", "Overlay", "KPH", "Other")

        catNames.forEachIndexed { index, name ->
            val category = Category(
                name = name,
                x = 7,
                y = yPos,
                width = sidebarWidth - 14,
                height = 32,
                selected = index == 0 
            )
            categories.add(category)
            yPos += category.height + 5
        }

        selectedCategory = categories.first()
    }

    private fun setupElements() {
        elements.clear()
        var yPos = 100
        
        val overlaySwitch = SwitchConfig(
            name = "Overlay",
            default = false,
            description = "Shows Slayer XP in a movable overlay"
        ).apply {
            x = 50
            y = yPos
        }
        elements.add(overlaySwitch)
        yPos += elementHeight + elementSpacing
        
        val kphSwitch = SwitchConfig(
            name = "KPHOverlay", 
            default = false,
            description = "Shows slayer kills per hour in a movable overlay"
        ).apply {
            x = 50
            y = yPos
        }
        elements.add(kphSwitch)
        yPos += elementHeight + elementSpacing
        
        FeatureManager.loadAllFeatureStates()
        elements.forEach { element ->
            if (element is SwitchConfig) {
                val currentState = FeatureManager.getAllConfigStates()[element.name] ?: element.default
                element.value = currentState
            }
        }
    }
    
    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        context.fill(0, 0, sidebarWidth, height, Color(0, 0, 0, 128).rgb)
        context.fill(sidebarWidth - 1, 0, sidebarWidth, height, Color(40, 40, 50, 255).rgb)
        //context.fill(0, 0, width, height, Color(0, 0, 0, 200).rgb)//black background

        val title = "§bSlayerXPOverlay §3Config"
        val titleWidth = textRenderer.getWidth(title)
        val boxWidth = 300
        val boxHeight = 200
        val boxX = (width - boxWidth) / 2
        val boxY = (height - boxHeight) / 2
        context.drawTextWithShadow(textRenderer, title, (sidebarWidth - titleWidth) / 2, 20, Color.WHITE.rgb)


        categories.forEach { cat ->
            val bgColor = if (cat.selected) Color(0, 120, 255, 255).rgb; else Color(30, 60, 120, 200).rgb

            context.fill(cat.x, cat.y, cat.x + cat.width, cat.y + cat.height, bgColor)

            val textX = cat.x + (cat.width - textRenderer.getWidth(cat.name)) / 2
            val textY = cat.y + (cat.height - textRenderer.fontHeight) / 2
            context.drawTextWithShadow(textRenderer, cat.name, textX, textY, Color.WHITE.rgb)
        }
        
        val instructions = listOf(
            "Click toggles to enable/disable features",
            "Press 'O' to open overlay manager for positioning",
            "Press ESC to close this menu"
        )
        
        var instructionY = height - 80
        instructions.forEach { instruction ->
            context.drawTextWithShadow(textRenderer, instruction, 15, instructionY, Color.GRAY.rgb)
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
                if (element.onClick(mouseX.toInt(), mouseY.toInt())) {
                    return true
                }
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
    
    override fun shouldPause() = false
    override fun shouldCloseOnEsc() = true
    override fun renderBackground(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {}
}

data class Category(
    val name: String,
    var x: Int,
    var y: Int,
    var width: Int,
    var height: Int,
    var selected: Boolean = false
) {
    fun contains(mouseX: Int, mouseY: Int): Boolean {
        return mouseX in x until (x + width) && mouseY in y until (y + height)
    }
}
